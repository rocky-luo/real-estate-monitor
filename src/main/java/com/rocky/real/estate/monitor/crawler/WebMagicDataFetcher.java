package com.rocky.real.estate.monitor.crawler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rocky.real.estate.monitor.dao.HouseDao;
import com.rocky.real.estate.monitor.model.HousePo;
import com.rocky.real.estate.monitor.utils.DateUtil;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.dungproxy.webmagic7.DungProxyDownloader;
import com.virjar.dungproxy.webmagic7.DungProxyProvider;
import com.virjar.dungproxy.webmagic7.OfflineStrategy;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Created by rocky on 18/6/17.
 */
public class WebMagicDataFetcher implements PageProcessor {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebMagicDataFetcher.class);
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(0)
            .setTimeOut(20000)
            .addHeader("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
            .setUseGzip(true)
            .setCycleRetryTimes(3);
    private AtomicInteger count = new AtomicInteger(0);
    private HouseDao houseDao;
    private String date;

    private Pattern DETAIL_PAGE = Pattern.compile("https://cd\\.lianjia\\.com/ershoufang/[0-9]+\\.html");


    private static DungProxyDownloader dungProxyDownloader;

    static {
        DungProxyProvider dungProxyProvider = new DungProxyProvider("cd.lianjia.com", "https://cd.lianjia.com/", new OfflineStrategy() {
            @Override
            public boolean needOfflineProxy(Page page, AvProxy avProxy) {
                if (!page.isDownloadSuccess()){
                    avProxy.block(2 * 60 * 60 * 1000);
                }
                return false;
            }
        });
        dungProxyDownloader = new DungProxyDownloader();
        dungProxyDownloader.setProxyProvider(dungProxyProvider);
    }


    public WebMagicDataFetcher(HouseDao houseDao) {
        this.houseDao = houseDao;
        this.date = DateUtil.dayFormat(DateTime.now());

    }

    @Override
    public void process(Page page) {
        // 区分url
        if (queryCondition(page) == PageType.LIST) {
            List<String> otherListPages = page.getHtml().links().regex("https://cd\\.lianjia\\.com/ershoufang/[0-9a-z]+/").all();
            List<String> detailPages = page.getHtml().links().regex("https://cd\\.lianjia\\.com/ershoufang/[0-9]+\\.html").all();
            page.addTargetRequests(otherListPages);
            page.addTargetRequests(detailPages);
        } else if (queryCondition(page) == PageType.DETAIL){

            HousePo housePo = houseInfoFromDetailPage(page);

            page.putField("housePos", Lists.newArrayList(housePo));
            int c = count.addAndGet(1);
            if (c % 100 == 0) {
                LOGGER.info("目前已抓取数据:" + c);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void FetchData() {
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(SimpleProxyProvider.from(
                new Proxy("210.201.90.134", 80)

        ));
        Spider.create(this).addUrl("https://cd.lianjia.com/ershoufang")
                .addPipeline(new Pipeline() {
                    @Override
                    public void process(ResultItems resultItems, Task task) {
                        List<HousePo> pos = resultItems.get("housePos");
                        if (!CollectionUtils.isEmpty(pos)) {
                            //为了防止死锁,需要对pos进行一次排序
                            pos.sort(new Comparator<HousePo>() {
                                @Override
                                public int compare(HousePo o1, HousePo o2) {
                                    String s1 = o1.getThirdpartyId() + o1.getDate();
                                    String s2 = o2.getThirdpartyId() + o2.getDate();
                                    return s1.compareTo(s2);
                                }
                            });
                            houseDao.batchInsert(pos);
                        }
                    }
                })
                .setDownloader(dungProxyDownloader)
                .thread(Runtime.getRuntime().availableProcessors())
                .run();

    }


    private enum PageType {
        LIST, //列表页面
        DETAIL //详情页
    }

    private PageType queryCondition(Page page) {
        if (DETAIL_PAGE.matcher(page.getUrl().toString()).matches()) {
            return PageType.DETAIL;
        }else {
            return PageType.LIST;
        }
    }

    private List<String> zoneUrls(Page page) {
        List<String> urls = page.getHtml().css("div[data-role=ershoufang]>div:first-child>a", "href").all();
        List<String> finalUrls = Lists.newArrayList();
        for (String url : urls) {
            finalUrls.add("https://cd.lianjia.com" + url);
        }
        return finalUrls;
    }

    private List<String> targetUrls(Page page) {
        String totalPage = page.getHtml().css(".house-lst-page-box", "page-data")
                .regex("\"totalPage\":([0-9]+)", 1).toString();
        String currentPage = page.getHtml().css(".house-lst-page-box", "page-data")
                .regex("\"curPage\":([0-9]+)", 1).toString();
        String uriPatten = page.getHtml().css(".house-lst-page-box", "page-url").toString();


        List<String> targetUrls = Lists.newArrayList();
        if (Strings.isNullOrEmpty(totalPage) && Strings.isNullOrEmpty(currentPage)) {
            return targetUrls;
        }
        for (Integer i = 1; i <= Integer.valueOf(totalPage); i++) {
            if (!i.equals(Integer.valueOf(currentPage))) {
                if (i.equals(1)) {
                    targetUrls.add("https://cd.lianjia.com" + uriPatten.replaceFirst("pg\\{page\\}/", ""));
                }
                targetUrls.add("https://cd.lianjia.com" + uriPatten.replaceFirst("\\{page\\}", i.toString()));
            }
        }
        return targetUrls;
    }

    private List<HousePo> houseInfosFromListPage(Page page) {
        List<HousePo> housePos = Lists.newArrayList();
        List<Selectable> infoSels = page.getHtml().css("ul.sellListContent>li>div.info").nodes();
        String zone = WebmagicUtil.xmlText(page.getHtml().css("div[data-role=ershoufang]>div:first-child>a.selected"));
        for (Selectable infoSel : infoSels) {
            String title = WebmagicUtil.xmlText(infoSel.css(".title>a"));
            String link = infoSel.css(".title>a", "href").toString();
            String thirdpartyId = infoSel.css(".title>a", "data-housecode").toString();
            String community = WebmagicUtil.xmlText(infoSel.css(".houseInfo>a"));
            String houseType = infoSel.css(".houseInfo").regex("[0-9]室[0-9]厅").toString();
            String area = infoSel.css(".houseInfo").regex("([0-9\\.]+)平米").toString();
            String block = WebmagicUtil.xmlText(infoSel.css(".positionInfo>a"));
            String priceTotal = WebmagicUtil.xmlText(infoSel.css(".totalPrice>span"));
            String pricePerSquare = infoSel.css(".unitPrice", "data-price").toString();
            HousePo housePo = new HousePo();
            housePo.setTitle(title);
            housePo.setLink(link);
            housePo.setThirdpartyId(thirdpartyId);
            housePo.setCommunity(community);
            housePo.setHouseType(houseType);
            housePo.setArea(new BigDecimal(area));
            housePo.setBlock(block);
            housePo.setPriceTotal(new BigDecimal(priceTotal));
            housePo.setPricePerSquare(new BigDecimal(pricePerSquare));
            housePo.setZone(zone);
            housePo.setDate(this.date);
            housePos.add(housePo);


        }
        return housePos;

    }

    private HousePo houseInfoFromDetailPage(Page page) {
        String title = WebmagicUtil.xmlText(page.getHtml().css(".title .main"));
        String link = page.getUrl().toString();
        String thirdpartyId = page.getUrl().regex("([0-9]+)\\.html", 1).toString();
        String community = WebmagicUtil.xmlText(page.getHtml().css(".communityName .info"));
        List<Selectable> baseContents = page.getHtml().css(".introContent .base li").nodes();
        String houseType = null;
        String area = null;
        for (Selectable li : baseContents) {
            String label = WebmagicUtil.xmlText(li.css("span"));
            if ("房屋户型".equals(label)){
                houseType = li.regex("[0-9]室[0-9]厅").toString();
            }else if ("建筑面积".equals(label)){
                area = li.regex("([0-9\\.]+)㎡", 1).toString();
            }
        }
        String zone = WebmagicUtil.xmlText(page.getHtml().css(".areaName .info a").nodes().get(0));
        String block = WebmagicUtil.xmlText(page.getHtml().css(".areaName .info a").nodes().get(1));
        String priceTotal = WebmagicUtil.xmlText(page.getHtml().css(".price .total"));
        String pricePerSquare = page.getHtml().css("span.unitPriceValue").regex("[0-9\\.]+").toString();
        HousePo housePo = new HousePo();
        housePo.setTitle(title);
        housePo.setLink(link);
        housePo.setThirdpartyId(thirdpartyId);
        housePo.setCommunity(community);
        housePo.setHouseType(houseType);
        housePo.setArea(area==null?null:new BigDecimal(area));
        housePo.setBlock(block);
        housePo.setPriceTotal(new BigDecimal(priceTotal));
        housePo.setPricePerSquare(new BigDecimal(pricePerSquare));
        housePo.setZone(zone);
        housePo.setDate(this.date);
        return housePo;

    }

}

package com.rocky.real.estate.monitor.crawler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by rocky on 18/6/18.
 */
public class ProxyFetcher implements PageProcessor {
    private List<Proxy> maybeUseful = Lists.newArrayList(new Proxy("210.201.90.134", 80));
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    @Override
    public void process(Page page) {
        ProxyWebSite proxyWebSite = webSiteType(page.getUrl().toString());
        if (proxyWebSite == ProxyWebSite.kuaidaili) {
            List<Proxy> proxies = kuaidailiProxy(page);
            page.putField("proxies", proxies);
        }else if (proxyWebSite == ProxyWebSite.xicidaili) {
            List<Proxy> proxies =null;
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ProxyFetcher()).addUrl("https://www.kuaidaili.com/free/inha/1/")
                .addPipeline(new ConsolePipeline()).thread(1).run();

    }

    public List<Proxy> fetch(){
        Map<Proxy, String> proxyStringMap = Maps.newConcurrentMap();
        Spider.create(this).addUrl("https://www.kuaidaili.com/free/inha/1/",
                "http://www.xicidaili.com/")
                .addPipeline(new Pipeline() {
                    @Override
                    public void process(ResultItems resultItems, Task task) {
                        List<Proxy> proxies = resultItems.get("proxies");
                        for (Proxy proxy : proxies) {
                            proxyStringMap.put(proxy, "");
                        }
                    }
                }).thread(3).run();
        List<Proxy> result = Lists.newArrayList(proxyStringMap.keySet());
        result.addAll(maybeUseful);
        return result;
    }


    private enum ProxyWebSite {
        kuaidaili("kuaidaili"),
        xicidaili("xicidaili");
        private String name;
        private Pattern pattern;

        ProxyWebSite(String name) {
            this.name = name;
            this.pattern = Pattern.compile(".*"+name+".*");
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }
    }

    private ProxyWebSite webSiteType(String url) {
        if(ProxyWebSite.kuaidaili.getPattern().matcher(url).matches()) {
            return ProxyWebSite.kuaidaili;
        }else {
            return null;
        }
    }

    private List<Proxy> kuaidailiProxy(Page page) {
        List<Selectable> nodes = page.getHtml().css("tbody>tr").nodes();
        List<Proxy> proxies = Lists.newArrayList();
        for (Selectable node :nodes) {
            String ip = WebmagicUtil.xmlText(node.css("[data-title=IP]"));
            String port = WebmagicUtil.xmlText(node.css("[data-title=PORT]"));
            proxies.add(new Proxy(ip.trim(), Integer.valueOf(port)));
        }
        return proxies;
    }

    private List<Proxy> xicidaili(Page page) {
        return null;
    }
}

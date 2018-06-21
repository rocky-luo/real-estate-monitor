package com.rocky.real.estate.monitor.controller;

import com.google.common.base.Stopwatch;
import com.rocky.real.estate.monitor.crawler.ProxyManager;
import com.rocky.real.estate.monitor.crawler.WebMagicDataFetcher;
import com.rocky.real.estate.monitor.dao.HouseDao;
import com.rocky.real.estate.monitor.model.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created by rocky on 18/6/18.
 */
@Controller
@RequestMapping("/crawler")
public class CrawlerController {
    private final static Logger LOGGER = LoggerFactory.getLogger(CrawlerController.class);
    @Resource
    private HouseDao houseDao;

    @RequestMapping("/lianjia")
    @ResponseBody
    public DataResult lianjia(){
        LOGGER.info("=======抓取数据开始======");
        Stopwatch stopwatch = Stopwatch.createStarted();
        new WebMagicDataFetcher(houseDao).FetchData();
        LOGGER.info("=========抓取数据结束,耗时{}分钟========", stopwatch.elapsed(TimeUnit.MINUTES));
        return new DataResult(200,null,null);

    }
}

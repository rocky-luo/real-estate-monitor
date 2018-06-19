package com.rocky.real.estate.monitor.tasks;

import com.google.common.base.Stopwatch;
import com.rocky.real.estate.monitor.crawler.WebMagicDataFetcher;
import com.rocky.real.estate.monitor.dao.HouseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created by rocky on 18/6/17.
 */
@Component
public class DataFetch {
    private final static Logger LOGGER = LoggerFactory.getLogger(DataFetch.class);
    @Resource
    private HouseDao houseDao;

    @Scheduled(cron = "0 0 3 * * ?")
    public void lianjiaData(){
        LOGGER.info("==========定时抓取链家数据开始==========");
        Stopwatch stopwatch = Stopwatch.createStarted();
        new WebMagicDataFetcher(houseDao).FetchData();
        LOGGER.info("定时抓取链家数据结束,总共耗时{}秒", stopwatch.elapsed(TimeUnit.SECONDS));

    }

}

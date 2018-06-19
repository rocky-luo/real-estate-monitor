package com.rocky.real.estate.monitor.controller;

import com.rocky.real.estate.monitor.crawler.ProxyManager;
import com.rocky.real.estate.monitor.crawler.WebMagicDataFetcher;
import com.rocky.real.estate.monitor.dao.HouseDao;
import com.rocky.real.estate.monitor.model.DataResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by rocky on 18/6/18.
 */
@Controller
@RequestMapping("/crawler")
public class CrawlerController {
    @Resource
    private HouseDao houseDao;

    @RequestMapping("/lianjia")
    @ResponseBody
    public DataResult lianjia(){
        new WebMagicDataFetcher(houseDao).FetchData();
        return new DataResult(200,null,null);

    }
}

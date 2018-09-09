package com.rocky.real.estate.monitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by rocky on 18/6/11.
 */
@Controller
@RequestMapping("/hello")
public class HelloController {
    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping("/time")
    @ResponseBody
    public String hello() {
        String msg = "hello it's " + new Date();
        logger.info(msg);
        return msg;
    }

    @RequestMapping("/cpu")
    @ResponseBody
    public String cpu(){
        return ""+Runtime.getRuntime().availableProcessors();
    }
}

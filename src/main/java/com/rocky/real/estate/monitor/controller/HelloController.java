package com.rocky.real.estate.monitor.controller;

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

    @RequestMapping("/time")
    @ResponseBody
    public String hello() {
        return "hello it's " + new Date();
    }

    @RequestMapping("/cpu")
    @ResponseBody
    public String cpu(){
        return ""+Runtime.getRuntime().availableProcessors();
    }
}

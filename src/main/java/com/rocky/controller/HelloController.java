package com.rocky.controller;

import com.google.common.collect.Lists;
import com.rocky.dao.IHelloWorldDao;
import com.rocky.model.DataResult;
import com.rocky.model.PersonPo;
import com.rocky.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by rocky on 18/6/11.
 */
@Controller
public class HelloController {
    @Resource
    private IHelloWorldDao helloWorldDao;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello it's " + new Date();
    }

    @RequestMapping("/json")
    @ResponseBody
    public DataResult json(){
        User user = new User();
        user.setName("rocky");
        user.setAge(18);
        user.setBrithDay(new Date());
        User user1 = new User();
        user1.setName("rocky1");
        user1.setAge(20);
        user1.setBrithDay(new Date());
        DataResult dataResult = new DataResult(200, null, Lists.newArrayList(user,user1));
        return dataResult;
    }

    @RequestMapping(value = "/person/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DataResult person(@PathVariable Long id) {
        PersonPo personPo = helloWorldDao.getById(id);
        return new DataResult(200, null, personPo);
    }

    @RequestMapping(value = "/person", method = RequestMethod.POST)
    @ResponseBody
    public DataResult personInsert(@RequestBody PersonPo personPo) {
        helloWorldDao.insert(personPo);
        PersonPo personPo1 = helloWorldDao.getById(personPo.getId());
        return new DataResult(200, null, personPo1);
    }

    @RequestMapping("/hello/person")
    public String helloPage(Model model){
        PersonPo personPo = helloWorldDao.getById(1L);
        model.addAttribute("name", personPo.getName());
        model.addAttribute("id", personPo.getId());
        return "helloworld";
    }

}

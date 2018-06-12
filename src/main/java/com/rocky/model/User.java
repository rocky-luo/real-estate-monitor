package com.rocky.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by rocky on 18/6/11.
 */
public class User {
    private String name;
    private int age;
    @JSONField(format ="yyyy-MM-dd HH:mm:ss" )
    private Date brithDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBrithDay() {
        return brithDay;
    }

    public void setBrithDay(Date brithDay) {
        this.brithDay = brithDay;
    }
}

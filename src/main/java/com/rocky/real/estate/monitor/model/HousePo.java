package com.rocky.real.estate.monitor.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by rocky on 18/6/18.
 */
public class HousePo {
    private Long id;

    // 名称
    private String title;

    // 第三方id
    private String thirdpartyId;

    // 区，成华区，锦江区等
    private String zone;

    // 街区，万年场，龙潭寺等
    private String block;

    // 小区，红枫林等
    private String community;

    // 面积
    private BigDecimal area;

    // 每平方单价（元）
    private BigDecimal pricePerSquare;

    // 总价（元）
    private BigDecimal priceTotal;

    // 户型，一室一厅等
    private String houseType;

    // 链接
    private String link;

    // 抓取日期，如20180603
    private String date;

    // 创建时间
    private Date ctime;

    // 更新时间
    private Date utime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThirdpartyId() {
        return thirdpartyId;
    }

    public void setThirdpartyId(String thirdpartyId) {
        this.thirdpartyId = thirdpartyId;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getPricePerSquare() {
        return pricePerSquare;
    }

    public void setPricePerSquare(BigDecimal pricePerSquare) {
        this.pricePerSquare = pricePerSquare;
    }

    public BigDecimal getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(BigDecimal priceTotal) {
        this.priceTotal = priceTotal;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }
}

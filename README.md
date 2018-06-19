** 数据库
```
CREATE TABLE `house` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(32) DEFAULT '' COMMENT '名称',
  `thirdparty_id` varchar(32) DEFAULT NULL COMMENT '第三方id',
  `zone` varchar(16) DEFAULT NULL COMMENT '区，成华区，锦江区等',
  `block` varchar(16) DEFAULT NULL COMMENT '街区，万年场，龙潭寺等',
  `community` varchar(16) DEFAULT NULL COMMENT '小区，红枫林等',
  `area` decimal(6,2) DEFAULT NULL COMMENT '面积',
  `price_per_square` decimal(7,1) DEFAULT NULL COMMENT '每平方单价（元）',
  `price_total` decimal(7,1) DEFAULT NULL COMMENT '总价（元）',
  `house_type` varchar(8) DEFAULT NULL COMMENT '户型，一室一厅等',
  `link` varchar(128) DEFAULT NULL COMMENT '链接',
  `date` varchar(8) NOT NULL DEFAULT '' COMMENT '抓取日期，如20180603',
  `ctime` datetime NOT NULL COMMENT '创建时间',
  `utime` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_thirdparty_id_date` (`thirdparty_id`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=54608 DEFAULT CHARSET=utf8;
```

** 启动命令
```
mvn clean spring-boot:run -Prasp -Dmaven.test.skip
```
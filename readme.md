#品优购项目简介

##系统架构
 ! [avator](img/架构图.png)
 
##启动顺序

 ###一、商家后台
 
1.`pinyougou-sellergoods-service` _9001_

2.`pinyougou-shop-web` _9102_

 ###二、运营商后台
 
1.`pinyougou-sellergoods-service` _9001_

2.`pinyougou-content-service` _9002_

3.`pinyougou-manager-web` _9101_

 ###三、网站首页
 
1.`pinyougou-content-service` _9002_

2.`cn.waylon.sms` _9003_

3.`pinyougou-portal-web` _9103_

 ###四、搜索频道
 
1.`pinyougou-search-service` _9004_

2.`pinyougou-search-web` _9104_

 ###五、商家首页
 
1.`pinyougou-page-service` _9005_

2.`pinyougou-page-web` _9105_

 ###六、用户中心
 
1.`pinyougou-user-service` _9006_

2.`pinyougou-pay-service` _9000_

3.`cas` _9100_

4.`pinyougou-user-web` _9106_

 ###七、购物车
 
1.`pinyougou-user-service` _9006_

2.`pinyougou-pay-service`  _9000_

3.`pinyougou-order-service`  _9008_

4.`pinyougou-cart-service`  _9007_

5.`pinyougou-cart-web`  _9107_

 ###八、秒杀
 
1.`pinyougou-seckill-service` _9009_

2.`pinyougou-pay-service` _9000_

3.`pinyougou-task-service` _9108_

4.`pinyougou-seckill-web` _9109_

##基础服务

1.`redis` _6370_

2.`mysql` _3306_

3.`activemq` _61616_ _8161_

4.`solr` _8380_ 

5.`dubbo ` _8080_

6.`cas` _9100_ 

7.`zookeeper` _2181_

8.`Sms_Service ` _9003_











# RocketMQ基本介绍

 源码地址∶ https://github.com/apache/rocketmq

 中文文档∶ https://github.com/apache/rocketmq/tree/master/docs/cn

商业版∶https://www.aliyun.com/product/rocketmq 

官网翻译∶http://www.itmuch.com/books/rocketmq/ 

FAQ: http://rocketmq.apache.org/docs/faq/

默认配置:https://www.cnblogs.com/jice/p/11981107.html

RocketMQ常用管理命令:https://blog.csdn.net/gwd1154978352/article/details/80829534

## 1.RocketMQ单机二进制安装
[参考](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/RocketMQ/CentOS%E4%BA%8C%E8%BF%9B%E5%88%B6%E6%96%B9%E5%BC%8F%E5%AE%89%E8%A3%85RocketMQ4.7.1.md)

启动服务时，先启动 NameServer，再启动 Broker，停机的时候相反。

## 2.RocketMQ 集群部署

[参考](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/RocketMQ/CentOS%E4%BA%8C%E8%BF%9B%E5%88%B6%E6%96%B9%E5%BC%8F%E5%AE%89%E8%A3%85RocketMQ4.7.1.md)

服务有了，有没有像 RabbitMQ一样，通过插件提供一个管理界面，方便我们查看服务状态和元数据呢?

## 3.管理控制台（web console）

[参考](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/RocketMQ/RocketMQ%20web%E6%8E%A7%E5%88%B6%E5%8F%B02.0.0%E7%89%88%E6%9C%AC%E6%BA%90%E7%A0%81%E6%96%B9%E5%BC%8F%E5%AE%89%E8%A3%85.md)

RocketMQ的 externals 包里面提供了一个 web 控制台，需要单独部署。除了控制台，这里面也包含对大数据 flume、hbase 等组件的对接和扩展。
1. 运维∶ 主要是设置 nameserver 和配置 vipchannel

2. 驾驶舱∶控制台的 dashboard，可以分别按 broker 和主题来查看消息的数量和趋势。

3. 集群∶ 整个 RocketMQ的集群情况，包括分片，编号，地址，版本，消息生产和消息消费的 TPS 等，这个在做性能测试的时候可以作为数据指标。

4. 主题∶即 topic，可以新增/更新 topic;也看查看 topic的信息，如状态，路由，消费者管理和发送消息等。

5. 消费者∶可以在当前 broker中查看/新建消费者 group，包括消费者信息和消费进度。

6. 生产者∶ 可以在当前 broker 中查看生产组下的生产者 group，包生产者信息和生产者状态。

7. 消息∶ 可以按照 topc，messagelD，messageKey 分别查询具体的消息。 8、用户中心∶ 切换语言和登陆相关（登陆需要在 console 的配置中打开对应配置，默认不需要登陆）。

  > 其中最常用的是集群，主题，消费者和消息这四部分。

## 4.RocketMQ架构

http://rocketmq.apache.org/docs/rmq-arc

![rmq-basic-arc](images/rmq-basic-arc.png)

> 一般见到的架构图都是这样的，其中这些重要的角色需要解释下。

### 4.1 Broker

RocketMQ的服务, 或者说一个进程，叫做 Broker, Broker的作用是存储和转发消息。

RocketMQ单机大约能承受 10万 QPS的请求。


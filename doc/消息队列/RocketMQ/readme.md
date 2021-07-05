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
7. 消息∶ 可以按照 topc，messagelD，messageKey 分别查询具体的消息。
8. 用户中心∶ 切换语言和登陆相关（登陆需要在 console 的配置中打开对应配置，默认不需要登陆）。

  > 其中最常用的是集群，主题，消费者和消息这四部分。

## 4.RocketMQ架构

http://rocketmq.apache.org/docs/rmq-arc

![rmq-basic-arc](images/rmq-basic-arc.png)

> 一般见到的架构图都是这样的，其中这些重要的角色需要解释下。

### 4.1 Broker

RocketMQ的服务, 或者说一个进程，叫做 Broker, Broker的作用是存储和转发消息，RocketMQ单机大约能承受 **10万**QPS的请求。

为了提升 Broker的可用性(防止单点故障)，以及提升服务器的性能(实现负载)，通常会做集群的部署, 跟kafka或者Redis Cluster一样，RocketMQ集群的每个 Broker节点保存总数据的一部分，因此可以实现横向扩展。

为了提升可靠性(防止数据丢失)，每个 Broker 可以有自己的副本(slave)，默认情况下，读写都发生在 master上。

在 `slaveReadEnable=true` 的情况下，slave也可以参与读负载，但是默认只有Brokerld=1的slave才会参与读负载，而且是在master消费慢的情况下，由 `whichBrokerWhenConsumeSlowly`这个参数决定。

### 4.2 Topic

Topic 用于将消息按主题做划分，比如订单消息、物流消息。

Producer将消息发往指定的Topic，Consumer订阅这个Topic就可以收到相应的消息。

跟kafka一样，如果Topic不存在，会自动创建，BrokerConfig：

```java
private boolean autoCreateTopicEnable = true;
```

Topic 跟生产者和消费者都是多对多的关系，一个生产者可以发送消息到多个 Topic，一个消费者也可以订阅多个 Topic。

> 注意，跟kafka不同的是，在RocketMQ中，Topic 是一个逻辑概念，消息不是按Topic划分存储的。

### 4.3 NameServer

当不同的消息存储在不同的Broker上，生产者和消费者对于Broker的选取，或者说路由选择是一个非常关键的问题。

1. (路由)生产者发一条消息，应该发到哪个Broker? 消费者接收消息，从哪个Broker获取消息?
2. (服务端增减)如果Broker增加或者减少了，客户端怎么知道?
3. (客户端增加)一个新的生产者或者消费者加入，怎么知道有哪些 Broker?

所以，跟分布式的服务调用的场景需要一个注册中心一样，在RocketMQ中需要有一个角色来管理Broker的信息，类似Kafka里Zookeeper的角色，而RocketMQ是自己实现了一个服务，这个服务叫做NameServer。

我们可以把 NameServer理解为是RocketMQ的路由中心，每一个NameServer节点都保存着全量的路由信息，为了保证高可用，NameServer 自身也可以做集群的部署，它的作用有点像Eureka或者Redis Sentinel。

也就是说，Broker会在NameServer上注册自己，Porducer和Consumer用NameServer来发现Broker。

**NameServer 作为路由中心到底怎么工作的呢?**
每个Broker节点在启动时，都会根据配置遍历NameServer列表rocketmq/conf/broker.conf

```properties
namesrvAddr=localhost:9876
```

与每个NameServer建立TCP长连接，注册自己的信息，之后每隔30s发送心跳信息(服务主动注册)。

如果Broker挂掉了，不发送心跳了，NameServer怎么发现呢?

所以除了主从注册，还有定时探活，每个NameServer每隔10s检查一下各个Broker的最近一次心跳时间，如果发现某个Broker超过120s都没发送心跳，就认为这个Broker已经挂掉了，会将其从路由信息里移除。



> 思考∶ 如果让你设计一个注册中心，你怎么实现心跳检测、探活文些基本功自?
>
> Nameserver 的作用是用来管理 Broker 的服务的，也就是服务注册与发现，那为什么不直接用 Zookeeper、Consul、etcd、Eureka 这样的组件呢?



**为什么不用 Zookeeper?**

实际上不是不用，在 RocketMQ的早期版本，即MetaQ1.x和2.x阶段，服务管理也是用Zookeeper 实现的，跟kafka一样，但 MetaQ3.x（即 RocketMQ）却去掉了ZooKeeper 依赖，转而采用自己的 NameServer。

RocketMQ的架构设计决定了只需要一个轻量级的元数据服务器就足够了，只需要保持最终一致，而不需要 Zookeeper 这样的强一致性解决方案，不需要再依赖另一个中间件，从而减少整体维护成本。

根据著名的 CAP 理论∶ 一致性（Consistency）、可用性（Availability）、分区容错（Partiton Tolerance）。Zookeeper 实现了 CP，NameServer 选择了AP放弃了实时一致性。



**一致性问题如何解决?**

NameServer 之间是互相不通信的，也没有主从之分，它们是怎么保持一致性的?

我们从3个点来分析一下∶ 

1. 服务注册

   如果新增了Broker，怎么新增到所有的NameServer中?

   因为没有master，Broker每隔30秒会向所有的NameServer发送心跳信息，所以还是能保持一致的。 

   

2. 服务剔除

   如果一个Broker挂了，怎么从所有的NameServer中移除它的信息?

   - 如果Broker正常关闭∶ 连接就断开了，Netty的通道关闭监听器会监听到连接断开事件，然后会将这个Broker信息剔除掉。
   - 如果 Broker 异常关闭∶ NameServer的定时任务每10秒扫描Broker列表，如果某个Broker的心跳包的最新时间戳超过当前时间120秒，就会被移除。

   通过以上两点，不管Broker是挂了，还是恢复了，增加了还是减少了，NameServer都能够保持数据—致。 

   

3. 路由发现
   如果Broker的信息更新了（增加或者减少节点），客户端怎么获取最新的 Broker列表?

   - 生产者：发送第一条消息的时候，根据Topic从NameServer 获取路由信息。

   - 消费者：消费者一般是订阅固定的Topic，在启动的时候就要获取Broker信息。

   如果Broker信息动态变化了怎么办?

   因为NameServer不会主动推送服务信息给客户端，客户端也不会发送心跳到Nameserver，所以在建立连接之后，需要生产者和消费者定期更新。

   在 MQClientInstance类（生产者消费者通用）的 start 方法中，启动了一个定时任务 (237行)：

   ```java
   //Start various schedule tasks 
   this.startScheduledTask();
   ```

   其中第二个任务 `updateTopicRoutelnfoFromNameServer`方法，是用来定期更新NameServer信息的，默认是30秒获取一次。

   ```java
   this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
   	@Override 
       public void run() {
           try {
   			MQClientInstance this.updateTopicRouteInfoFromNameServer();
           } catch (Exception e) {
   			log.error("ScheduledTask updateTopicRouteInfoFromNameServer exception", e);
           }
       }
       , 10, this.clientConfig.getPollNameServerInterval(), TimeUnit.MLLISECONDS);
   }
   ```

   消费者和生产者以相同的时间间隔，更新NameServer信息

   ```java
   public void updateTopicRouteInfoFromNameServer() {
       Set<String topicList = new HashSet<String>();
       //Consumer
       {
           Iterator<Entry<String, MQConsumerlInner>> it = this.consumerTable.entrySet().iterator();
           while (it.hasNext()){
               Entry<String, MQConsumerInner> entry = it.next();
               MQConsumerInner impl = entry.getValue();
               if (impl != null) {
                   Set<SubscriptionData> subList = impl.subscriptions();
                   if (subList != null) {
                       for(SubscriptionData subData: subList) {
                           topicList.add(subData.getTopic));
                       }
                   }
               }
           }
       }
       
       // Producer
       {
           Iterator<Entry<String, MQProducerInner>> it = this.producerTable.entrySet().iterator();
           while (it.hasNext()) {
               Entry<String, MQProducerInner> entry = it.next();
               MQProducerInner impl = entry.getValue();
               if (impl!= null) {
                   Set<String> lst = impl.getPublishTopicList();
                   topicList.addAll(lst);
               }
           }
       }
       
       for (String topic: topicList) {
           this.updateTopicRouteInfoFromNameServer(topic);
       }
   }
   ```

   拉取的时间间隔由 DefaultMQPushConsumer的 pollNameServerlnterval参数决定，默认是 30 秒。

   `org.apache.rocketmq.client.ClientConfig`

   ```java
   private int pollNameServerInterval=1000*30;
   ```

   总结一下，各个 NameServer 的数据是能够保持一致的。而且生产者和消费者会定期更新路由信息，所以可以获取最新的信息。

   问题∶ 如果 Broker 刚挂，客户端 30 秒以后才更新路由信息，那是不是会出现最多30 秒钟的数据延迟?比如说一个 Broker 刚挂了，客户端缓存的还是旧的路由信息，发消息和接收消息都会失败。

   这个问题有几个解决思路∶

   1. 重试;
   2. 把无法连接的 Broker 隔离掉，不再连接;
   3. 3）或者优先选择延迟小的节点，就能避免连接到容易挂的 Broker 了。
      问题∶ 如果作为路由中心的 NameServer 全部挂掉了，而且暂时没有恢复呢?
      也没有关系，客户端肯定要缓存 Broker 的信息，不能完全依赖于NameServer。
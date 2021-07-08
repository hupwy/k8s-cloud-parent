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

根据著名的 CAP 理论∶ 一致性（Consistency）、可用性（Availability）、分区容错（Partiton Tolerance）。

Zookeeper 实现了 CP，NameServer 选择了AP放弃了实时一致性。



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

   拉取的时间间隔由DefaultMQPushConsumer的pollNameServerlnterval参数决定，默认是 30 秒。

   `org.apache.rocketmq.client.ClientConfig`

   ```java
   private int pollNameServerInterval=1000*30;
   ```

   总结一下，各个NameServer的数据是能够保持一致的，而且生产者和消费者会定期更新路由信息，所以可以获取最新的信息。

   > 问题∶ 如果 Broker 刚挂，客户端30秒以后才更新路由信息，那是不是会出现最多30 秒钟的数据延迟?
   >
   > 比如说一个 Broker 刚挂了，客户端缓存的还是旧的路由信息，发消息和接收消息都会失败。

   这个问题有几个解决思路∶

   1. 重试
   2. 把无法连接的Broker隔离掉，不再连接;
   3. 者优先选择延迟小的节点，就能避免连接到容易挂的Broker了。

   > 问题∶ 如果作为路由中心的NameServer全部挂掉了，而且暂时没有恢复呢?
   > 也没有关系，客户端肯定要缓存Broker的信息，不能完全依赖于NameServer。

### 4.4 Producer

生产者，用于生产消息，会定时从NameServer拉取路由信息(不用配置RocketMQ的服务地址)，然后根据路由信息与指定的Broker建立TCP长连接，从而将消息发送到Broker中。

**特点**

1. 发送逻辑一致的Producer可以组成一个Group。
2. RocketMQ的生产者同样支持批量发送，不过List要自己传进去。
3. Producer写数据只能操作 master节点。

### 4.5 Consumer

消息的消费者，通过NameServer集群获得Topic的路由信息，连接到对应的Broker上消费消息。

**特点**

- 消费逻辑一致的Consumer可以组成一个 Group，这时候消息会在Consumer之间负载。

- 由于Master和Slave都可以读取消息，因此Consumer会与Master和Slave 都建立连接。

> 注意∶ 同一个 consumer group内的消费者应该订阅同一个topic。
>
> 或者反过来，消费不同 topic的消费者不应该采用相同的consumer group名字。
>
> 如果不一样，后面的消费者的订阅，会覆盖前面的订阅。

**消费者有两种消费方式∶** 

- 一种是集群消费（消息轮询）

- 一种是广播消费（全部收到相同副本）。

#### 4.5.1 pull

从消费模型来说，RocketMQ 支持 pull 和 push 两种模式。

- Pull 模式是consumer 轮询从broker拉取消息。

  - 实现类∶`DefaultMQPulIConsumer`(过时)，替代类:`DefaultLitePullConsumer`。

    ```java
    DefaultMQPullConsumer consumer =new DefaultMQPullConsumer("my test consumer group");
    ```

- Pull有两种实现方式∶ 

  - 一种是普通的轮询（Polling）

    不管服务端数据有无更新，客户端每隔定长时间请求拉取一次数据，可能有更新数据返回，也可能什么都没有。

    > 普通轮询的缺点∶因为大部分时候没有数据，这些无效的请求会大大地浪费服务器的资源。而且定时请求的间隔过长的时候，会导致消息延迟。

  - RocketMQ用长轮询(Long Polling)来实现

    客户端发起Long Polling，如果此时服务端没有相关数据，会hold住请求，直到服务端有相关数据，或者等待一定时间超时才会返回。

    返回后，客户端又会立即再次发起下一次Long Polling(所谓的hold住请求指的服务端暂时不回复结果，不关闭请求连接，等相关数据准备好，写回客户端）。

    > 长轮询解决了轮询的问题，唯一的缺点是服务器在挂起的时候比较耗内存。

#### 4.5.2 push

Push模式是Broker推送消息给consumer，实现类: `DefaultMQPushConsumer`

```java
DefaultMOPushConsumer consumer= new DefaultMQPushConsumer("my test consumer group")
```

RocketMQ的push模式实际上是基于pull 模式实现的，只不过是在 pull模式上封装了一层，所以RocketMQ的push模式并不是真正意义上的"推模式"。

在 RocketMQ中，`PushConsumer`会注册`MessageListener`监听器，取到消息后，唤醒`MessageListener`的`consumeMessage`来消费，对用户而言，感觉消息是被推送过来的。

```java
while (!this.isStopped()) {
    try {
        PullRequest pullRequest = this.pullRequestQueue.take();
        this.pullMessage(pullRequest);
    } catch (InterruptedException ignored){
    } catch (Exception e) {
        log.error("Pull Message Service Run Method exception",e);
    }
}
```

### 4.6 Message Queue

RocketMQ支持多master的架构。

> 思考一个这样的问题∶ 当有多个master的时候，发往一个Topic的多条消息会在多个master的Broker上存储，那么发往某一个Topic的多条消息，是不是在所有的Broker上存储完全相同的内容?
>
> 肯定不是的。
>
> 如果所有的master存储相同的内容，而slave又跟master存储相同的内容∶ 
>
> 1. 浪费了存储空间。
>
> 2. 无法通过增加机器数量线性地提升Broker的性能，
>
> 也就是只能垂直扩展，通过升级硬件的方式提升性能，无法实现横向(水平)扩展，那么在分布式的环境中，RocketMQ的性能肯定会受到非常大的限制。
>
> 一句话∶不符合分片的思想。



> 那么最关键的问题来了，怎么把发到一个Topic里面的消息分布到不同的master上呢?
>
> 在kafka里面设计了一个partition，一个Topic可以拆分成多个partition，这些partition可以分布在不同的Broker上，这样就实现了数据的分片，也决定了kafka 可以实现横向扩展。



RocketMQ 有没有这样的设计呢?

在一个Broker上，RocketMQ只有一个存储文件，并没有像kafka一样按照不同的Topic分开存储。数据目录∶

```
/rocketmq/store/broker-a/commitlog
```

如果有3个Broker，也就是只有3个用来存储不同数据的commitlog。



那问题就来了，如果不按照分区去分布，数据应该根据什么分布呢?

RocketMQ里面设计了一个叫做 Message Queue的逻辑概念，作用跟 partition类似。

首先，我们创建Topic的时候会指定队列的数量，一个叫**writeQueueNums** (写队列数量)，一个**readQueueNums**(读队列数量)

写队列的数量决定了有几个Message Queue，读队列的数量决定了有几个线程来消费这些Message Queue(只是用来负载的)

那不指定MQ的时候，默认有几个MQ呢?

服务端创建一个 Topic 默认8个队列(`BrokerConfig`)∶

```java
private int defaultTopicQueueNums=8;
```

topic不存在，生产者发送消息时创建默认4个队列(`DefaultMQProducer`)

```java
private volatile int defaultTopicQueueNums=4;
```

服务端创建的时候有一个判断，取小一点的值，MQClientInstance 616 行∶

```java
int queueNums= Math.min(defaultMQProducer.getDefaultTopicQueueNums(), data.getReadQueueNums());
```

MessageQueue 在磁盘上是可以看到的，但是数量只跟写队列相关。 

比如TopicTest有4个写队列，consumequeue目录下面就会出现四个目录∶/usr/local/soft/rocketmq/store/broker-a/consumequeue/TopicTest/

```
drwxr-xr-x.2 root root 34 Sep 22 04:26 0 
drwxr-xr-x.2 root root 34 Sep 22 04:26 1
drwxr-xr-x.2 root root 34 Sep 22 04:26 2
drwxr-xr-x.2 root root 34 Sep 22 04:26 3
```

客户端封装了一个MessageQueue对象，里面其实就是三块内容

```java
private String topic; 
private String brokerName; 
private int queueld;
```

- Topic表示它是哪个topic的队列
- Broker代表它在哪个Broker上，比如有两个master，一个叫broker-a，一个叫broker-b
- queueld代表它是第几个分片

> 例如：
>
> -  一个Topic有3个Message Queue，编号是1、2、3。
>
> - 刚好有三个Broker，第一个MQ指向Broker1，第二个MQ指向Broker2，第三个MQ指向 Broker3。
>
> - 发送消息的时候，生产者会根据一定的规则，获得MessageQueue，只要拿到了queueld，就知道要发往哪个Broker，然后在commitlog写入消息。

磁盘上看到的队列数量，是由写队列的数量决定的，而且在所有master上的个数是一样的（但是数据存储不一样）。



#### 事例1

> 例如∶集群有两个master。如果创建一个topic，有2个写队列、1个读队列（topic名字∶q-2-1）。
>
> 那么两台机器的 consumequeue 目录会出现2个队列，一共4个队列。

/usr/local/soft/rocketmq/store/broker-a/consumequeue/q-2-1也就是总队列数量是∶**写队列数*节点数。**

![image-20210708112316140](images/image-20210708112316140.png)

如果我们发送6条消息，给消息依次编号，会选择什么队列发送呢?

```java
for (int i= 0; i<6; i++){
    Message msg = new Message("q-2-1",
                              "TagA",
                              "test",
                              ("RocketMQ" + String.format("%05d",i)).getBytes());
    SendResult sendResult = producer.send(msg);
    System.out.println(String.format("%05d",i)+":"+sendResult)
}
```

**消息接收顺序（看SendResult）∶ a-q0,a-q1,b-q0,b-q1,a-q0,a-q1**

| 输出列表                      |
| ----------------------------- |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-a,queueId=1 |
| BrokerName=broker-b,queueId=0 |
| BrokerName=broker-b,queueId=1 |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-a,queueId=1 |

因为消费者只有1个读队列，只能消费编号为0的队列，所以读到的消息是 a-q0，b-q0，a-q0的消息，序号是0，2，4。

**则1，3，5序号的消息没有被消费**

| 输出列表                      |
| ----------------------------- |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-b,queueId=0 |
| BrokerName=broker-a,queueId=0 |



#### 事例2

> 如果是1个写队列，2个读队列（topic名字∶q-1-2），那么两个 broker 的 consumequeue 目录会出现1个队列。

/usr/local/soft/rocketmq/store/broker-a/consumequeue/q-1-2

![image-20210708112341042](images/image-20210708112341042.png)

发送6条消息，依次编号∶消息接收顺序 a-q0，b-q0，a-q0，b-q0a-q0，b-q0

| 输出列表                      |
| ----------------------------- |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-b,queueId=0 |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-b,queueId=0 |
| BrokerName=broker-a,queueId=0 |
| BrokerName=broker-b,queueId=0 |

消费者有 2 个读队列，可以消费编号0 的队列。

**所有消息都可以接收到。**



#### 结论

根据`事例1`和`事例2`的表述，读写队列数量最好一致，否则会出现消费不了的情况。



> 思考∶Queue 的数量到底会产生什么影响? 
>
> Queue的数量要比Broker的数量多（倍数），才能实现尽量平均的负载，或者应对未来的扩容。
>
> 队列数量也要比消费者数量多，否则有部分消费者无法消费消息。



## 5. RocketMQ原理

### 5.1 生产者

前面我们说Message Queue是用来实现横向扩展的，生产者利用队列可以实现消息的负载和平均分布，那什么时候消息会发到哪个队列呢?

##### 5.1.1.消息发送规则

从Producer的send方法开始跟踪，在`DefaultMQProducerlmpl`的`select`方法会选择要发送的Queue(568 行)∶

```java
public MessageQueue selectOneMessageQueue(final TopicPublishInfo tpInfo, final String lastBrokerName){
    return this.mqFaultStrategy.selectOneMessageQueue(tpInfo, lastBrokerName);
}   
```

调用的是`MQFaultStratage`的选择队列的方法，这个类是 MQ负载均衡的核心类 

```java
int index= tpInfo.getSendWhichQueue().getAndIncrement();
for (int i=0;i<tpInfo.getMessageQueueList().size();i++){
    int pos= Math.abs(index++) % tpInfo.getMessageQueueList().size();
    if(pos < 0)
        pos = 0;
    MessageQueue mq= tpInfo.getMessageQueueList().get(pos);
    if (latencyFaultTolerance.isAvailable(mq.getBrokerName))){
        if (null == lastBrokerName || mq.getBrokerName().equals(lastBrokerName))
            return mq;
    }
}

```

之前我们看过默认的结果，是轮询的∶a-q0，a-q1，b-q0，b-q1……

MessageQueueSelector 有三个实现类∶

1. `SelectMessageQueueByHash`(默认) : 它是一种不断自增、轮询的方式。

2. `SelectMessageQueueByRandom`∶ 随机选择一个队列。

3. `SelectMessageQueueByMachineRoom`∶ 返回空，没有实现。

除了上面自带的策略，也可以自定义`MessageQueueSelector`，作为参数传进去：

```java
SendResult sendResult = producer.send(msg, new MessageQueueSelector(){
    public MessageQueue select(List<MessageQueue>mqs, Message msg, Object arg) {
        Integer id = (Integer) arg;
        int index = id % mqs.size();
        return mqs.get(index);
    }
}.i);
```

##### 5.1.2 顺序消息

顺序消息的场景∶ 一个客户先提交了一笔订单，订单号位1688，然后支付，后面又发起了退款，产生了三条消息∶ 

1. 提交订单的消息
2. 支付的消息
3. 退款的消息。

这三笔消息到达消费者的顺序，肯定要跟生产者产生消息的顺序一致。不然，没有订单，不可能付款，没有付款，是不可能退款的。

在RPC调用的场景中我们不用考虑有序性的问题，本来代码中调用就是有序的。而消息中间件经过了Broker的转发，而且可能出现多个消费者并发消费，就会导致乱序的问题。

这里我们先区分一个概念，全局有序和局部有序。

- 全局有序就是不管有几个生产者，在服务端怎么写入，有几个消费者，消费的顺序跟生产的顺序都是一致的，实现比较麻烦，而且即使实现了，也会对MQ的性能产生很大的影响

- 我们这里说的顺序消息其实是局部有序

  比如不同的颜色表示不同的订单相关的消息，只要同一个订单相关的消费的时候是有序的时候就OK了。
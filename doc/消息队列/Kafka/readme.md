# Kafka

## 基础介绍

### 官方网站

- http://kafka.apache.org/documentation.html#introduction 
- https://github.com/apache/kafka
- https://kafka.apachecn.org/documentation.html

### 作用

- 异步
- 解耦
- 削峰

### 应用场景

- 消息传递 Messaging

	- Website Activity Tracking 网站活动跟踪

		- 把用户活动发布到数据管道中，可以用来做监控，实时处理，报表等。
例如：外卖，物流，电力系统的实时数据

	- Log Aggregation 日志聚合

		- 实现分布式的日志聚合

	- metics 应用指标监控

		- 对运维数据的监控，CPU，内存，磁盘，网络连接的使用情况，可以实现告警

- 数据集成+流计算

	- 数据集成

		- kafka的数据导入Hadoop，Hbase等离线数据仓库，实现数据分析

	- 流式计算

		- 对Stream做实时的计算

			- Kafka Streams API

### Kafka安装与命令

- [CentOSKafka 单机集群安装（伪集群）](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/Kafka/Kafka%E5%8D%95%E8%8A%82%E7%82%B9%E9%9B%86%E7%BE%A4(%E4%BC%AA%E9%9B%86%E7%BE%A4).md)

- [kafka 常用命令](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/Kafka/kafka%E5%B8%B8%E7%94%A8%E5%91%BD%E4%BB%A4(%E5%9F%BA%E4%BA%8E2.6%E7%89%88%E6%9C%AC).md)

- [基于Canal 和Kafka 实现数据同步](https://github.com/hupwy/k8s-cloud-parent/blob/main/doc/%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97/Kafka/%E5%9F%BA%E4%BA%8ECanal%E5%92%8CKafka%E5%AE%9E%E7%8E%B0%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5.md)

### Kafka与ZK的关系

- 利用ZK的有序节点，临时节点，和监控机制

	- 配置中心

		- Broker
		- Topic
		- Partition
		- Consumer
		- 元数据的变动

	- 负载均衡
	- 命名服务
	- 分布式通知
	- 集群管理
	- 分布式锁

### 界面管理工具

- [kafka-manager](https://github.com/yahoo/kafka-manager/releases)

	- 需要JDK11的支持

- [kafka-eagle(国产)](https://github.com/smartloli/kafka-eagle)

	- 对内存的要求比较高

### 架构分析

- Broker

	- Kafka服务叫做Broker，默认9092端口，生产者与消费者都需要跟这个Broker建立连接才可以实现消息的收发

- 消息

	- 客户端之间传输的数据叫做消息，或者叫做记录(Record)
	- 生产者对应的封装类是ProducerRecord，消费者对应的封装类是 ConsumerRecord。
	- 消息在传输的过程中需要序列化，所以代码里面要指定序列化工具
	- [消息在服务端的存储格式（RecordBatch 和 Record）](http://kafka.apache.org/documentation/#messageformat)

- 生产者

	- 发送消息的一方叫做生产者
	- 为了提高效率生产者不是逐条发送到Broker，而是批量发送，是定参数（batch.size）

- 消费者

	- 消费消息的一方叫消费者
	- 获取模式

		- pull

			- 消费者自己决定什么时候获取

				- 根据参数[max.poll.records]来控制消费的消息条数，默认是500

		- push

			- 消息一到达Broker就直接推送给消费者

				- [Kafka不支持该模式](http://http:/kafka.apache.org/documentation/#design_pul)

				- 原因是push模式下，消息生产速度远远大于消费的速度，消费者就会不堪重负。直接挂掉

- Topic

	- 关联生产者和消费者的队列叫做Topic，是一个逻辑概念，可以理解成一组消息的集合（不同业务通途的消息）
	- 生产者与Topic以及Topic和消费者的关系都是多对多，一个消费者从多个Topic获取消息（不推荐这么使用）
	- 发送消息是，Topic不存在，会自动创建，有参数（auto.create.topics.enable）控制，默认为true，如果要彻底删除一个Topic，这个参数必须设定成false，不然只要有使用这个Topic消息就会自动创建

- Partition与Cluster

	- 给每个Topic分区成多个Partition

		- 作用

			- 方便Topic的横向扩展，可以将Topic拆分到不同的集群节点上
			- 提高并发量和负载问题，防止一个Topic的场景下性能下降

		- 命令

			- ./kafka-topics.sh --create--zookeeper localhost:2181-replication-factor I--partitions 1--topic gptest
			- 如果没有指定分区数默认为1，参数为(num.partitions=1)

		- 物理目录

			- /tpm/kafka-logs/

				- mytopic-0
				- mytopic-1

		- Partition里面的消息读取之后不会被删除，用一批消息再一个Partition里面顺序，追加写入的，这也是kafka吞吐量大的重要原因
		- 分区个数最好通过性能测试脚本验证

- Partition副本Replica机制

	- 每个partition可以有若干个副本(Replica),副本必须在不同的Broker上，
	- partitions是分区数, replication-factor是Topic的副本数

		- ./kafka-topics.sh --create--zookeeper localhost:2181-replication-factor I--partitions 1--topic gptest

	- 服务端有个参数控制默认副本数

		- offsets.topic.replication.factor

	- 副本有leader,follower之分，生产者和消费者都是针对leader进行读写，避免消息延时同步，发送数据不一致情况

- Segment
- Consumer Group
- Consumer Offset

### Java 开发

### 进阶功能

- 消息幂等性
- 生产者事物

## 原理分析

*XMind - Evaluation Version*
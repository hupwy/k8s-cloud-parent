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

- CentOSKafka 单机集群安装（伪集群）

- kafka 常用命令

- 基于Canal 和Kafka 实现数据同步

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

- kafka-manager

	- 需要JDK11的支持

- kafka-eagle(国产)

	- 对内存的要求比较高

### 架构分析

- Broker

	- Kafka服务叫做Broker，默认9092端口，生产者与消费者都需要跟这个Broker建立连接才可以实现消息的收发

- 消息

	- 客户端之间传输的数据叫做消息，或者叫做记录(Record)
	- 生产者对应的封装类是ProducerRecord，消费者对应的封装类是 ConsumerRecord。
	- 消息在传输的过程中需要序列化，所以代码里面要指定序列化工具
	- 消息在服务端的存储格式（RecordBatch 和 Record）

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

				- Kafka不支持该模式

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

	- 原因

		- 将kafka数据【XXX.log】分段（Segment）存储，提高检索数据效率

	- 默认路径

		- /tmp/kafka-logs/

	- 文件结构

		- 数据文件

			- XXX.log

		- 索引文件

			- XXX.index

		- 时间索引文件

			- XXX.timeindex

	- 默认大小

		- 1073741824 bytes(1G)

	- 设置参数

		- log.segment.bytes

- Consumer Group

	- 同一个消费者组才能消费相同消息
	- 同一个group中的消费者，不能消费相同的partition
	- 例子

		- 消费者比partition少，一个消费者可以消费多个partition
		- 消费者比partition多，肯定有消费者没有partition可以消费
		- 如果想要同时消费同一个partition的消息，需要其他group来消费

- Consumer Offset

	- 消息是顺序写入的，别读取后不会被删除，所以把每一个消息进行编号，用来作为消息的唯一表示，这个标号就是offset
	- 避免重复消费和漏消费
	- 消费者和partition之间的偏移量保存在服务端，没有保存在ZK上

### Java 开发

- Producer API

	- 用于将应用数据发送到kafka的topic

- Consumer API

	- 用于应用从Kafka的topic中读取消息

- Admin API

	- 允许管理和检测Topic，broker以及其他kafka实例，与kafka自带的脚本命令作用类似

- Streams API

	- 用于从来源topic转化到摸底topic转换数据流，作用跟Spark，Storm，Flink一样

- Connect API

	- 用于持续地从一些源系统输入数据到kafka，或者从kafka推送数据到一些系统，比如数据库或者hadoop等等

### Spring Boot集成

- 版本对应关系

### 进阶功能

- 消息幂等性

	- 参数

		- enable.idempotence=true

	- 机制

		- PID

			- 生产者每个客户端都有一个唯一的编码

		- sequence number

			- 生产者发送每一个消息时都会带效应的序列码，Server端就是根据这个值来判断数据是否重复，如果发送序列码比服务器端记录的值小，就肯定会出现重复消费

	- 作用范围

		- 只能保证单分区上的幂等性，一个幂等性的Producer能够保证某个主题的一个分区上不出现重复消费
		- 这能实现但回话的幂等性，也就是Producer进程的一侧运行，当重启Producer进程后幂等性不保证

- 生产者事物

	- 资料连接

		- http://kafka.apache.org/documentation/#semantics
		- https://www.confluent.io/blog/transactions-apache-kafka/
		- https://cwiki.apache.org/confluence/display/KAFKA/Transactional+Messaging+in+Kafka
		- https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging

	- 保证跨生产者回话的消息幂等性
	- 使用场景

		- 1个broker，1个topic的分区只有一个副本，多条消息保证全部成功或失败
		- 生产者发送消息到多个topic或者多个partition，它们可能分布在不同的服务器上，全部消息发送成功或失败
		- 消费者和生产者在同一块代码中（consume-process-produce），从上游接收消息，经过处理后发送给下游，保证接收消息和发送消息同事成功

	- 方法

		- initTransactions

			- 初始化事务

		- beginTransaction

			- 开始事务

		- commitTransaction

			- 提交事务

		- abortTransaction

			- 终止事务

		- sendOffsetToTransaction

			- 消费者和生产者在同一段代码使用，在提交是吧消费者的offset发给consumer Corordinator

	- Spring Boot

		- executeInTransaction
		- @Transaction

	- 分布式事务

		- 分析

			- 生产者的消息可能跨分区，所有采取的两阶段提交
			- 协调者位 Transaction Coordinator
			- 必须写入事务日志来记录事务状态，Coordinator以外挂掉之后可以继续处理原来的事务，状态标记为topic_transaction_state
			- 生产者挂了，事务要在重启后可以继续处理，这样必须要有一个唯一的ID(transaction.id),则此时enable.idempotence会被设置位true（事务的幂等性），事务ID相同的生产者可以接着处理原来的事务

		- 步骤

			- 1.生产者通过initTransactionsAPI向Coordinator注册事务ID
			- 2.Coordinator记录事务日志
			- 3.生产者吧消息写入目标分区
			- 4.分区和Coordinator的交互，当事务完成以后，消息状态应该是已提交，这样消费者才可以消费

### 特点

- 高吞吐，低延迟

	- 每秒可以处理几十万条消息，最低延迟只有几毫秒

- 高伸缩性

	- 可以通过增加分区partition来实现扩容，不同的分区可以在不同的broker中，通过zk来管理broker实现扩展，zk管理Consumer可以实现负载

- 持久性，可靠性

	- 容许数据的持久化存储，消息被持久化到磁盘，并支持数据备份防止数据丢失

- 容错性

	- 容许集群中的节点失败，某个节点宕机，集群能够正常工作

- 高并发

	- 支持数千个客户端同事读写

## 原理分析

### 生产者原理

- 生产者消息发送流程

	- mian线程

		- KafkaProducer

			- 创建生产者客户端

		- 拦截器(ProducerIntercept)

			- 实现消息的定制化（类似Spring interceptor，MyBatis的插件，Quartz的监听器）
			- 实现ProducerInterceptor接口，添加到ProducerConfig.INTERCEPTOR_CLASSES_CONFIG属性里

				- List<String> interceptors = new ArrayList<();
interceptors.add("com.qingshan.interceptor.ChargingInterceptor"));
props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG interceptors;

			- 可以指定多个拦截器，形成拦截器链

		- 序列化(Serializer)

			- 利用指定工具对key和value进行序列化

				- 自带序列化工具之外还可以用

					- Avro
					- Json
					- Thrift
					- Protobuf
					- 自定义，需实现Serializer接口

				- 自带序列化工具

					- ByteArraySerializer
					- ByteBufferSerializer·
					- BytesSerializer·
					- FloatSerializer·
					- IntegerSerializer
					- LongSerializer
					- ShortSerializer
					- StringSerializer·
					- UUIDSerializer

		- 分区器(Pratitioner)

			- 指定partition
			- 没有指定partition，自定义分区器
			- 没有指定partition，没有自定义分区器，但是key不为空
			- 没有指定partition，没有自定义分区器，但是key是空的

		- 消息累加器

	- sender线程

		- 在创建KafkaProducer的时候，创建一个sender线程，并且启动了一个IO线程

- 数据可靠性保证ACK

### 存储原理

### 消费者原理

### 为什么这么快

*XMind - Trial Version*
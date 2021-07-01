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

				- 直接将指定的值作为partition值

			- 没有指定partition，自定义分区器

				- 自定义分区器算法选择分区

					- props.put("partitioner.class","com.qingshan.partition.SimplePartitioner")

			- 没有指定partition，没有自定义分区器，但是key不为空

				- 使用默认分区器DefaultPartitioner将key的hash值与topic的partition数进行取余得到partition值

					- Utils.toPositive(Utils.murmur2(keyBytes))% numPartitions;

			- 没有指定partition，没有自定义分区器，但是key是空的

				- 第一次调用时随机生成一个整数（后面每次调用在这个整数上自增），将这个值与topic可用的partition总数取余得到partition值，也就是常说的round-robin算法

					- Integer random= Utils.toPositive(ThreadLocalRandom.current().nextIntO));
newPart=availablePartitions.get(random %availablePartitions.size()).partition)

		- 消息累加器

			- RecordAccumulator本质上是一个ConcurrentMap

				- ConcurrentMap<TopicPartition.Deque<ProducerBatch>>batches

			- 一个partition一个Batch，Batch满了之后会唤醒sender线程发送消息

				- if (result.batchIsFull | result.newBatchCreated) {
log.trace("Waking up the sender since topic {partition {is either full or getting a new batch". record.topic(), partition); 
this.sender. wakeup();

	- sender线程

		- 在创建KafkaProducer的时候，创建一个sender线程，并且启动了一个IO线程,用于发送消息

	- 小结

		- 拦截器里面自定义消息处理逻辑，也可以选择自己喜欢的序列化工具，还可以自由选择分区

- 数据可靠性保证ACK

	- 服务端效应策略

		- 1.需要半数以上的follower节点完成同步，发送ACK给客户端，等待时间会短一些，延迟低
		- 2.所有的follower节点全部完成同步，发送ACK给客户端，延迟高，但是节点挂掉的影响小一些，因为所有节点数据都是完整的
		- Kafka选择了第2中策略，因为可靠性更高，数据不会丢失，网络延迟对kafka的影响不大

	- ISR

		- 定义

			- 正常和leader保持同步的replica维护起来，叫做in-sync replica set （ISR）队列
			- 不能正常和leader保持同步的replica踢出ISR队列
			- 不正常同步的标准是大于阈值replicalag.time.max.ms决定（默认值30秒）这个时间不能与leader正常通信
			- 如果恢复正常通信，则可以再次加入ISR队列
			- 如果leader挂了，从ISR里重新选举leader，一般默认是首个节点

		- 作用

			- leader收到数据，有一个follower出现网络问题，不能同步leader数据，这样leader不能长时间等待不发送ACK，所以这样ISR队列中的follower同步完成就可以发送ACK

	- ACK应答机制

		- 参数（acks）

			- 0：不用等待broker的ack，broker一接收到还没有写入磁盘就已经返回，broker故障有事可能会丢失数据，但是延迟最低
			- 1：等待broker的ack，leader落盘成功后返回ack，follower同步成功之前leader故障，会丢失数据
			- -1：等待broker的ack，leader和follower全部落盘成功后才返回ack

				- follower同步完成后，发送ack之前，leader发送故障，没有发送ack，会造成数据重复，这样需要把reties设置成0（不重发），才不会重复发送消息

		- 三种机制性能依次递减，数据的健壮性依次增强
		- 类比Mysql的Binlog组从复制，同步，异步，半同步

### 存储原理

- 文件存储结构

	- 配置文件：config/erver.properties
	- logs.dir配置

		- 默认/tmp/kafka-logs

	- partition分区

		- 把topic中的数据分割成多个partition，存放到不同的broker上，减低单台服务器的访问压力
		- 一个partition中消息是有序的，但不一个全局有序
		- 服务器上每个partition都有一个物理目录，topic名字后面的数字标号代码分区

			- 例如：topic-0，topic-1，topic-2

	- replica副本

		- 为了提高可靠性，但是副本只从leader异步拉取数据，做数据备份
		- 创建topic命令里，replication-factor确定topic的副本数，副本数必须小于等于节点数，否则报错，这样就可以保证，一个分区不会出现两个副本
		- 命令：./kafka-topics.sh --create--zookeeper localhost:2181 --replication-factor 4 --partitions 2 --topic overrep
		- 只要leader副本对外提供读写服务，这样就不存在数据同步的一致性问题，被叫做单调读一致性

	- 副本在Broker的分布

		- 查看副本中谁是leader

			- ./kafka-topics.sh --topic test --describe--zookeeperlocalhost:2181

		- 副本的编号从1开始，第一个副本位leader
		- 分布规则

			- firt fo all，副本因子不能大于broker的个数
			- 第一个分区（编号为0的分区）第一个副本防止位置随机从borkerlist选择
			- 其他分区的第一个副本放置位置相对于第0个分区依次往后移动，这样可以把每个分区的第一个副本放到不公的broker上
			- 每个分区剩余的副本相对于第1个副本放置位置其实由nextReplicaShift决定的，这个数也是随机产生的

		- 优点

			- 提高容灾能力
			- 每个分区的第一个副本错开之后，一般第一个分区的第一个副本都是leader，leader错开，不至于一个broker挂了影响太大

		- 命令

			- bin目录下的kafka-reassign-partitions.sh可以根据Broker数量变化情况重新分配分区

	- segment

		- 为了防止log不断追加导致文件过大，检索消息效率变低，一个partition又被划分成多个segment来组织数据
		- 组成文件

			- 一个log文件和2个index文件，这三个文件是成套出现的
			- leader-epoch-checkpoint中保存了每一任leader开始些人消息时的offset

		- .log 日志文件

			- 日志就是数据，日志追加写入，满足一定条件就会切分日志文件，产生一个新的segment
			- 控制切分segment的参数

				- 文件大小 log.segment.bytes（默认1073741824 bytes 1G）
				- 最大时间戳 

					- log.roll.hours=168（小时，一周）
					- log.roll.ms（毫秒），优先级比小时（hours）的高

				- 索引文件或timestamp索引文件达到一定大小，默认是10485760字节（10M）,log.index.size.max.bytes

	- 索引(稀疏索引)

		- 偏移量索引

			- 记录offset和物理地址映射关系
			- 特点

				- 系数索引，不是每一条都记录，是隔几天才产生一条索引，这个间隔由参数设定控制

					- log.index.intervalbytes=4096（4KB）

				- 只要超过4KB，偏移量索引文件.index和时间戳索引文件.timeindex就会增加一条记录
				- 这个值越小，索引越密集，越大，索引越稀疏
				- 越稠密检索数据越快，消耗的存储空间越多，越稀疏索引占用存储空间越小，单插入和删除时所需维护的开销越小
				- 时间复杂度位O(log2n)+O(m),  n是索引文件里索引的个数，m为稀疏程度

		- 时间戳索引

			- 记录时间戳和offset的关系
			- 背景

				- 要基于时间切分日志文件，必须要记录时间戳
				- 要基于时间清理消息，必须要记录时间戳

			- 类型

				- 创建的时间戳

					- log.message.timestamp.type=CreateTime（默认）

				- broker上追加写入的时间

					- log.message.timestamp.type=LogAppendTime

		- 查看工具

			- ./kafka-dump-log.sh --files /tmp/kafka-logs/mytopic-0/00000000000000000000.index|head-n 10

		- 检索步骤

			- 1.消费的时候可以确定分区，这样找到了在那个segment中，segment文件用base offset命名，所以用二分法很抠确定
			- 2.这个segment有对应的所有文件，他们成套出现，所以现在要在索引文件中根据offset找position
			- 3.得到position之后，到对应的log文件开始查找offset，和消息的offset进行比较，知道找到消息

	- 消息保留机制

		- 开关

			- log.cleaner.enable=true（默认位true）
			- log.cleanup.policy

				- delete 直接删除（默认值）
				- compact 压缩

		- 刪除策略

			- log.retention.check.interval.ms=300000（5分钟），每个5分钟删除一次
			- 删除周期

				- log.retention.hours=168（默认一周）
				- log.retention.minutes（默认值空），优先级比小时高
				- log.retention.ms（默认值空），优先级比分钟高

			- 删除大小

				- log.retention.bytes（默认值-1），代表不限制大小
				- log.segment.bytes（1073741824字节）1G

		- 压缩策略

			- 把相同key合并为最后一个value
			- log compaction执行后的偏移量不再是连续的，不过不影响日志查询

	- 高可用架构

		- Controller选举

			- 选举是有其中一个broker统一指挥，这个broker的角色就叫做Controller（控制器）。
			- 因为利用zk帮助选举Controller，Controller节点宕机后，broker通过watch监听到下线通知，开始竞选Controller
			- Controller节点的任务

				- 监听Broker变化
				- 监听Topic变化
				- 监听Partition变化
				- 获取和管理Broker，Topic，Partition的信息
				- 管理Partition的主从信息

		- 分区副本leader选举

			- 资料网站

				- https://kafka.apache.org/documentation/#replication
				- https://kafka.apache.org/documentation/#design_replicatedlog

			- 队列

				- 一个人去的所有副本叫Assigned-Replicas（AR）
				- 所有副本中跟leader数据保持一定程度同步的叫做In-Sync Replicas（ISR）
				- 跟leader同步滞后过多的副本叫做Out-Sysnc-Replicas（OSR）
				- AR=ISR+OSR

			- 默认情况下leader副本发生故障是，只有ISR集合中的副本才有资格被选举位新leader
			- 如果ISR为空，只能从OSR队列中选举，需要设置unclean.leader.election,默认位false，一般不建议开启，会造成数据丢失
			- 选举算法

				- Zab（ZK）
				- Raft（Redis Sentinel）
				- PacificA（kafka）

					- 默认让ISR第一个repica编程leader

		- 主从同步

			- LEO（log end offset）

				- 下一条等待写入的消息的offset（最新的offset+1）
				- 命令：./kafka-consumer-groups.sh--bootstrap-server 192.168.44.160:9092--describe--group gp-test-group

			- HW（hign watermark）

				- ISR中最小的LEO，leader会管理所有的ISR中最小的LEO作为HW

			- consumer最多只能消费到HW之前的位置，因为同步成功之前，consumer group的offset会偏大，如果leader崩溃，中间会缺失消息
			- 步骤

				- 1.follower节点会向leader发送一个fetch请求，leader向follower发送数据后，需要更新follower的LEO
				- 2.follower接收到数据响应后，依次写入消息并更新LEO
				- 3.leader更新HW（ISR最小的LEO）

			- kafka设计了独特的ISR复制，可以在保障数据一致性情况下又可提高高吞吐量

		- replica故障处理

			- follower故障处理

				- follower发生故障，会被踢出ISR
				- follower恢复后，首先根据之前的记录HW，把高于现在HW的消息截掉，向leader同步消息，追上leader后，重新加入ISR

			- leader故障处理

				- 在ISR中选取leader节点，默认第一个
				- 其他follower需要把高于HW的消息截取掉
				- 依照follower故障处理的步骤依次进行消息同步

			- 缺点：只能保证副本建数据一致性，并不能保证数据不丢失或不重复

### 消费者原理

- Offset维护

	- Offset存储（__consumer_offsets）

		- 特殊的topic,默认值有50个分区,需要自定义可以修改参数offsets.tipic.unm.partitions，每个分区默认一个replication
		- 存储对象

			- GroupMetadata

				- 保存消费者组中各个消费者的信息（每个消费者的编号）

			- OffsetAndMetadata

				- 保存消费者组和各个partition的offset位移信息元数据

			- 命令

				- ./kafka-console-consumer.sh --topic consumer offsets--bootstrap-server 192.168.44.161:9093,192.168.44.161:9094,192.168.44.161:9095--formatter
"kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter"--from-beginning

	- 消费的offset

		- 这种情况可以改变参数[auto.offset.reset=[latest(默认), earliest, none]]

			- latest

				- 从最新（最后）消息开始消费

			- earliest

				- 从最早消息开始消费

			- none

				- 报错

	- Offset更新

		- enable.auto.commit 设置自动提交

			- 默认是true，表示消费者消费消息后自动提交此时Broker更新消费者组的offset

		- auto.commit.interval.ms 设置提交频率

			- 默认5秒

		- 如果消费完做业务逻辑处理后才commit，就要把enable.auto.commit改为false，必须调用方法才能更新offset

			- consumer.commitSync()

				- 手动同步提交

			- consumer.commitAsync()

				- 手动异步提交

- 消费者消费策略

	- 消费策略

		- RangeAssignor(范围)-（默认策略）
		- RoundRobinAssignor(轮训)
		- StickyAssignor(粘滞)

			- 分区的分配尽可能均匀
			- 分区的分配尽可能和上次分配保持相同

	- 通过partition.assignment.strategy 修改消费策略

		- props.put("partition.assignment.strategy","org.apache.kafka.clients.consumer.RoundRobinAssignor")

	- 消费方法

		- assign

			- 手动指定分区消费，相当于consumer group id失效了

				- TopicPartition tp=new TopicPartition("ass5part",0)
consumer.assign(Arrays.asList(tp));

		- subscribe

			- 自动分配消费者组的分区

- rebalance

	- 触发条件

		- 消费者组的消费者数量变化
		- Topic的分区数变化

	- 步骤

		- 1.从每一个broker选举出一个GroupCoordinator
		- 2.消费者连接到GroupCoordinator，这个过程叫join group请求
		- 3.GroupCoordinator在所有的消费者里选举出一个leader，由这个leader根据情况设置策略，确定方案，上报给GroupCoordinator，由GroupCoordinator通知所有消费者

### 为什么这么快

- 磁盘顺序I/O

	- 读写数据在磁盘上是集中的，不需要重复寻址的过程，kafka的消息是不断追加到本地磁盘文件的末尾，而不是随机写入，显著提高写入吞吐量

- 索引机制

	- 索引(稀疏索引)

- 批量操作

	- 把所有消息都变成一个批量的文件，并进行合理的批量压缩，减少网络IO损耗

- 零拷贝

	- 虚拟内存

		- 内核空间

			- 可以执行任意命令调用系统的一切资源

		- 用户空间

			- 需要调用系统接口向内核发出指令

	- 读取数据

		- kafka消费，从磁盘读取数据，必须先把数据从磁盘拷贝到内核缓冲区，然后从内核缓冲区到用户缓冲区，最后才能返回给用户
		- DMA拷贝，直接内存访问，在I/O设备和内存数据传输时，数据的拷贝工作交给DMA控制器，解放CPU资源

	- kafka数据拷贝过程

		- 1.通过DMA把磁盘数据拷贝到内核缓冲区
		- 2.通过CPU拷贝到用户缓冲区
		- 3.通过CPU拷贝到socket缓冲区
		- 4.通过DMA拷贝到网卡设备

	- kafka只有DMA拷贝把磁盘文件从内核缓冲区拷贝到网卡设备，没有CPU拷贝叫做零拷贝，linux里有个“sendFile”函数可以实现零拷贝，性能至少提高一倍

### 消息不丢失的配置

- producer端是有带有回调的send方法，如果回调消息失败，进行业务处理

	- producer.send(msg, calback)

- 设置acks=all 表明所有broker都接收到消息，该消息才算“已提交”
- 设置retries为一个较大的值
- 设置unclean.leader.election.enable= false
- 设置 replication.factor>=3。需要三个以上的副本。

*XMind - Trial Version*
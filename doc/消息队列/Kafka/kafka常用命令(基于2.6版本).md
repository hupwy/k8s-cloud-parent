## 一、脚本概览

bin目录下的脚本作用

| 脚本                                | 作用                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| connect-distributed.sh              | 用于启动多节点的Distributed模式的Kafka Connect组件           |
| connect-standalone.sh               | 用于启动单节点的Standalone模式的Kafka Connect组件            |
| kafka-acls.sh                       | 用于设置Kafka权限，比如设置哪些用户可以访问Kafka的哪些TOPIC的权限 |
| kafka-broker-api-versions.sh        | 主要用于验证不同Kafka版本之间服务器和客户端的适配性          |
| kafka-configs.sh                    | 配置管理脚本                                                 |
| kafka-console-consumer.sh           | kafka消费者控制台                                            |
| kafka-console-producer.sh           | kafka生产者控制台                                            |
| kafka-consumer-groups.sh            | kafka消费者组相关信息                                        |
| kafka-consumer-perf-test.sh         | kafka消费者性能测试脚本                                      |
| kafka-delegation-tokens.sh          | 用于管理Delegation Token。基于Delegation Token的认证是一种轻量级的认证机制，是对SASL认证机制的补充。 |
| kafka-delete-records.sh             | 用于删除Kafka的分区消息                                      |
| kafka-dump-log.sh                   | 用于查看Kafka消息文件的内容                                  |
| kafka-log-dirs.sh                   | 用于查询各个Broker上的各个日志路径的磁盘占用情况             |
| kafka-mirror-maker.sh               | 用于在Kafka集群间实现数据镜像                                |
| kafka-preferred-replica-election.sh | 用于执行Preferred Leader选举，可以为指定的主题执行更换Leader的操作 |
| kafka-producer-perf-test.sh         | kafka生产者性能测试脚本                                      |
| kafka-reassign-partitions.sh        | 用于执行分区副本迁移以及副本文件路径迁移。                   |
| kafka-replica-verification.sh       | 复制进度验证脚本                                             |
| kafka-run-class.sh                  | 用于执行任何带main方法的Kafka类                              |
| kafka-server-start.sh               | 启动kafka服务                                                |
| kafka-server-stop.sh                | 停止kafka服务                                                |
| kafka-streams-application-reset.sh  | 用于给Kafka Streams应用程序重设位移，以便重新消费数据        |
| kafka-topics.sh                     | topic管理脚本                                                |
| kafka-verifiable-consumer.sh        | 可检验的kafka消费者                                          |
| kafka-verifiable-producer.sh        | 可检验的kafka生产者                                          |
| trogdor.sh                          | Kafka的测试框架，用于执行各种基准测试和负载测试              |
| zookeeper-server-start.sh           | 启动zk服务                                                   |
| zookeeper-server-stop.sh            | 停止zk服务                                                   |
| zookeeper-shell.sh                  | zk客户端                                                     |

## 二、Broker服务

启动服务

```shell
./kafka-server-start.sh ../config/server.properties
```

后台启动

```shell
./kafka-server-start.sh -daemon ../config/server.properties
```

停止服务

```shell
./kafka-server-stop.sh ../config/server.properties
```

## 三、元数据

创建topic

```shell
./kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic my-test-topic
```

查看所有topic

```shell
./kafka-topics.sh --bootstrap-server localhost:9092 --list
```

从Kafka 2.2版本开始，Kafka社区推荐用–bootstrap-server参数替换–zookeeper参数用于指定Kafka Broker。集群的多个IP端口用逗号`,`隔开

查看topic详细信息

```shell
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic topic_name
```

给topic增加分区（只能增加不能减少）

```shell
./kafka-topics.sh --bootstrap-server localhost:9092 --alter --topic my-test-topic --partitions 10
```

删除topic（标记删除）

```shell
./kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic my-test-topic
```

永久删除需要修改配置文件：delete.topic.enable=true

强制删除TOPIC的方法：
A、手动删除ZooKeeper节点/admin/delete_topics下以待删除TOPIC为名的znode。
B、手动删除TOPIC在磁盘上的分区目录。
C、在ZooKeeper中执行rmr /controller，触发Controller重选举，刷新Controller缓存。可能会造成大面积的分区Leader重选举。可以不执行，只是Controller缓存中没有清空待删除TOPIC，不影响使用。

修改partition副本数：
先配置一个reassign.json文件，内容：
例如my-test-topic有3个分区，原来只有一个副本，增加到2个副本

```shell
{"version":1, "partitions":[
 {"topic":"my-test-topic","partition":0,"replicas":[0,1]}, 
  {"topic":"my-test-topic","partition":1,"replicas":[1,2]},
  {"topic":"my-test-topic","partition":2,"replicas":[2,0]}
]}
```

执行kafka-reassign-partitions脚本：

```shell
./kafka-reassign-partitions.sh --zookeeper localhost:2181 --reassignment-json-file reassign.json --execute
```

## 四、生产者

发送消息

```shell
./kafka-console-producer.sh --broker-list localhost:9092 --topic my-test-topic
```

## 五、消费者

消费消息

```shell
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-test-topic 
```

查看消费者组提交的位移数据:

```shell
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic __consumer_offsets --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --from-beginning
```
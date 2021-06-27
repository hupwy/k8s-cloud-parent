> 以安装路径/usr/local/soft，IP 192.168.100.1为例
> 实际上就是在同一台机器上，运行多个kafka服务，只是端口不同。
>
>  注意：单机的kafka和集群的kafka不要混用一个zk，否则会出现数据混乱的问题。

## 1、下载解压kafka

```shell
cd /usr/local/soft
wget https://mirror.bit.edu.cn/apache/kafka/2.5.0/kafka_2.13-2.5.0.tgz
tar -xzvf kafka_2.13-2.5.0.tgz
cd kafka_2.13-2.5.0
```

## 2、修改配置文件

复制3个配置文件

```shell
cd config
cp server.properties server1.properties 
cp server.properties server2.properties 
cp server.properties server3.properties 
```

修改配置文件中的broker.id分别为1、2、3
listeners这一行取消注释，端口号分别为9093、9094、9095
log.dirs分别设置为kafka-logs1、kafka-logs2、kafka-logs3（先创建）

```shell
mkdir -p /tmp/kafka-logs1 /tmp/kafka-logs2 /tmp/kafka-logs3
```

server1.properties 的配置：

```shell
broker.id=1
listeners=PLAINTEXT://192.168.100.1:9093
log.dirs=/tmp/kafka-logs1
```

server2.properties 的配置：

```shell
broker.id=2
listeners=PLAINTEXT://192.168.100.1:9094
log.dirs=/tmp/kafka-logs2
```

server3.properties 的配置：

```shell
broker.id=3
listeners=PLAINTEXT://192.168.100.1:9095
log.dirs=/tmp/kafka-logs3
```

## 3、启动3个服务

第一步：启动ZK。

再启动kafka。

```shell
cd ../bin
./kafka-server-start.sh -daemon ../config/server1.properties
./kafka-server-start.sh -daemon ../config/server2.properties
./kafka-server-start.sh -daemon ../config/server3.properties
```

> PS：如果遇到zk node exists的问题，先把brokers节点删掉（临时解决方案）。

## 4、集群下创建Topic

在bin目录下，
创建一个名为gptest的topic，只有一个副本，一个分区：

```shell
sh kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic gptest
```

查看已经创建的 topic：

```shell
sh kafka-topics.sh -list -zookeeper localhost:2181
```

## 5、集群下启动Consumer

在一个新的远程窗口中：

```shell
sh kafka-console-consumer.sh --bootstrap-server 192.168.100.1:9093,192.168.100.1:9094,192.168.100.1:9095 --topic gptest --from-beginning
```

## 6、集群下启动Producer

打开一个新的窗口，在kafka解压目录下：

```shell
sh kafka-console-producer.sh --broker-list 192.168.100.1:9093,192.168.100.1:9094,192.168.100.1:9095 --topic gptest
```

## 7、集群下Producer窗口发送消息

在生产者窗口输入hello world 回车
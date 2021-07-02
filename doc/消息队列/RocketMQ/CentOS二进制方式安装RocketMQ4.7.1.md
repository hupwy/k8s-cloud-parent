# CentOS二进制方式安装RocketMQ4.7.1

## 一、下载

路径：/usr/local/soft

官网
http://rocketmq.apache.org/

```shell
cd /usr/local/soft
wget https://mirror.bit.edu.cn/apache/rocketmq/4.7.1/rocketmq-all-4.7.1-bin-release.zip
```

## 二、解压

解压二进制包，改个名字

```shell
unzip rocketmq-all-4.7.1-bin-release.zip
mv rocketmq-all-4.7.1-bin-release rocketmq
```

创建数据存储目录

```shell
mkdir -p /usr/local/soft/rocketmq/store/broker-a /usr/local/soft/rocketmq/store/broker-a/consumequeue /usr/local/soft/rocketmq/store/broker-a/commitlog /usr/local/soft/rocketmq/store/broker-a/index 
/usr/local/soft/rocketmq/broker-a/logs 
```

## 三、修改配置文件

```shell
cd /usr/local/soft/rocketmq/conf
vim broker.conf
```

增加内容

```properties
#Broker 对外服务的监听端口
listenPort=10911
#是否允许 Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
#是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
#nameServer地址，分号分割
namesrvAddr=localhost:9876
#存储路径
storePathRootDir=/usr/local/soft/rocketmq/store/broker-a
#commitLog 存储路径
storePathCommitLog=/usr/local/soft/rocketmq/store/broker-a/commitlog
#消费队列存储路径存储路径
storePathConsumeQueue=/usr/local/soft/rocketmq/store/broker-a/consumequeue
#消息索引存储路径
storePathIndex=/usr/local/soft/rocketmq/store/broker-a/index
#checkpoint 文件存储路径
storeCheckpoint=/usr/local/soft/rocketmq/store/broker-a/checkpoint
#abort 文件存储路径
abortFile=/usr/local/soft/rocketmq/store/broker-a/abort
```

> 在虚拟机中有可能因为内存不够而启动失败
> 修改bin目录下的runbroker.sh 和 runserver.sh 文件
> 比如把8g 4g 改成512m， 4g 2g改成256m

```shell
cd /usr/local/soft/rocketmq/bin
```

## 四、启动

> 依次启动nameserver和broker
> 这两个命令可以做成alias

```
nohup sh mqnamesrv &
nohup sh mqbroker -c /usr/local/soft/rocketmq/broker.conf &
```

## 五、查看日志

> 启动成功后查看mq动态日志：

```shell
tail -f ~/logs/rocketmqlogs/namesrv.log
tail -f ~/logs/rocketmqlogs/broker.log
```

## 六、关闭服务

```shell
cd /usr/local/soft/rocketmq/bin

sh mqshutdown namesrv
sh mqshutdown broker
```

[RocketMQ常用管理命令](https://gper.club/articles/7e7e7f7ff3g58gc3g69)
# 分布式

## 为什么会出现分布式

### 集中式Centralized

- 有一个中心化的节点，可能是一台或多台机器组成的，所有的数据存储，计算都在主机上完成
- 单节点无法满足行灯需求，配置升级（Scale Up）收效太低
- 分布式Distributed

	- 分布式系统由一组通过网络进行通信。为了完成共同的任务而协调工作的计算机节点组成的系统。Scale Out

### 特点

- 集中式

	- 架构简单
	- 可靠性高
	- 强一致性
	- 可维护性好

- 分布式

	- 解决了性能，可用性，可扩展性问题
	- 架构设计复杂
	- 运维复杂

## 出发点

### 高性能（High performance）

- 高吞吐（QPS衡量）
- 低延迟
- 高并发

### 高可用（High available）

- 高成功率（正常服务时间占比：请求成功次数比列）

### 高扩展（scalability）

## 依赖技术

### 存储

- 需要解决节点故障：进程Crash，断电，磁盘损坏等问题

### 通信

- 需要解决网络故障：断网，延迟，丢包，乱序

### 计算

- 一个业务流程，多个系统（服务）来执行
- 多个任务，多个节点执行

## 解决思路

### 分片（Parttition）

### 冗余（Replication）副本机制

## 副本一致性问题（consistency）

### CAP理论

### BASE理论

### ZAB协议

### Paxos算法

### Raft算法

## 分布式技术

### 服务协调

- Zookeeper

### 异步消息通信

- RabbitMQ
- Kafka
- RocketMQ

### NoSQL存储

- Redis
- MongoDB

### 任务调度

- Elastic Job
- xxl-job

### 数据存储

- Mycat
- Sharding-JDBC

### 负载均衡

- Nginx
- HAProxy+KeepAlived
- Lvs

### 文件系统

- FastDFS
- HDFS

### 日志系统

- ELK
- EFK

### 通信

- Netty

### RPC

- Dubbo
- gRpc

### 容器

- docker
- k8s

*XMind - Trial Version*
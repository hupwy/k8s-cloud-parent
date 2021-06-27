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

## 原理分析

*XMind - Evaluation Version*
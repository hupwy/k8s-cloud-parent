# RabbitMQ

## MQ入门

### 为什么用MQ

- 异步

	- 手机跨行转账

- 解耦

	- 一个功能需要多个服务处理，例如在线购票，由订单系统下单，配合库存系统，支付系统，通知系统分别进行扣减和通知处理才能完成这次完整业务，可以使用MQ将各个服务解耦（涉及分布式事务）

- 削峰

	- 防止服务瞬间流量峰值飙高，导致后端服务器崩溃，使用MQ将请求排队，保证服务器正常运行

- 广播

	- 一对多广播通信（不涉及分布式事务）

### 使用MQ带来的问题

- 复杂性提高，开发人员有多学习MQ知识并且应用
- 可用性降低，考虑到可靠性和延迟性，一旦网络出现问题会导致请求失败，严重影响业务
- 运维成本增加，多维护MQ服务

## 介绍

### 工作模型

- 核心对象

	- Broker

		- RabbitMQ服务器就叫做Broker

	- Conncetion

		- 生产者发送消息，消费者接收消息，需要跟Broker之间创建一个TCP的长连接

	- Channel

		- 多路复用，减少资源消耗
		- 虚拟连接，可以在保持TCP长连接里面去创建和释放Channel
		- 不同的Channel是相互隔离的，对于每个客户端线程来说Channel不共享
		- 定义交换机，队列，绑定关系，发送消息，校服消息都是调用Channel接口的方法

	- Queue

		- 存储消息对象，生产之和消费者的纽带，持久发到Mnesia数据库里

	- Consumer

		- Pull模式

			- 消费者中拉取队列中的消息，对应方法（basicGet）
			- 需要自己写定时任务拉取小时，实时性会降低，但是可以根基自己的消费能力决定拉取消息的频率

		- Push模式

			- 消息服务器推送给消费者，实时性很高，消费者能力不够会产生消息积压
			- Spring AMQP是用得push模式，通过时间机制对队列进行监听

		- 一条消息被消费者接口后，broker才会把消息从数据库中删除
		- 一个消费者可以监听多个队列，一个队列也可以被多个消费者监听，生产环境里推荐一个消费之只处理一个队列，想提高消费能力可以增多消费者

	- Exchange

		- 交换机，根据规则分发消息
		- 和每个接收消息的队列建立绑定关系，并为每个消息队列指定特殊的标识

	- Vhost

		- 可以根据不用的业务创建不同的虚拟机，使得提高硬件资源的利用率，并实现资源和权限的隔离
		- 不同的Vhost里可以有同名的Exchange和Queue
		- 默认Vhost是“/”

- 路由模型

	- 直连Direct

		- 规则

			- 队列与直连交换机绑定，需指定一个明确的绑定建（binding key）
			- 生产者发送消息时会写到一个路由键（routing key）
			- 消息的路由键与摸个队列的绑定建必须完全匹配

		- 直连类交换机适用于业务明确的消息

	- 广播Tipic

		- 规则

			- #代表0或多个单词
			- *代码不多不少一个单词
			- 例子：单词（word）指的是用英文的点“.”隔开的字符。a.bc.def是3个单词

		- 适用于根据业务主题或消息等级过滤消息的场景，例如：一条消息和资金有关，又跟风控有关，可以写两个单词一个管理资金系统，一个关联风控系统

	- 广播Fanout

		- 规则

			- 不需要绑定键，也不需要路由键
			- 例子：使用于一些通用的业务消息，例如：修改用户信息，向各级业务系统发送变更消息

	- headers（不常用，忽略）

### 高级特性

- 死信队列（Dead Letter Queue）

	- 进入条件

		- 超过队列最大长度（Max length），队头消息被抛弃
		- 超过队列最大容量（Max length bytes），队头消息被抛弃
		- Queue TTL（x-message-ttl超时）
		- MessageTTL超时
		- 消费者拒绝并且未设置重回队列，(NACKI Reject)&&requeue == false

	- 存储

		- 由死信交换机DLX（Dead Letter Exchange）路由到死信队列

	- 使用

		- 1.声明原交换机，原队列，相互绑定，并且指定死信交换机
		- 2.声明死信交换机，死信队列，并用“#”绑定，代码无条件路由
		- 3.最终消费者监听死信队列，进行业务处理

- 延时队列

	- MessageTTL

		- messageProperties.setExpiration（"4000"），超时进入死信队列进行处理

	- Queue TTL

		- 设置x-message-ttl属性，超时进入死信队列进行处理

	- 定时任务

		- 消息落表，定时任务扫描，但是如果消息过多会影响业务程序

- 流量控制

	- 服务器端

		- 队列长度

			- 超过队列最大长度（Max length），队头消息被抛弃
			- 超过队列最大容量（Max length bytes），队头消息被抛弃

		- 内存控制

			- 默认占用当前MQ的40%以上内存时会报警，阻塞所有连接，修改rabbitmq.config文件里的内存阈值

				- [{rabbit.[vm_memory _high_watermark.0.4}]

			- 命令

				- rabbitmqctl set_vm_memory high_watermark 0.3

			- 设置成0，则所有的消息都不能发布

		- 磁盘控制

			- 当磁盘剩余低于指定值时触发流控措施

				- disk_free limit.relative=3.0
disk_free limit.absolute=2GB

	- 消费端

		- 默认情况下消费者会本地缓存消息，如果数量过多可能会导致OOM或者影响其他进程
		- 消费者数太少，处理时间过长，可以考虑设置prefetch count的值，含义是消费端最大的unacked messages数目，当超过这个数会停止投递给这个消费者

			- 例如：
channel.basicQos（2）;//如果超过 2条消息没有发送 ACK，当前消费者不再接受队列消息 channel.basicConsume((QUEUE NAME, false, consumer);

## 入门

### 安装

- Docker安装RabbitMQ集群

- HAProxy+Keepalived搭建 RabbitMQ高可用集群

### 界面管理

- windows

	- cd rabbit_home/sbin 
rabbitmg-plugins.bat enable rabbitmq_management

- linux

	- cd rabbit_home/bin
/rabbitmq-plugins enable rabbitmqmanagement

## 使用

### RabbitMQ Java API

### Spring AMQP

- 生产者

	- 模板

		- 通过@Autowired注入AmqpTemplate实现send方法进行消息发送

- 消费者

	- 对象定义

		- 配置文件

			- 定义交换机

				- 利用@Bean将new DirectExchange放到Ioc容器管理
				- 利用@Bean将new TopicExchange放到Ioc容器管理
				- 利用@Bean将new FanoutExchange放到Ioc容器管理

			- 定义队列

				- 利用@Bean将new Queue放到Ioc容器管理

			- 定义绑定关系

				- 利用@Bean将BindingBuilder.bind(queue).to(exchange).with("gupao.best");放到Ioc容器管理

	- 监听

		- @RabbitListener

			- queues

				- 指定消费队列

			- @RabbitHandler

				- 指定消费处理方法

			- @Payload

				- 取得消费主题

### Spring Boot 参数

- https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/common-application-properties.html 
- https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html 

## 可靠性于高可用

### 可靠性分析

- 服务端ACK

	- 事务模式（Transaction）

		- Java API中设置

			- 在创建channel时设置事务模式

				- channel txSelect()

			- 发送消息后提交事务

				- channel.txCommit()

			- 如果事务处理中发送异常可以进行回滚

				- channel.txRollback()

		- SpringBoot中的设置

			- rabbitTemplate.setChannelTransacted(true)

		- 缺点：生产者和服务器之间进行事务确认时会阻塞，降低RabbitMQ服务器的性能，不建议在生产环境中使用

	- 确认模式（Confirm）

		- Java API设置

			- 通过channel.confirmSelect()开启确认模式

				- 普通确认模式

					- 通过channel.waitForConfirms(true|false)确认消息是否成功发送到Broker
缺点：发送一条确认一条，效率低

				- 批量确认模式

					- 通过channel.waitForConfirmsOrDie()方法可以批量确认消息发送是否成功
批量确认的效率比单挑确认效率高
缺点：一批消息中只要有一个消息不成功全部回滚

				- 异步确认模式

					- 添加一个ConfirmListener，用一个SortedSet来维护批次中没确认的消息

			- 网络错误会抛出异常，交换机不存在会抛出404

		- Spring Boot设置

			- 再模板中通过rabbitTemplate.setConfirmCallback()进行确认

- 路由

	- 消息重发

		- Java API设置

			- 添加channel.addReturnListener来监听无法路由的消息进行重发

		- Spring Boot设置

			- rabbitTemplate.setMandatory(true)
在rabbitTemplate.setReturnCallback()回调函数里进行重发

	- 备份交换机

		- 使用属性[alternate-exchange]来指定备份交换机

- 队列存储
- 消费者ACK
- 回调
- 重发
- 幂等
- 最终一致性

### 高可用架构

- 集群节点

	- 磁盘节点
	- 内存节点

- 集群模式

	- 镜像队列
	- 普通集群

- HAProxy+KeepAlived

- Lvs

## 特性

### 支持多客户端

- 对主流开发语言（Python、Java、Ruby、PHP、C#、JavaScript、Go、Elixir、Objective-C、Swift 等）都有客户端实现。

### 灵活的路由

- 通过交换机（Exchange）实现消息的灵活路由。

### 权限管理

- 通过用户与虚拟机实现权限管理。

### 插件系统

- 支持各种丰富的插件扩展，同时也支持自定义插件。

### Spring集成

- Spring对AMQP进行了封装。

### 高可靠

- RabbitMQ提供了多种多样的特性让你在可靠性和性能之间做出权衡，包括持久化、发送应答、发布确认以及高可用性。

### 集群与扩展性

- 多个节点组成一个逻辑的服务器，支持负载。

### 高可用队列

- 通过镜像队列实现队列中数据的复制。

## 实践经验

### 资源管理

### 配置文件与命名规范

### 调用封装

### 信息落库+定时任务

### 生产环境运维监控

### 日志追踪

### 如何减少链接

*XMind - Trial Version*
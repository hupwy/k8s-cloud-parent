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

					- 通过channel.waitForConfirms(true|false)确认消息是否成功发送到Broke
					- 缺点：发送一条确认一条，效率低

				- 批量确认模式

					- 通过channel.waitForConfirmsOrDie()方法可以批量确认消息发送是否成功，批量确认的效率比单挑确认效率高
					- 缺点：一批消息中只要有一个消息不成功全部回滚

				- 异步确认模式

					- 添加一个ConfirmListener，用一个SortedSet来维护批次中没确认的消息，推荐使用

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

	- 队列持久化

		- channel.queueDeclare(QUEUE_NAME,false,false,false,null);

			- durable

				- 没有持久化的队列，保存在内存中，服务重启后队列和消息都会消失

			- autoDelete

				- 没有消费者连接的时候，自动删除

			- exclusive

				- 排他性队列的特点是

					- 只对首次声明它的连接（Connection）可见
					- 会在其连接断开的时候自动删除

	- 交换机持久化

		- new DirectExchange("EXCHANGE",true,false,new HashMap<))

			- durable

				- 没有持久化的队列，保存在内存中，服务重启后队列和消息都会消失

			- autoDelete

				- 没有消费者连接的时候，自动删除

			- exclusive

				- 排他性队列的特点是

					- 只对首次声明它的连接（Connection）可见
					- 会在其连接断开的时候自动删除

	- 消息持久化

		- deliveryMode(2)//2 代表持久化

			- 如果消息没有持久化，保存在内存中，队列还在 ，但是消息在重启后会消失

	- 集群

		- 备份机制

			- 磁盘节点
			- 内存节点

- 消费者ACK

	- 自动

		- 会在消息收到时自动发送ACK，而不是在方法执行完毕时发送ACK，并不关心消息是否正常被消费
		- autoAck(true) 默认值就是true

	- 手动

		- Java API

			- 1.autoAck(false)
			- 2.channel.basicAck(envelope.getDeliveryTag),true));

		- SpringBoot

			- 代码

				- spring.rabbitmqlistener.direct acknowledge-mode=manual 
spring.rabbitmqlistener.simple.acknowledge-mode=manual
				- SimpleRabbitListenerContainer 或者SimpleRabbitListenerContainerFactory

					- factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

			- 设定值分类

				- NONE：自动ACK
				- MANUAL：手动ACK
				- AUTO：

					- 未抛出异常发送ACK
					- 抛出异常

						- 不是AmqpRejectAndDontRequeueException

							- 发送nack，重新入队列

						- AmqpRejectAndDontRequeueException

							- 发送nack，不重新入队列

	- 消费者代码

		- 调用ACK

			- channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

		- 拒绝消费

			- Basic.Reject() 拒绝单挑
			- BasicNack() 批量拒绝

- 回调

	- 调用生产者API

		- 消费者处理完成后需要调用生产者API改变数据状态，表示已成功消费
		- 缺点：服务之间耦合度高

	- 发送响应消息给生产者

		- 消费者处理完成后需要给生产者发出响应消息，表示已成功消费

- 重发（消息补偿）

	- 回调失败尝试重发消息

		- 设定超时时间

			- 1，设计一张表，保存所有异步消息的中间状态（确认中，完成），重发次数，发送时间，消息内容等信息
			- 2，该时间范围内没得到消息回执（消息还处于中间状态“确认中”），确认消息发送失败
			- 3，创建定时任务每个30秒扫描一次发送未成功的消息进行重新发送

		- 设置衰减重试规则，比如：30s，1m，2m，5m，8m，1m等 逐渐加长时间
		- 设置重试次数，控制重发次数

- 幂等

	- 原因

		- 生产者问题，比如开启的Confirm模式单未收到确认消息，消息重复发送
		- 消费者位发送ACK，消息也会重新发送
		- 生产者代码或网络问题，消息重新发送

	- 解决方案

		- 对每条消息生成一个唯一的业务ID
		- 通过数据库主键唯一性排他处理
		- 通过redis的setnx进行排他处理
		- 通过记录日志文件进行排他处理

- 最终一致性

	- 无法正常消费的消息怎么保证最终一致性呢？

		- 人工介入，查找丢失数据

- 消息的顺序性

	- 一个队列多个消费者是无法保证顺序性
	- 一个队列只有一个消费者是可以保证顺序性
	- 结论：尽量保证一个消费者进行消费

### 高可用架构

- 为什么做集群

	- 高可用

		- 保证某个节点服务器不可用，可以连接其他节点，不影响业务

	- 负载均衡

		- 在高并发的场景下，可以分发给多个MQ服务器，减少延迟

- 集群节点

	- 磁盘节点

		- 将元数据保存到磁盘（默认节点）

			- 队列属性
			- 交换机类型名字属性
			- 绑定关系
			- vhost

		- 集群中至少要有一个磁盘节点做持久化

	- 内存节点

		- 将元数据放到内存中
		- 集群一般会连接到内从节点，读写快

	- 集群配置步骤

		- 配置host以便相互通信
		- 同步erlang.cookie
		- 加入集群（join cluster命令）

- 集群模式

	- 镜像队列

		- 不用节点只会相互同步元数据（交换机，队列，绑定关系，vhost的定义），但是不同步消息内容
		- 访问A节点，消费B节点的消息时，请求会从A节点转发到B节点
		- 缺点：消息没有备份，一点有一个节点宕机，会丢失数据

	- 普通队列

		- 消息内容也会在不同的节点上同步，这样可用性更高，但是系统性能会降低

- HAProxy+KeepAlived

	- 四层，七层负载

- Lvs

	- 四层负载

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

- 元数据命名集中在properties里
- 元数据类型

	- 虚拟机命名

		- XXX_VHOST

	- 交换机命名

		- XXX_EXCHANGE

	- 队列命名

		- XXX_QUEUE

### 调用封装

- 抽象封装，减少代码改动量

### 信息落库+定时任务

- 消息补偿

### 生产环境运维监控

- zabbix+grafana
- 监控磁盘，内存，连接数

### 日志追踪

- Firehose

	- https:/www.rabbitmg.com/firehose.html
https//www.rabbitma.com/plugins.html

### 如何减少链接

- 批量发送消息时，可以把批量消息系列化成一个JSON数据包发送

## Troubleshoot

### channel和vhost的作用

- channel

	- 减少TCP资源的消耗，也是重要的编程接口

- Vhost

	- 提高硬件资源的利用率，实现资源隔离

### RabbitMQ的消息有哪些路由方式

- direct
- topic
- fanout

### 无法路由的消息去哪了

- 直接丢弃，可用备份交换机接收

### 消息在什么时候会编程Dead Letter(死信)

- 消息过期
- 消息超过队列长度和容量
- 消息呗拒绝并且未设置回收队列

### 如何时间延时队列

- 基于数据库+定时任务
- 消息过期+死信队列
- 延迟队列插件

*XMind - Trial Version*
# RocketMQ web控制台2.0.0版本源码方式安装

## 下载项目源代码

```shell
cd /usr/local/soft
wget https://github.com/apache/rocketmq-externals/archive/master.zip
```

解压：

```shell
unzip master.zip
```

解压出来的文件夹名字：
rocketmq-externals-master

## 修改配置文件

```shell
cd /usr/local/soft/rocketmq-externals-master/rocketmq-console/src/main/resources/
vim application.properties
```

修改端口号：

```properties
server.port=7298
```

修改name server地址（多个地址用英文分号隔开）

```properties
rocketmq.config.namesrvAddr=localhost:9876
```

注意后面改了配置文件要重新打包

## 解压编译

```shell
cd /usr/local/soft/rocketmq-externals-master/rocketmq-console/
mvn clean package -Dmaven.test.skip=true
```

## 启动jar包

```shell
cd target
java -jar rocketmq-console-ng-2.0.0.jar
```

## 访问
http://192.168.44.162:7298

![image.png](\images\448d19ed905f411e92fee3ee69b34c6b.png)

## 日志
日志配置：

```shell
rocketmq-externals-master/rocketmq-console/src/main/resources/application.properties
```

指定了logback.xml 为日志配置文件

```xml
<file>${user.home}/logs/consolelogs/rocketmq-console.log</file>
```

实际路径

```shell
cd ~/logs/consolelogs/rocketmq-console.log
```
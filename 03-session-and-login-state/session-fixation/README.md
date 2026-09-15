# session fixation

演示 Session fixation 防护和并发会话限制。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./03-session-and-login-state/session-fixation/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./03-session-and-login-state/session-fixation/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




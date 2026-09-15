# one time token 03

演示带扩展字段的 One-Time Token 服务。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/one-time-token-extra-fields/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/one-time-token-extra-fields/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。



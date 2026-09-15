# csrf

演示 CSRF 防护以及带 CSRF Token 的请求。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./02-web-security/csrf/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./02-web-security/csrf/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




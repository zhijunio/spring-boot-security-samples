# user details service 01

演示自定义 UserDetailsService 和内存用户。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./01-authentication/user-details-service-01/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./01-authentication/user-details-service-01/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




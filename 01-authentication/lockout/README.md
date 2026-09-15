# lockout

演示账户锁定状态导致认证失败。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./01-authentication/lockout/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./01-authentication/lockout/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




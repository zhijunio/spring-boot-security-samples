# remember me

演示基于持久化 Token 的 Remember-Me 登录。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./03-session-and-login-state/remember-me/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./03-session-and-login-state/remember-me/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




# webauthn

演示 WebAuthn 无密码认证。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/webauthn/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/webauthn/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。




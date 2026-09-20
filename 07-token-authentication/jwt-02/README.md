# jwt 02

演示基于 RSA 公钥的 JWT 资源服务器。

仓库中的 RSA 密钥仅用于本地演示，不能用于生产环境。部署真实应用时应通过密钥管理系统或外部配置注入密钥，并定期轮换。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./07-token-authentication/jwt-02/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./07-token-authentication/jwt-02/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。



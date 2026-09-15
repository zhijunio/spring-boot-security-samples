# mfa-formLogin-ott-authorization-manager

演示基于 One-Time Token 的邮件多因素认证，并使用 `AuthorizationManagerFactories.multiFactor()` 配置密码和 OTT 因素的有效期。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-ott-authorization-manager/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-ott-authorization-manager/pom.xml spring-boot:run
~~~

部分示例依赖 MySQL 或其他外部服务，启动前请先查看该项目的配置文件和 `compose.yaml`。

`@EnableMultiFactorAuthentication` 要求每个授权规则同时具备 `FactorGrantedAuthority.PASSWORD_AUTHORITY` 和 `FactorGrantedAuthority.OTT_AUTHORITY`。`formLogin` 与 `oneTimeTokenLogin` 使用官方默认配置；缺哪个因素，框架会自动跳转到对应登录页。

令牌由内置 `JdbcOneTimeTokenService` 保存到 MySQL。生成成功后由 `MagicLinkOneTimeTokenGenerationSuccessHandler` 把登录链接打印到日志（模拟发信），再跳转到 `/ott/sent`。

示例用户：`user` / `admin`，邮箱分别为 `user@example.com`、`admin@example.com`，密码均为 `password`。
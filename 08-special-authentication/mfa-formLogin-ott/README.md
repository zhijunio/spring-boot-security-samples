# mfa-formLogin-ott

密码 + One-Time Token 的最小 MFA 示例。使用 Spring Security 默认登录页和令牌提交页；用户与令牌存在内存里，进程退出即清空。没有角色、没有数据库。

自定义 Thymeleaf 页面 + JDBC 版本见同目录 `mfa-formLogin-ott-thymeleaf`。

`oneTimeTokenLogin` 需要一个 `OneTimeTokenGenerationSuccessHandler`。本示例只把魔法链接打印到日志（模拟发信），再跳到默认提交页 `/login/ott`，不提供自定义页面。

## 运行

```bash
mvn -f ./08-special-authentication/mfa-formLogin-ott/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-ott/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。密码登录后请求一次性令牌，从日志里复制 Magic Link，在默认提交页完成第二因子。

## 关键配置

`@EnableMultiFactorAuthentication` 同时要求 `FACTOR_PASSWORD` 和 `FACTOR_OTT`。`formLogin` 与 `oneTimeTokenLogin` 使用官方默认页面。`/ott/generate` 和 `/login/ott` 只要求密码，以便在密码登录后申请并提交令牌。

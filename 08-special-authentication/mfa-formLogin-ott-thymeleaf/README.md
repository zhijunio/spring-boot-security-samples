# mfa-formLogin-ott-thymeleaf

密码 + One-Time Token 的 MFA 示例，自定义 Thymeleaf 登录页、令牌发送页和提交页。用户与邮箱、一次性令牌存在 MySQL；OTT 默认开启。不连接真实邮件服务，魔法链接打印到应用日志。

页面与授权结构对齐 `mfa-formLogin-totp-thymeleaf`；用户表带 `email` 列。默认页面、内存版本见同目录 `mfa-formLogin-ott`。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/mfa-formLogin-ott-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-ott-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`，邮箱 `user@example.com`。密码登录后请求令牌，从日志复制 Magic Link，在 `/login/ott` 完成第二因子。顶栏 Log out 直接 `POST /logout`。

## 关键配置

`@EnableMultiFactorAuthentication` 要求 `FACTOR_PASSWORD` 与 `FACTOR_OTT`。`UserService` 从 `users` 表加载密码和邮箱；`JdbcOneTimeTokenService` 把令牌写到 `one_time_tokens`。`/ott/generate` 只要求密码。

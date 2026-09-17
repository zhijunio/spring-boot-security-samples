# mfa-formLogin-email-thymeleaf

密码 + 邮件验证码的 MFA 示例，自定义 Thymeleaf 登录页和校验页。用户与邮箱存在 MySQL；邮件因素默认开启，没有绑定/解绑。不连接真实邮件服务，验证码打印到应用日志。

页面与授权结构对齐 `mfa-formLogin-totp-thymeleaf`；用户表带 `email` 列，对齐 `mfa-pluggable`（本示例始终要求邮件码，因此没有 `email_mfa_enabled` 开关）。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/mfa-formLogin-email-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-email-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`，邮箱 `user@example.com`。密码登录后跳转到 `/email/verify`，验证码出现在日志里；也可在校验页再点 Send code。

## 关键配置

`@EnableMultiFactorAuthentication` 要求 `FACTOR_PASSWORD` 与 `FACTOR_EMAIL`。`UserService` 从 `users` 表加载密码和邮箱。密码成功后由 `EmailMfaAuthenticationSuccessHandler` 发码并跳到 `/email/verify`；缺邮件因素访问受保护页时同样跳到该页。顶栏 Log out 直接 `POST /logout`。

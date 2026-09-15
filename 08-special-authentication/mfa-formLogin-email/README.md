# mfa-formLogin-email

演示密码 + 邮件验证码的多因素认证。项目不连接真实邮件服务，验证码会打印到应用日志，便于本地学习和测试。

## 运行

```bash
mvn -f ./08-special-authentication/mfa-formLogin-email/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-email/pom.xml spring-boot:run
```

先使用密码登录，系统会自动跳转到 `/email/verify`。点击页面按钮请求验证码，验证码会出现在日志中，输入验证码后提交即可完成第二因素认证。项目同时保留 `POST /email/request` 和 `POST /email/verify` 接口；两个接口都会校验当前 Session 已完成密码因素且用户名一致。

`@EnableMultiFactorAuthentication` 声明密码因素和自定义 `FACTOR_EMAIL` 因素均必须满足。

密码认证成功后由 `EmailMfaAuthenticationSuccessHandler` 统一重定向到 `/email/verify`。

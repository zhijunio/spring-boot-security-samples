# mfa-formLogin-webauthn

密码 + WebAuthn 的最小 MFA 示例。使用 Spring Security 默认登录页和 Passkey 注册页；凭证存在内存里，进程退出即清空。没有角色、没有数据库。

自定义 Thymeleaf 页面 + JDBC 版本见同目录 `mfa-formLogin-webauthn-thymeleaf`。

## 运行

```bash
mvn -f ./08-special-authentication/mfa-formLogin-webauthn/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-webauthn/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。浏览器需支持 WebAuthn；RP ID 与允许来源是 `localhost`。

登录后若尚未登记 Passkey，会进入第二因子页，可先打开默认注册页再验证。

## 关键配置

`@EnableMultiFactorAuthentication` 同时要求 `FACTOR_PASSWORD` 和 `FACTOR_WEBAUTHN`。`formLogin` 与 `webAuthn` 使用官方默认页面。`/webauthn/**` 只要求密码，以便登记和完成断言。

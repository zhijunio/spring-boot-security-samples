# mfa-formLogin-webauthn-thymeleaf

密码 + WebAuthn 的 MFA 示例，自定义 Thymeleaf 登录页和 Passkey 注册页。账号和 Passkey 都存在 MySQL；每个账号最多一把 Passkey，删掉后才能再登记。没有角色。

默认登录/注册页、内存仓库版本见同目录 `mfa-formLogin-webauthn`。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/mfa-formLogin-webauthn-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-webauthn-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。浏览器需支持 WebAuthn；RP ID 与允许来源是 `localhost`。

登录后若尚未登记 Passkey，会进入第二因子页，可先注册再验证。

## 关键配置

`@EnableMultiFactorAuthentication` 同时要求 `FACTOR_PASSWORD` 和 `FACTOR_WEBAUTHN`。`/webauthn/**` 只要求密码，以便登记和完成断言。`UserService` 从 `users` 表加载账号；`JdbcPublicKeyCredentialUserEntityRepository` / `JdbcUserCredentialRepository` 持久化凭证；`OnePasskeyUserCredentialRepository` 拒绝同一 WebAuthn 用户的第二把密钥。

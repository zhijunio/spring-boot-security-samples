# webauthn-thymeleaf

WebAuthn 示例，自定义 Thymeleaf 登录页和 Passkey 登记页。用户与凭证存在 MySQL。密码或 Passkey 任一即可登录（不是 MFA）。

页面风格对齐 `mfa-formLogin-webauthn-thymeleaf`。默认页面、内存版本见同目录 `webauthn`。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/webauthn-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/webauthn-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。用密码登录后打开 `/webauthn/register` 登记 Passkey。顶栏 Log out 直接 `POST /logout`。

## 关键配置

`formLogin` 使用自定义 `/login`；`webAuthn` 关闭默认登记页。`UserService` 从 `users` 表加载密码；`JdbcPublicKeyCredentialUserEntityRepository` / `JdbcUserCredentialRepository` 持久化凭证。

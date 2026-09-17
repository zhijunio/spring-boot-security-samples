# webauthn

最小 WebAuthn 示例。使用 Spring Security 默认登录页和 Passkey 注册页；用户与凭证存在内存里，进程退出即清空。没有角色、没有数据库。密码登录后可登记 Passkey，之后也可以用 Passkey 登录。

自定义 Thymeleaf 页面 + JDBC 版本见同目录 `webauthn-thymeleaf`。

## 运行

```bash
mvn -f ./08-special-authentication/webauthn/pom.xml test
mvn -f ./08-special-authentication/webauthn/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。浏览器需支持 WebAuthn；RP ID 与允许来源是 `localhost`。

## 关键配置

`formLogin` 与 `webAuthn` 使用官方默认页面。未启用多因素：密码或 Passkey 任一即可访问受保护页。

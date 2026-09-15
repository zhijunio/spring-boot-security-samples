# mfa-webauthn

演示使用密码和 WebAuthn 两个认证因素的多因素认证。

## 运行

项目使用 MySQL 保存用户数据，并使用浏览器 WebAuthn/Passkey 完成第二个认证因素：

```bash
mvn -f ./08-special-authentication/mfa-webauthn/pom.xml test
mvn -f ./08-special-authentication/mfa-webauthn/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。浏览器需要支持 WebAuthn，且 WebAuthn 的 RP ID 和允许来源配置为本地地址。

## 关键配置

`@EnableMultiFactorAuthentication` 声明密码因素和 WebAuthn 因素均必须满足；`.webAuthn(...)` 配置 WebAuthn 的 RP 信息和允许来源。

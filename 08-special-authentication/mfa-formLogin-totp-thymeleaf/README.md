# mfa-formLogin-totp-thymeleaf

密码 + TOTP（RFC 6238）的 MFA 示例，自定义 Thymeleaf 登录页和校验页。用户与 TOTP 密钥存在 MySQL；TOTP 默认开启，没有绑定/解绑。

页面风格对齐 `mfa-formLogin-webauthn-thymeleaf`；用户表与密钥列对齐 `mfa-pluggable`（本示例始终要求 TOTP，因此没有 `totp_mfa_enabled` 开关）。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/mfa-formLogin-totp-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/mfa-formLogin-totp-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。密码登录后跳转到 `/totp/verify`。

演示密钥（仅供学习，不要用于生产）：`JBSWY3DPEHPK3PXP`

用 Authenticator 扫 URI，或用 `oathtool` 当场算码：

```bash
oathtool --totp --base32 JBSWY3DPEHPK3PXP
```

`--totp` 默认 SHA1、6 位、30 秒步进，与本示例一致。把输出的 6 位数字填进校验页即可。

URI：`otpauth://totp/mfa-formLogin-totp-thymeleaf:user?secret=JBSWY3DPEHPK3PXP&issuer=mfa-formLogin-totp-thymeleaf&algorithm=SHA1&digits=6&period=30`

校验允许当前 30 秒窗口及前后各一个窗口（约 ±30 秒）。

## 关键配置

`@EnableMultiFactorAuthentication` 要求 `FACTOR_PASSWORD` 与 `FACTOR_TOTP`。`UserService` 从 `users` 表加载密码；`TotpService` 用同一行的 `totp_secret` 校验。密码成功后由 `TotpMfaAuthenticationSuccessHandler` 跳到 `/totp/verify`；缺 TOTP 因素访问受保护页时同样跳到该页。

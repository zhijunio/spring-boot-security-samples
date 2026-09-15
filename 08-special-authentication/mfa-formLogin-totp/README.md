# mfa-formLogin-totp

演示密码 + TOTP（RFC 6238，认证器 App 动态码）的多因素认证。

Spring Security 没有 TOTP DSL。本示例使用官方 `@EnableMultiFactorAuthentication`，第二因素是自定义 `FACTOR_TOTP`。这与 `mfa-formLogin-ott` 不同：OTT 由服务端生成并投递；TOTP 由客户端用预置共享密钥计算。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-totp/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-totp/pom.xml spring-boot:run
~~~

访问 `http://localhost:8080`。用户 `user` / `admin`，密码均为 `password`。密码登录后跳转到 `/totp/verify`。把页面上的密钥或 `otpauth://` URI 加入 Authenticator，输入 6 位码完成第二因素。

演示密钥（仅供学习，不要用于生产）：

| 用户 | Base32 密钥 |
| --- | --- |
| `user` | `JBSWY3DPEHPK3PXP` |
| `admin` | `KVKFKRCPNZQUYMLX` |

URI 示例：`otpauth://totp/mfa-formLogin-totp:user?secret=JBSWY3DPEHPK3PXP&issuer=mfa-formLogin-totp&algorithm=SHA1&digits=6&period=30`

校验允许当前 30 秒窗口及前后各一个窗口（约 ±30 秒）。密钥预置在内存中，没有绑定/解绑流程。

## 关键配置

`@EnableMultiFactorAuthentication` 要求 `FACTOR_PASSWORD` 与 `FACTOR_TOTP`。密码成功后由 `TotpMfaAuthenticationSuccessHandler` 跳到 `/totp/verify`；缺 TOTP 因素访问受保护页时同样跳到该页。

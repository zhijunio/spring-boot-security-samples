# mfa-pluggable

演示**可插拔**第二因素：平台运行中开关 provider，用户按需绑定，登录时在已启用且已绑定的方法里 **OR** 选一种完成。

这与 `mfa-formLogin-totp` / `mfa-formLogin-backupCode` 等「整站写死 PASSWORD ∧ 某因素」的样例不同。本示例用 `@EnableMultiFactorAuthentication(authorities = {})` 打开 MFA 基础设施，再用自定义 `PluggableMfaAuthorizationManager`：无绑定则只要密码；有绑定则还要通用 `FACTOR_MFA`（任意 provider 验过即发）。

第一期 provider：

| id | 说明 |
| --- | --- |
| `email` | 控制台打印 6 位码 |
| `totp` | RFC 6238 |
| `bip39` | 官方 English 词表 + 校验和，**可复用** 12 词 |

WebAuthn 不在第一期。

## 测试

~~~bash
mvn -f ./08-special-authentication/mfa-pluggable/pom.xml test
~~~

## 启动

~~~bash
mvn -f ./08-special-authentication/mfa-pluggable/pom.xml spring-boot:run
~~~

访问 `http://localhost:8080`。密码均为 `password`。

| 用户 | 预绑定 |
| --- | --- |
| `plain` | 无 MFA，密码后直达 |
| `user` | TOTP（密钥 `JBSWY3DPEHPK3PXP`） |
| `mailer` | Email（登录后验证码打印到控制台） |
| `multi` | TOTP + BIP-39 |
| `admin` | TOTP；可开 `/mfa/platform` |

`multi` 的演示 BIP-39：

`payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas`

## 插拔方式

1. 实现 `MfaProvider` 并注册为 Spring bean。
2. 管理员在 `/mfa/platform` 开关（热配置，不必改 Java）。
3. 用户在 `/mfa/settings` 绑定/解绑。

关闭某 provider 后，挑战页不再出现它。若用户只有被关的方法，挑战页显示 blocked，不能靠密码混过受保护页。

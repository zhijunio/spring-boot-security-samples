# mfa-formLogin-backupCode

演示密码 + 可复用 12 词 BIP-39 助记词（第二因素）的多因素认证。

Spring Security 没有 backup-code DSL。本示例使用官方 `@EnableMultiFactorAuthentication`，第二因素是自定义 `FACTOR_BACKUP_CODE`。助记词必须是 BIP-39：官方 2048 词 English 词表 + SHA-256 校验和（128 位熵对应 12 词）。同一短语可以反复使用；服务端只保存规范化短语的 SHA-256，比较用 `MessageDigest.isEqual`。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-backupCode/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-backupCode/pom.xml spring-boot:run
~~~

访问 `http://localhost:8080`。用户 `user` / `admin`，密码均为 `password`。密码登录后跳转到 `/backup/verify`，输入对应 12 词完成第二因素。大小写与多余空白会被规范化。

演示助记词（仅供学习，不要用于生产）：

| 用户 | BIP-39 12 词 |
| --- | --- |
| `user` | `payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas` |
| `admin` | `movie shrimp volcano merge enforce sing alarm burst total plastic uncover duck` |

词表来自 [BIP-39 English](https://github.com/bitcoin/bips/blob/master/bip-0039/english.txt)，放在 `src/main/resources/bip39/english.txt`。

## 关键配置

`@EnableMultiFactorAuthentication` 要求 `FACTOR_PASSWORD` 与 `FACTOR_BACKUP_CODE`。密码成功后由 `BackupCodeMfaAuthenticationSuccessHandler` 跳到 `/backup/verify`；缺 backup 因素访问受保护页时同样跳到该页。

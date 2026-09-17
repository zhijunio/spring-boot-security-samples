# mfa-pluggable

Spring Security 7 多因子认证演示：密码之后按已启用的主因子做 AND；Mnemonic 随时可启用，有主因子时作恢复，仅启用 Mnemonic 时登录必须校验。

## 因子

| 类型 | 说明 |
| --- | --- |
| 密码 | 必选第一因子 |
| TOTP | 认证器 App |
| Email code | 6 位码，演示环境打到日志，不发邮件 |
| Passkey | WebAuthn，自定义 `/enable-webauthn` |
| Mnemonic | 12 词 BIP39；抄写勾选后，在打乱词库按序点选确认 |

登录后统一进入 `/challenge`，同一页展示所有待验证方式（含 Passkey）。主因子做 AND；Mnemonic 在本页即可恢复。

## 环境

- JDK 21
- Docker（MySQL；测试还要用 Testcontainers / Playwright）
- Maven Wrapper（`./mvnw`）

## 运行

```bash
./mvnw spring-boot:run
```

`spring-boot-docker-compose` 会拉起 `compose.yaml` 里的 MySQL 8.4（库 `mfa` / 用户 `user`）。打开 [http://localhost:8080](http://localhost:8080)，注册后在 Welcome 页启用因子。

WebAuthn 的 `rpId` 是 `localhost`。生产端口按 `8080` 配了 origin；测试随机端口会按请求 Origin 适配。

也可用 Testcontainers 起库：

```bash
./mvnw -DskipTests spring-boot:test-run
```

（入口是 `TestSpringBootSecurityMfaApplication`。）

## 测试

```bash
./mvnw test
```

- `com.example.mfa.**`：因子单元测试（部分会起 MySQL 容器）
- `DemoMfaE2ETest`：Playwright + 虚拟 authenticator；TOTP/Email 在测试配置里接受 `1234`

## 说明

- Schema 在 `src/main/resources/schema.sql`，`spring.sql.init.mode=always`。`CREATE TABLE IF NOT EXISTS` **不会**改已有表，改结构需要重建库（例如 `docker compose down -v`）。
- 这是演示，不是生产模板：邮件码明文记日志；Passkey 仅适合本机 `localhost`。

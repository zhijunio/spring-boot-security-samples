# one-time-token-jdbc

演示使用 Spring Security 内置的 `JdbcOneTimeTokenService` 持久化 One-Time Token。令牌生成后写入 MySQL 的 `one_time_tokens` 表，消费时校验有效期并删除记录，因此同一个令牌只能成功使用一次；框架还会定期清理过期令牌。

## 运行

项目使用 `compose.yaml` 启动 MySQL：

```bash
mvn -f ./08-special-authentication/one-time-token-jdbc/pom.xml test
mvn -f ./08-special-authentication/one-time-token-jdbc/pom.xml spring-boot:run
```

访问 `http://localhost:8080`，在登录页请求 One-Time Token。令牌会打印在应用日志中。

## 关键代码

- `SecurityConfig`：注册 Spring Security 内置的 `JdbcOneTimeTokenService`。
- `schema.sql`：创建 `one_time_tokens` 表。
- `SecurityConfig`：启用 `oneTimeTokenLogin`。

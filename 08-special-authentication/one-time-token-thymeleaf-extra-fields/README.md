# one-time-token-thymeleaf-extra-fields

在 `one-time-token-thymeleaf` 之上，申请一次性令牌时必须再提交密码（扩展字段）。用户与令牌仍存在 MySQL；`JdbcOneTimeTokenService` 发 UUID 令牌；魔法链接打印到日志。

自定义 `GenerateOneTimeTokenRequestResolver`：用户名或密码不对则不发令牌，请求落到 `POST /ott/generate` 并回到 `/login?error`。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/one-time-token-thymeleaf-extra-fields/pom.xml test
mvn -f ./08-special-authentication/one-time-token-thymeleaf-extra-fields/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。登录页下方「Send token」需要用户名和密码。顶栏 Log out 直接 `POST /logout`。

## 与 REST 示例的关系

`one-time-token-rest` 不并入本模块。本示例令牌走带外投递（日志里的 Magic Link）；REST 示例把令牌写进 JSON 响应，契约不同。

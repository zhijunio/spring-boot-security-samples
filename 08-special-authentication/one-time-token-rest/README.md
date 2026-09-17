# one-time-token-rest

在 `one-time-token` 之上，用 REST 发令牌：默认登录页和 `/login/ott` 提交页，用户与令牌存在内存里，没有数据库、没有自定义页面。

和 `one-time-token` 的差别：令牌由 `POST /api/ott/generate` 返回 JSON，而不是打印 Magic Link。默认登录页上的「Send Token」只会得到 `204`，拿不到令牌。

## 运行

```bash
mvn -f ./08-special-authentication/one-time-token-rest/pom.xml test
mvn -f ./08-special-authentication/one-time-token-rest/pom.xml spring-boot:run
```

演示账号 `user` / `password`。

```bash
curl -X POST http://localhost:8080/api/ott/generate \
  -H 'Content-Type: application/json' \
  -d '{"username":"user","password":"password"}'
```

把返回的 `token` 填进默认提交页 `http://localhost:8080/login/ott`，或 `POST /login/ott`。

## 关键配置

`InMemoryOneTimeTokenService` 同时给 REST 和 `oneTimeTokenLogin` 用。`/api/ott/generate` 忽略 CSRF，并先用 `AuthenticationManager` 校验用户名密码。

# one-time-token-rest

演示通过 REST API 请求 One-Time Token，并继续使用 Spring Security 的 OTT 登录过滤器消费令牌。项目不包含网页端 Magic Link 生成 Handler。

## 运行

```bash
mvn -f ./08-special-authentication/one-time-token-rest/pom.xml test
mvn -f ./08-special-authentication/one-time-token-rest/pom.xml spring-boot:run
```

使用用户名和密码请求令牌。网页端不提供 Magic Link 生成流程，OTT 生成统一通过 REST 接口完成：

```bash
curl -X POST http://localhost:8080/api/ott/generate \
  -H 'Content-Type: application/json' \
  -d '{"username":"user","password":"password"}'
```

接口返回令牌、用户名和过期时间。返回的令牌可以提交到现有的 `/login/ott` OTT 登录流程中。

## 关键代码

- `OneTimeTokenRestController`：完成 REST 请求、用户名密码认证和令牌生成。
- `SecurityConfig`：允许 REST 生成接口，并启用 `oneTimeTokenLogin`。

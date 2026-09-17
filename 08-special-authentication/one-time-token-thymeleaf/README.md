# one-time-token-thymeleaf

One-Time Token 示例，自定义 Thymeleaf 登录页、令牌发送页和提交页。用户与邮箱、一次性令牌存在 MySQL。密码或魔法链接任一即可登录（不是 MFA）。不连接真实邮件服务，魔法链接打印到应用日志。

页面风格对齐 `mfa-formLogin-ott-thymeleaf`。默认页面、内存版本见同目录 `one-time-token`。

## 运行

需要 Docker。`spring-boot:run` 会按 `compose.yaml` 启动 MySQL。

```bash
mvn -f ./08-special-authentication/one-time-token-thymeleaf/pom.xml test
mvn -f ./08-special-authentication/one-time-token-thymeleaf/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`，邮箱 `user@example.com`。也可在登录页请求令牌，从日志复制 Magic Link。顶栏 Log out 直接 `POST /logout`。

## 关键配置

`formLogin` 使用自定义 `/login`；`oneTimeTokenLogin` 关闭默认提交页。`UserService` 从 `users` 表加载密码和邮箱；`JdbcOneTimeTokenService` 把令牌写到 `one_time_tokens`。

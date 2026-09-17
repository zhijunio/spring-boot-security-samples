# one-time-token

最小 One-Time Token 示例。使用 Spring Security 默认登录页和令牌提交页；用户与令牌存在内存里，进程退出即清空。没有角色、没有数据库。密码或魔法链接任一即可登录。

`oneTimeTokenLogin` 需要一个 `OneTimeTokenGenerationSuccessHandler`。本示例只把魔法链接打印到日志（模拟发信），再跳到默认提交页 `/login/ott`。

自定义 Thymeleaf 页面 + JDBC 版本见同目录 `one-time-token-thymeleaf`。

## 运行

```bash
mvn -f ./08-special-authentication/one-time-token/pom.xml test
mvn -f ./08-special-authentication/one-time-token/pom.xml spring-boot:run
```

访问 `http://localhost:8080`。演示账号 `user` / `password`。也可在默认登录页请求一次性令牌，从日志复制 Magic Link。

## 关键配置

`formLogin` 与 `oneTimeTokenLogin` 使用官方默认页面。未启用多因素：密码或 OTT 任一即可访问受保护页。

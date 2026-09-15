# mfa-formLogin-oauth2

对齐 [Spring Security 官方 MFA OAuth2 示例](https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/authentication/mfa/oauth2)：用 MFA 的「缺 authority 再认证」做 **OAuth2 增量授权（step-up）**。

这不是密码 + 第二登录因素。`@EnableMultiFactorAuthentication(authorities = {})` 只打开机制，不要求 `FACTOR_PASSWORD`。登录走 `oauth2Login`（Google）。访问 `/profile` 需要额外的 Gmail 只读 scope；没有该 `SCOPE_…` 时，自定义 `AuthenticationEntryPoint` 再向 Google 申请这一条权限。

官方启动类虽名为 `FormLoginOAuth2Application`，其 `SecurityConfig` 同样没有 `formLogin`。本仓库也不加本地密码登录：Google 用户名与本地 `user` 不是同一身份，MFA 无法把 scope 合并上去。

官方该目录 README 提到 Maildev / magic link，与这份 `oauth2Login` 代码不符，此处不采用。

## 测试

在仓库根目录执行（不连接真实 Google）：

~~~bash
mvn -f ./08-special-authentication/mfa-formLogin-oauth2/pom.xml test
~~~

## 启动

在 Google Cloud 创建 OAuth 客户端（授权重定向 URI：`http://localhost:8080/login/oauth2/code/google`），然后：

~~~bash
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
mvn -f ./08-special-authentication/mfa-formLogin-oauth2/pom.xml spring-boot:run
~~~

未设置环境变量时使用占位 `id` / `secret`，仅够测试重定向 URL，不能完成真实登录。

访问 `http://localhost:8080`，经 `/login` 用 Google 登录后打开 `/profile`。若当前令牌没有 Gmail 只读 scope，会再次跳转授权页。

## 关键配置

`/profile` 要求 `SCOPE_https://www.googleapis.com/auth/gmail.readonly`；`OAuth2ScopeAuthenticationEntryPoint` 在缺该 authority 时发起只含该 scope 的授权请求。

# userlocation

演示使用 `AuthenticationDetailsSource` 将登录请求中的 `location` 参数放入认证详情，并由自定义 `AuthenticationProvider`
校验位置。

登录：`user / password`，同时提交 `location=office` 才能认证成功。示例用于说明认证详情的扩展点，生产环境应使用可信的位置来源，不能直接信任客户端参数。

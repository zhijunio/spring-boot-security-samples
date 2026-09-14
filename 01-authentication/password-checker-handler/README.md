# password-checker-handler

演示 `NbvcxzPasswordChecker` 与 `CompromisedPasswordAwareAuthenticationSuccessHandler` 的组合使用。认证成功后检查密码强度，密码强度不足时在
Session 中写入 `compromised_password` 标记，供后续页面提示用户修改密码。

登录：`user / safe-password`；`compromised / password` 用于演示专用失败处理。

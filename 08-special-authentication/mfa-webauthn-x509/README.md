# mfa-webauthn-x509

演示 Spring Security 官方多因素认证：WebAuthn（Passkey）因素 + X.509 客户端证书因素。本示例不启用 `formLogin`，没有角色。

X.509 在 TLS 握手时由容器校验客户端证书；Spring Security 从证书 CN 解析用户名，并授予 `FactorGrantedAuthority.X509_AUTHORITY`。证书 CN 必须与用户名一致（`user`）。WebAuthn 使用 `https://localhost:8443` 作为允许来源，`rpId` 为 `localhost`。Passkey 默认保存在内存中，重启后需要重新注册。

## 测试

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-webauthn-x509/pom.xml test
~~~

## 启动

在仓库根目录执行：

~~~bash
mvn -f ./08-special-authentication/mfa-webauthn-x509/pom.xml spring-boot:run
~~~

访问 `https://localhost:8443`。服务端证书由示例 CA 签发，浏览器会提示不受信任，可先导入 `src/main/resources/certs/ca.crt`。

将 `user.p12` 导入浏览器（密钥库密码均为 `password`），建立 HTTPS 连接时选择该客户端证书。访问首页时若尚未完成 WebAuthn，会跳到 `/login?factor.type=webauthn&factor.reason=missing`。在已识别用户后打开 `/webauthn/register` 注册 Passkey，再用 Passkey 完成第二因素。两个因素都满足后才能访问首页。

`server.ssl.client-auth=want`：没有客户端证书时仍可打开公开资源。缺 X.509 时框架返回 403（证书无法通过 Passkey 页面补全）。`/webauthn/**` 只要求 X.509 因素，以便在完成 Passkey 前登记凭证。

## 证书文件

演示证书放在 `src/main/resources/certs/`，仅用于本地学习，不要用于生产。密钥库密码均为 `password`。

| 文件 | 用途 |
| --- | --- |
| `ca.crt` | 示例 CA。浏览器需导入后才信任服务端证书。 |
| `server.p12` | 服务端证书和私钥，CN=`localhost`，SAN 含 `localhost` 与 `127.0.0.1`。 |
| `truststore.p12` | 信任库，只包含 CA，用于校验客户端证书。 |
| `user.p12` | 客户端证书和私钥，导入浏览器。CN 为 `user`。 |
| `user.crt` | 客户端证书公钥，供测试里 `x509("certs/user.crt")` 使用。 |

仓库里可能仍有 `admin.*` 证书文件，本示例不再使用。私钥只打进 PKCS12，不单独提交 `*.key`。

## 证书如何生成

在 `src/main/resources/certs` 目录执行。依赖 `openssl` 与 `keytool`。有效期 3650 天，算法 RSA 2048。

先创建自签 CA：

~~~bash
openssl req -x509 -newkey rsa:2048 -days 3650 -nodes \
  -keyout ca.key -out ca.crt \
  -subj "/CN=SampleCA/O=Example"
~~~

再签发服务端证书，并导出 `server.p12`：

~~~bash
openssl req -newkey rsa:2048 -nodes -keyout server.key -out server.csr \
  -subj "/CN=localhost/O=Example"
printf 'subjectAltName=DNS:localhost,IP:127.0.0.1\n' > server.ext
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out server.crt -days 3650 -extfile server.ext
openssl pkcs12 -export -in server.crt -inkey server.key -certfile ca.crt \
  -out server.p12 -name server -passout pass:password
~~~

把 CA 导入信任库，供 Tomcat 校验客户端证书：

~~~bash
keytool -importcert -alias ca -file ca.crt -keystore truststore.p12 \
  -storetype PKCS12 -storepass password -noprompt
~~~

为 `user` 签发客户端证书。CN 必须与 `UserDetailsService` 中的用户名一致：

~~~bash
openssl req -newkey rsa:2048 -nodes -keyout user.key -out user.csr \
  -subj "/CN=user/O=Example"
openssl x509 -req -in user.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out user.crt -days 3650
openssl pkcs12 -export -in user.crt -inkey user.key -certfile ca.crt \
  -out user.p12 -name user -passout pass:password
~~~

最后删除中间文件，只保留仓库中的证书产物：

~~~bash
rm -f *.key *.csr *.ext *.srl server.crt
~~~

## 关键配置

`@EnableMultiFactorAuthentication` 声明 WebAuthn 因素和 X.509 因素均必须满足；`.webAuthn(...)` 配置 RP 与允许来源，`.x509()` 使用官方默认配置。自定义账号页；Passkey 登记页仍用框架默认页。

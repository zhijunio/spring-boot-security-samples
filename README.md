# Spring Boot Security Samples

[![CI](https://github.com/zhijunio/spring-boot-security-samples/actions/workflows/ci.yml/badge.svg)](https://github.com/zhijunio/spring-boot-security-samples/actions/workflows/ci.yml)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-7.x-brightgreen.svg)](https://spring.io/projects/spring-security)
[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://www.oracle.com/java/technologies/downloads/)

Spring Boot Security 示例项目集合，用于演示 Spring Security 常见认证、授权和 Web 安全功能。

## 项目结构

每个子目录都是一个独立的 Maven 项目，可以单独运行和学习。

### 01-authentication

认证基础与认证扩展：

- basic-login
- password-encoder-01
- password-encoder-02
- password-checker
- password-checker-handler
- lockout
- user-details-service-01～05
- userlocation
- async

### 02-web-security

Web 安全配置：

- form-login-01～03
- http-basic-01～02
- csrf
- cors
- security-headers

### 03-session-and-login-state

Session 与登录状态：

- session-fixation
- remember-me

### 04-authentication-extension

认证扩展点：

- authentication-provider-01～02
- authentication-manager
- authentication-filter-01～02
- authentication-token
- authentication-handler

### 05-method-authorization

方法授权与视图集成：

- secured
- permission-evaluator
- freeMarker
- groovy
- htmx
- jte
- mustache
- thymeleaf
- vaadin

### 06-error-handling

异常处理：

- error-handling

### 07-token-authentication

Token 认证：

- jwt-01～03

### 08-special-authentication

特殊认证：

- one-time-token
- one-time-token-custom
- one-time-token-extra-fields
- one-time-token-jdbc
- one-time-token-rest
- mfa-formLogin-ott、mfa-formLogin-ott-authorization-manager
- mfa-formLogin-webauthn
- mfa-formLogin-email
- mfa-formLogin-totp
- mfa-formLogin-backupCode
- mfa-pluggable
- mfa-formLogin-oauth2
- mfa-formLogin-x509
- mfa-webauthn-x509
- webauthn

Reactive Security 示例暂不包含在当前项目中。

## 环境要求

- Java 25
- Maven
- Spring Boot 4.1.1

部分示例使用 MySQL，需要提前启动对应的数据库服务。

## 运行示例

进入目标项目目录后执行：

```bash
cd 01-authentication/basic-login
mvn spring-boot:run
```

也可以直接运行指定项目：

```bash
mvn -f 01-authentication/basic-login/pom.xml spring-boot:run
```

## 测试

运行单个项目测试：

```bash
mvn -f 01-authentication/basic-login/pom.xml test
```

使用 Java 25 运行测试：

```bash
mvn -f 01-authentication/basic-login/pom.xml test \
  -Dmaven.compiler.release=25
```

CI 会自动检查仓库中的各个 Maven 项目。依赖 MySQL 的样例在测试时改用内存 H2，不需要本机或 CI 先起数据库。

## 学习建议

建议按照以下顺序学习：

1. 基础认证与用户信息
2. Form Login、HTTP Basic、CSRF 和 CORS
3. Session、Remember-Me 与 Logout
4. AuthenticationProvider、AuthenticationManager 和 Filter
5. Endpoint Authorization 与 Method Security
6. 异常处理与安全测试
7. JWT 与其他 Token 认证
8. One-Time Token、MFA 和 WebAuthn

## 参考资料

- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [Spring Boot 官方文档](https://docs.spring.io/spring-boot/index.html)
- [参考示例项目](https://github.com/zhijunio/spring-security6-samples)

## 贡献

欢迎提交 Issue 和 Pull Request。

每个功能应尽量保持为独立项目，并遵循现有目录结构、包名和代码风格。

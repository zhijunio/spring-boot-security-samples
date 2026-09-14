# async

演示异步任务中传播 `SecurityContext` 的几种实现方式。所有端点都需要登录，测试用户为
`user / password`。

## 实现方式

| 端点 | 实现 | 适用场景 |
| --- | --- | --- |
| `/async-user` | `DelegatingSecurityContextAsyncTaskExecutor` | Spring `AsyncTaskExecutor` 或 `@Async` 使用的统一线程池包装 |
| `/async-user/runnable` | `DelegatingSecurityContextRunnable` | 手动提交单个 `Runnable` 任务 |
| `/async-user/callable` | `DelegatingSecurityContextCallable` | 手动包装带返回值的 `Callable` 任务 |
| `/async-user/executor-service` | `DelegatingSecurityContextExecutorService` | 包装标准 `ExecutorService`，支持 `submit` 等操作 |
| `/async-user/task-decorator` | Spring `TaskDecorator` | 在线程池配置层统一包装所有提交的任务 |
| `/async-user/explicit-context` | 显式传递 `SecurityContext` | 业务代码需要明确控制上下文来源和生命周期 |

### `DelegatingSecurityContextAsyncTaskExecutor`

将任务提交给委托执行器时自动捕获当前线程的安全上下文，并在工作线程执行任务前设置它。任务结束后恢复工作线程原来的上下文。适合 Spring 应用中的默认异步执行器，业务代码不需要感知上下文包装。

### `DelegatingSecurityContextRunnable` 和 `Callable`

这两个类分别包装无返回值和有返回值的任务。它们适合只需要在少数位置传播上下文的场景；调用方可以传入明确的 `SecurityContext`，因此比依赖隐式线程本地状态更容易控制。

### `DelegatingSecurityContextExecutorService`

它包装标准 `ExecutorService`，对提交的任务统一执行安全上下文委托，适合已有线程池 API 使用 `submit`、`invokeAll` 等方法的代码。

### `TaskDecorator`

`TaskDecorator` 是 Spring 线程池的通用任务装饰扩展点。示例在任务提交时复制认证信息，在任务执行前设置上下文，并在 `finally` 中清理。它不仅适用于 Security，也可以用同样方式传播 trace、租户等线程上下文。

### 显式传递

显式传递方式直接把 `SecurityContext` 作为任务执行所需的数据传入，再由工作线程设置并清理。它不依赖特定执行器，边界最清晰，但需要业务代码承担更多样板代码。

## 注意事项

- `SecurityContextHolder` 默认基于 `ThreadLocal`，线程池不会自动继承请求线程的上下文。
- 不建议在线程池中使用 `MODE_INHERITABLETHREADLOCAL`：线程复用可能导致用户身份泄漏或串用。
- `CompletableFuture`、`parallelStream` 和普通 `ExecutorService` 默认也不会自动传播安全上下文。
- 任务结束后必须清理上下文；本示例的委托类和 `TaskDecorator` 都遵循这一点。

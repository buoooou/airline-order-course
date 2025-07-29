### 2025Q3 练习

- For develop and local test

1. create environment file
   path:

   `/backend/env.sh`

   content:

```shell
export DB_HOST=xxx.xxx.xxx.xxx
export DB_PORT=3306
export DB_NAME=database_name
export DB_USER=user_of_db
export DB_PASS=password_of_db
export JWT_SECRET=jwt_secret
export JWT_EXPIRATION_MS=jwt_expiration_ms
```

2. build

`./backend/build.sh`

3. test

`./backend/test.sh`

4. run

`./backend/run.sh`

5. URL of swagger:

http://localhost:8080/swagger-ui.html

- Day1 作业

1. 已更新 TestController 作为练习, 以及练习中使用到的 service, repo 等相关代码。

   已更新 OrderActionController, OrderController 作为练习, 以及练习中使用到的 service, repo 等相关代码。

2. 思考状态机的非人工扭转方法：

对于 `PENDING_PAYMENT` 并 `超时未支付` 的 order 的状态自动变更 (to `CANCELLED`)，创建了延时 schedule(`com.postion.airlineorderbackend.component.ScheduledTask`)进行自动处理。

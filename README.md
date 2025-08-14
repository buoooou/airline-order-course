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

- Day2 作业

1. 添加了 JWT 权限控制 以及 `LoginController` 相关代码。

2. 使用 `ShedLock` 控制 Scheduled Task。

   在 `airline-order-course/backend/src/main/resources/sql/init_xyl.sql` 中添加 ShedLock 用数据表的 DDL。

3. 使用 Scheduled Task 完善状态机控制。

   增加了 `PAID` -> `TICKETING_INPROGRESS` 的状态流转控制。

   增加了 `TICKETING_INPROGRESS` -> `TICKETTED/TICKET_FAILED` 的状态流转控制。

4. 完善异常处理。

   将 Service 中的 null 返回修改为抛出具体异常，在 Controller 中进行异常处理。

5. 使用 `MapperStruct` 实现 Mapper。

6. 对于不为空且后期无需赋值的依赖项，将注入方式从 `@Autowired` 修改为 `@RequiredArgsConstructor` 以提高可测试性和线程安全性。

- Day3 作业

1. 使用 Angular 完成 frontend

2. 使用 GitHubs Actions 实现 CI/CD, 自动部署到 AWS EC2

   URL: http://3.22.168.207:8080

   实验用用户名: admin  密码: password

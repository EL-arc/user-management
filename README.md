# 用户管理 API

基于 Java 17、Spring Boot 4.1.1 的基础用户管理服务，包含新增、列表查询、按 ID 查询、修改姓名、删除，以及统一响应和参数校验。

## 运行与测试

需要 **JDK 17 或更新版本**，首次运行需要联网下载 Maven 和项目依赖，无需单独安装 Maven。

```bash
# Linux / macOS
bash mvnw spring-boot:run

# 运行测试并生成可执行 JAR
bash mvnw clean verify
java -jar target/user-management-0.0.1-SNAPSHOT.jar
```

Windows 使用 `mvnw.cmd spring-boot:run` 和 `mvnw.cmd clean verify`。

默认地址为 `http://localhost:8081`；通过 `SERVER_PORT` 环境变量覆盖端口。

## 接口

| 方法 | 路径 | 请求 JSON | 成功状态 |
| --- | --- | --- | --- |
| GET | `/users` | 无 | 200，返回用户数组 |
| GET | `/users/{id}` | 无 | 200，返回用户 |
| POST | `/users` | `{"id":1,"name":"张三"}` | 201，返回新增用户和 Location 头 |
| PUT | `/users/{id}` | `{"name":"李四"}` | 200，返回修改后的用户 |
| DELETE | `/users/{id}` | 无 | 200，data 为 null |

`id` 由调用方指定，范围为 1–2147483647，且必须唯一。`name` 必填，不能全部为空白，最多 100 个 Java 字符（UTF-16 代码单元），保存时去除首尾空白。更新只修改姓名，ID 以路径为准。列表按新增顺序返回，没有用户时返回空数组。

所有响应使用相同结构，`code` 与 HTTP 状态码一致：

```json
{"code":200,"message":"success","data":{"id":1,"name":"张三"}}
```

错误状态：参数非法、缺少请求体或 JSON 格式错误为 **400**，用户/接口不存在为 **404**，请求方法不支持为 **405**，ID 重复为 **409**，请求体媒体类型不支持为 **415**。错误响应示例：

```json
{"code":404,"message":"用户不存在","data":null}
```

## 调用示例

```bash
curl -i -X POST http://localhost:8081/users \
  -H 'Content-Type: application/json' -d '{"id":1,"name":"张三"}'
curl http://localhost:8081/users
curl http://localhost:8081/users/1
curl -X PUT http://localhost:8081/users/1 \
  -H 'Content-Type: application/json' -d '{"name":"李四"}'
curl -X DELETE http://localhost:8081/users/1
```

## 存储与测试范围

保持原项目的**内存存储**设计：启动时用户列表为空，数据仅保存在当前进程内，重启后清空。存储操作支持并发调用，并通过副本隔离外部修改。当前项目没有数据库、登录认证或权限管理。

自动测试覆盖完整 HTTP 增删改查流程、重复 ID、用户不存在、非法参数、错误请求体、姓名长度边界、存储副本隔离及并发重复新增。GitHub Actions 在推送和 Pull Request 时执行 `clean verify`。

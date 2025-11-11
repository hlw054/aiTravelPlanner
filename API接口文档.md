# AI旅行规划助手 - API接口文档

## 概述
本文档提供了AI旅行规划助手后端系统的用户相关API接口说明，供前端开发人员集成使用。

## 接口基础信息
- 基础URL: `http://localhost:8081/api`
- 认证方式: 基于Token的认证（Sa-Token框架）
- 响应格式: JSON

## 通用响应结构

所有API接口返回统一的响应结构：

```json
{
  "code": "状态码",
  "msg": "响应消息",
  "data": "响应数据（根据接口不同而变化）"
}
```

### 状态码说明

| 状态码 | 描述 | 常见场景 |
| :--- | :--- | :--- |
| 0 | 成功 | 接口调用成功 |
| 400 | 业务错误 | 参数错误、用户名已存在、邮箱已存在等 |
| 401 | 认证失败 | 未登录、token无效、token过期、用户名或密码错误 |
| 404 | 资源不存在 | 请求的接口路径不存在 |
| 500 | 服务器内部错误 | 系统异常、空指针异常等 |

### 异常处理说明

系统已实现全局异常处理，所有异常将以统一的JSON格式返回，包含相应的错误码和错误消息。

## 用户管理接口

### 1. 用户注册接口

#### 接口说明
用户注册新账号

#### 请求信息
- **URL**: `/users/register`
- **方法**: `POST`
- **Content-Type**: `application/json`

#### 请求参数

| 参数名 | 类型 | 必选 | 描述 |
| :--- | :--- | :--- | :--- |
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| email | String | 否 | 邮箱地址（可选） |
| phone | String | 否 | 手机号码（可选） |

#### 请求示例

```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",  // 可选
  "phone": "13800138000"       // 可选
}
```

不包含可选字段的示例：
```json
{
  "username": "testuser",
  "password": "123456"
}
```

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": "注册成功"
}
```

##### 失败响应

##### 用户名已存在
```json
{
  "code": 400,
  "msg": "用户名已存在",
  "data": null
}
```

##### 邮箱已存在
```json
{
  "code": 400,
  "msg": "邮箱已存在",
  "data": null
}
```

##### 参数错误
```json
{
  "code": 400,
  "msg": "参数错误：用户名和密码不能为空",
  "data": null
}
```

### 2. 用户登录接口

#### 接口说明
用户登录获取访问令牌

#### 请求信息
- **URL**: `/users/login`
- **方法**: `POST`
- **Content-Type**: `application/json`

#### 请求参数

| 参数名 | 类型 | 必选 | 描述 |
| :--- | :--- | :--- | :--- |
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

#### 请求示例

```json
{
  "username": "testuser",
  "password": "123456"
}
```

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "phone": "13800138000",
      "createdAt": "2023-06-01T10:00:00",
      "updatedAt": "2023-06-01T10:00:00"
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
  }
}
```

##### 失败响应

##### 用户名或密码错误
```json
{
  "code": 401,
  "msg": "用户名或密码错误",
  "data": null
}
```

##### 系统内部错误
```json
{
  "code": 500,
  "msg": "系统内部错误，请联系管理员",
  "data": null
}
```

### 3. 用户登出接口

#### 接口说明
用户退出登录，清除会话

#### 请求信息
- **URL**: `/users/logout`
- **方法**: `POST`
- **Headers**: `Authorization: Bearer {token}`（需携带登录时获取的token）

#### 请求参数
无

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

##### 失败响应

##### 未登录或token无效
```json
{
  "code": 401,
  "msg": "请先登录",
  "data": null
}
```

##### token已过期
```json
{
  "code": 401,
  "msg": "认证失败，请重新登录",
  "data": null
}
```

### 4. 获取当前用户信息接口

#### 接口说明
获取当前登录用户的详细信息

#### 请求信息
- **URL**: `/users/current`
- **方法**: `GET`
- **Headers**: `Authorization: Bearer {token}`（需携带登录时获取的token）

#### 请求参数
无

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "createdAt": "2023-06-01T10:00:00",
    "updatedAt": "2023-06-01T10:00:00"
  }
}
```

##### 失败响应

##### 未登录或token无效
```json
{
  "code": 401,
  "msg": "请先登录",
  "data": null
}
```

##### 用户不存在
```json
{
  "code": 400,
  "msg": "用户不存在",
  "data": null
}
```

##### 系统内部错误
```json
{
  "code": 500,
  "msg": "系统内部错误，请联系管理员",
  "data": null
}
```

### 5. 测试权限访问接口

#### 接口说明
用于测试权限访问控制的简单接口，需要有效的token才能访问

#### 请求信息
- **URL**: `/hello`
- **方法**: `GET`
- **Headers**: `Authorization: Bearer {token}`（需携带登录时获取的token）

#### 请求参数
无

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": "Hello! 您已成功访问受保护的接口"
}
```

##### 失败响应

##### 未登录或token无效
```json
{
  "code": 401,
  "msg": "请先登录",
  "data": null
}
```

##### token已过期
```json
{
  "code": 401,
  "msg": "认证失败，请重新登录",
  "data": null
}

### 6. AI生成行程计划接口

#### 接口说明
调用阿里云百炼模型服务，根据用户的旅游要求生成行程计划，并保存到数据库

#### 请求信息
- **URL**: `/api/ai/generate-travel-plan`
- **方法**: `POST`
- **Headers**: `Authorization: Bearer {token}`（需携带登录时获取的token）
- **Content-Type**: `application/json`

#### 请求参数

请求体直接传递字符串，包含用户的旅游要求。

#### 请求示例

```
上海三日游，预算3000元，两人，包含迪士尼乐园和外滩，需要住宿推荐
```

**注意**：请求体应该是纯文本字符串，不需要JSON格式包装。

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "travelPlanId": 1,
    "totalDays": 3,
    "totalBudget": 3000.00,
    "totalPeople": 2,
    "schedulesCount": 10
  }
}
```

##### 失败响应

##### 用户未登录
```json
{
  "code": 401,
  "msg": "用户未登录",
  "data": null
}
```

##### 参数错误
```json
{
  "code": 400,
  "msg": "旅游要求不能为空",
  "data": null
}
```

##### AI生成失败
```json
{
  "code": 500,
  "msg": "生成行程计划失败：无法连接到AI服务",
  "data": null
}
```

### 7. AI API连接测试接口

#### 接口说明
测试AI行程计划API的连接性

#### 请求信息
- **URL**: `/api/ai/test`
- **方法**: `GET`
- **Headers**: `Authorization: Bearer {token}`（需携带登录时获取的token）

#### 请求参数
无

#### 响应信息

##### 成功响应

```json
{
  "code": 0,
  "msg": "success",
  "data": "AI行程计划API连接正常"
}
```

##### 失败响应

```json
{
  "code": 500,
  "msg": "API连接失败：连接超时",
  "data": null
}

### UserRegistrationRequest

```json
{
  "username": "String",  // 用户名
  "password": "String",  // 密码
  "email": "String",     // 邮箱（可选）
  "phone": "String"      // 手机号（可选）
}
```

### UserLoginRequest

```json
{
  "username": "String",  // 用户名
  "password": "String"   // 密码
}
```

### UserVO

```json
{
  "id": 1,                // 用户ID
  "username": "String",  // 用户名
  "email": "String",     // 邮箱
  "phone": "String",     // 手机号
  "createdAt": "2023-06-01T10:00:00",  // 创建时间
  "updatedAt": "2023-06-01T10:00:00"   // 更新时间
}
```

### LoginResponse

```json
{
  "user": { /* UserVO对象 */ },  // 用户信息
  "token": "String"             // 访问令牌
}
```

## 使用说明

1. 前端在调用需要认证的接口时，需要在请求头中携带Token：
   ```
   Authorization: Bearer {token}
   ```

2. 登录成功后，将返回的token存储在本地（如localStorage或sessionStorage）

3. 当token过期或无效时，服务器会返回401错误，前端应跳转到登录页面

4. 对于接口返回的错误信息，前端应友好地展示给用户
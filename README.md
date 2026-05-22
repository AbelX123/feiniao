# 飞鸟 Feiniao

品牌方与视频创作者撮合平台的后端服务，帮助品牌方高效发现并对接合适的视频创作者，支持在线下单与支付宝付款，并集成 AI 智能推荐能力。

> 技术栈选用了 Java 21 + Spring Boot 3 + Spring AI，含 MCP（Model Context Protocol）协议落地实践。

---

## 功能概览

- **创作者档案管理**：多维度标签（平台、类型、擅长品类、国家、年龄段），支持条件筛选与分页
- **品牌方工作台**：收藏创作者、查看案例作品、发起合作订单
- **在线支付**：支付宝电脑网站支付，含异步回调验签与订单状态同步
- **AI 智能推荐**：基于 DeepSeek + MCP 协议的 WebSocket 多轮对话，AI 可调用内部工具查询并推荐合适创作者
- **对象存储**：创作者头像、案例视频/封面上传至 MinIO，预签名 URL 自动刷新
- **短信验证码登录**：阿里云短信服务，支持手机号快捷登录

---

## 系统架构

```
                        ┌─────────────────────────────┐
                        │          Nginx 网关           │
                        │    feiniao.video (port 80)    │
                        └──────────────┬──────────────┘
                                       │ 路由分发
          ┌──────────┬─────────────────┼──────────────┬──────────────┐
          ▼          ▼                 ▼              ▼              ▼
   ┌────────────┐ ┌────────┐   ┌────────────┐ ┌──────────┐ ┌─────────────┐
   │   users    │ │ dicts  │   │   orders   │ │ payments │ │   recommend │
   │  :8080     │ │ :8081  │   │   :8082    │ │  :8085   │ │   :8083     │
   └────────────┘ └────────┘   └────────────┘ └──────────┘ └──────┬──────┘
        │                                                          │ MCP Client
        │                                                   ┌──────▼──────┐
        │                                                   │    mcp      │
        │                                                   │   :8084     │
        └───────────────────────────────────────────────────┤ (MCP Server)│
                                                            └─────────────┘
                                       │
              ┌────────────────────────┼──────────────────────┐
              ▼                        ▼                       ▼
         MySQL 8.0                  Redis                   MinIO
```

### 服务说明

| 服务 | 端口 | 职责 |
|------|------|------|
| `feiniao-users` | 8080 | 用户注册/登录（密码+短信）、JWT 鉴权、品牌方/创作者档案、案例上传、收藏、菜单 |
| `feiniao-dicts` | 8081 | 平台、模特类型、标签、国家、擅长品类、年龄段等字典数据 |
| `feiniao-orders` | 8082 | 订单创建、查询、取消 |
| `feiniao-payments` | 8085 | 支付宝电脑网站支付、异步 notify 回调验签 |
| `feiniao-recommendation` | 8083 | WebSocket 多轮对话 + DeepSeek 流式推理 + MCP Client 工具调用 |
| `feiniao-mcp` | 8084 | MCP Server，暴露「创作者筛选」工具供 AI 调用 |

---

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 / 运行时 | Java 21 |
| 框架 | Spring Boot 3.3.7、Spring WebFlux（WebSocket/MCP） |
| ORM | MyBatis-Plus 3.5.6 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis（验证码、Token 黑名单） |
| 安全 | Spring Security + JJWT 0.12.6 |
| 对象存储 | MinIO 8.5 |
| AI 推理 | Spring AI 1.0.0 对接 DeepSeek（OpenAI 协议兼容） |
| AI 工具协议 | MCP（Model Context Protocol）Client + Server |
| 支付 | 支付宝开放平台 SDK 4.40 |
| 短信 | 阿里云 dysmsapi |
| 部署 | Docker + Docker Compose + Nginx |
| ID 生成 | 雪花算法 |

---

## 快速开始

### 前置依赖

- Docker & Docker Compose
- MySQL 8.0（建库 `feiniao`，导入 `init.sql`）
- Redis
- MinIO（建 Bucket：`avatars`、`cases`）
- DeepSeek API Key（[申请地址](https://platform.deepseek.com/)）
- 阿里云短信服务 AccessKey（可选，开启 fallback 后本地调试可跳过）
- 支付宝开放平台应用（可选，仅支付功能需要）

### 1. 初始化数据库

```bash
mysql -u root -p feiniao < init.sql
```

### 2. 配置环境变量

```bash
cp .env.example .env
# 按实际情况修改 .env 中的配置项
```

主要配置项说明见 `.env.example`。

### 3. 启动所有服务

```bash
docker compose up -d --build
```

### 4. 验证服务

```bash
# 用户服务健康检查
curl http://localhost:8080/actuator/health

# 字典接口
curl http://localhost:8081/api/dicts/platforms
```

---

## 环境变量说明

复制 `.env.example` 为 `.env` 并填写以下配置：

```bash
# 数据库
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=feiniao
MYSQL_USERNAME=your_username
MYSQL_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_password

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=your_access_key
MINIO_SECRET_KEY=your_secret_key

# DeepSeek（AI 推荐功能必填）
DEEPSEEK_API_KEY=sk-xxxx

# 阿里云短信（手机号登录必填，可设 ALIYUN_SMS_FALLBACK_ENABLED=true 跳过）
ALIYUN_SMS_ACCESS_KEY_ID=xxxx
ALIYUN_SMS_ACCESS_KEY_SECRET=xxxx
ALIYUN_SMS_SIGN_NAME=your_sign
ALIYUN_SMS_TEMPLATE_CODE=SMS_xxxx

# 支付宝（支付功能必填）
ALIPAY_APP_ID=xxxx
ALIPAY_MERCHANT_PRIVATE_KEY=xxxx
ALIPAY_PUBLIC_KEY=xxxx
ALIPAY_RETURN_URL=http://your-domain/orders
ALIPAY_NOTIFY_URL=http://your-domain/api/alipay/notify
```

---

## API 概览

### 用户与认证（:8080）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/users/sign-up` | 注册 |
| POST | `/api/users/sign-in` | 密码登录 |
| POST | `/api/users/sign-in-by-phone` | 手机验证码登录 |
| POST | `/api/users/refresh-token` | 刷新 Token |
| POST | `/api/users/sign-out` | 登出 |

### 创作者（:8080）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users/creators` | 条件筛选创作者列表 |
| PUT | `/api/users/creators` | 更新创作者档案 |
| GET | `/api/creators/cases` | 获取案例列表 |
| POST | `/api/creators/cases` | 上传案例（multipart） |

### 订单（:8082）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 查询订单列表 |
| DELETE | `/api/orders/{orderId}` | 取消订单 |

### 支付（:8085）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/alipay/trade/page/pay` | 发起支付宝电脑网站支付 |
| POST | `/api/alipay/notify` | 支付宝异步回调（验签） |

### AI 推荐（:8083）

| 协议 | 路径 | 说明 |
|------|------|------|
| WebSocket | `/api/chat` | 多轮对话，AI 可调用 MCP 工具查询并推荐创作者 |

---

## MCP 工具设计

`feiniao-recommendation` 通过 MCP 协议与 `feiniao-mcp` 通信，AI 在对话中可调用以下工具：

| 工具名 | 说明 |
|--------|------|
| `creator_select` | 根据平台、类型、标签、国家等条件筛选创作者 |
| `weather` | 示例工具（天气查询） |

**调用链路**：用户消息 → DeepSeek 推理 → 工具调用决策 → MCP Client → MCP Server HTTP → 内部 API → 结果返回 AI → 流式回复用户

---

## 项目背景

本项目源于电商选品与内容营销时遇到的真实痛点：品牌方手动在各平台搜索视频创作者效率极低，洽谈、下单、对账流程完全依赖人工。飞鸟的目标是将这个撮合流程产品化，并通过 AI 对话降低品牌方的选人门槛。

---

## License

MIT

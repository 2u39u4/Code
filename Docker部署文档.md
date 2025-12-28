# Docker 数据库和缓存服务部署文档

## 📋 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [服务说明](#服务说明)
- [配置说明](#配置说明)
- [常用命令](#常用命令)
- [本地开发配置](#本地开发配置)
- [故障排查](#故障排查)

---

## 🔧 环境要求

- **Docker**: 版本 20.10+
- **Docker Compose**: 版本 1.29+
- **系统内存**: 建议 2GB+
- **磁盘空间**: 建议 5GB+

### 检查环境

```bash
# 检查Docker版本
docker --version

# 检查Docker Compose版本
docker-compose --version
```

---

## 🚀 快速开始

### 1. 进入Code目录

```bash
cd Code
```

### 2. 配置环境变量（可选）

创建 `.env` 文件（可选，不创建会使用默认值）：

```bash
# MySQL配置
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=chatbot
MYSQL_PORT=3306

# Redis配置
REDIS_PASSWORD=
REDIS_PORT=6379
```

### 3. 一键启动服务

```bash
# 启动MySQL和Redis服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 4. 验证服务

```bash
# 检查MySQL
docker-compose exec mysql mysql -uroot -proot -e "SELECT 1"

# 检查Redis
docker-compose exec redis redis-cli ping
```

### 5. 初始化数据库

数据库会在首次启动时自动执行 `test.sql` 初始化脚本。

如果需要手动初始化：

```bash
# 进入MySQL容器
docker-compose exec mysql bash

# 登录MySQL
mysql -uroot -proot chatbot

# 执行SQL脚本
source /docker-entrypoint-initdb.d/init.sql
```

---

## 🏗️ 服务说明

### 服务架构

```
┌─────────────┐
│  Frontend   │  手动启动 (npm run serve)
│   (Vue)     │
└──────┬──────┘
       │
       │ HTTP/WebSocket
       │
┌──────┴──────┐
│  Backend    │  手动启动 (IDEA)
│  (Java)     │
└──────┬──────┘
       │
   ┌───┴───┬───────────┐
   │       │           │
┌──┴──┐ ┌─┴──┐  ┌────┴───┐
│MySQL│ │Redis│  │Python  │
│3306 │ │6379 │  │:8000   │
└─────┘ └─────┘  └────────┘
   ↑       ↑
   └───────┴─── Docker服务
```

### 服务列表

| 服务 | 容器名 | 端口 | 说明 |
|------|--------|------|------|
| MySQL | chatbot-mysql | 3306 | 数据库服务 |
| Redis | chatbot-redis | 6379 | 缓存服务 |

**注意**：后端和前端服务需要手动启动，不在Docker中运行。

---

## ⚙️ 配置说明

### 环境变量配置（.env文件，可选）

```bash
# MySQL配置
MYSQL_ROOT_PASSWORD=root          # MySQL root密码（默认：root）
MYSQL_DATABASE=chatbot            # 数据库名（默认：chatbot）
MYSQL_PORT=3306                   # MySQL端口（默认：3306）

# Redis配置
REDIS_PASSWORD=                   # Redis密码（留空表示无密码）
REDIS_PORT=6379                   # Redis端口（默认：6379）
```

如果不创建 `.env` 文件，将使用上述默认值。

### 数据库连接配置

**后端 application.yml 配置：**

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/chatbot?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: root
  redis:
    host: 127.0.0.1
    port: 6379
    password: # 如果设置了Redis密码，在这里填写
    database: 3
```

---

## 📝 常用命令

### 启动和停止

```bash
# 启动所有服务（后台运行）
docker-compose up -d

# 启动并查看日志
docker-compose up

# 停止所有服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器、网络、卷（⚠️ 会删除数据）
docker-compose down -v
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f mysql
docker-compose logs -f redis
```

### 服务管理

```bash
# 重启服务
docker-compose restart mysql
docker-compose restart redis

# 查看服务状态
docker-compose ps

# 查看服务资源使用
docker stats
```

### 数据库操作

```bash
# 进入MySQL容器
docker-compose exec mysql bash

# 连接MySQL
docker-compose exec mysql mysql -uroot -proot chatbot

# 备份数据库
docker-compose exec mysql mysqldump -uroot -proot chatbot > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -uroot -proot chatbot < backup.sql

# 查看数据库列表
docker-compose exec mysql mysql -uroot -proot -e "SHOW DATABASES;"

# 查看表
docker-compose exec mysql mysql -uroot -proot chatbot -e "SHOW TABLES;"
```

### Redis操作

```bash
# 进入Redis容器
docker-compose exec redis sh

# 连接Redis CLI
docker-compose exec redis redis-cli

# 如果设置了密码
docker-compose exec redis redis-cli -a ${REDIS_PASSWORD}

# 查看所有键
docker-compose exec redis redis-cli KEYS "*"

# 查看特定键
docker-compose exec redis redis-cli GET "chat:1"

# 清空所有数据（⚠️ 谨慎使用）
docker-compose exec redis redis-cli FLUSHALL
```

---

## 💻 本地开发配置

### 后端启动（IDEA）

1. **确保Docker服务已启动**
   ```bash
   cd Code
   docker-compose ps
   ```

2. **配置数据库连接**
   - 在 `backend-development-1.0-master/llm-framework/src/main/resources/application.yml` 中确保数据库连接配置正确
   - 主机：`127.0.0.1` 或 `localhost`
   - 端口：`3306`
   - 用户名：`root`
   - 密码：`root`（或你在 `.env` 中设置的密码）

3. **配置Redis连接**
   - 主机：`127.0.0.1` 或 `localhost`
   - 端口：`6379`
   - 密码：如果设置了密码，在配置文件中填写

4. **在IDEA中启动**
   - 打开 `backend-development-1.0-master` 项目
   - 运行 `LlmApplication.java`
   - 检查控制台日志，确认连接成功

### 前端启动（npm）

1. **进入前端目录**（从项目根目录）
   ```bash
   cd ../sztu-chatbot-master
   ```

2. **安装依赖（首次）**
   ```bash
   npm install
   ```

3. **启动开发服务器**
   ```bash
   npm run serve
   ```

4. **访问应用**
   - 前端：http://localhost:8080（或vue.config.js中配置的端口）
   - 后端API：http://localhost:8080/api

### 配置前端代理

确保 `vue.config.js` 中的代理配置正确：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // 后端地址
    ws: false,
    changeOrigin: true,
    pathRewrite: {
      '^/api': ''
    }
  },
  '/socket': {
    target: 'ws://localhost:8080',  // WebSocket地址
    changeOrigin: true,
    pathRewrite: {
      '^/socket': ''
    },
    ws: true
  }
}
```

---

## 🔍 故障排查

### 1. 服务无法启动

**检查服务状态：**
```bash
docker-compose ps
```

**查看错误日志：**
```bash
docker-compose logs mysql
docker-compose logs redis
```

**常见问题：**
- **端口被占用**：修改 `.env` 文件中的端口配置，或停止占用端口的服务
- **内存不足**：检查系统内存，建议至少2GB
- **镜像拉取失败**：检查网络连接或使用国内镜像源

### 2. 数据库连接失败

**检查MySQL是否启动：**
```bash
docker-compose ps mysql
```

**检查数据库连接：**
```bash
docker-compose exec mysql mysql -uroot -proot -e "SELECT 1"
```

**查看MySQL日志：**
```bash
docker-compose logs mysql
```

**常见问题：**
- 连接地址错误：确保使用 `127.0.0.1` 或 `localhost`
- 密码错误：检查 `.env` 文件中的 `MYSQL_ROOT_PASSWORD`
- 端口被占用：检查3306端口是否被其他服务占用

### 3. Redis连接失败

**检查Redis是否启动：**
```bash
docker-compose ps redis
```

**测试Redis连接：**
```bash
docker-compose exec redis redis-cli ping
```

**如果设置了密码：**
```bash
docker-compose exec redis redis-cli -a your_password ping
```

**查看Redis日志：**
```bash
docker-compose logs redis
```

### 4. 数据库初始化失败

**检查SQL文件：**
```bash
# 查看SQL文件是否存在
ls -la test.sql

# 检查SQL文件语法
docker-compose exec mysql mysql -uroot -proot -e "SOURCE /docker-entrypoint-initdb.d/init.sql"
```

**手动执行SQL：**
```bash
docker-compose exec -T mysql mysql -uroot -proot chatbot < test.sql
```

### 5. 数据丢失问题

**检查数据卷：**
```bash
# 查看数据卷
docker volume ls

# 查看数据卷详情
docker volume inspect code_mysql_data
docker volume inspect code_redis_data
```

**备份数据：**
```bash
# 备份MySQL
docker-compose exec mysql mysqldump -uroot -proot chatbot > backup_$(date +%Y%m%d).sql

# 备份Redis
docker-compose exec redis redis-cli SAVE
docker cp chatbot-redis:/data/dump.rdb ./redis_backup_$(date +%Y%m%d).rdb
```

---

## 🚀 生产环境建议

### 1. 安全配置

**修改默认密码：**
```bash
# .env文件中修改
MYSQL_ROOT_PASSWORD=your_strong_password
REDIS_PASSWORD=your_strong_password
```

**限制网络访问：**
- 生产环境建议不暴露端口到公网
- 使用内网IP或Docker网络通信

### 2. 数据持久化

数据已通过Docker Volume持久化：
- `mysql_data`: MySQL数据
- `redis_data`: Redis数据

**备份策略：**
- 定期备份MySQL数据
- 配置Redis持久化（已启用AOF）

### 3. 性能优化

**MySQL优化：**
- 根据数据量调整 `innodb_buffer_pool_size`
- 创建必要的索引

**Redis优化：**
- 根据内存使用情况调整 `maxmemory` 策略
- 配置合适的过期时间

---

## 📚 文件说明

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | Docker Compose编排文件（仅MySQL和Redis） |
| `.env` | 环境变量配置文件（可选） |
| `test.sql` | 数据库初始化脚本 |
| `Docker部署文档.md` | 本文档 |

---

## 🔗 相关链接

- [Docker官方文档](https://docs.docker.com/)
- [Docker Compose文档](https://docs.docker.com/compose/)
- [MySQL Docker镜像](https://hub.docker.com/_/mysql)
- [Redis Docker镜像](https://hub.docker.com/_/redis)

---

## ❓ 常见问题

**Q: 如何更新数据库结构？**
```bash
# 修改test.sql后，重新创建容器
docker-compose down -v
docker-compose up -d
```

**Q: 如何查看服务日志？**
```bash
docker-compose logs -f [service_name]
```

**Q: 如何进入容器内部？**
```bash
docker-compose exec mysql bash
docker-compose exec redis sh
```

**Q: 如何清理所有数据重新开始？**
```bash
# ⚠️ 警告：这会删除所有数据
docker-compose down -v
docker-compose up -d
```

**Q: 如何修改MySQL密码？**
```bash
# 1. 修改.env文件中的MYSQL_ROOT_PASSWORD
# 2. 重启服务
docker-compose down
docker-compose up -d
```

**Q: 后端连接不上数据库？**
- 检查Docker服务是否启动：`docker-compose ps`
- 检查连接地址是否为 `127.0.0.1:3306`
- 检查用户名密码是否正确
- 查看MySQL日志：`docker-compose logs mysql`

---

**最后更新**: 2024年
**维护者**: 开发团队


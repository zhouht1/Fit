# Fit 健身系统 — 部署指南

## 1. 环境要求

### 最低版本

| 软件 | 版本 | 用途 |
|------|------|------|
| Java JDK | 17+ | 后端运行环境 |
| Maven | 3.9+ | 后端构建 |
| Node.js | 20+ | 前端构建 |
| npm | 10+ | 前端依赖管理 |
| MySQL | 8.4 LTS | 数据库 |

### 推荐版本

| 软件 | 版本 |
|------|------|
| Java | 17.0.20 |
| Spring Boot | 3.5.16 |
| Maven | 3.9.16 |
| Node.js | 24.18.0 |
| npm | 11.16.0 |
| MySQL | 8.4.11 |

---

## 2. 环境准备

### 2.1 安装 Java 17 (macOS)

```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
```

### 2.2 安装 MySQL 8.4 (macOS)

```bash
brew install mysql@8.4
brew services start mysql@8.4
```

### 2.3 安装 Node.js (macOS)

```bash
brew install node
node -v
npm -v
```

### 2.4 安装 Maven (macOS)

```bash
brew install maven
mvn -version
```

### 2.5 验证环境

```bash
java -version    # 期望: 17.x
mvn -version     # 期望: 3.9+
node -v          # 期望: 20+
npm -v           # 期望: 10+
mysql --version  # 期望: 8.4.x
```

---

## 3. 数据库配置

### 3.1 启动 MySQL

```bash
# macOS
mysql.server start

# 或使用 Homebrew
brew services start mysql@8.4
```

### 3.2 创建数据库

```bash
mysql -u root -e "
CREATE DATABASE IF NOT EXISTS fit
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
"
```

### 3.3 验证数据库

```bash
mysql -u root -e "USE fit; SELECT DATABASE();"
# 期望输出: fit
```

### 3.4 数据库表结构

数据库表会在首次启动后端时自动创建（通过 `schema.sql` 和 `spring.sql.init.mode=always`）。

如果已有数据，修改 `application.yml`：

```yaml
spring:
  sql:
    init:
      mode: never  # 避免重复执行建表语句
```

---

## 4. 后端部署

### 4.1 配置环境变量

创建 `fit-backend/.env` 文件（或设置系统环境变量）：

```bash
# JWT 密钥（生产环境必须修改为强随机字符串）
export JWT_SECRET="your-production-secret-key-change-this"

# MySQL 配置（如果使用非默认配置）
export MYSQL_HOST="localhost"
export MYSQL_PORT="3306"
export MYSQL_DATABASE="fit"
export MYSQL_USERNAME="root"
export MYSQL_PASSWORD=""
```

### 4.2 修改 application.yml（生产环境）

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:fit}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: never  # 生产环境关闭自动建表

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24小时

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl  # 生产环境关闭 SQL 日志
```

### 4.3 构建

```bash
cd fit/fit-backend
mvn clean package -DskipTests
```

构建产物：`target/fit-backend-1.0.0.jar`

### 4.4 运行

```bash
# 开发模式
mvn spring-boot:run

# 生产模式
java -jar target/fit-backend-1.0.0.jar

# 指定端口
java -jar target/fit-backend-1.0.0.jar --server.port=8080

# 后台运行
nohup java -jar target/fit-backend-1.0.0.jar > app.log 2>&1 &
```

### 4.5 验证后端

```bash
# 健康检查
curl http://localhost:8080/api/auth/register \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@fit.com","password":"test123"}'

# 期望返回: {"code":200,"message":"success","data":{"token":"..."}}
```

---

## 5. 前端部署

### 5.1 安装依赖

```bash
cd fit/fit-frontend
npm install
```

### 5.2 配置 API 代理

`vite.config.ts` 中已配置开发代理：

```typescript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### 5.3 构建生产版本

```bash
cd fit/fit-frontend
npm run build
```

构建产物：`dist/` 目录

### 5.4 部署方式

#### 方式 A：Vite 开发服务器（开发环境）

```bash
npm run dev
# 访问 http://localhost:3000
```

#### 方式 B：Nginx 静态文件部署（生产环境）

```nginx
server {
    listen 80;
    server_name fit.example.com;

    root /var/www/fit/dist;
    index index.html;

    # 前端静态文件
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# 部署静态文件
cp -r dist/* /var/www/fit/dist/
nginx -s reload
```

#### 方式 C：Spring Boot 静态资源（一体部署）

将前端构建产物复制到后端静态资源目录：

```bash
cp -r fit-frontend/dist/* fit-backend/src/main/resources/static/
```

然后重新构建后端，前后端将运行在同一端口。

---

## 6. Docker 部署

### 6.1 Dockerfile（后端）

创建 `fit-backend/Dockerfile`：

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/fit-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.2 Dockerfile（前端）

创建 `fit-frontend/Dockerfile`：

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 6.3 docker-compose.yml

创建 `fit/docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: fit123
      MYSQL_DATABASE: fit
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./fit-backend
    environment:
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
      MYSQL_DATABASE: fit
      MYSQL_USERNAME: root
      MYSQL_PASSWORD: fit123
      JWT_SECRET: change-this-to-a-random-secret
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy

  frontend:
    build: ./fit-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

### 6.4 启动 Docker

```bash
docker-compose up -d
```

---

## 7. 生产环境检查清单

### 安全

- [ ] 修改 JWT Secret 为强随机字符串（`openssl rand -base64 64`）
- [ ] 配置 MySQL 用户密码（不要使用空密码）
- [ ] 关闭 MyBatis SQL 日志
- [ ] 配置 HTTPS（Nginx + Let's Encrypt）
- [ ] 配置防火墙（仅开放 80/443 端口）
- [ ] 关闭 `spring.sql.init.mode` 或设为 `never`

### 配置

- [ ] 修改 `application.yml` 中的 JWT 过期时间
- [ ] 配置 CORS 允许的域名（替换 `*` 为具体域名）
- [ ] 配置日志级别为 `INFO` 或 `WARN`
- [ ] 设置时区（`serverTimezone=Asia/Shanghai`）

### 数据库

- [ ] 定期备份数据库
- [ ] 配置数据库连接池参数
- [ ] 创建专用数据库用户（非 root）

### 监控

- [ ] 配置应用健康检查端点
- [ ] 设置日志收集
- [ ] 配置服务器监控（CPU/内存/磁盘）

---

## 8. 常见问题

### Q: MySQL 启动失败 - "Cannot downgrade"

```bash
# 清理旧数据并重新初始化
rm -rf /opt/homebrew/var/mysql/*
mysqld --initialize-insecure --user=$(whoami) --datadir=/opt/homebrew/var/mysql
mysql.server start
```

### Q: 端口被占用

```bash
# 查看占用端口的进程
lsof -i :8080
lsof -i :3000

# 杀死进程
kill -9 <PID>
```

### Q: Maven 编译失败

```bash
# 清理缓存
mvn clean
rm -rf ~/.m2/repository/com/fit

# 重新编译
mvn compile
```

### Q: 前端代理不生效

确保后端已启动：

```bash
# 先启动后端
cd fit-backend && mvn spring-boot:run

# 再启动前端
cd fit-frontend && npm run dev
```

---

## 9. 快速启动（开发环境）

```bash
# 1. 启动 MySQL
mysql.server start

# 2. 创建数据库
mysql -u root -e "CREATE DATABASE IF NOT EXISTS fit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 启动后端（终端 1）
cd fit/fit-backend
mvn spring-boot:run

# 4. 启动前端（终端 2）
cd fit/fit-frontend
npm install && npm run dev

# 5. 访问
# 前端: http://localhost:3000
# 后端: http://localhost:8080
```

---

## 10. 目录结构

```
fit/
├── fit-backend/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
├── fit-frontend/
│   ├── package.json
│   ├── vite.config.ts
│   ├── Dockerfile
│   └── src/
├── docs/
│   ├── PROJECT_SUMMARY.md
│   └── DEPLOYMENT_GUIDE.md
├── docker-compose.yml
├── README.md
└── .gitignore
```
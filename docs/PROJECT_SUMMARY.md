# Fit 健身系统 — 项目总结文档

## 1. 产品概述

**Fit** 是一款极简、高级、克制的个人健身记录与进步管理 Web App。

### 核心用户流程

```
Register → Login → Onboarding → Today → Workout Plan → Start Workout
→ 记录训练 → Finish Workout → History → Progress → 看到自己变强
```

### 核心价值

打开 Fit → 看到今天训练 → 开始训练 → 记录每组 → 完成训练 → 查看自己的进步

---

## 2. 技术栈

### Backend

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.5.16 |
| Spring Security | 6.5.x |
| MyBatis-Plus | 3.5.12 |
| JWT (jjwt) | 0.12.6 |
| MySQL | 8.4 LTS |
| Maven | 3.9+ |
| Lombok | latest |

### Frontend

| 技术 | 版本 |
|------|------|
| React | 19.2.8 |
| TypeScript | 6.0.2 |
| Vite | 8.2.2 |
| Tailwind CSS | 4.3.3 |
| Recharts | latest |

### Database

| 配置 | 值 |
|------|-----|
| 数据库 | fit |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |

---

## 3. 项目结构

```
fit/
├── fit-backend/                     # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/fit/
│       ├── FitApplication.java      # 启动类
│       ├── config/                  # 配置（Security, CORS, MyBatis-Plus）
│       ├── security/                # JWT 工具类与过滤器
│       ├── controller/              # REST 控制器
│       ├── service/                 # 业务接口
│       │   └── impl/                # 业务实现
│       ├── mapper/                  # MyBatis-Plus Mapper
│       ├── entity/                  # 数据库实体
│       ├── dto/                     # 请求 DTO
│       ├── vo/                      # 响应 VO
│       ├── common/                  # 统一响应 Result
│       └── exception/               # 全局异常处理
├── fit-frontend/                    # React 前端
│   ├── src/
│   │   ├── api/                     # API 调用模块
│   │   ├── components/              # 可复用组件
│   │   ├── hooks/                   # 自定义 Hooks
│   │   ├── App.tsx                  # 主应用
│   │   └── main.tsx                 # 入口
│   └── vite.config.ts
└── docs/                            # 文档
```

---

## 4. 数据库设计（14 张表）

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| user | 用户 | username, email, password(BCrypt) |
| profile | 个人资料 | name, age, height, weight, gender, fitness_goal, training_frequency, experience |
| body_measurement | 身体测量 | weight, body_fat, chest, waist, hip, arm, thigh |
| recovery | 恢复状态 | date, status, activity_type |
| exercise | 动作库 | name, muscle_group, equipment, description |
| workout_plan | 训练计划 | name, goal, training_days, estimated_duration |
| workout_plan_exercise | 计划动作 | plan_id, exercise_id, target_sets, target_reps, order_num |
| workout | 训练记录 | plan_id, name, start_time, end_time, duration, total_volume, total_sets, status |
| workout_set | 训练组 | workout_id, exercise_id, set_number, weight, reps, completed |
| personal_record | 个人记录 | exercise_id, record_type, value |
| meal | 饮食 | name, meal_type, calories, protein, carbs, fat |
| water_log | 饮水 | amount_ml |
| sleep_log | 睡眠 | sleep_time, wake_time, duration_minutes, quality |
| progress_photo | 进度照片 | photo_url, taken_at |

### 核心关系

```
User ──┬── Profile
       ├── BodyMeasurement
       ├── Recovery
       ├── WorkoutPlan ── WorkoutPlanExercise
       ├── Workout ── WorkoutSet
       ├── PersonalRecord
       ├── Meal
       ├── WaterLog
       ├── SleepLog
       └── ProgressPhoto
```

---

## 5. API 文档（24 个端点）

### 认证 (Phase 2)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 注册 |
| POST | /api/auth/login | 登录 |
| POST | /api/auth/logout | 登出 |
| GET | /api/auth/me | 当前用户 |

### 个人资料 (Phase 3)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/profile | 获取资料 |
| PUT | /api/profile | 更新资料 |

### 动作库 (Phase 4)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/exercises | 列表（keyword, muscleGroup） |
| GET | /api/exercises/{id} | 详情 |

### 训练计划 (Phase 5)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/workout-plans | 列表 |
| POST | /api/workout-plans | 创建 |
| GET | /api/workout-plans/{id} | 详情 |
| PUT | /api/workout-plans/{id} | 更新 |
| DELETE | /api/workout-plans/{id} | 删除 |

### 训练记录 (Phase 6)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/workouts | 开始训练 |
| GET | /api/workouts | 训练列表 |
| GET | /api/workouts/{id} | 训练详情 |
| POST | /api/workouts/{id}/sets | 添加组 |
| POST | /api/workouts/{id}/finish | 完成训练 |

### 身体测量 (Phase 9)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/body-measurements | 列表（period: 7d/30d/90d/all） |
| POST | /api/body-measurements | 新增测量 |

### 进度 (Phase 9)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/progress | 综合进度（weight/volume/duration） |

### 统计 (Phase 10)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/stats/personal-records | 个人记录 |
| GET | /api/stats/progressive-overload | 渐进超负荷 |
| GET | /api/stats/streak | 连续训练天数 |

### 今日 (Phase 11)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/today | 仪表盘聚合数据 |

### 统一响应格式

```json
// 成功
{ "code": 200, "message": "success", "data": {} }

// 错误
{ "code": 400, "message": "Invalid request", "data": null }
```

---

## 6. 安全设计

- BCrypt 密码加密
- JWT 无状态认证
- Spring Security 过滤器链
- 用户数据隔离（用户 A 绝不能访问用户 B 的数据）
- Bean Validation 输入校验
- 全局异常处理
- 敏感信息通过环境变量注入

---

## 7. 前端组件（16 个）

| 组件 | 说明 |
|------|------|
| TodayPage | 今日仪表盘 |
| WorkoutTimer | 训练计时器 |
| RestTimer | 组间休息计时器 |
| WorkoutComplete | 训练完成摘要 |
| WorkoutHistory | 训练历史列表 |
| WorkoutDetail | 训练详情（按动作分组） |
| BodyMeasurement | 身体测量（Metric Selector） |
| ProgressChart | 进度图表（Recharts） |
| PersonalRecords | 个人记录 |
| ProgressiveOverload | 渐进超负荷 |
| StreakBanner | 连续训练横幅 |
| BottomNav | 底部导航 |
| Toast | 通知组件 |
| useTimer | 秒表 Hook |
| useTheme | 主题 Hook |

### UI 特性

- Light / Dark / System 主题
- Mobile First 响应式布局
- Skeleton 骨架屏加载
- Toast 通知
- 按钮交互动画
- 底部导航栏（Today / Workout / Progress / Profile）

---

## 8. 设计系统

### Light Mode

```
背景: #F5F5F7
卡片: #FFFFFF
主文字: #1D1D1F
次文字: #86868B
强调: #007AFF
正向: #30D158
```

### Dark Mode

```
背景: #000000
卡片: #1C1C1E
主文字: #F5F5F7
次文字: #86868B
强调: #0A84FF
正向: #30D158
```

---

## 9. 训练容量公式

```
Volume = Weight × Reps

Total Volume = 所有 WorkoutSet Volume 之和

示例:
  Bench Press 60kg × 10 = 600
  Bench Press 60kg × 10 = 600
  Bench Press 62.5kg × 10 = 625
  Total = 1825 kg
```

---

## 10. 渐进超负荷规则

```
重量增加 → "New Best: +X% Weight"
次数增加 → "New Best: +N Rep"
持平 → "Suggested: Consider increasing weight by 2.5kg"
```

---

## 11. Streak 规则

- 今天或昨天有训练 → 计入连续
- 允许最多 1 天恢复日间隔
- Recovery Day 不中断连续记录

---

## 12. 测试覆盖

| 测试类 | 测试数 |
|--------|--------|
| FitApplicationTests | 1 |
| UserMapperTest | 1 |
| AuthControllerTest | 7 |
| ProfileControllerTest | 6 |
| ExerciseControllerTest | 6 |
| WorkoutPlanControllerTest | 8 |
| WorkoutControllerTest | 9 |
| ProgressControllerTest | 5 |
| StatsControllerTest | 4 |
| TodayControllerTest | 2 |
| FitAcceptanceTest | 17 |
| **总计** | **66** |

### 验收测试流程（17 步）

```
 1. Register
 2. Login
 3. Get current user
 4. Onboarding (set profile)
 5. Browse exercises (10 available)
 6. Generate workout plan (Push Day + 3 exercises)
 7. Today dashboard
 8. Start workout
 9. Record Set 1 (Bench Press 60kg × 10 = 600kg)
10. Record Set 2 (Bench Press 60kg × 10 = 600kg)
11. Record Set 3 (Bench Press 62.5kg × 10 = 625kg)
12. Finish workout (1825kg total)
13. Workout history
14. Body measurement
15. Progress data
16. Personal records
17. Streak
```

---

## 13. 如何运行

### 前置条件

- Java 17
- Maven 3.9+
- Node.js 20+
- MySQL 8.4

### 启动 MySQL

```bash
mysql.server start
mysql -u root -e "CREATE DATABASE IF NOT EXISTS fit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 启动后端

```bash
cd fit/fit-backend
mvn spring-boot:run
# 运行在 http://localhost:8080
```

### 启动前端

```bash
cd fit/fit-frontend
npm install
npm run dev
# 运行在 http://localhost:3000
```

### 运行测试

```bash
cd fit/fit-backend
mvn test
```

### 构建前端

```bash
cd fit/fit-frontend
npm run build
```

---

## 14. Git 提交历史

```
feat(phase-0): initialize project
feat(phase-1): setup database architecture
feat(phase-2): implement authentication
feat(phase-3): implement profile and onboarding
feat(phase-4): implement exercise library
feat(phase-5): implement workout plans
feat(phase-6): implement workout core
feat(phase-7): implement timers
feat(phase-8): implement workout complete and history
feat(phase-9): implement progress and body measurements
feat(phase-10): implement PR, progressive overload, and streak
feat(phase-11): implement today dashboard
feat(phase-12): polish UI
test(phase-13): final acceptance testing
```

---

## 15. 项目统计

| 指标 | 数值 |
|------|------|
| 数据库表 | 14 |
| 后端 Java 文件 | 73 |
| 前端组件 | 16 |
| API 端点 | 24 |
| 测试用例 | 66 |
| 测试通过率 | 100% |
| Git 提交 | 14 |
| 开发 Phase | 14 (0-13) |

---

## 16. MVP 范围

### P0 已完成

- ✅ 注册/登录/JWT
- ✅ Onboarding
- ✅ Profile
- ✅ Today 首页
- ✅ 动作库（10 个基础动作）
- ✅ 训练计划 CRUD
- ✅ 开始训练
- ✅ 训练计时器
- ✅ 组间休息计时器
- ✅ 组记录（Weight/Reps）
- ✅ 训练完成摘要
- ✅ 训练历史
- ✅ 进度（体重/容量/时长图表）
- ✅ 基本统计
- ✅ 个人记录
- ✅ Streak 连续天数
- ✅ Dark Mode
- ✅ 响应式 UI

### P1 暂未实现

- Progress Photo
- Nutrition / Meal / Water / Sleep
- Recovery
- Workout Templates
- Progressive Overload 复杂算法
- AI Coach
- Notifications

### P2 暂未实现

- Apple Health / Apple Watch
- AI 动作识别
- Social / Friends / Leaderboard
- Coach / Membership
- Food Database
- Smart Training Plan
# Spring Boot AI示例项目

## 项目简介

这是一个集成AI服务的Spring Boot后端项目，提供了知识库检索、AI对话和多Agent协作功能。项目采用内存虚拟数据存储，无需数据库配置，适合快速开发和测试AI应用。

## 技术栈

- Spring Boot 3.3.x
- Spring Web
- Java 17

## 项目结构

```
com.example.ai
├── controller/          # 控制器层
├── service/             # 服务层
├── config/              # 配置层
├── model/               # 数据模型
└── util/                # 工具类
```

## 快速开始

### 1. 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 2. 运行项目

```bash
# 使用Maven运行
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/spring-ai-bailiang-demo-0.0.1-SNAPSHOT.jar
```

### 3. 前端页面

项目包含一个基于Vue3的前端页面，位于`frontend/index.html`，可以直接在浏览器中打开使用。

### 4. API接口

项目提供以下API接口：

- POST `/api/rag/search` - 知识库检索
- POST `/api/ai/chat` - AI对话
- POST `/api/ai/chat/stream` - 流式AI对话
- POST `/api/agent/collaborate` - 多Agent协作

## 注意事项

1. 当前项目使用内存模拟AI响应
2. 项目未包含安全认证机制
3. 异常处理已实现，可根据需求扩展
# 多阶段构建：Maven 构建 + JRE 运行
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# 复制 Maven 配置和源码
COPY pom.xml .
COPY feiniao-common feiniao-common
COPY feiniao-security feiniao-security
COPY feiniao-dicts feiniao-dicts
COPY feiniao-users feiniao-users
COPY feiniao-orders feiniao-orders
COPY feiniao-mcp feiniao-mcp
COPY feiniao-recommendation feiniao-recommendation
COPY feiniao-payments feiniao-payments

# 安装 Maven（Alpine 精简版）
RUN apk add --no-cache maven

# 构建参数：指定要构建的模块
ARG MODULE=feiniao-users

# 构建指定模块（跳过测试）
RUN mvn clean package -pl ${MODULE} -am -DskipTests -q

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser

ARG MODULE=feiniao-users

# 从构建阶段复制 jar
COPY --from=builder /build/${MODULE}/target/*.jar app.jar

USER appuser

EXPOSE 8080 8081 8082 8083 8084 8085

# 根据模块使用不同端口（由 docker-compose 覆盖）
ENTRYPOINT ["java", "-jar", "app.jar"]

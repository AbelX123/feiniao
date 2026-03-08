# 多阶段构建：Maven 构建 + JRE 运行
# 使用官方 Maven 镜像，避免构建时 apk 安装 maven 导致网络卡顿
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

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

# 构建参数：指定要构建的模块
ARG MODULE=feiniao-users

# 构建指定模块（跳过测试）
RUN mvn clean package -pl ${MODULE} -am -DskipTests -q
# 固定输出要运行的 jar，明确排除 *.original
RUN JAR_PATH=$(ls /build/${MODULE}/target/*.jar | grep -v '\.original$' | head -n 1) \
    && cp "${JAR_PATH}" /build/app.jar

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser

ARG MODULE=feiniao-users

# 从构建阶段复制 jar
COPY --from=builder /build/app.jar app.jar

USER appuser

EXPOSE 8080 8081 8082 8083 8084 8085

# 根据模块使用不同端口（由 docker-compose 覆盖）
ENTRYPOINT ["java", "-jar", "app.jar"]


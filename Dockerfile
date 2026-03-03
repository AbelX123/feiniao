# 多阶段构建：Maven 构建 + JRE 运行
# 使用官方 Maven 镜像，避免构建时 apk 安装 maven 导致网络卡顿
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# 添加阿里云 Maven 镜像加速（解决依赖下载慢的问题）
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?> \
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd"> \
        <mirrors> \
            <mirror> \
                <id>aliyunmaven</id> \
                <name>阿里云公共仓库</name> \
                <url>https://maven.aliyun.com/repository/public</url> \
                <mirrorOf>central</mirrorOf> \
            </mirror> \
        </mirrors> \
    </settings>' > /root/.m2/settings.xml

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

# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -pl ${MODULE} -am -B

# 构建指定模块（跳过测试）- 移除 -q 参数以便看到进度
RUN mvn clean package -pl ${MODULE} -am -DskipTests

# 固定输出要运行的 jar，明确排除 *.original
RUN JAR_PATH=$(ls /build/${MODULE}/target/*.jar | grep -v '\.original$' | head -n 1) \
    && echo "Selected JAR: $JAR_PATH" \
    && cp "${JAR_PATH}" /build/app.jar

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser

ARG MODULE=feiniao-users

# 从构建阶段复制 jar
COPY --from=builder /build/app.jar app.jar

# 添加时区设置（可选）
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

USER appuser

EXPOSE 8080 8081 8082 8083 8084 8085

# 添加 JVM 优化参数（可选）
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+HeapDumpOnOutOfMemoryError", "-jar", "app.jar"]
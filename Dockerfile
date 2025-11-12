# 使用轻量级JDK镜像
FROM openjdk:17-jre-slim

# 设置工作目录
WORKDIR /app

# 复制构建好的jar包
COPY target/aiTravelPlanner-0.0.1-SNAPSHOT.jar app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 暴露端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
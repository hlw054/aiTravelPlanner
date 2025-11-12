#!/bin/bash

# 错误时退出
set -e

echo "=== AI旅行规划助手部署脚本 ==="
echo ""

# 检查是否已构建项目
if [ ! -f "target/aiTravelPlanner-0.0.1-SNAPSHOT.jar" ]; then
    echo "错误: 未找到构建好的jar包，请先运行 'mvn clean package -DskipTests'"
    exit 1
fi

# 设置变量
IMAGE_NAME="ai-travel-planner"
CONTAINER_NAME="ai-travel-planner-container"
PORT=8081

echo "开始构建Docker镜像..."
docker build -t $IMAGE_NAME .

echo ""
echo "检查是否已有同名容器运行..."
if [ $(docker ps -q -f name=$CONTAINER_NAME) ]; then
    echo "停止并删除现有容器..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
fi

echo ""
echo "运行Docker容器..."
docker run -d \
  --name $CONTAINER_NAME \
  -p $PORT:$PORT \
  --restart unless-stopped \
  $IMAGE_NAME

echo ""
echo "=== 部署完成 ==="
echo "容器名称: $CONTAINER_NAME"
echo "暴露端口: $PORT"
echo "访问地址: http://localhost:$PORT"
echo ""
echo "查看容器日志: docker logs -f $CONTAINER_NAME"
echo "停止容器: docker stop $CONTAINER_NAME"
echo "启动容器: docker start $CONTAINER_NAME"
echo "删除容器: docker rm $CONTAINER_NAME"
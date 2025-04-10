SOURCE_PATH=/usr/local/collab-ai
SERVER_NAME=collab-ai
TAG=latest
SERVER_PORT=7654

cd $SOURCE_PATH
docker build -t $SERVER_NAME:$TAG .

docker run --restart=always --name $SERVER_NAME -d -p $SERVER_PORT:$SERVER_PORT -v /usr/local/CollabAI-Springboot/static:/home/static $SERVER_NAME:$TAG

echo "成功创建并运行容器$SERVER_NAME"

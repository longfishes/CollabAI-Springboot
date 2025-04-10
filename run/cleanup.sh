server_name="collab-ai"
tag="latest"

docker stop $server_name
docker rm $server_name
docker rmi $server_name:$tag

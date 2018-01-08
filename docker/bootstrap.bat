docker ps -aqf "name=hyperspy-container" | xargs docker stop | xargs docker rm
docker build -t hyperspy-image .
docker run --name hyperspy-container -d hyperspy-image
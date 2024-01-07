#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

ALL_PORTS=("8080","8081")
AVAILABLE_PORT=()
SERVER_NAME=motivoo-server

DOCKER_PS_OUTPUT=$(docker ps | grep $SERVER_NAME)
RUNNING_CONTAINER_NAME=$(echo "$DOCKER_PS_OUTPUT" | awk '{print $NF}')
RUNNING_SERVER_PORT=$(echo "$RUNNING_CONTAINER_NAME" | awk -F'-' '{print $NF}')

IS_REDIS_ACTIVATE=$(docker ps | grep redis)

WEB_HEALTH_CHECK_URL=/actuator/health

# Redis Docker Image Pull
if [ -z $IS_REDIS_ACTIVATE ];then
  echo "###### REDIS ######"
  echo "[$NOW_TIME] Redis 도커 이미지 pull"
  docker-compose pull redis
  echo "[$NOW_TIME] Redis 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d redis
fi

# 실행 중인 서버 포트 확인
if [ -z "$RUNNNG_SERVER_PORT" ]; then
    echo "[$NOW_TIME] 실행 중인 서버 포트: 없음"
else
    echo "[$NOW_TIME] 실행 중인 서버 포트: $RUNNING_SERVER_PORT"
fi

# 현재 실행 중인 포트 외 실행가능한 포트 확인
for iten in "{$ALL_PORT[@]}"; do
  if [ "$item" != "$RUNNING_SERVER_PORT" ]; then
    AVAILABLE_PORT+=("$item")
  fi
done;

# 실행 가능한 포트 없으면 끝내기
if [ ${#AVAILABLE_PORT[@]} -eq 0 ]; then
    echo "[$NOW_TIME] 실행 가능한 포트가 없습니다."
    exit 1
fi

# Green Up
if [ $RUNNING_SERVER_PORT -eq 8080 ]; then
  echo "[$NOW_TIME] 현재 구동중인 Port: Blue(:8080)"

  echo "[$NOW_TIME] ###### 스위칭 ######"
  echo "[$NOW_TIME] ###### BLUE -> GREEN ######"
  echo "[$NOW_TIME] Green 도커 이미지 pull"
  docker-compose pull green
  echo "[$NOW_TIME] Green 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d green

  for retry_count in {1..10}; do
    echo "[$NOW_TIME] Green health check ..."
    sleep 3

    RESPONSE=$(curl http://localhost:${RUNNING_SERVER_PORT}${WEB_HEALTH_CHECK_URL})
    UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)
    echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

    if [ $UP_COUNT -ge 1 ]; then  # "UP" 문자열이 1개 이상 존재한다면 헬스체크 통과
      echo "[$NOW_TIME] Health check 성공!"
      break;
    else
      echo "[$NOW_TIME] Health check의 응답을 알 수 없거나 status가 UP이 아닙니다."
    fi

    if [ $retry_count -eq 10 ]; then
      echo "[$NOW_TIME] Health check 실패.."
      echo "[$NOW_TIME] Nginx에 연결하지 않고 배포를 종료합니다."
      exit 1
    fi
  done;
  sleep 3

  echo "----------------------------------------------------------------------"

  echo "[$NOW_TIME] Nginx Reload (Port 스위칭 적용)"
  echo "set \$service_url http://127.0.0.1:${RUNNING_SERVER_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] Blue 컨테이너 중단"
  docker-compose stop blue

# Blue Up
else
  echo "[$NOW_TIME] 현재 구동중인 Port: Green(:8081)"

  echo "[$NOW_TIME] ###### 스위칭 ######"
  echo "[$NOW_TIME] ###### GREEN -> BLUE ######"
  echo "[$NOW_TIME] Blue 도커 이미지 pull"
  docker-compose pull blue
  echo "[$NOW_TIME] Blue 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d blue

  for retry_count in {1..10}; do
    echo "[$NOW_TIME] Blue health check ..."
    sleep 3

    RESPONSE=$(curl http://localhost:${RUNNING_SERVER_PORT}/actuator/health)
    UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)
    echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

    if [ $UP_COUNT -ge 1 ]; then  # "UP" 문자열이 1개 이상 존재한다면 헬스체크 통과
      echo "[$NOW_TIME] Health check 성공!"
      break;
    else
      echo "[$NOW_TIME] Health check의 응답을 알 수 없거나 status가 UP이 아닙니다."
    fi

    if [ $retry_count -eq 10 ]; then
      echo "[$NOW_TIME] Health check 실패.."
      echo "[$NOW_TIME] Nginx에 연결하지 않고 배포를 종료합니다."
      exit 1
    fi
  done;
  sleep 3

  echo "----------------------------------------------------------------------"

  echo "[$NOW_TIME] Nginx Reload (Port 스위칭 적용)"
  echo "set \$service_url http://127.0.0.1:${RUNNING_SERVER_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] Green 컨테이너 중단"
  docker-compose stop green
fi

echo "----------------------------------------------------------------------"

# Nginx를 통해서 서버에 접근 가능한지 확인
RESPONSE=$(curl http://localhost:${RUNNING_SERVER_PORT}/actuator/health)
UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)
echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

if [ $UP_COUNT -ge 1 ]
then
    echo "> 서버 변경 성공"
else
    echo "> 서버 변경 실패"
    echo "> 서버 응답 결과: ${RESPONSE}"
    exit 1
fi

# 스위칭 이전의 서버 있다면 중단
if [ -n "$RUNNING_SERVER_PORT" ]; then
    echo "> 기존 ${RUNNING_SERVER_PORT}포트 서버 중단"
    sudo docker rm -f ${SERVER_NAME}-${RUNNING_SERVER_PORT}
fi


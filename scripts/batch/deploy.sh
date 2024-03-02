#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

ALL_PORTS=("8081","8083")
AVAILABLE_PORT=()
SERVER_NAME=motivoo-batch-server

DOCKER_PS_OUTPUT=$(docker ps | grep $SERVER_NAME)
RUNNING_CONTAINER_NAME=$(echo "$DOCKER_PS_OUTPUT" | awk '{print $NF}')

IS_REDIS_ACTIVATE=$(docker ps | grep redis)
IS_GREEN_ACTIVATE=$(docker ps | grep green-batch)

WEB_HEALTH_CHECK_URL=/api/health

# 실행 중인 서버 포트 확인
if [ ${RUNNING_CONTAINER_NAME} == "blue-batch" ]; then
    echo "[$NOW_TIME] 실행 중인 서버 포트: blue-batch (:8081)"
    RUNNING_SERVER_PORT=8081
elif [ ${RUNNING_CONTAINER_NAME} == "green-batch" ]; then
    echo "[$NOW_TIME] 실행 중인 서버 포트: green-batch (:8083)"
    RUNNING_SERVER_PORT=8083
else
  echo "[$NOW_TIME] 실행 중인 서버 포트: 없음"
fi


# 현재 실행 중인 포트 외 실행가능한 포트 확인
for item in "${ALL_PORTS[@]}"; do
  if [ "$item" != "${RUNNING_SERVER_PORT}" ]; then
    AVAILABLE_PORT+=("$item")
  fi
done;

# 실행 가능한 포트 없으면 끝내기
if [ ${#AVAILABLE_PORT[@]} -eq 0 ]; then
    echo "[$NOW_TIME] 실행 가능한 포트가 없습니다."
#    exit 1
fi

# Green Up
if [ -z $IS_GREEN_ACTIVATE ]; then
  echo "[$NOW_TIME] ###### 스위칭 ######"
  echo "[$NOW_TIME] ###### BLUE -> GREEN ######"
  CURRENT_SERVER_PORT=8083

  echo "[$NOW_TIME] Green 도커 이미지 pull"
  docker-compose pull green-batch
  echo "[$NOW_TIME] Green 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d green-batch
  echo "[$NOW_TIME] 10초 후 Health Check 시작"
  sleep 10

  for retry_count in {1..15}; do
    echo "[$NOW_TIME] Green health check ..."
    sleep 3

    RESPONSE=$(sudo lsof -i :${CURRENT_SERVER_PORT})
    UP_COUNT=$(echo $RESPONSE | grep 'docker' | wc -l)
    echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

    if [ $UP_COUNT -ge 1 ]; then  # "success" 문자열이 1개 이상 존재한다면 헬스체크 통과
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
  sudo cp /etc/nginx/conf.d/green-url-batch.inc /etc/nginx/conf.d/service-url-batch.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] 스위칭 후 실행 중인 Port: $(sudo cat /etc/nginx/conf.d/service-url-batch.inc)"
  echo "[$NOW_TIME] Blue 컨테이너 중단"
  docker-compose stop blue-batch

# Blue Up
else

  echo "[$NOW_TIME] ###### 스위칭 ######"
  echo "[$NOW_TIME] ###### GREEN -> BLUE ######"
  CURRENT_SERVER_PORT=8081

  echo "[$NOW_TIME] Blue 도커 이미지 pull"
  docker-compose pull blue-batch
  echo "[$NOW_TIME] Blue 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d blue-batch
  echo "[$NOW_TIME] 10초 후 Health Check 시작"
  sleep 10


  for retry_count in {1..15}; do
    echo "[$NOW_TIME] Blue health check ..."
    sleep 3

    RESPONSE=$(sudo lsof -i :${CURRENT_SERVER_PORT})
    UP_COUNT=$(echo $RESPONSE | grep 'docker' | wc -l)
    echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

    if [ $UP_COUNT -ge 1 ]; then  # "success" 문자열이 1개 이상 존재한다면 헬스체크 통과
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
  sudo cp /etc/nginx/conf.d/blue-url-batch.inc /etc/nginx/conf.d/service-url-batch.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] 스위칭 후 실행 중인 Port: $(sudo cat /etc/nginx/conf.d/service-url-batch.inc)"
  echo "[$NOW_TIME] Green 컨테이너 중단"
  docker-compose stop green-batch
fi

echo "----------------------------------------------------------------------"

# Nginx를 통해서 서버에 접근 가능한지 확인
RESPONSE=$(curl -s http://localhost:${CURRENT_SERVER_PORT}${WEB_HEALTH_CHECK_URL})
UP_COUNT=$(echo $RESPONSE | grep 'success' | wc -l)
echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"

if [ $UP_COUNT -ge 1 ]
then
    echo "[$NOW_TIME] 서버 변경 성공"
else
    echo "[$NOW_TIME] 서버 변경 실패"
    echo "[$NOW_TIME] 서버 응답 결과: ${RESPONSE}"
    exit 1
fi


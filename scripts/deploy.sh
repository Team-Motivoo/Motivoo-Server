#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"


IS_GREEN_ACTIVATE=$(docker ps | grep green)
IS_REDIS_ACTIVATE=$(docker ps | grep redis)

# Redis Docker Image Pull
if [ -z $IS_REDIS_ACTIVATE ];then
  echo "### REDIS ###"
  echo "[$NOW_TIME] Redis 도커 이미지 pull"
  docker-compose pull redis
  echo "[$NOW_TIME] Redis 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d redis
fi

# Green Up
if [ -z $IS_GREEN_ACTIVATE ]; then
  echo "[$NOW_TIME] 현재 구동중인 Port: Green(:8080)"

  echo "[$NOW_TIME] 스위칭"
  echo "[$NOW_TIME] BLUE -> GREEN ###"
  echo "[$NOW_TIME] Green 도커 이미지 pull"
  # docker-compose pull green
  echo "[$NOW_TIME] Green 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d green

  for retry_count in {1..10}; do
    echo "[$NOW_TIME] Green health check ..."
    sleep 3

#    REQUEST=$(curl http://localhost:8081)
    RESPONSE=$(curl http://localhost:8081/actuator/health)
    UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)

    if [ $UP_COUNT -ge 1 ]; then  # "UP" 문자열이 1개 이상 존재한다면 헬스체크 통과
      echo "[$NOW_TIME] Health check 성공!"
      break;
    else
      echo "[$NOW_TIME] Health check의 응답을 알 수 없거나 status가 UP이 아닙니다."
      echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"
    fi

    if [ $retry_count -eq 10 ]; then
      echo "[$NOW_TIME] Health check 실패.."
      echo "[$NOW_TIME] Nginx에 연결하지 않고 배포를 종료합니다."
      exit 1
    fi
  done;
  sleep 3

  echo "[$NOW_TIME] Nginx Reload (Port 스위칭 적용)"
  sudo cp /etc/nginx/conf.d/green-url.inc /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] Blue 컨테이너 중단"
  docker-compose stop blue

# Blue Up
else
  echo "[$NOW_TIME] 현재 구동중인 Port: Blue(:8081)"

  echo "[$NOW_TIME] 스위칭"
  echo "[$NOW_TIME] GREEN -> BLUE ###"
  echo "[$NOW_TIME] Blue 도커 이미지 pull"
  # docker-compose pull blue
  echo "[$NOW_TIME] Blue 컨테이너 Up (빌드 & 실행)"
  docker-compose up -d blue

  for retry_count in {1..10}; do
    echo "[$NOW_TIME] Blue health check ..."
    sleep 3

#    REQUEST=$(curl http://localhost:8080)
    RESPONSE=$(curl http://localhost:8080/actuator/health)
    UP_COUNT=$(echo $RESPONSE | grep 'UP' | wc -l)

    if [ $UP_COUNT -ge 1 ]; then  # "UP" 문자열이 1개 이상 존재한다면 헬스체크 통과
      echo "[$NOW_TIME] Health check 성공!"
      break;
    else
      echo "[$NOW_TIME] Health check의 응답을 알 수 없거나 status가 UP이 아닙니다."
      echo "[$NOW_TIME] Health check 응답: ${RESPONSE}"
    fi

    if [ $retry_count -eq 10 ]; then
      echo "[$NOW_TIME] Health check 실패.."
      echo "[$NOW_TIME] Nginx에 연결하지 않고 배포를 종료합니다."
      exit 1
    fi
  done;
  sleep 3

  echo "[$NOW_TIME] Nginx Reload (Port 스위칭 적용)"
  sudo cp /etc/nginx/conf.d/blue-url.inc /etc/nginx/conf.d/service-url.inc
  sudo nginx -s reload
  echo "[$NOW_TIME] Green 컨테이너 중단"
  docker-compose stop green
fi

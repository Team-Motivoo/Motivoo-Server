FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./build/libs/motivooServer-0.0.1-SNAPSHOT.jar /app/APPLICATION.jar
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=deploy", "APPLICATION.jar"]
FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./motivoo-api/build/libs/motivoo-api-0.0.1-SNAPSHOT.jar /app/API_APPLICATION.jar
COPY ./motivoo-batch/build/libs/motivoo-batch-0.0.1-SNAPSHOT.jar /app/BATCH_APPLICATION.jar
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=deploy", "API_APPLICATION.jar"]
CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=deploy", "BATCH_APPLICATION.jar"]
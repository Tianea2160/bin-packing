FROM openjdk:21

WORKDIR /app

COPY build/libs/box-recommend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
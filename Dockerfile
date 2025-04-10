FROM openjdk:17

ADD collab-ai-springboot-1.0.1.jar app.jar
ADD application.yml app.yml

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Duser.timezone=Asia/Shanghai","-jar","/app.jar","--spring.config.location=/app.yml"]

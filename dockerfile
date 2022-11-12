FROM --platform=linux/amd64 openjdk:11
COPY build/libs/workduo-0.0.1-SNAPSHOT.jar workduo-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "workduo-0.0.1-SNAPSHOT.jar"]
#define base docker image
FROM openjdk:11
LABEL maintainer="Egor Mitrofanov"
ADD target/School_Yandex-0.0.1-SNAPSHOT.jar School_Yandex.jar
ENTRYPOINT ["java", "-jar", "School_Yandex.jar"]
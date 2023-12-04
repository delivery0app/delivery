FROM docker.io/library/openjdk:17
COPY target/delivery-0.0.1-SNAPSHOT.jar backend.jar
CMD ["java", "-jar", "backend.jar"]
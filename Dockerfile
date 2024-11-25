FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY lib ./lib
COPY src ./src

RUN mvn clean package

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/target/SearchEngine-1.0-SNAPSHOT.jar app.jar
COPY --from=build /app/lib ./lib

CMD ["java", "-cp", "lib/*:app.jar", "searchengine.Main"]

EXPOSE 8080
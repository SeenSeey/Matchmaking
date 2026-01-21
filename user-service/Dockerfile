FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Собираем events-contract (зависимость для user-service)
COPY events-contract/pom.xml ./events-contract/
COPY events-contract/src ./events-contract/src
RUN cd events-contract && mvn clean install -DskipTests -B

# Копируем pom.xml для user-service и загружаем зависимости
COPY user-service/pom.xml ./user-service/
RUN cd user-service && mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY user-service/src ./user-service/src
RUN cd user-service && mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=builder /build/user-service/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar

USER javauser

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]

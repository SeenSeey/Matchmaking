FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

ARG JAR_FILE=target/*.jar

# Копируем JAR внутрь образа под именем app.jar
COPY ${JAR_FILE} app.jar

# Меняем владельца файла
RUN chown javauser:javauser /app/app.jar

# Переключаемся на пользователя
USER javauser

# Документируем порт (этот порт должен совпадать с server.port в properties)
EXPOSE 8083

# Запускаем приложение
# Использование exec формы ["..."] позволяет корректно передавать сигналы остановки (SIGTERM)
ENTRYPOINT ["java", "-jar", "app.jar"]

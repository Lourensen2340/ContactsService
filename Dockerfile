# === Этап 1: Сборка приложения ===
# Используем официальный образ Maven 3.9.13 с Java 17 на борту
FROM maven:3.9.13-eclipse-temurin-17 AS builder
WORKDIR /app

# Копируем только файл описания зависимостей
COPY pom.xml .

# Скачиваем все зависимости заранее (кэшируем этот слой)
RUN mvn dependency:go-offline -B

# Копируем исходный код проекта
COPY src ./src

# Собираем проект (пакет .jar), игнорируя тесты для экономии времени
RUN mvn clean package -DskipTests

# === Этап 2: Запуск приложения ===
# Для работы готового jar-ника нам нужна только JRE 17 (alpine — супер-легкий образ)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем папку для нашей SQLite базы данных внутри контейнера
RUN mkdir -p /app/data

# Забираем собранный jar-файл из предыдущего шага сборщика
COPY --from=builder /app/target/*.jar app.jar

# Открываем порт наружу
EXPOSE 8080

# Инструкция для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
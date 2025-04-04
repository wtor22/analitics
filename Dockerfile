FROM openjdk:17-jdk-slim

# Устанавливаем шрифты + fontconfig
RUN apt-get update && apt-get install -y fontconfig fonts-dejavu fonts-liberation \
    && rm -rf /var/lib/apt/lists/* \


# Обновляем кеш шрифтов (чтобы система их увидела)
RUN fc-cache -fv

WORKDIR /app
COPY target/analitics-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application*.yml /app/
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
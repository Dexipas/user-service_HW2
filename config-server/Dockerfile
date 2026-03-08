FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Копируем jar-файл
COPY target/*.jar app.jar

# Создаем директорию для конфигов внутри контейнера
RUN mkdir -p /config-repository

# Указываем, что приложение будет использовать native profile и читать конфиги из /config-repo
ENV SPRING_PROFILES_ACTIVE=native
ENV CONFIG_PATH=/config-repository

ENTRYPOINT ["java", "-jar", "/app.jar"]
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
COPY src ./src

RUN ./gradlew build -x test -PskipIntegrationTests --no-daemon

FROM eclipse-temurin:17-jdk AS runtime
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 8080

 RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
 HEALTHCHECK --interval=30s --timeout=5s \
   CMD curl -f http://localhost:8080/actuator/health || exit 1

RUN addgroup --system app && adduser --system --group app
USER app

ENTRYPOINT ["java", "-jar", "app.jar"]
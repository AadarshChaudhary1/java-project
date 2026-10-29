FROM maven:3.9.11-eclipse-temurin-25-noble AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S spring && adduser -S -G spring spring
WORKDIR /app
COPY --from=build --chown=spring:spring /workspace/target/java-project-1.0.0.jar app.jar

USER spring
ENV PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD ["sh", "-c", "wget -q -O /dev/null \"http://127.0.0.1:${PORT:-8080}/api/health\" || exit 1"]

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

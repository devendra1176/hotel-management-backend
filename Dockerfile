# ==========================================
# STAGE 1 → BUILD APPLICATION
# ==========================================

FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests=true


# ==========================================
# STAGE 2 → RUN APPLICATION
# ==========================================

FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user
RUN useradd -m spring

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose container port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=$PORT -jar app.jar"]
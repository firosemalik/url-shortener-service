# Use an official OpenJDK 21 runtime as a base image
FROM eclipse-temurin:21-jre-alpine

# Set environment variables (optional but useful)
#ENV TZ=UTC \
#    LANG=C.UTF-8 \
#    JAVA_OPTS=""

# Create a non-root user (optional but recommended for security)
#RUN addgroup -S spring && adduser -S spring -G spring

# Create app directory
#WORKDIR /app

# Copy the Spring Boot jar to the container
COPY target/urlshortener-0.0.1-SNAPSHOT.jar app.jar

# Change file ownership to the non-root user
#RUN chown spring:spring app.jar

# Switch to non-root user
#USER spring

# Expose port (optional, but good for documentation)
EXPOSE 8085

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

FROM eclipse-temurin:21-jre

WORKDIR /app

# The following ENV variables are set by default. Overwrite them to suit your configuration.
ENV JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

# Copy the app
COPY common/service/app.jar /app/app.jar

# Start application
CMD java $JAVA_OPTIONS -jar app.jar

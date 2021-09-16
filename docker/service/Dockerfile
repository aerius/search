FROM openjdk:11-jre

# The following ENV variables are set by default. Overwrite them to suit your configuration.
ENV JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

# Copy the app
COPY app.jar /

# Start application
CMD java $JAVA_OPTIONS -jar /app.jar

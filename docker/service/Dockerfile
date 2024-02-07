# Builder part for maven packaging.
FROM eclipse-temurin:17 as builder

WORKDIR /app

RUN apt-get update && \
    apt-get install -y maven

# Do the actual build
COPY . /app
RUN mvn --no-transfer-progress -DskipTests=true clean package

# Move the resultant jar to a known name so it can be picked up by the final image.
RUN mv -v search-service/target/search-service-*.jar app.jar \
# Clean up image so we can rerun without the cache being invalidated because target files are changed
    && mvn clean

FROM eclipse-temurin:17

WORKDIR /app

# The following ENV variables are set by default. Overwrite them to suit your configuration.
ENV JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

# Copy the app
COPY --from=builder /app/app.jar /app/

# Start application
CMD java $JAVA_OPTIONS -jar app.jar

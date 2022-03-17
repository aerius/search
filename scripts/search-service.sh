api_key=$(cat api_key.txt)
log=$(pwd)/conf/logback.xml
cd ..

mvn clean install -pl :search-service -am -DskipTests

mvn spring-boot:run -pl :search-service -Dspring-boot.run.arguments="--logging.config=$log --nl.aerius.bing.apiKey=$api_key" -DskipTests

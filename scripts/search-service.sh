log=$(pwd)/conf/logback.xml
cd ..

mvn spring-boot:run -pl :search-service -Dspring-boot.run.arguments=--logging.config=$log

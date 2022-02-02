cd ${1:-..}
mvn com.mycila:license-maven-plugin:3.0:format -Dlicense.skipExistingHeaders=true

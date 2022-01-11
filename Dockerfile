FROM gradle:4.8.0-jdk8-alpine
WORKDIR app/
COPY . app/
RUN gradle build --stacktrace
CMD java -jar /app/build/libs/apigateway-0.0.1-SNAPSHOT.jar
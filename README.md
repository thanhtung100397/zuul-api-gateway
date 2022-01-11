![Logo of the project](readme_assets/netflix_zuul_logo.png)

# Zuul API Gateway

A brief description of your project, what it is used for.

## Installing / Getting started

#### 2. Install JDK8

```bash
$ sudo apt update
$ sudo apt install openjdk-8-jdk
```

Check JDK version

```bash
$ java -version
```

#### 3. Install Gradle

```bash
$ wget https://services.gradle.org/distributions/gradle-4.9-bin.zip
$ sudo mkdir /opt/gradle
(if zip and unzip are not installed: $ sudo apt-get install zip unzip)
$ sudo unzip -d /opt/gradle gradle-4.9-bin.zip
$ export PATH=$PATH:/opt/gradle/gradle-4.9/bin
$ rm -rf /opt/gradle gradle-4.9-bin.zip
```

Check Gradle version

```bash
$ gradle -v
```

#### 4. Install Supervisor

```bash
$ sudo apt-get update
$ sudo apt-get install supervisor
$ service supervisor restart
```

Check Supervisor status

```bash
$ service supervisor status
```


## Developing

####1. Built With

Spring Boot 2.0.4.RELEASE

Gradle 4.9

####2. Setting up Dev

```bash
$ git clone git@gitlab.com:worksvn-dev-team/base-project/zuul-api-gateway.git
$ cd zuul-api-gateway
$ gradle dependencies
```

####3. Building executable .jar

```bash
$ gradle build
```

When build successful, the executable .jar file will be located at `build/libs/zuul_api_gateway-{version}.jar`

####4. Deploying / Publishing

1) Run directly

```bash
$ gradle bootRun
```

2) Run executable .jar

```bash
$ java -jar build/libs/zuul_api_gateway-{version}.jar
$ java -jar build/libs/zuul_api_gateway-{version}.jar --server.port=<other port>
```

3) register to supervisor

```bash
$ sudo nano /etc/supervisor/conf.d/zuul_api_gateway.conf
```

Then copy the config below to `zuul_api_gateway.conf`

````
[program:zuul_api_gateway]
user = [your user]
directory = [path to zuul api gateway folder]
command = java -jar build/libs/zuul_api_gateway-{version}.jar
;process_name = %(process_num)02d

;numprocs = 1
priority = 900
autostart = true
autorestart = true
stopsignal = QUIT

redirect_stderr = true
stdout_logfile = [path to zuul api gateway folder]/var/log/%(program_name)s.log
stderr_logfile = [path to zuul api gateway folder]/var/log/%(program_name)s.log

````

Then restart `supervisor` service

```bash
$ service supervisor restart
```

Check `zuul_api_gateway` service is running or not

```bash
$ sudo supervisorctl
```


## Versioning

Build `version` is located in `build.gradle`

## Configuration

####1. Application configuration

Create file `application.yml` an put into `zuul_api_gateway/src/main/resources`

Note: Each change at `application.yml` require rebuild whole project, see **Building** section

```bash
$ nano zuul_api_gateway/src/main/resources/application.yml
```

````
server.port=8900

spring.profiles.active=local

spring.application.name=api-gateway
application.modules-package.name=modules
spring.application.swagger.info.path=data/swagger-info.json

spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500
spring.cloud.consul.discovery.health-check-path=/actuator/health
spring.cloud.consul.discovery.health-check-interval=10s
spring.cloud.consul.discovery.health-check-timeout=5s
spring.cloud.consul.discovery.register-health-check=true
spring.cloud.consul.discovery.prefer-ip-address=true
spring.cloud.service-registry.auto-registration.enabled=false

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL55Dialect

spring.datasource.hibernate.ddl-auto=update
spring.datasource.jdbc-url=jdbc:mysql://localhost:3306/gateway_database?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
spring.datasource.username=root
spring.datasource.password=123456

spring.oauth2-datasource.hibernate.ddl-auto=update
spring.oauth2-datasource.jdbc-url=jdbc:mysql://localhost:3306/oauth2_database?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true
spring.oauth2-datasource.type=com.zaxxer.hikari.HikariDataSource
spring.oauth2-datasource.username=root
spring.oauth2-datasource.password=123456

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

security.oauth2.resource-server.id=api_gateway
security.oauth2.authorization-server.token-signing-key=secret_sgn_key

ribbon.ConnectTimeout=3000
ribbon.ReadTimeout=3000
````

####2. Loging configuration

Create file `logback-spring.xml` an put into `zuul_api_gateway/src/main/resources`

Note: Each change at `logback-spring.yml` require rebuild whole project, see **Building** section

```bash
$ nano zuul_api_gateway/src/main/resources/logback-spring.xml
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--CREATE NEW <springProfile> FOR EACH ENVIRONMENT, FOR EX: dev-->
    <springProfile name="[VALUE OF spring.profiles.active IN application.yml]">
        
        <!--ADD LOGSTASH LOGGING-->
        <appender name="STASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
            <destination>[LOGSTASH HOST]:[LOGSTASH_PORT]</destination>
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <customFields>{"service_name":"[NAME OF THIS SERVICE ON LOGSTASH]"}</customFields>
            </encoder>
        </appender>
        
        <!--ADD CONSOLE LOGGING-->
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <logger name="ApiGatewayApplication" additivity="false" level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="STASH"/>
        </logger>
        <logger name="GlobalRequestFilter" additivity="false" level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="STASH"/>
        </logger>
        <logger name="GlobalResponseFilter" additivity="false" level="INFO">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="STASH"/>
        </logger>
        <logger name="GlobalExceptionFilter" additivity="false" level="ERROR">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="STASH"/>
        </logger>
        <root level="ERROR">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="STASH"/>
        </root>
    </springProfile>
</configuration>
```

## Tests

Describe and show how to run the tests with code examples.
Explain what these tests test and why.

```shell
Give an example
```

## Api Reference

View Doc Api at `http://<host>:<port>/swagger-ui.html`

## Database

None

## Licensing

State what the license is and how to find the text version of the license.

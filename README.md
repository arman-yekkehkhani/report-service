# Report Service

This project demonstrate a simple service based on the Spring Boot framework. Through this service, users can add a new 
report, edit an existing one or delete it. Each user is only allowed to modify reports created by him/her self. These 
API are also secured by JWT Authorization method leveraging Spring Security. There are a multitude of tests in different
layers of the application, ranging from unit tests to integration and end-to-end test.

* In order to run tests, run the following command in the project directory:
```
$ mvn clean test
```
* or run it locally with:
```
$ mvn clean spring-boot:run
```

This project is designed with a focus on the observability aspect. Several metrics are gathered through Loki/Prometheus
and visualized in the Grafana. The grafana dashboard is stored as a json which can be imported later. In order to
monitor the application and see logs, run the following command to run Loki/Prometheus/Grafana. Then add datasource and 
import the dashboard json.
```
$ docker-compose up -d
```

# How to test the API
Swagger UI is also integrated into the application which enables automatic documentation of REST API of the application,
and facilitate the testing. The swagger docs can be accessed through the following link:
```
http://localhost:8080/swagger-ui/index.html
```

# How to create a containerized native image
I have also added the necessary configurations to create a native image and containerize it. This can be simply done by
running the following command in terminal.

```
$ mvn clean -Pnative spring-boot:build-image -DskipTests
```

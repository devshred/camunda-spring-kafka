# event-driven Camunda process engine

Example on how to implement and operate a Camunda process engine, that communicates only via Kafka events.
Synchronous REST-calls are processed via ReplyingKafkaTemplate.

## setup infrastructure

```shell
docker compose up -d
```

## build and start services

```shell
mvn clean install
```

### REST-API

```shell
mvn -pl rest-api spring-boot:run
```

### quote worker

```shell
mvn -pl quote-worker spring-boot:run
```

### process engine

```shell
mvn -pl process-engine spring-boot:run
```

## start process and request results

Use [rest-api.http](./rest-api.http) to start the business process.

## Camunda-Admin
http://localhost:9081/ (admin/admin123)
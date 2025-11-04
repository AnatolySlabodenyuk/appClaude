# Java-заглушка на Spring Boot для приёма сообщений и записи в БД

Приложение представляет собой минимальный REST-сервис на Spring Boot, который принимает HTTP POST-запросы с сообщениями и асинхронно сохраняет их в базу данных PostgreSQL. Сервис оснащён Spring Boot Actuator для мониторинга и экспорта метрик (в том числе в Prometheus) и поддерживает динамическую настройку времени искусственной задержки перед записью в БД.

## Что умеет сервис
- Принимать POST-запрос с данными сообщения на эндпоинт `/post-message` и сохранять запись в таблицу `messages`.
- Сохранять поля:
  - `msgUuid` (UUID) — идентификатор сообщения
  - `head` (BOOLEAN) — произвольный флаг из запроса
  - `timeRq` (TIMESTAMP) — время обработки на сервере
- Работать асинхронно (запись выполняется в отдельном пуле потоков).
- Экспортировать метрики и служебные эндпоинты через Spring Boot Actuator: `health`, `info`, `metrics`, `prometheus`.
- Позволять на лету менять время задержки перед записью (в миллисекундах) через REST-эндпоинты.

## Технологии
- Java 21
- Spring Boot (Web, Data JPA, Actuator)
- PostgreSQL (JDBC driver)
- Micrometer + Prometheus Registry

## Эндпоинты приложения
### 1) Приём сообщения
- Метод: `POST`
- Путь: `/post-message`
- Тело запроса (JSON):
```json
{
  "msg_uuid": "c8d4a6d2-6b2d-4f41-8d7f-6b5b3b0a1234",
  "head": true
}
```
- Успешный ответ: `200 OK`, текст: `Message accepted`

Пример с curl:
```bash
curl -X POST http://localhost:8080/post-message \
  -H "Content-Type: application/json" \
  -d '{"msg_uuid":"c8d4a6d2-6b2d-4f41-8d7f-6b5b3b0a1234","head":true}'
```

### 2) Управление задержкой записи
- Текущая задержка (мс):
  - Метод: `GET`
  - Путь: `/api/message-delay`
  - Ответ: число (миллисекунды)

- Установить задержку (мс):
  - Метод: `PUT`
  - Путь: `/api/message-delay?ms={число}`
  - Ограничение: значение должно быть `>= 0`
  - Пример:
```bash
# Получить текущую задержку
curl http://localhost:8080/api/message-delay

# Установить задержку 1500 мс
curl -X PUT "http://localhost:8080/api/message-delay?ms=1500"
```

Примечание: задержка применяется перед сохранением сообщения в БД. Значение по умолчанию задаётся свойством `app.message.delay-ms`.

### 3) Actuator
Доступные (экспонированные) по HTTP эндпоинты actuator:
- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/metrics`
- `GET /actuator/prometheus` — метрики в формате Prometheus

Настройка экспозиции задана в `application.properties`:
```
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.prometheus.metrics.export.enabled=true
```

## Конфигурация окружения
Основные настройки находятся в `src/main/resources/application.properties`.

- Порт приложения:
```
server.port=8080
```

- Подключение к PostgreSQL (есть значения по умолчанию через переменные окружения):
```
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5430}/${DB_NAME:postgres_db}
spring.datasource.username=${DB_USERNAME:postgres_user}
spring.datasource.password=${DB_PASSWORD:postgres_password}
spring.datasource.driver-class-name=org.postgresql.Driver
```

- JPA/Hibernate:
```
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
```

- Задержка по умолчанию:
```
app.message.delay-ms=1000
```

Важно: `spring.jpa.hibernate.ddl-auto=none` — схема БД должна существовать заранее. Таблица `messages` ожидается со следующими столбцами и типами:
```sql
CREATE TABLE IF NOT EXISTS messages (
  "msgUuid" UUID PRIMARY KEY,
  "head" BOOLEAN NOT NULL,
  "timeRq" TIMESTAMP NOT NULL
);
```

## Запуск локально
### Требования
- Java 21+
- Maven 3.9+
- Доступная PostgreSQL (по умолчанию: `localhost:5430`, БД `postgres_db`, пользователь `postgres_user`, пароль `postgres_password`), либо переопределите переменные окружения `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.
- Созданная таблица `messages` (см. SQL выше).

### Команды
С использованием Maven:
```bash
# Сборка
mvn clean package

# Запуск
mvn spring-boot:run
# или
java -jar target/appClaude-0.0.1-SNAPSHOT.jar
```

Переопределить настройки можно переменными окружения или параметрами JVM, например:
```bash
# Изменить порт и параметры БД на лету
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9090 -DDB_HOST=127.0.0.1 -DDB_PORT=5432 -DDB_NAME=mydb -DDB_USERNAME=user -DDB_PASSWORD=pass"
```

## Как это работает (вкратце)
- Контроллер `/post-message` принимает JSON с полями `msg_uuid` и `head` и отвечает `200 OK` сразу после приёма.
- Сервисная логика обрабатывает сохранение асинхронно: перед записью поток спит указанное количество миллисекунд (настраивается), затем сохраняет запись в БД с текущим временем сервера (`timeRq`).
- Задержка может быть просмотрена и изменена во время работы процесса через `/api/message-delay`.
- Actuator предоставляет служебные эндпоинты и метрики, включая `/actuator/prometheus` для систем мониторинга.

## Примеры использования
- Быстрая нагрузка без задержки:
```bash
curl -X PUT "http://localhost:8080/api/message-delay?ms=0"
```
- Имитация «тяжёлой» записи:
```bash
curl -X PUT "http://localhost:8080/api/message-delay?ms=2000"
```
- Отправка тестового сообщения:
```bash
curl -X POST http://localhost:8080/post-message \
  -H "Content-Type: application/json" \
  -d '{"msg_uuid":"11111111-2222-3333-4444-555555555555","head":false}'
```
- Проверка метрик Prometheus:
```bash
curl http://localhost:8080/actuator/prometheus
```

## Логи
Логи выводятся в консоль с шаблоном времени. В логе фиксируются моменты получения HTTP-запроса и записи в БД.

## Частые проблемы
- Ошибка подключения к БД: проверьте доступность PostgreSQL и корректность переменных окружения `DB_*`.
- Таблица отсутствует: создайте таблицу `messages` по SQL выше.
- Неверный формат `msg_uuid`: должен быть валидным UUID.

---
Если нужна Docker-обвязка или миграции (Flyway/Liquibase), их можно добавить отдельно при необходимости.



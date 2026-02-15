# Atomikos Batch Demo

Distributed JTA/XA transaction demo using Spring Boot, Atomikos, MySQL, and PostgreSQL, with focus on batched inserts.

## Current Status (February 15, 2026)

This repository is currently a **working technical demo** for:

- XA transaction coordination across MySQL and PostgreSQL via Atomikos.
- Batch saving data to both databases in one global transaction.
- Comparing XA batch inserts with a non-XA MySQL-only insert path.

It is **not production-ready yet**. Main gaps right now:

- Test coverage is not implemented yet (current test run executes 0 tests).
- Dev credentials and verbose SQL logging are enabled in `application.properties`.
- Schema management uses `hibernate.hbm2ddl.auto=update` (demo convenience).

## What This Project Demonstrates

1. Spring Boot multi-datasource configuration with Atomikos JTA transaction manager.
2. Single service method writing to MySQL and PostgreSQL inside one XA transaction.
3. Batch-oriented inserts (Hibernate/JDBC batch settings + MySQL batched statement rewrite).
4. Optional non-XA insert flow for local comparison.

## Tech Stack

- Java 21
- Spring Boot 3.5.4
- Atomikos 6.0.0
- MySQL Connector/J 9.3.0
- PostgreSQL JDBC 42.7.7
- Docker Compose (local databases)

## Architecture (High Level)

- `config/`
  - Atomikos transaction manager setup.
  - XA datasource + entity manager per database.
  - Separate non-XA MySQL datasource/transaction manager.
- `entity/`
  - `MySQLEmployee` mapped to `employee_mysql`.
  - `PostgresEmployee` mapped to `employee_pg`.
- `repo/`
  - Spring Data repositories for MySQL XA, MySQL non-XA, and PostgreSQL XA.
- `service/`
  - XA batch generation and save service.
  - Non-XA batch insert service.

## XA Batch Save Flow

The main XA flow is in:

- `src/main/java/com/example/batch/service/DummyDataGeneratorServiceImpl.java`

Method:

- `generateAndSaveDummyData(int count)`

Behavior:

1. Generate dummy rows for MySQL + PostgreSQL.
2. Save both lists in a method annotated with `@Transactional("transactionManager")`.
3. Atomikos coordinates the global transaction across both resource managers.

If one branch fails, XA should roll back the global transaction and keep both databases consistent.

## Local Setup

### 1. Start databases

```bash
docker compose up -d
```

PostgreSQL already starts with:

- `max_prepared_transactions=100`

which is required for XA.

### 2. Grant MySQL XA recovery privilege

```bash
docker exec -it local-mysql mysql -u root -p
```

Then run:

```sql
GRANT XA_RECOVER_ADMIN ON *.* TO 'user'@'%';
FLUSH PRIVILEGES;
```

### 3. Run the application

If needed once:

```bash
chmod +x mvnw
```

Then:

```bash
./mvnw spring-boot:run
```

App default port:

- `http://localhost:8888`

## Main API Endpoints

### XA batch insert (MySQL + PostgreSQL)

```bash
curl -X POST "http://localhost:8888/api/test/generate?count=25000"
```

### Non-XA batch insert (MySQL only)

```bash
curl -X POST "http://localhost:8888/api/non-xa/generate?count=50000&flushingBatchSize=10000&hibernateJdbcBatchSize=50"
```

## Verify Data Was Saved

PostgreSQL:

```bash
docker exec -it local-postgres psql -U user -d postgres_db -c "SELECT COUNT(*) FROM employee_pg;"
```

MySQL:

```bash
docker exec -it local-mysql mysql -u user -ppassword -D mysql_db -e "SELECT COUNT(*) FROM employee_mysql;"
```

## Optional Experimental Parts

Pagination and benchmark endpoints exist in the project for experimentation, but they are secondary to the primary XA + batch-save goal.

## Known Limitations

- No automated integration tests proving XA commit/rollback behavior yet.
- Error handling and API response models are minimal.
- Hardcoded local credentials and permissive CORS in demo configuration.
- Batch/sizing values are fixed for demo simplicity.

## License

MIT. See `LICENSE`.

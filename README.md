# 🧩 Atomikos Batch Demo – Distributed JTA Transactions with MySQL & PostgreSQL

This project demonstrates how to configure **Spring Boot 3.5.4** with **Atomikos 6.0.0**
to perform **distributed JTA transactions** across two databases (**MySQL** and **PostgreSQL**)
with **batched inserts** for maximum performance.

---

## ⚙️ Tech Stack

| Component | Version | Description |
|------------|----------|-------------|
| **Java** | 21 | JDK for modern Spring Boot |
| **Spring Boot** | 3.5.4 | Application framework |
| **Atomikos** | 6.0.0 | JTA transaction manager for multi-DB XA coordination |
| **MySQL JDBC Driver** | 9.3.0 | JDBC connector for MySQL |
| **PostgreSQL JDBC Driver** | 42.7.7 | JDBC connector for PostgreSQL |
| **Docker** | latest | Containerized MySQL & PostgreSQL databases |

---

## 🧱 Project Overview

The application inserts dummy employee records into both **MySQL** and **PostgreSQL** within a single JTA transaction.

If either insert fails, **Atomikos** ensures both databases are rolled back, maintaining global consistency.

**Core packages:**
```
com.example.batch
 ┣ config/        → Atomikos, DataSource & EntityManager configs
 ┣ entity/        → MySQLEmployee, PostgresEmployee JPA entities
 ┣ repo/          → Spring Data JPA repositories
 ┣ service/       → DummyDataGeneratorService + Impl
 ┣ utils/         → DummyDataGeneratorUtil (generates random employees)
```

---

## 🐳 Docker Compose Setup

```yaml
services:
  postgres:
    image: postgres:17
    container_name: local-postgres
    restart: always
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: postgres_db
    ports:
      - "5433:5432"
    volumes:
      - pg_data:/var/lib/postgresql/data
    command: >
      postgres -c max_prepared_transactions=100

  mysql:
    image: mysql
    container_name: local-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: mysql_db
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  pg_data:
  mysql_data:
```

✅ PostgreSQL must enable `max_prepared_transactions`, which is **required for XA**.

---

## 🧩 XA Permissions (MySQL)

Atomikos requires the MySQL user to have `XA_RECOVER_ADMIN` privileges for XA recovery.

Run inside the MySQL container:

```bash
docker exec -it local-mysql mysql -u root -p
```

Then execute:

```sql
GRANT XA_RECOVER_ADMIN ON *.* TO 'user'@'%';
FLUSH PRIVILEGES;
```

---

## ⚙️ Application Properties

### `application.properties`
```properties
# ============= POSTGRES (Atomikos XA DataSource) =============
postgres.datasource.unique-resource-name=postgresDataSource
postgres.datasource.xa-data-source-classname=org.postgresql.xa.PGXADataSource
postgres.datasource.xa-properties.user=user
postgres.datasource.xa-properties.password=password
postgres.datasource.xa-properties.databaseName=postgres_db
postgres.datasource.xa-properties.serverName=localhost
postgres.datasource.xa-properties.portNumber=5433
postgres.datasource.max-pool-size=10
postgres.datasource.min-pool-size=2

# ============= MYSQL (Atomikos XA DataSource) =============
mysql.datasource.unique-resource-name=mysqlDataSource
mysql.datasource.xa-data-source-class-name=com.mysql.cj.jdbc.MysqlXADataSource
mysql.datasource.xa-properties.user=user
mysql.datasource.xa-properties.password=password
# IMPORTANT: Enables true batch rewriting for performance
mysql.datasource.xa-properties.url=jdbc:mysql://localhost:3307/mysql_db?rewriteBatchedStatements=true
mysql.datasource.max-pool-size=10
mysql.datasource.min-pool-size=2
```

---

## ⚙️ Transaction Manager Configuration

```java
@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws SystemException {
        UserTransactionManager txManager = new UserTransactionManager();
        txManager.setForceShutdown(false);
        txManager.setTransactionTimeout(300);
        return txManager;
    }

    @Bean
    public UserTransaction atomikosUserTransaction() throws SystemException {
        UserTransactionImp userTx = new UserTransactionImp();
        userTx.setTransactionTimeout(300);
        return userTx;
    }

    @Bean
    @Primary
    public JtaTransactionManager transactionManager() throws SystemException {
        return new JtaTransactionManager(atomikosUserTransaction(), atomikosTransactionManager());
    }
}
```

---

## 🧠 Notes on Batch Performance

- PostgreSQL supports JDBC batching natively.
- MySQL requires `rewriteBatchedStatements=true` in the JDBC URL.
- Recommended `hibernate.jdbc.batch_size = 20–50` for best trade-off.
- Atomikos adds a small overhead for XA, but guarantees atomicity across DBs.

Typical Hibernate statistics log after optimization:
```
executing 200 JDBC batches
executing 0 JDBC statements
```

---

## 🧪 Testing the Application

Once both databases are up:

```bash
mvn spring-boot:run
```

Then call the endpoint that triggers dummy data generation (if exposed), or run a test that calls:
```java
dummyDataGeneratorService.generateAndSaveDummyData(1000);
```

Check both databases via Docker:
```bash
docker exec -it local-postgres psql -U user -d postgres_db -c "SELECT COUNT(*) FROM employee_postgres;"
docker exec -it local-mysql mysql -u user -p -D mysql_db -e "SELECT COUNT(*) FROM employee_mysql;"
```

---

## 🧾 License

This demo is for educational and research purposes. You may reuse or adapt freely.

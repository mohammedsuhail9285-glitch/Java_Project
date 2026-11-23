# DATABASE SETUP & MIGRATION GUIDE
## RegTech Sentinel — PostgreSQL & H2 Persistence Architecture

This document covers database architecture, Flyway schema migrations, PostgreSQL production setup, H2 development mode, and data seed inventory.

---

## 1. Database Architecture Overview

RegTech Sentinel supports two database profiles:
- **Development & Testing:** Embedded H2 Database running in PostgreSQL compatibility mode (`MODE=PostgreSQL`). It requires zero external software installation and seeds all data automatically.
- **Production:** PostgreSQL 16 (or higher) with enterprise connection pooling via HikariCP, database constraints, performance indexes, and Flyway automated schema versioning.

```
+-----------------------------------------------------------------------------------+
|                        FLYWAY SCHEMA VERSIONING PIPELINE                          |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [V1__init_schema.sql]                                                            |
|    ├── users, roles, products, scans, violations, rules                           |
|                                                                                   |
|  [V2__audit_logs.sql]                                                             |
|    └── audit_logs (immutable compliance event audit trail)                        |
|                                                                                   |
|  [V3__auth_provider_and_otp.sql]                                                  |
|    └── ocr_field_confidences, oauth_identities, otp_records                       |
|                                                                                   |
|  [V4__add_batch_and_performance_indexes.sql]                                      |
|    └── B-Tree indexes on foreign keys, timestamps, and search columns             |
|                                                                                   |
|  [V5__maritime_freight_schema.sql]                                                |
|    └── SIH26006 MARITIME FREIGHT DOMAIN MODEL                                     |
|        ├── maritime_ports (East Coast & loading origins with draft/LOA/beam)      |
|        ├── vessel_classes (Capesize, Panamax, Supramax, Handymax specs)           |
|        ├── trade_routes (nautical distances, steaming days, bunker costs)         |
|        ├── freight_market_data (BDI, BCI, BPI, BSI, Brent, LSFO 380 rates)        |
|        ├── procurement_plans (coking coal, iron ore, landed cost scenarios)       |
|        └── freight_forecasts (Holt-Winters 14-day predictions & 95% CIs)          |
|                                                                                   |
|  [V6__seed_demo_users.sql]                                                        |
|    └── BCrypt encrypted accounts: regulator, admin, analyst, reviewer             |
|                                                                                   |
|  [V7__production_auth_enhancements.sql]                                           |
|    ├── user lockout columns: failed_login_attempts, locked_until, account_status  |
|    ├── security_tokens (email verification & password reset tokens with expiry)   |
|    └── default VIEWER role assignment                                             |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. PostgreSQL 16 Production Setup

### Option A: Using Docker Compose (Recommended)
The repository includes a ready-to-run PostgreSQL 16 Docker Compose service:

```bash
docker compose up -d postgres
```

The database container binds to port `5432` with credentials:
- **Database Name:** `regtech`
- **Username:** `postgres`
- **Password:** `postgres`
- **Port:** `5432`

### Option B: Local PostgreSQL Installation
1. Install PostgreSQL 16 from [postgresql.org](https://www.postgresql.org/download/).
2. Open psql or pgAdmin and run:
   ```sql
   CREATE DATABASE regtech;
   CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE regtech TO postgres;
   ```
3. In `backend/.env` or system environment, configure:
   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/regtech
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=postgres
   SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
   ```

---

## 3. H2 In-Memory Development Configuration

By default, when `SPRING_DATASOURCE_URL` points to H2 or when running tests, the backend uses:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:regtech;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
    driverClassName: org.h2.Driver
    username: sa
    password: ""
  h2:
    console:
      enabled: true
      path: /h2-console
```

The H2 web console is accessible during development at:
`http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:regtech`
- **User Name:** `sa`
- **Password:** *(leave blank)*

---

## 4. Flyway Migrations Breakdown

All migration scripts reside in [backend/src/main/resources/db/migration](file:///d:/JAVA_PROJECT/backend/src/main/resources/db/migration):

| Migration Version | Description | Key Tables Created / Modified |
| :--- | :--- | :--- |
| `V1__init_schema.sql` | Base LMPC & User Schema | `users`, `roles`, `user_roles`, `products`, `scans`, `violations`, `rules` |
| `V2__audit_logs.sql` | Immutable Audit Trail | `audit_logs` |
| `V3__auth_provider_and_otp.sql` | Auth & OCR Field Confidence | `ocr_field_confidences`, `auth_identities`, `otp_codes` |
| `V4__add_batch_and_performance_indexes.sql` | B-Tree Performance Indexes | Indexes on `scans`, `violations`, `products`, `users` |
| `V5__maritime_freight_schema.sql` | **SIH26006 Maritime Freight Schema** | `maritime_ports`, `vessel_classes`, `trade_routes`, `freight_market_data`, `procurement_plans`, `freight_forecasts` |
| `V6__seed_demo_users.sql` | Production Demo Accounts | BCrypt encrypted accounts for `regulator`, `admin`, `analyst`, `reviewer` |
| `V7__production_auth_enhancements.sql` | Production Auth & Security Tokens | `failed_login_attempts`, `locked_until`, `account_status`, `security_tokens` table |

---

## 5. Seed Data Inventory

### 5.1 Maritime Ports Seeded
1. **Paradip (India):** Max Draft 17.1m, Max LOA 300m, Max Beam 48m, Capesize compatible.
2. **Visakhapatnam (India):** Max Draft 18.5m, Max LOA 320m, Max Beam 50m, Capesize / VLOC compatible.
3. **Haldia Dock Complex (India):** Max Draft 8.5m, Max LOA 230m, Max Beam 32.2m, requires Sandheads lighterage.
4. **Kamarajar - Ennore (India):** Max Draft 16.0m, Max LOA 290m, Max Beam 45m, Capesize compatible.
5. **Chennai Port (India):** Max Draft 15.5m, Max LOA 280m, Max Beam 45m, Panamax compatible.
6. **Hay Point (Australia):** Coking Coal export hub; Max Draft 19.5m, Max LOA 340m.
7. **Newcastle (Australia):** Thermal/Coking Coal export hub; Max Draft 15.2m, Max LOA 300m.
8. **Gladstone (Australia):** Metallurgical Coal export terminal; Max Draft 16.5m, Max LOA 315m.
9. **Richards Bay (South Africa):** Coal export terminal; Max Draft 17.5m, Max LOA 310m.
10. **Samarinda (Indonesia):** Sub-bituminous coal loading anchorage; Max Draft 11.5m, Supramax geared.

### 5.2 Vessel Classes Seeded
1. **Capesize:** DWT 180,000 MT, LOA 292m, Beam 45m, Draft 18.2m, Geared: No.
2. **Panamax:** DWT 75,000 MT, LOA 225m, Beam 32.2m, Draft 14.2m, Geared: No.
3. **Supramax:** DWT 58,000 MT, LOA 190m, Beam 32.2m, Draft 12.8m, Geared: Yes (4x30t cranes).
4. **Handymax:** DWT 45,000 MT, LOA 180m, Beam 28.0m, Draft 10.5m, Geared: Yes (4x25t cranes).

### 5.3 Trade Routes Seeded
1. `Hay Point -> Paradip` (4,420 nautical miles, ~13.2 steaming days)
2. `Newcastle -> Visakhapatnam` (4,850 nautical miles, ~14.4 steaming days)
3. `Gladstone -> Haldia` (4,560 nautical miles, ~13.6 steaming days via Sandheads lighterage)
4. `Richards Bay -> Paradip` (4,620 nautical miles, ~13.8 steaming days)
5. `Richards Bay -> Ennore` (4,280 nautical miles, ~12.7 steaming days)
6. `Samarinda -> Haldia` (2,180 nautical miles, ~6.5 steaming days)

---

## 6. Database Verification Commands

To verify database status and row counts:

```bash
# In PostgreSQL terminal:
psql -U postgres -d regtech -c "SELECT version();"
psql -U postgres -d regtech -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';"
psql -U postgres -d regtech -c "SELECT count(*) FROM maritime_ports;"
psql -U postgres -d regtech -c "SELECT count(*) FROM vessel_classes;"
psql -U postgres -d regtech -c "SELECT username, role, account_status FROM users;"
```

# PRODUCTION DEPLOYMENT & DEVOPS HARDENING GUIDE
## RegTech Sentinel — Enterprise Deployment Architecture

This guide details the production deployment, containerization, reverse proxy configuration, SSL/TLS hardening, and operational monitoring for RegTech Sentinel.

---

## 1. Production Architecture Overview

In a production enterprise deployment, the architecture transitions from local development servers to an orchestrated containerized topology behind an Nginx reverse proxy with TLS termination:

```
+-----------------------------------------------------------------------------------+
|                           PRODUCTION DEPLOYMENT TOPOLOGY                          |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|                                [Public Internet]                                  |
|                                        │                                          |
|                                        ▼                                          |
|                             [Edge Firewall / CDN]                                 |
|                                        │                                          |
|                                        ▼ (HTTPS:443)                              |
|                         [Nginx Reverse Proxy & TLS Engine]                        |
|                         ├── SSL/TLS Termination (TLS 1.3)                         |
|                         ├── HSTS, CSP, X-Frame-Options                            |
|                         ├── Static Asset Caching (React Bundle)                   |
|                         └── Reverse Proxy Routing                                 |
|                                │                 │                                |
|        ┌───────────────────────┘                 └───────────────────────┐        |
|        ▼ (/api/v1/**, /oauth2/**)                                        ▼ (/ai/**)
|  [Spring Boot App Cluster]                                      [Python AI Cluster]
|  ├── Java 21 LTS Container                                      ├── Python 3.14 Container
|  ├── HikariCP Connection Pool                                   ├── Uvicorn Multi-Worker  
|  └── Actuator Metrics & Logs                                    └── NumPy / SciPy Engine  
|        │                                                                          |
|        ▼ (Port 5432)                                                              |
|  [PostgreSQL 16 Cluster]                                                          |
|  ├── Primary Read/Write DB                                                        |
|  ├── Read Replicas                                                                |
|  └── Automated Daily Backups                                                      |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. Docker & Containerization

The repository provides production-ready Dockerfiles for all microservices.

### 2.1 Backend Dockerfile (`backend/Dockerfile`)
```dockerfile
# Multi-stage build for Java 21 Spring Boot
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests=true -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
COPY --from=builder /app/target/lmchecker-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

### 2.2 Frontend Dockerfile (`frontend/Dockerfile`)
```dockerfile
# Multi-stage build: Vite build -> Nginx alpine
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 2.3 AI Microservice Dockerfile (`ai-service/Dockerfile`)
```dockerfile
FROM python:3.11-slim
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends \
    tesseract-ocr \
    libtesseract-dev \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4"]
```

### 2.4 Production Docker Compose (`docker-compose.prod.yml`)
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: regtech-postgres
    restart: always
    environment:
      POSTGRES_DB: regtech
      POSTGRES_USER: ${POSTGRES_USER:-postgres}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-ChangeThisInProd!}
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - internal-net

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: regtech-backend
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/regtech
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-postgres}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-ChangeThisInProd!}
      JWT_SECRET: ${JWT_SECRET}
      AI_SERVICE_URL: http://ai-service:8000
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
    depends_on:
      - postgres
    networks:
      - internal-net

  ai-service:
    build:
      context: ./ai-service
      dockerfile: Dockerfile
    container_name: regtech-ai-service
    restart: always
    networks:
      - internal-net

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: regtech-frontend
    restart: always
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend
    networks:
      - internal-net

volumes:
  pgdata:

networks:
  internal-net:
    driver: bridge
```

---

## 3. Nginx Reverse Proxy & TLS Configuration

Below is the production Nginx configuration (`nginx.conf`):

```nginx
# Rate Limiting Zones
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=30r/s;
limit_req_zone $binary_remote_addr zone=auth_limit:10m rate=5r/s;

server {
    listen 80;
    server_name regtech.example.gov.in;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name regtech.example.gov.in;

    # SSL / TLS Hardening
    ssl_certificate /etc/letsencrypt/live/regtech.example.gov.in/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/regtech.example.gov.in/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_session_tickets off;

    # Security Headers
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' https://apis.google.com; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; connect-src 'self' https://regtech.example.gov.in;" always;

    # Static Frontend Assets
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
        expires 7d;
        add_header Cache-Control "public, no-transform";
    }

    # Backend API Routing & Rate Limiting
    location /api/ {
        limit_req zone=api_limit burst=20 nodelay;
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Strict Auth Rate Limiting
    location /api/v1/auth/ {
        limit_req zone=auth_limit burst=5 nodelay;
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 4. Operational Monitoring & Health Checks

### Health Actuator Endpoint
The backend exposes Spring Boot Actuator health and metric telemetry:
- **Health Check:** `GET /actuator/health`
  ```json
  {
    "status": "UP",
    "components": {
      "db": {"status": "UP", "details": {"database": "PostgreSQL", "validationQuery": "isValid()"}},
      "diskSpace": {"status": "UP", "details": {"free": 452938472960}},
      "ping": {"status": "UP"}
    }
  }
  ```
- **Prometheus Metrics:** `GET /actuator/prometheus` (can be scraped by Prometheus and visualized in Grafana).

---

## 5. Automated Backup & Recovery Strategy

### PostgreSQL Automated Nightly Backup Script
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/regtech"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
mkdir -p $BACKUP_DIR
pg_dump -U postgres -h localhost -d regtech -F c -b -v -f "$BACKUP_DIR/regtech_$TIMESTAMP.dump"
# Retain last 30 days of backups
find $BACKUP_DIR -type f -name "*.dump" -mtime +30 -exec rm {} +
```

### Database Restoration Command
```bash
pg_restore -U postgres -h localhost -d regtech -v "/var/backups/regtech/regtech_20261123.dump"
```

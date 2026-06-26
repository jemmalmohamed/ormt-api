#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="docker/demo/docker-compose.demo.yml"

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║           ORMT API — Demo Environment                ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# ── 1. Build the JAR ─────────────────────────────────────────────────────────
echo "▶  Building application JAR (tests skipped)..."
./mvnw clean package -DskipTests -q
echo "   JAR built: target/ormt-api.jar"
echo ""

# ── 2. Start all services ─────────────────────────────────────────────────────
echo "▶  Starting services (Postgres, Keycloak, MinIO, API)..."
docker compose -f "$COMPOSE_FILE" up --build -d
echo ""

# ── 3. Print access URLs ──────────────────────────────────────────────────────
echo "╔══════════════════════════════════════════════════════╗"
echo "║  Services ready (first boot may take ~60 seconds)   ║"
echo "║                                                      ║"
echo "║  API          http://localhost:8093                  ║"
echo "║  Swagger UI   http://localhost:8093/swagger-ui.html  ║"
echo "║  Keycloak     http://localhost:8092  (admin/admin)   ║"
echo "║  MinIO        http://localhost:9001  (minio/minio@ormt) ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
echo "  Logs:  docker compose -f $COMPOSE_FILE logs -f ormt-api"
echo "  Stop:  ./stop-demo.sh"
echo ""

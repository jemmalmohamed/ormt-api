#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="docker/demo/docker-compose.demo.yml"

echo "▶  Stopping demo environment..."
docker compose -f "$COMPOSE_FILE" down
echo "   Done."
echo ""
echo "  To also delete all data volumes:"
echo "  docker compose -f $COMPOSE_FILE down -v"
echo ""

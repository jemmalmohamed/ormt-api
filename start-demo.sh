#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WEB_DIR="${WEB_DIR:-$ROOT_DIR/../ormt-web-v1}"
PID_DIR="$ROOT_DIR/.demo-pids"
LOG_DIR="$ROOT_DIR/logs"

API_PORT="${API_PORT:-8093}"
WEB_PORT="${WEB_PORT:-3000}"
SPRING_PROFILE="${SPRING_PROFILE:-dev}"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx1024m}"
SKIP_BUILD="${SKIP_BUILD:-true}"
API_JAR="$ROOT_DIR/target/ormt-api.jar"

API_PID_FILE="$PID_DIR/api.pid"
WEB_PID_FILE="$PID_DIR/web.pid"
API_LOG="$LOG_DIR/demo-api.log"
WEB_LOG="$LOG_DIR/demo-web.log"

is_running() {
  local pid_file="$1"
  [[ -f "$pid_file" ]] && kill -0 "$(cat "$pid_file")" 2>/dev/null
}

port_is_busy() {
  local port="$1"
  timeout 1 bash -c "</dev/tcp/127.0.0.1/$port" >/dev/null 2>&1
}

require_command() {
  local command_name="$1"
  if ! command -v "$command_name" >/dev/null 2>&1; then
    echo "Missing required command: $command_name" >&2
    exit 1
  fi
}

cleanup_on_error() {
  echo ""
  echo "Startup failed. Cleaning up started processes..."
  "$ROOT_DIR/stop-demo.sh" >/dev/null 2>&1 || true
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local pid_file="$3"
  local log_file="$4"
  local attempts="$5"

  for _ in $(seq 1 "$attempts"); do
    if port_is_busy "$port"; then
      echo "   $name is ready on port $port"
      return 0
    fi

    if ! is_running "$pid_file"; then
      echo "$name stopped during startup. Last log lines:" >&2
      tail -n 40 "$log_file" >&2 || true
      return 1
    fi

    sleep 1
  done

  echo "$name did not become ready on port $port. Last log lines:" >&2
  tail -n 40 "$log_file" >&2 || true
  return 1
}

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║           ORMT — Local Demo Launcher                 ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

require_command java
require_command npm
require_command timeout

if [[ ! -d "$WEB_DIR" ]]; then
  echo "Frontend directory not found: $WEB_DIR" >&2
  echo "Override it with: WEB_DIR=/path/to/ormt-web-v1 ./start-demo.sh" >&2
  exit 1
fi

mkdir -p "$PID_DIR" "$LOG_DIR"
trap cleanup_on_error ERR

if is_running "$API_PID_FILE" || is_running "$WEB_PID_FILE"; then
  echo "Demo already appears to be running."
  echo "Stop it first with: ./stop-demo.sh"
  exit 1
fi

if port_is_busy "$API_PORT"; then
  echo "Port $API_PORT is already in use. Stop the process or set API_PORT=..." >&2
  exit 1
fi

if port_is_busy "$WEB_PORT"; then
  echo "Port $WEB_PORT is already in use. Stop the process or set WEB_PORT=..." >&2
  exit 1
fi

if [[ "$SKIP_BUILD" == "true" ]]; then
  if [[ ! -f "$API_JAR" ]]; then
    echo "API JAR not found: target/ormt-api.jar" >&2
    echo "Run once with: SKIP_BUILD=false ./start-demo.sh" >&2
    exit 1
  fi
  echo "▶  Skipping API build; using target/ormt-api.jar"
else
  echo "▶  Building API JAR (tests skipped)..."
  (cd "$ROOT_DIR" && ./mvnw clean package -DskipTests -q)
  echo "   JAR built: target/ormt-api.jar"
fi
echo ""

echo "▶  Starting API on port $API_PORT..."
(
  cd "$ROOT_DIR"
  nohup env SPRING_PROFILES_ACTIVE="$SPRING_PROFILE" \
    SERVER_PORT="$API_PORT" \
    java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar "$API_JAR" \
    >"$API_LOG" 2>&1 &
  echo $! >"$API_PID_FILE"
)
echo "   API PID: $(cat "$API_PID_FILE")"
wait_for_port "API" "$API_PORT" "$API_PID_FILE" "$API_LOG" 90
echo ""

echo "▶  Starting Angular frontend on port $WEB_PORT..."
(
  cd "$WEB_DIR"
  if [[ ! -d node_modules ]]; then
    echo "node_modules not found, running npm install first..." >>"$WEB_LOG"
    npm install >>"$WEB_LOG" 2>&1
  fi
  nohup npx ng serve --host 0.0.0.0 --port "$WEB_PORT" >"$WEB_LOG" 2>&1 &
  echo $! >"$WEB_PID_FILE"
)
echo "   Web PID: $(cat "$WEB_PID_FILE")"
wait_for_port "Angular frontend" "$WEB_PORT" "$WEB_PID_FILE" "$WEB_LOG" 120
echo ""

trap - ERR

echo "╔══════════════════════════════════════════════════════╗"
echo "║  Local demo started                                 ║"
echo "║                                                      ║"
printf "║  API          http://localhost:%-4s                 ║\n" "$API_PORT"
printf "║  Swagger UI   http://localhost:%-4s/swagger-ui.html ║\n" "$API_PORT"
printf "║  Frontend     http://localhost:%-4s                 ║\n" "$WEB_PORT"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
echo "  Infra expected from Jenkins/local stack:"
echo "    Postgres  localhost:5436"
echo "    Keycloak  localhost:8092"
echo "    MinIO     localhost:9000"
echo ""
echo "  Logs:"
echo "    tail -f $API_LOG"
echo "    tail -f $WEB_LOG"
echo "  Stop:  ./stop-demo.sh"
echo ""

#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_DIR="$ROOT_DIR/.demo-pids"

stop_process() {
  local name="$1"
  local pid_file="$2"

  if [[ ! -f "$pid_file" ]]; then
    echo "   $name: not running (no PID file)"
    return
  fi

  local pid
  pid="$(cat "$pid_file")"

  if kill -0 "$pid" 2>/dev/null; then
    echo "   Stopping $name (PID $pid)..."
    kill "$pid"

    for _ in {1..20}; do
      if ! kill -0 "$pid" 2>/dev/null; then
        break
      fi
      sleep 0.5
    done

    if kill -0 "$pid" 2>/dev/null; then
      echo "   $name did not stop cleanly, forcing..."
      kill -9 "$pid" 2>/dev/null || true
    fi
  else
    echo "   $name: stale PID $pid"
  fi

  rm -f "$pid_file"
}

echo "▶  Stopping demo environment..."
stop_process "Angular frontend" "$PID_DIR/web.pid"
stop_process "ORMT API" "$PID_DIR/api.pid"
echo "   Done."
echo ""

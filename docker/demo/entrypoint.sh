#!/bin/sh
MARKER=/state/.initialized

if [ ! -f "$MARKER" ]; then
  echo "[demo] Premier démarrage — seed activé"
  export STARTER_DATABASE_RESET=true
  export STARTER_DATABASE_SEED=true
  export STARTER_MINIO_RESET=true
  export STARTER_MINIO_SEED=true
  export STARTER_KEYCLOAK_RESET=true
  export STARTER_KEYCLOAK_SEED=true
  touch "$MARKER"
else
  echo "[demo] Données existantes — seed désactivé"
  export STARTER_DATABASE_RESET=false
  export STARTER_DATABASE_SEED=false
  export STARTER_MINIO_RESET=false
  export STARTER_MINIO_SEED=false
  export STARTER_KEYCLOAK_RESET=false
  export STARTER_KEYCLOAK_SEED=false
fi

exec java -Djava.security.egd=file:/dev/./urandom -jar /ormt-api.jar

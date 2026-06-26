# ORMT — Local Demo Environment

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java  | 25+             |
| Node.js / npm | requis pour Angular |
| Maven | inclus via `./mvnw` |

L'infrastructure doit déjà être disponible, par exemple via Jenkins ou une stack locale :

| Service   | URL attendue              |
|-----------|---------------------------|
| PostgreSQL | `localhost:5436`        |
| Keycloak  | `http://localhost:8092`   |
| MinIO     | `http://localhost:9000`   |

---

## Démarrage

```bash
git clone <url-du-repo>
cd ormt-api
./start-demo.sh
```

Le script fait automatiquement :
1. Réutilisation du JAR existant (`target/ormt-api.jar`)
2. Démarrage de l'API avec `java -jar target/ormt-api.jar`
3. Démarrage du frontend Angular depuis `../ormt-web-v1` sur le port `3000`

Si le JAR n'existe pas encore, lancez une première fois avec compilation :

```bash
SKIP_BUILD=false ./start-demo.sh
```

---

## Accès

| Service   | URL                                        | Credentials          |
|-----------|--------------------------------------------|----------------------|
| API       | http://localhost:8093                      | —                    |
| Swagger   | http://localhost:8093/swagger-ui.html      | —                    |
| Frontend  | http://localhost:3000                      | —                    |
| Keycloak  | http://localhost:8092                      | admin / admin        |
| MinIO     | http://localhost:9001                      | minio / minio@ormt   |

---

## Commandes utiles

```bash
# Suivre les logs de l'API
tail -f logs/demo-api.log

# Suivre les logs Angular
tail -f logs/demo-web.log

# Arrêter l'API et Angular
./stop-demo.sh

# Changer les ports si besoin
API_PORT=8094 WEB_PORT=3001 ./start-demo.sh

# Forcer la recompilation du JAR avant le démarrage
SKIP_BUILD=false ./start-demo.sh

# Changer le chemin du frontend si besoin
WEB_DIR=/path/to/ormt-web-v1 ./start-demo.sh
```

---

## Notes WSL

Angular est lancé avec `--host 0.0.0.0`, donc la page reste accessible depuis le navigateur Windows via `http://localhost:3000`.
L'API utilise le profil `dev` par défaut. Les données ne sont pas réinitialisées par ces scripts.

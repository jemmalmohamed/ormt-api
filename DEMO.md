# ORMT API — Demo Environment

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java  | 25+             |
| Docker | 20.10+         |
| Maven | inclus via `./mvnw` |

---

## Démarrage

```bash
git clone <url-du-repo>
cd ormt-api
./start-demo.sh
```

Le script fait automatiquement :
1. Build du JAR (`mvnw clean package -DskipTests`)
2. Démarrage de tous les services Docker en arrière-plan

---

## Accès (disponible ~60s après le démarrage)

| Service   | URL                                        | Credentials          |
|-----------|--------------------------------------------|----------------------|
| API       | http://localhost:8093                      | —                    |
| Swagger   | http://localhost:8093/swagger-ui.html      | —                    |
| Keycloak  | http://localhost:8092                      | admin / admin        |
| MinIO     | http://localhost:9001                      | minio / minio@ormt   |

---

## Commandes utiles

```bash
# Suivre les logs de l'API
docker compose -f docker/demo/docker-compose.demo.yml logs -f ormt-api

# Arrêter l'environnement
./stop-demo.sh

# Arrêter et supprimer toutes les données
docker compose -f docker/demo/docker-compose.demo.yml down -v
```

---

## Comportement des données

### Premier démarrage
L'API détecte automatiquement qu'aucune donnée n'existe et initialise :
- Le schéma de base de données (migrations Flyway)
- Le realm Keycloak avec les clients et rôles
- Les buckets MinIO
- Les données initiales (seed)

```
[demo] Premier démarrage — seed activé
```

### Redémarrages suivants
Les starters sont automatiquement désactivés — les données existantes sont préservées.

```
[demo] Données existantes — seed désactivé
```

Cela signifie que redémarrer l'environnement (`./stop-demo.sh` puis `./start-demo.sh`) ne réinitialise pas les données.

### Repartir de zéro

```bash
docker compose -f docker/demo/docker-compose.demo.yml down -v
./start-demo.sh
```

Le flag `-v` supprime tous les volumes Docker (base de données, Keycloak, MinIO, marqueur d'initialisation). Le prochain démarrage repart d'un état vide.

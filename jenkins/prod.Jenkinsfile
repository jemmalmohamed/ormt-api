

// Define methods for common tasks
def toolVerification() {
  try {
    sh 'docker version'
    sh 'docker info'
    sh 'docker compose version'
    sh 'curl --version'
    sh 'mvn -v'
    sh 'java -version'
  } catch (Exception e) {
    echo "Error in verifying tooling: ${e.getMessage()}"
    throw e // Rethrow exception to fail the build
  }
}

// keycloak containers
def pruneKeycloakContainers() {
  try {
    sh '''
      # Prune keycloak containers
      docker compose --env-file ./docker/services/keycloak/env/.env.prod \
      -f ./docker/services/keycloak/docker-compose.kc.base.yml \
      -f ./docker/services/keycloak/docker-compose.kc.prod.yml \
      down -v
    '''
  } catch (Exception e) {
     echo "Error in pruning Keycloak containers: ${e.getMessage()}"
     throw e
  }
}
def runKeycloakContainer() {
  try {
    sh '''
      # Build keycloak image
      docker compose --env-file  ./docker/services/keycloak/env/.env.prod \
      -f ./docker/services/keycloak/docker-compose.kc.base.yml \
      -f ./docker/services/keycloak/docker-compose.kc.prod.yml \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in building Keycloak image: ${e.getMessage()}"
     throw e
  }
}

 

 


// api database containers
def pruneApiDatabaseContainer() {
  try {
    sh '''
      # Prune postgres container
      docker compose --env-file  ./docker/services/postgres/env/.env.prod \
      -f ./docker/services/postgres/docker-compose.postgres.base.yml \
      -f ./docker/services/postgres/docker-compose.postgres.prod.yml \
      down -v
    '''
  } catch (Exception e) {
     echo "Error in pruning API database containers: ${e.getMessage()}"
     throw e
  }
}

def runApiDatabaseContainer() {
  try {
    sh '''
      # Build postgres image
      docker compose --env-file  ./docker/services/postgres/env/.env.prod \
      -f ./docker/services/postgres/docker-compose.postgres.base.yml \
      -f ./docker/services/postgres/docker-compose.postgres.prod.yml \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in building API database image: ${e.getMessage()}"
     throw e
  }
}

// ormt api containers
def pruneOrmtApiContainer() {
  try {
    sh '''
      # Prune ormt-api containers
      docker compose --env-file  ./docker/app/env/.env.prod \
      -f ./docker/app/docker-compose.ormt-api.base.yml \
      -f ./docker/app/docker-compose.ormt-api.prod.yml \
      --project-name ormt-api \
      down -v
    '''
  } catch (Exception e) {
     echo "Error in pruning ormt-api containers: ${e.getMessage()}"
     throw e
  }
}

def buildOrmtApi() {
  try {
    sh '''
     mvn clean install -Pprod    -DskipTests
    '''
  } catch (Exception e) {
     echo "Error in building ormt-api image: ${e.getMessage()}"
     throw e
  }
}

def buildOrmtApiImage() {
  try {
    sh '''
      # Build ormt-api image
       docker build -t  $IMAGE_TAG .
    '''
  } catch (Exception e) {
     echo "Error in building ormt-api image: ${e.getMessage()}"
     throw e
  }
}

def runOrmtApiContainer() {
  try {
    sh '''
      # Build ormt-api image
      docker compose --env-file  ./docker/app/env/.env.prod \
      -f ./docker/app/docker-compose.ormt-api.base.yml \
      -f ./docker/app/docker-compose.ormt-api.prod.yml \
      --project-name ormt-api \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in runing ormt-api container: ${e.getMessage()}"
     throw e
  }
}

pipeline {
  agent { node { label 'vps-ormt-ms.net' } }  
  parameters {
    
    booleanParam(name: 'prune_keycloak', defaultValue: false, description: 'Prune Keycloak containers individually')
    booleanParam(name: 'run_keycloak', defaultValue: false, description: 'Run Keycloak container individually')
     booleanParam(name: 'prune_postgres', defaultValue: false, description: 'Prune Postgres container individually')
    booleanParam(name: 'run_postgres', defaultValue: false, description: 'Run Postgres container individually')
    booleanParam(name: 'prune_ormt_api', defaultValue: true, description: 'Prune ormt-api container before building and deploying')
    booleanParam(name: 'build_ormt_api', defaultValue: true, description: 'Build ormt-api before building and deploying')
    booleanParam(name: 'build_ormt_api_image', defaultValue: true, description: 'Build ormt-api image before building and deploying')
    booleanParam(name: 'run_ormt_api', defaultValue: true, description: 'Run ormt-api')
    }
  tools {
        maven 'maven' 
    }
  environment {
        IMAGE_TAG = 'ormt/ormt-api:latest'
    }
  stages {

    
    stage("Verify tooling"){
      when {  expression { params.verify_tooling }       }
      steps{
          script {
            toolVerification()
          }
      }
    }


    stage("Build and Deploy App Services") {
      // when {  expression {  params.build_services  }  }
       parallel{
          stage("Build Keycloak containers"){
           when {  expression {  params.prune_keycloak || params.run_keycloak  }  }
           steps{
             script {
               if (params.prune_keycloak) {
                 pruneKeycloakContainers()
               }
               if (params.run_keycloak) {
                 runKeycloakContainer()
               }
             }
      }
    }

  
       
        stage("Build Postgres container"){
          when {  expression {  params.prune_postgres || params.run_postgres  }  }
           steps{
            script {
              if (params.prune_postgres) {
                pruneApiDatabaseContainer()
              }
              if (params.run_postgres) {
                runApiDatabaseContainer()
              }
        }
      }
    }
      }
     }



     // ormt api
    stage("Prune ormt-api container"){
      when {  expression {  params.prune_ormt_api  }  }
      steps{
        script {
            pruneOrmtApiContainer()
        }
       }
     }

      stage("Build ormt-api jar"){
        when {  expression {  params.build_ormt_api  }  }
        steps{
          script {
              buildOrmtApi()  
          }
        }
      }

      stage("Build ormt-api docker image"){
        when {  expression {  params.build_ormt_api_image  }  }
        steps{
          script {
              buildOrmtApiImage()
          }
        }
      }
      stage(" Run ormt-api container"){
        when {  expression {  params.run_ormt_api  }  }
        steps{
          script {
              runOrmtApiContainer()
          }
        }
      }
      
  }
}
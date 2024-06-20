

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
      docker compose --env-file ./docker/services/keycloak/env/.env.stage \
      -f ./docker/services/keycloak/docker-compose.kc.base.yml \
      -f ./docker/services/keycloak/docker-compose.kc.stage.yml \
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
      docker compose --env-file  ./docker/services/keycloak/env/.env.stage \
      -f ./docker/services/keycloak/docker-compose.kc.base.yml \
      -f ./docker/services/keycloak/docker-compose.kc.stage.yml \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in building Keycloak image: ${e.getMessage()}"
     throw e
  }
}

// geoserver containers
def pruneGeoserverContainer() {
  try {
    sh '''
      # Prune geoserver container
      docker compose --env-file ./docker/services/geoserver/env/.env.stage \
      -f ./docker/services/geoserver/docker-compose.geoserver.base.yml \
      -f ./docker/services/geoserver/docker-compose.geoserver.stage.yml \
      down -v
    '''
  } catch (Exception e) {
     echo "Error in pruning Geoserver containers: ${e.getMessage()}"
     throw e
  }
}

def runGeoserverContainer() {
  try {
    sh '''
      # Build geoserver image
      docker compose --env-file  ./docker/services/geoserver/env/.env.stage \
      -f ./docker/services/geoserver/docker-compose.geoserver.base.yml \
      -f ./docker/services/geoserver/docker-compose.geoserver.stage.yml \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in building Geoserver image: ${e.getMessage()}"
     throw e
  }
}


def pruneApiDatabaseContainer() {
  try {
    sh '''
      # Prune sqlserver container
      docker compose --env-file  ./docker/services/sqlserver/env/.env.stage \
      -f ./docker/services/sqlserver/docker-compose.sqlserver.base.yml \
      -f ./docker/services/sqlserver/docker-compose.sqlserver.stage.yml \
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
      # Build sqlserver image
      sh 'chmod +x ./services/sqlserver/env/init-db-stage.sh'
      docker compose --env-file  ./docker/services/sqlserver/env/.env.stage \
      -f ./docker/services/sqlserver/docker-compose.sqlserver.base.yml \
      -f ./docker/services/sqlserver/docker-compose.sqlserver.stage.yml \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in building API database image: ${e.getMessage()}"
     throw e
  }
}
 

// pva api containers
def prunePvaApiContainer() {
  try { 
    sh '''
      # Prune pva-api containers
      docker compose --env-file  ./docker/app/env/.env.stage \
      -f ./docker/app/docker-compose.pva-api.base.yml \
      -f ./docker/app/docker-compose.pva-api.stage.yml \
      --project-name pva-api \
      down -v

      docker rmi -f $IMAGE_TAG || true
     '''
  } catch (Exception e) {
     echo "Error in pruning pva-api containers: ${e.getMessage()}"
     throw e
  }
}

def buildPvaApi() {
  try {
    sh '''
     mvn clean install   
    '''
  } catch (Exception e) {
     echo "Error in building pva-api image: ${e.getMessage()}"
     throw e
  }
}

def buildPvaApiImage() {
  try {
    sh '''
      # Build pva-api image
       docker build -t  $IMAGE_TAG .
    '''
  } catch (Exception e) {
     echo "Error in building pva-api image: ${e.getMessage()}"
     throw e
  }
}

def runPvaApiContainer() {
  try {
    sh '''
      # Build pva-api image
      docker compose --env-file  ./docker/app/env/.env.stage \
      -f ./docker/app/docker-compose.pva-api.base.yml \
      -f ./docker/app/docker-compose.pva-api.stage.yml \
      --project-name pva-api \
      up -d
    '''
  } catch (Exception e) {
     echo "Error in runing pva-api container: ${e.getMessage()}"
     throw e
  }
}

pipeline {
  agent { node { label 'dev-host' } }  
  parameters {
    
    booleanParam(name: 'prune_keycloak', defaultValue: false, description: 'Prune Keycloak containers individually')
    booleanParam(name: 'run_keycloak', defaultValue: false, description: 'Run Keycloak container individually')
    booleanParam(name: 'prune_geoserver', defaultValue: false, description: 'Prune Geoserver container individually')
    booleanParam(name: 'run_geoserver', defaultValue: false, description: 'Run Geoserver container individually')
    booleanParam(name: 'prune_sqlserver', defaultValue: false, description: 'Prune Sql Server container individually')
    booleanParam(name: 'run_sqlserver', defaultValue: false, description: 'Run  Sql Server container individually')
    booleanParam(name: 'prune_pva_api', defaultValue: false, description: 'Prune pva-api container before building and deploying')
    booleanParam(name: 'build_pva_api', defaultValue: false, description: 'Build pva-api before building and deploying')
    booleanParam(name: 'build_pva_api_image', defaultValue: false, description: 'Build pva-api image before building and deploying')
    booleanParam(name: 'run_pva_api', defaultValue: false, description: 'Run pva-api')
    }
  tools {
        maven 'maven' 
    }
  environment {
        IMAGE_TAG = 'ancfcc/pva-api:latest'
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

       stage("Build Geoserver container"){
      when {  expression {  params.prune_geoserver || params.run_geoserver  }  }
      steps{
        script {
          if (params.prune_geoserver) {
            pruneGeoserverContainer()
          }
          if (params.run_geoserver) {
            runGeoserverContainer()
          }
        }
      }
    }
       
        stage("Build Sql Server container"){
          when {  expression {  params.prune_sqlserver || params.run_sqlserver  }  }
           steps{
            script {
              if (params.prune_sqlserver) {
                pruneApiDatabaseContainer()
              }
              if (params.run_sqlserver) {
                runApiDatabaseContainer()
              }
        }
      }
    }
      }
     }



     // pva api
    stage("Prune pva-api container"){
      when {  expression {  params.prune_pva_api  }  }
      steps{
        script {
            prunePvaApiContainer()
        }
       }
     }

      stage("Build pva-api jar"){
        when {  expression {  params.build_pva_api  }  }
        steps{
          script {
              buildPvaApi()  
          }
        }
      }

      stage("Build pva-api docker image"){
        when {  expression {  params.build_pva_api_image  }  }
        steps{
          script {
              buildPvaApiImage()
          }
        }
      }
      stage(" Run pva-api container"){
        when {  expression {  params.run_pva_api  }  }
        steps{
          script {
              runPvaApiContainer()
          }
        }
      }
      
  }
}
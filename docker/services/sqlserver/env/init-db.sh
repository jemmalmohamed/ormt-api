#!/bin/bash

# Print environment variables
echo "SQLSERVER_API_PASSWORD: ${SQLSERVER_API_PASSWORD}"
echo "SQLSERVER_API_DB: ${SQLSERVER_API_DB}"

# Wait for SQL Server to start
echo "Waiting for SQL Server to start..."
timeout=30  # Timeout in seconds 
interval=3  # Sleep interval in seconds
elapsed=0

until /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "SELECT 1" > /dev/null 2>&1; do
  echo "Waiting for SQL Server to be available..."
  sleep $interval
  elapsed=$((elapsed + interval))
  if [ $elapsed -ge $timeout ]; then
    echo "SQL Server took too long to start. Exiting."
    exit 1
  fi
done

echo "SQL Server is available. Running initialization commands."

# Run SQL commands to create the database and enable spatial support
echo "Creating database: ${SQLSERVER_API_DB}"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "CREATE DATABASE [${SQLSERVER_API_DB}]"

echo "Creating table: SpatialTable"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -d "${SQLSERVER_API_DB}" -Q "CREATE TABLE SpatialTable (ID INT PRIMARY KEY, Location GEOMETRY)"

echo "Initialization script completed."

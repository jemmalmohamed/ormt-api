#!/bin/bash

FLAG_FILE="/var/opt/mssql/data/.init-db-completed"

# Check if the initialization has already been completed
if [ -f "${FLAG_FILE}" ]; then
  echo "Initialization already completed. Exiting."
  exit 0
fi

 

# Wait for SQL Server to start
echo "Waiting for SQL Server to start..."
timeout=10  # Timeout in seconds
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

# Run SQL commands to create the database
echo "Creating database: ${SQLSERVER_API_DB}"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "CREATE DATABASE [${SQLSERVER_API_DB}]"

# Create a flag file to indicate that the initialization has been completed
touch "${FLAG_FILE}"

echo "Initialization script completed."

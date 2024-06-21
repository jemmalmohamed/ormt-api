#!/bin/bash

# Log the start time
echo "Initialization script started at $(date)"

# Check if the init script has already run
if [ -f /var/opt/mssql/data/.init_done ]; then
  echo "Database already initialized"
  exit 0
fi

# Wait for SQL Server to start up
echo "Waiting for SQL Server to start..."
sleep 30s

# Run your initialization SQL scripts
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "IF DB_ID('${SQLSERVER_API_DB}') IS NULL CREATE DATABASE [${SQLSERVER_API_DB}]"

# Check if the command succeeded
if [ $? -eq 0 ]; then
  # Create a flag file to indicate that the initialization has been done
  touch /var/opt/mssql/data/.init_done
  echo "Initialization script completed."
else
  echo "Initialization script failed."
  exit 1
fi

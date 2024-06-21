#!/bin/bash

# Check if the init script has already run
if [ -f /var/opt/mssql/data/.init_done ]; then
  echo "Database already initialized"
  exit 0
fi

# Run your initialization SQL scripts
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "IF DB_ID('${SQLSERVER_API_DB}') IS NULL CREATE DATABASE [${SQLSERVER_API_DB}]"

# Create a flag file to indicate that the initialization has been done
touch /var/opt/mssql/data/.init_done

echo "Initialization script completed."

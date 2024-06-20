#!/bin/bash

 

echo "SQL Server is available. Running initialization commands."

# Run SQL commands to create the database
echo "Creating database: ${SQLSERVER_API_DB}"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "CREATE DATABASE [${SQLSERVER_API_DB}]"

 

echo "Initialization script completed."

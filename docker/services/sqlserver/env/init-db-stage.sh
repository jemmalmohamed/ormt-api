#!/bin/bash

echo "SQL Server is available. Running initialization commands."

# Run SQL commands to create the database
echo "Creating database: ${SQLSERVER_API_DB}"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}" -Q "CREATE DATABASE [${SQLSERVER_API_DB}]"

if [ $? -eq 0 ]; then
    echo "Database ${SQLSERVER_API_DB} created successfully."
else
    echo "Error creating database ${SQLSERVER_API_DB}."
    exit 1
fi

echo "Initialization script completed."
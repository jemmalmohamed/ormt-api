#!/bin/bash

echo "SQL Server is available. Running initialization commands."

# Check if the database already exists

/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${SQLSERVER_API_PASSWORD}"-Q "IF DB_ID('${SQLSERVER_API_DB}') IS NULL CREATE DATABASE [ ${SQLSERVER_API_DB}]"

if [ $? -eq 0 ]; then

     echo "Database ${SQLSERVER_API_DB} created successfully or already exists."

else

    echo "Error creating database ${SQLSERVER_API_DB}."
    
exit 1
fi

echo "Initialization script completed."
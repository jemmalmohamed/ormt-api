#!/bin/bash

# Wait for SQL Server to start
echo "Waiting for SQL Server to start..."
sleep 30s


echo "SQL Server is available. Running initialization commands."

# Run SQL commands to create the database and enable spatial support
echo "Creating database: $SQLSERVER_API_DB"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SQLSERVER_API_PASSWORD" -Q "CREATE DATABASE [$SQLSERVER_API_DB]"

echo "Creating table: SpatialTable"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SQLSERVER_API_PASSWORD" -d "$SQLSERVER_API_DB" -Q "CREATE TABLE SpatialTable (ID INT PRIMARY KEY, Location GEOMETRY)"

echo "Initialization script completed."

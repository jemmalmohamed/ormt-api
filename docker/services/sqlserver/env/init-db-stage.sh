#!/bin/bash

# Wait for SQL Server to start
echo "Waiting for SQL Server to start..."
sleep 30s

# Run SQL commands to create the database and enable spatial support
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SQLSERVER_API_PASSWORD -d master -Q "CREATE DATABASE $SQLSERVER_API_DB"
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SQLSERVER_API_PASSWORD -d $SQLSERVER_API_DB -Q "CREATE TABLE SpatialTable (ID INT PRIMARY KEY, Location GEOMETRY)"

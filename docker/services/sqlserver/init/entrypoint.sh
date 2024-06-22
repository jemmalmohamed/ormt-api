#!/bin/bash
# entrypoint.sh

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &
# Wait for  SQL Server to start
sleep 30 s
# Run the initialization script
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P pva@database2024 -i /docker-entrypoint-initdb.d/init.sql
# Wait  for  SQL Server to exit
wait

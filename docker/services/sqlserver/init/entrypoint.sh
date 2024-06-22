#!/bin/bash

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to start (use a loop to check when SQL Server is ready)
for i in {1..30}; do
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P pva@database2024 -Q "SELECT 1" &>/dev/null
    if [ $? -eq 0 ]; then
        echo "SQL Server is up and running!"
        break
    else
        echo "Waiting for SQL Server to start..."
        sleep 1
    fi
done

# Run the initialization script
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P pva@database2024 -i /docker-entrypoint-initdb.d/init.sql

# Wait for SQL Server to exit
wait

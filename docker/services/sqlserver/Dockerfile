FROM mcr.microsoft.com/mssql/server:2022-latest

# Accept EULA
ARG ACCEPT_EULA
ENV ACCEPT_EULA=${ACCEPT_EULA}

# Set SA password
ARG SA_PASSWORD
ENV SA_PASSWORD=${SA_PASSWORD}

# Set SQL Server API DB name
ARG SQLSERVER_API_DB
ENV SQLSERVER_API_DB=${SQLSERVER_API_DB}

# Start SQL Server and run the initialization commands
CMD /bin/bash -c "\
    /opt/mssql/bin/sqlservr & \
    sleep 30 && \
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P ${SA_PASSWORD} -Q \"IF DB_ID('${SQLSERVER_API_DB}') IS NULL CREATE DATABASE [${SQLSERVER_API_DB}]\" && \
    if [ $? -eq 0 ]; then \
    touch /var/opt/mssql/data/.init_done && \
    echo 'Initialization script completed.'; \
    else \
    echo 'Initialization script failed.'; \
    fi"

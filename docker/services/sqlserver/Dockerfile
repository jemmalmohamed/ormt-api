FROM mcr.microsoft.com/mssql/server:2022-latest

# Accept EULA
ARG ACCEPT_EULA
ENV ACCEPT_EULA=${ACCEPT_EULA}

# Set SA password
ARG SA_PASSWORD
ENV SA_PASSWORD=${SA_PASSWORD}

# Copy initialization script from the correct location
COPY --chmod=0755 ./env/init-db-stage.sh /docker-entrypoint-initdb.d/

# Start SQL Server and run the initialization script
CMD /bin/bash -c "/opt/mssql/bin/sqlservr & sleep 30 && /docker-entrypoint-initdb.d/init-db-stage.sh"

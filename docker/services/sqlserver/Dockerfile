FROM mcr.microsoft.com/mssql/server:2022-latest

# Accept EULA
ARG ACCEPT_EULA
ENV ACCEPT_EULA=${ACCEPT_EULA}

# Set SA password
ARG SA_PASSWORD
ENV SA_PASSWORD=${SA_PASSWORD}

# Copy initialization script
COPY ./env/init-db-stage.sh /docker-entrypoint-initdb.d/

# Grant execute permission on the initialization script
RUN chmod +x /docker-entrypoint-initdb.d/init-db.sh

# Start SQL Server and run the initialization script
CMD /bin/bash -c "/opt/mssql/bin/sqlservr & sleep 30 && /docker-entrypoint-initdb.d/init-db.sh"

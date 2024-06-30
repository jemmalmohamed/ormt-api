-- CREATE GEOMETRY COLUMNS
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='geometry_columns' and xtype='U')
BEGIN
    CREATE TABLE geometry_columns
    (
        f_table_catalog VARCHAR(256),
        f_table_schema VARCHAR(256),
        f_table_name VARCHAR(256),
        f_geometry_column VARCHAR(256),
        coord_dimension INTEGER,
        srid INTEGER,
        geometry_type VARCHAR(30) ,
        geometry_columns_id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    );

    -- Insert the geometry column metadata into the Geometry Columns Table 
    INSERT INTO geometry_columns
        ( f_table_catalog, f_table_schema, f_table_name, f_geometry_column, geometry_type, coord_dimension, srid )
    VALUES
        ( 'pva', 'dbo', 'mission', 'delimitation', 'MULTIPOLYGON', 2 , 4326);
END;


-- CREATE PLAN D'ACTIONS TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='plan_action' and xtype='U') 
BEGIN
    CREATE TABLE plan_action
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        nom varchar(255) NOT NULL,
        description varchar(255) NULL,
        debut_date DATE NOT NULL,
        fin_date DATE NOT NULL,
        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;

-- CREATE PLAN D'ACTIONS TABLE

IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='basemap' and xtype='U') 
BEGIN
    CREATE TABLE basemap
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        nom varchar(255) NOT NULL,
        url varchar(255) NULL,
        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;

-- CREATE ORGANISME TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='organisme' and xtype='U')
BEGIN
    CREATE TABLE organisme
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        nom varchar(255) NOT NULL,
        secteur varchar(255) NOT NULL,

        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;

--  CREATE CAPTEUR TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='capteur' and xtype='U')
BEGIN
    CREATE TABLE capteur
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        nom varchar(255) NOT NULL,
        categorie varchar(255) NOT NULL,
        serial varchar(255) NULL,
        mode varchar(255) NOT NULL,
        description varchar(255) NULL,
        format varchar(255) NOT NULL,
        constructeur varchar(255) NULL,

        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;


--  CREATE AVION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='avion' and xtype='U')
BEGIN
    CREATE TABLE avion
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,

        matricule varchar(255) NOT NULL,
        marque varchar(255) NULL,
        modele varchar(255) NOT NULL,

        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;

--  CREATE MISSION OBJET TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='objet' and xtype='U')
BEGIN
    CREATE TABLE objet
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,

        nom varchar(255) NOT NULL,
        description varchar(255) NULL,

        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL
    );
END;
--  CREATE MISSION  TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='mission' and xtype='U')
BEGIN
    CREATE TABLE mission
    (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,

        nom varchar(255) NOT NULL,
        code varchar(255) NOT NULL,
        etat varchar(255) NULL,
        date_pva DATE NULL,
        superficie FLOAT NULL,


        description varchar(255) NULL,
        delimitation GEOMETRY NULL,

        organisme_id UNIQUEIDENTIFIER NOT NULL,
        CONSTRAINT FK_mission_organisme FOREIGN KEY (organisme_id) REFERENCES organisme(id),
        plan_action_id UNIQUEIDENTIFIER NOT NULL,
        CONSTRAINT FK_mission_plan_action FOREIGN KEY (plan_action_id) REFERENCES plan_action(id),

        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL


    );
END;


IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='mission_objet' and xtype='U')
BEGIN
    CREATE TABLE mission_objet
    (
        mission_id UNIQUEIDENTIFIER NOT NULL,
        objet_id UNIQUEIDENTIFIER NOT NULL,
        CONSTRAINT FK_mission_objet_mission FOREIGN KEY (mission_id) REFERENCES mission(id),
        CONSTRAINT FK_mission_objet_objet FOREIGN KEY (objet_id) REFERENCES objet(id),
        PRIMARY KEY (mission_id, objet_id)
    );
END;
 

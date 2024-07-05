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
        geometry_type VARCHAR(30),
        geometry_columns_id BIGINT IDENTITY(1,1) PRIMARY KEY
    );
    -- Insert the geometry column metadata into the Geometry Columns Table  
    INSERT INTO geometry_columns
        (f_table_catalog, f_table_schema, f_table_name, f_geometry_column, geometry_type, coord_dimension, srid)
    VALUES
        ('pva', 'dbo', 'mission', 'delimitation', 'MULTIPOLYGON', 2 , 4326),
        ('pva', 'dbo', 'bande', 'axe_planification', 'LINESTRING', 2 , 4326),
        ('pva', 'dbo', 'scan_execution', 'emprise', 'POLYGON', 2 , 4326),
        ('pva', 'dbo', 'photo_planification', 'centre', 'POINT', 2 , 4326),
        ('pva', 'dbo', 'photo_orientation', 'centre', 'POINT', 2 , 4326),
        ('pva', 'dbo', 'photo_execution', 'emprise', 'POLYGON', 2 , 4326);
END;

-- CREATE PLAN D'ACTIONS TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='plan_action' and xtype='U')
BEGIN
    CREATE TABLE plan_action
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        description VARCHAR(255) NULL,
        debut_date DATE NOT NULL,
        fin_date DATE NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE BASEMAP TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='basemap' and xtype='U')
BEGIN
    CREATE TABLE basemap
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        url VARCHAR(255) NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE ORGANISME TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='organisme' and xtype='U')
BEGIN
    CREATE TABLE organisme
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        secteur VARCHAR(255) NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE CAPTEUR TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='capteur' and xtype='U')
BEGIN
    CREATE TABLE capteur
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        code VARCHAR(255) NOT NULL,
        categorie VARCHAR(255) NOT NULL,
        serial VARCHAR(255) NULL,
        mode VARCHAR(255) NOT NULL,
        description VARCHAR(255) NULL,
        format VARCHAR(255) NOT NULL,
        constructeur VARCHAR(255) NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE AVION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='avion' and xtype='U')
BEGIN
    CREATE TABLE avion
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        matricule VARCHAR(255) NOT NULL,
        marque VARCHAR(255) NULL,
        modele VARCHAR(255) NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE OBJET TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='objet' and xtype='U')
BEGIN
    CREATE TABLE objet
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        description VARCHAR(255) NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL
    );
END;

-- CREATE MISSION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='mission' and xtype='U')
BEGIN
    CREATE TABLE mission
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        code VARCHAR(255) NOT NULL,
        etat VARCHAR(255) NULL,
        date_pva DATE NULL,
        superficie FLOAT NULL,
        description VARCHAR(255) NULL,
        delimitation GEOMETRY NULL,
        organisme_id BIGINT NOT NULL,
        capteur_id BIGINT NOT NULL,
        plan_action_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_mission_organisme FOREIGN KEY (organisme_id) REFERENCES organisme(id),
        CONSTRAINT FK_mission_capteur FOREIGN KEY (capteur_id) REFERENCES capteur(id),
        CONSTRAINT FK_mission_plan_action FOREIGN KEY (plan_action_id) REFERENCES plan_action(id)
    );
END;

-- CREATE MISSION OBJET TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='mission_objet' and xtype='U')
BEGIN
    CREATE TABLE mission_objet
    (
        mission_id BIGINT NOT NULL,
        objet_id BIGINT NOT NULL,
        CONSTRAINT FK_mission_objet_mission FOREIGN KEY (mission_id) REFERENCES mission(id),
        CONSTRAINT FK_mission_objet_objet FOREIGN KEY (objet_id) REFERENCES objet(id),
        PRIMARY KEY (mission_id, objet_id)
    );
END;

-- CREATE NUMERIQUE ATTRIBUT TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='numerique_attribut' and xtype='U')
BEGIN
    CREATE TABLE numerique_attribut
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        resolution INT NULL,
        mission_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_numerique_attribut_mission FOREIGN KEY(mission_id) REFERENCES mission(id)
    );
END;

-- CREATE ANALOGIQUE ATTRIBUT TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='analogique_attribut' and xtype='U')
BEGIN
    CREATE TABLE analogique_attribut
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        echelle INT NULL,
        mission_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_analogique_attribut_mission FOREIGN KEY(mission_id) REFERENCES mission(id)
    );
END;

-- CREATE LIDAR ATTRIBUT TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='lidar_attribut' and xtype='U')
BEGIN
    CREATE TABLE lidar_attribut
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        densite FLOAT NULL,
        mission_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_lidar_attribut_mission FOREIGN KEY(mission_id) REFERENCES mission(id)
    );
END;

-- CREATE BANDE TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='bande' and xtype='U')
BEGIN
    CREATE TABLE bande
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        label VARCHAR(255) NOT NULL,
        observation VARCHAR(255) NULL,
        axe_planification GEOMETRY NULL,
        mission_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_bande_mission FOREIGN KEY(mission_id) REFERENCES mission(id)
    );
END;

-- CREATE PHOTO PLANIFICATION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='photo_planification' and xtype='U')
BEGIN
    CREATE TABLE photo_planification
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        label VARCHAR(255) NOT NULL,
        observation VARCHAR(255) NULL,
        centre GEOMETRY NULL,
        bande_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_photo_planification_bande FOREIGN KEY(bande_id) REFERENCES bande(id)
    );
END;

-- CREATE PHOTO EXECUTION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='photo_execution' and xtype='U')
BEGIN
    CREATE TABLE photo_execution
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        observation VARCHAR(255) NULL,
        emprise GEOMETRY NULL,
        date_pva DATE NULL,
        bobine VARCHAR(255) NULL,
        photo_planification_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_photo_execution_photo_planification FOREIGN KEY(photo_planification_id) REFERENCES photo_planification(id)
    );
END;

-- CREATE PHOTO ORIENTATION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='photo_orientation' and xtype='U')
BEGIN
    CREATE TABLE photo_orientation
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        observation VARCHAR(255) NULL,
        centre GEOMETRY NULL,
        altitude FLOAT NULL,
        omega FLOAT NULL,
        phi FLOAT NULL,
        kappa FLOAT NULL,
        geoid_model VARCHAR(255) NULL,
        temps_gps FLOAT NULL,
        photo_planification_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_photo_orientation_photo_planification FOREIGN KEY(photo_planification_id) REFERENCES photo_planification(id)
    );
END;

-- CREATE SCAN EXECUTION TABLE
IF NOT EXISTS (SELECT *
FROM sysobjects
WHERE name='scan_execution' and xtype='U')
BEGIN
    CREATE TABLE scan_execution
    (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nom VARCHAR(255) NOT NULL,
        label VARCHAR(255) NOT NULL,
        observation VARCHAR(255) NULL,
        emprise GEOMETRY NULL,
        date_pva DATE NULL,
        bande_id BIGINT NOT NULL,
        status_code INT NULL,
        created_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        version BIGINT NULL,
        created_by VARCHAR(255) NULL,
        last_modified_by VARCHAR(255) NULL,
        CONSTRAINT FK_scan_execution_bande FOREIGN KEY(bande_id) REFERENCES bande(id)
    );
END;

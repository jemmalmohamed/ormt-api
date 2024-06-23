-- CREATE PLAN D'ACTIONS TABLE
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='plan_action' and xtype='U')
BEGIN
    CREATE TABLE plan_action (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
        nom varchar(255) NOT NULL,
        description varchar(255) NULL,
        debut_date datetime2 NOT NULL, 
        fin_date datetime2 NOT NULL,
        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL 
    );
END;

-- CREATE ORGANISME TABLE
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='organisme' and xtype='U')
BEGIN
    CREATE TABLE organisme (
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
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='capteur' and xtype='U')
BEGIN
    CREATE TABLE capteur (
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
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='avion' and xtype='U')
BEGIN
    CREATE TABLE avion (
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
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='objet' and xtype='U')
BEGIN
    CREATE TABLE objet (
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
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='mission' and xtype='U')
BEGIN
    CREATE TABLE mission (
        id UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
      
        nom varchar(255) NOT NULL,
	    code varchar(255) NOT NULL,
	    etat varchar(255) NULL,
	    date_pva DATE NULL,
	    superficie   BIGINT   NULL,
	    description varchar(255) NULL,
	    delimitation GEOMETRY NULL,
	  
        status_code int NULL,
        created_date datetime2 NOT NULL DEFAULT SYSDATETIME(),
        last_modified_date datetime2 NULL DEFAULT SYSDATETIME(),
        version bigint NULL,
        created_by varchar(255) NULL,
        last_modified_by varchar(255) NULL 
    );
END;
 

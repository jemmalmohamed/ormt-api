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
 

CREATE TABLE
   IF NOT EXISTS  partenaire (
        id BIGSERIAL PRIMARY KEY,
        nom VARCHAR(50) NOT NULL,  
        photo_url VARCHAR(50) NOT NULL, 
        description varchar(255) NULL, 
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50) ,
     );
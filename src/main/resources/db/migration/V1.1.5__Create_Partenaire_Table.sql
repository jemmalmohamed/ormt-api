CREATE TABLE
   IF NOT EXISTS  partenaire (
        id BIGSERIAL PRIMARY KEY,
        nom VARCHAR(50) NOT NULL,  
        description varchar(255) NULL, 
        site_web_url VARCHAR(255) NULL,
        image_url VARCHAR(255) NOT NULL, 
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50) 
     );



CREATE TABLE IF NOT EXISTS sous_domaine (
    id BIGSERIAL PRIMARY KEY,
    domaine_id BIGINT NOT NULL,
    titre varchar(255) NOT NULL,
    description varchar(255) NULL,
    
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    
    CONSTRAINT fk_domaine 
        FOREIGN KEY (domaine_id) 
        REFERENCES domaine(id)
);
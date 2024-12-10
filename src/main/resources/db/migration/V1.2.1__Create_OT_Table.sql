-- First create the source_donnees table (based on the UML diagram)
CREATE TABLE IF NOT EXISTS source_donnees (
    id BIGSERIAL PRIMARY KEY,
    nom varchar(255) NOT NULL,
    type varchar(255) NOT NULL,
    frequence_mise_a_jour varchar(255) NOT NULL,
    
    -- Standard audit fields
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);

-- Then create the offre_travail table
CREATE TABLE IF NOT EXISTS offre_travail (
    id BIGSERIAL PRIMARY KEY,
    
    -- Core business fields from the diagram
    population_active int NOT NULL,
    taux_activite float4 NOT NULL,
    nombre_neets int NOT NULL,
    taux_neet float4 NOT NULL,
    
    -- Foreign key relationship
    source_donnees_id int8 NULL,
    CONSTRAINT fk_source_donnees FOREIGN KEY (source_donnees_id) REFERENCES source_donnees(id),
    
    -- Standard audit fields
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
); 
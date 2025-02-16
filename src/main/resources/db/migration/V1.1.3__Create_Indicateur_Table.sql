-- Periodicite
CREATE TABLE IF NOT EXISTS periodicite (
    id BIGSERIAL PRIMARY KEY,
    code varchar(50) NOT NULL,
    libelle varchar(255) NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);
-- Indicateur
CREATE TABLE IF NOT EXISTS indicateur (
    id BIGSERIAL PRIMARY KEY,
    id_sous_domaine BIGINT NOT NULL,
    nom varchar(255) NOT NULL,
    description varchar(255) NULL,
    abreviation varchar(50) NULL,
    categorie varchar(50) NULL,
    type_tb varchar(50) NULL,
    unite varchar(50) NULL,
    source varchar(255) NULL,
    regle_calcul varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_indicateur_sous_domaine FOREIGN KEY (id_sous_domaine) REFERENCES sous_domaine(id)
);

CREATE TABLE IF NOT EXISTS  dimension (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    description VARCHAR(255),
    libelle VARCHAR(255),
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);

-- Pivot table for indicateur and dimension
CREATE TABLE IF NOT EXISTS  indicateur_dimension (
    id_indicateur BIGINT NOT NULL,
    id_dimension BIGINT NOT NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    PRIMARY KEY (id_indicateur, id_dimension),
    CONSTRAINT fk_indicateur_dimension_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur(id),
    CONSTRAINT fk_indicateur_dimension_dimension FOREIGN KEY (id_dimension) REFERENCES dimension(id)
);


CREATE TABLE IF NOT EXISTS donnee_indicateur (
    id BIGSERIAL PRIMARY KEY,
  
    id_indicateur BIGINT NOT NULL,
    valeur numeric NOT NULL,
 
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    
    CONSTRAINT fk_donnee_indicateur_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur(id)
);

CREATE TABLE IF NOT EXISTS valeur_dimension (
    id BIGSERIAL PRIMARY KEY,
    id_dimension BIGINT NOT NULL,
    id_donnee_indicateur BIGINT NOT NULL,
    valeur varchar(255) NOT NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_valeur_dimension_dimension FOREIGN KEY (id_dimension) REFERENCES dimension(id),
    CONSTRAINT fk_valeur_dimension_donnee_indicateur FOREIGN KEY (id_donnee_indicateur) REFERENCES donnee_indicateur(id)
);




-- Create indexes for the pivot table
CREATE INDEX idx_indicateur_dimension_indicateur ON indicateur_dimension(id_indicateur);
CREATE INDEX idx_indicateur_dimension_dimension ON indicateur_dimension(id_dimension);
CREATE INDEX idx_indicateur_dimension_composite ON indicateur_dimension(id_indicateur, id_dimension);



-- Enable CITEXT extension for case-insensitive text fields
CREATE EXTENSION IF NOT EXISTS citext;


-- acteur
CREATE TABLE
    IF NOT EXISTS source (
        id BIGSERIAL PRIMARY KEY,
        nom CITEXT NOT NULL,
        description TEXT, 
        role VARCHAR(20) NOT NULL, 
        statut varchar(255) NOT NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );
  

-- Indicateur
CREATE TABLE
    IF NOT EXISTS indicateur (
        id BIGSERIAL PRIMARY KEY,
        nom CITEXT NOT NULL,
        description TEXT,
        abreviation VARCHAR(10),
        role VARCHAR(20) NOT NULL,
         statut varchar(255) NOT NULL,
        categorie VARCHAR(30),
        type_tb VARCHAR(30),
        unite VARCHAR(20),
        source_id BIGINT,
        regle_calcul TEXT,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_indicateur_source FOREIGN KEY (source_id) REFERENCES source (id)
    );

CREATE TABLE
    IF NOT EXISTS dimension (
        id BIGSERIAL PRIMARY KEY,
        nom CITEXT NOT NULL,
        type VARCHAR(30),
        description TEXT,
        libelle VARCHAR(100),
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );

-- Pivot table for indicateur and sous_domaine
CREATE TABLE
    IF NOT EXISTS indicateur_sous_domaine (
        id_indicateur BIGINT NOT NULL,
        id_sous_domaine BIGINT NOT NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        PRIMARY KEY (id_indicateur, id_sous_domaine),
        CONSTRAINT fk_indicateur_sous_domaine_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur (id),
        CONSTRAINT fk_indicateur_sous_domaine_sous_domaine FOREIGN KEY (id_sous_domaine) REFERENCES sous_domaine (id)
    );

-- Pivot table for indicateur and dimension
CREATE TABLE
    IF NOT EXISTS indicateur_dimension (
        id BIGSERIAL PRIMARY KEY,
        id_indicateur BIGINT NOT NULL,
        id_dimension BIGINT NOT NULL,
        principale BOOLEAN NOT NULL DEFAULT FALSE,
        temporelle BOOLEAN NOT NULL DEFAULT FALSE,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_indicateur_dimension_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur (id),
        CONSTRAINT fk_indicateur_dimension_dimension FOREIGN KEY (id_dimension) REFERENCES dimension (id)
    );

CREATE TABLE
    IF NOT EXISTS donnee_indicateur (
        id BIGSERIAL PRIMARY KEY,
        id_indicateur BIGINT NOT NULL,
        valeur varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_donnee_indicateur_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur (id)
    );

CREATE TABLE
    IF NOT EXISTS valeur_dimension (
        id BIGSERIAL PRIMARY KEY,
        id_dimension BIGINT NOT NULL,
        id_donnee_indicateur BIGINT NOT NULL,
        valeur varchar(255) NOT NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_valeur_dimension_dimension FOREIGN KEY (id_dimension) REFERENCES dimension (id),
        CONSTRAINT fk_valeur_dimension_donnee_indicateur FOREIGN KEY (id_donnee_indicateur) REFERENCES donnee_indicateur (id) ON DELETE CASCADE
    );

-- Create indexes for the pivot table
CREATE INDEX idx_indicateur_dimension_indicateur ON indicateur_dimension (id_indicateur);

CREATE INDEX idx_valeur_dimension_donnee ON valeur_dimension (id_donnee_indicateur);

CREATE INDEX idx_indicateur_dimension_dimension ON indicateur_dimension (id_dimension);

CREATE INDEX idx_indicateur_dimension_composite ON indicateur_dimension (id_indicateur, id_dimension);

CREATE INDEX idx_indicateur_sous_domaine_indicateur ON indicateur_sous_domaine (id_indicateur);

CREATE INDEX idx_indicateur_sous_domaine_sous_domaine ON indicateur_sous_domaine (id_sous_domaine);

CREATE INDEX idx_indicateur_sous_domaine_composite ON indicateur_sous_domaine (id_indicateur, id_sous_domaine);

 

-- Add column comments
COMMENT ON TABLE indicateur IS 'Table storing indicator definitions and metadata';

COMMENT ON COLUMN indicateur.nom IS 'The unique name of the indicator';

COMMENT ON COLUMN indicateur.role IS 'Role classification: PRIMAIRE, SECONDAIRE, or TERTIAIRE';

COMMENT ON COLUMN indicateur.type_tb IS 'Dashboard type: PERFORMANCE, PILOTAGE, or RESULTAT';

COMMENT ON TABLE dimension IS 'Table storing dimension definitions for indicators';

COMMENT ON COLUMN dimension.type IS 'Dimension type: TEMPORELLE, SPATIALE, or ORGANISATIONNELLE';
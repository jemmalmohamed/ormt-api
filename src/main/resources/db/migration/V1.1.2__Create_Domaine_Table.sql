-- Domaine
CREATE TABLE
    IF NOT EXISTS espace (
        id BIGSERIAL PRIMARY KEY,
        nom varchar(255) NOT NULL UNIQUE,
        
        actif BOOLEAN NOT NULL DEFAULT TRUE,
        image_url VARCHAR(255) NOT NULL,
        apropos TEXT NULL,
        description varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );

CREATE TABLE
    IF NOT EXISTS domaine (
        id BIGSERIAL PRIMARY KEY,
        nom varchar(255) NOT NULL UNIQUE,
        actif BOOLEAN NOT NULL DEFAULT TRUE,
         description varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );

-- Legacy portail association removed.
-- Espaces are now linked to domaine_analytique through espace_domaine_analytique.

CREATE TABLE
    IF NOT EXISTS sous_domaine (
        id BIGSERIAL PRIMARY KEY,
        id_domaine BIGINT NOT NULL,
        ordre INT NULL DEFAULT 0,
        nom varchar(255) NOT NULL UNIQUE,
        actif BOOLEAN NOT NULL DEFAULT TRUE,
        description varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_sous_domaine_domaine FOREIGN KEY (id_domaine) REFERENCES domaine (id) ON DELETE CASCADE
    );

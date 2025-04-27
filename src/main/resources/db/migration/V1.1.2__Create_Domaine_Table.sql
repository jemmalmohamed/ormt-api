-- Domaine
CREATE TABLE
    IF NOT EXISTS espace (
        id BIGSERIAL PRIMARY KEY,
        nom varchar(255) NOT NULL UNIQUE,
        
        statut varchar(255) NOT NULL,
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
        role varchar(255) NOT NULL,
        statut varchar(255) NOT NULL,
        description varchar(255) NULL,
        apropos TEXT NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );

CREATE TABLE
    IF NOT EXISTS espace_domaine (
        id BIGSERIAL PRIMARY KEY,
        id_espace BIGINT NOT NULL,
        id_domaine BIGINT NOT NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_espace_domaine_espace FOREIGN KEY (id_espace) REFERENCES espace (id) ON DELETE CASCADE,
        CONSTRAINT fk_espace_domaine_domaine FOREIGN KEY (id_domaine) REFERENCES domaine (id) ON DELETE CASCADE
    );

CREATE TABLE
    IF NOT EXISTS sous_domaine (
        id BIGSERIAL PRIMARY KEY,
        id_domaine BIGINT NOT NULL,
        nom varchar(255) NOT NULL UNIQUE,
        role varchar(255) NOT NULL,
        statut varchar(255) NOT NULL,
        description varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_sous_domaine_domaine FOREIGN KEY (id_domaine) REFERENCES domaine (id) ON DELETE CASCADE
    );
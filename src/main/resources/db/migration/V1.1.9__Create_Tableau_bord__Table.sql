-- Table Tableau de Bord
CREATE TABLE
    IF NOT EXISTS tableau_bord (
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

 

-- Table TB Domaine
CREATE TABLE
    IF NOT EXISTS tb_domaine (
        id BIGSERIAL PRIMARY KEY,
        nom varchar(255) NOT NULL UNIQUE,
        libelle varchar(255) NOT NULL,
        actif BOOLEAN NOT NULL DEFAULT FALSE,
        description varchar(255) NULL,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50)
    );
-- Table TB Domaine indicateur
CREATE TABLE
    IF NOT EXISTS tb_domaine_indicateur (
        id BIGSERIAL PRIMARY KEY,
        id_tb_domaine BIGINT NOT NULL,
        id_indicateur BIGINT NOT NULL,
        categorie varchar(255) NULL,
        ordre INT NULL DEFAULT 0,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50) 
    );

-- Table Many-to-Many: Tableau Bord ↔ TB Domaine
CREATE TABLE
    IF NOT EXISTS tableau_bord_tb_domaine (
        id BIGSERIAL PRIMARY KEY,
        id_tableau_bord BIGINT NOT NULL,
        id_tb_domaine BIGINT NOT NULL,
        ordre INT NULL DEFAULT 0,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_tableau_bord_tb_domaine_tableau_bord FOREIGN KEY (id_tableau_bord) REFERENCES tableau_bord (id) ON DELETE CASCADE,
        CONSTRAINT fk_tableau_bord_tb_domaine_tb_domaine FOREIGN KEY (id_tb_domaine) REFERENCES tb_domaine (id) ON DELETE CASCADE,
        CONSTRAINT uk_tableau_bord_tb_domaine UNIQUE (id_tableau_bord, id_tb_domaine)
    );

 
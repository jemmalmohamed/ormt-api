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

-- Table Catégorie TB Domaine
CREATE TABLE
    IF NOT EXISTS tb_domaine_categorie (
        id BIGSERIAL PRIMARY KEY,
        nom varchar(255) NOT NULL UNIQUE,
        ordre INT NULL DEFAULT 0,
        actif BOOLEAN NOT NULL DEFAULT TRUE,
        description varchar(255) NULL,
        couleur VARCHAR(50) NULL,
        icon VARCHAR(100) NULL,
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
        actif BOOLEAN NOT NULL DEFAULT FALSE,
        description varchar(255) NULL,
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

-- Table Many-to-Many: TB Domaine ↔ TB Domaine Catégorie
CREATE TABLE
    IF NOT EXISTS tb_domaine_tb_domaine_categorie (
        id BIGSERIAL PRIMARY KEY,
        id_tb_domaine BIGINT NOT NULL,
        id_tb_domaine_categorie BIGINT NOT NULL,
        ordre INT NULL DEFAULT 0,
        status_code INT NULL,
        created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        version BIGINT NOT NULL DEFAULT 0,
        created_by VARCHAR(50),
        last_modified_by VARCHAR(50),
        CONSTRAINT fk_tb_domaine_categorie_tb_domaine FOREIGN KEY (id_tb_domaine) REFERENCES tb_domaine (id) ON DELETE CASCADE,
        CONSTRAINT fk_tb_domaine_categorie_categorie FOREIGN KEY (id_tb_domaine_categorie) REFERENCES tb_domaine_categorie (id) ON DELETE CASCADE,
        CONSTRAINT uk_tb_domaine_categorie UNIQUE (id_tb_domaine, id_tb_domaine_categorie)
    );

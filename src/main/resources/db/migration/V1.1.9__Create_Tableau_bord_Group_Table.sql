-- Table Tableau de Bord
CREATE TABLE
    IF NOT EXISTS tb_group (
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
-- Legacy tb_domaine and tb_domaine_indicateur removed.
-- TB groups are now linked to domaine_analytique through tableau_bord_domaine_analytique.

 

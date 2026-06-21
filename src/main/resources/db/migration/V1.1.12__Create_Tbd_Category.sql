CREATE TABLE IF NOT EXISTS tbd_categorie (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    libelle VARCHAR(255) NOT NULL,
    description TEXT NULL,
    ordre INT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    tb_domaine_id BIGINT NULL,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_tbd_categorie_tb_domaine
        FOREIGN KEY (tb_domaine_id) REFERENCES tb_domaine (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tbd_categorie_domaine
    ON tbd_categorie (tb_domaine_id, ordre, libelle);

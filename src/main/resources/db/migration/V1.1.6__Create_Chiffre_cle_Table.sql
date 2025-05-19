CREATE TABLE IF NOT EXISTS chiffre_cle (
    id BIGSERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    valeur VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    donnee_indicateur_id BIGINT,
    status_code INT,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_donnee_indicateur FOREIGN KEY (donnee_indicateur_id) REFERENCES donnee_indicateur (id)
);

CREATE TABLE IF NOT EXISTS chiffre_cle_espace (
    id BIGSERIAL PRIMARY KEY,
    id_chiffre_cle BIGINT NOT NULL,
    id_espace BIGINT NOT NULL,
    status_code INT,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_chiffre_cle_espace_chiffre_cle FOREIGN KEY (id_chiffre_cle) REFERENCES chiffre_cle (id) ON DELETE CASCADE,
    CONSTRAINT fk_chiffre_cle_espace_espace FOREIGN KEY (id_espace) REFERENCES espace (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chiffre_cle_domaine (
    id BIGSERIAL PRIMARY KEY,
    id_chiffre_cle BIGINT NOT NULL,
    id_domaine BIGINT NOT NULL,
    status_code INT,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_chiffre_cle_domaine_chiffre_cle FOREIGN KEY (id_chiffre_cle) REFERENCES chiffre_cle (id) ON DELETE CASCADE,
    CONSTRAINT fk_chiffre_cle_domaine_domaine FOREIGN KEY (id_domaine) REFERENCES domaine (id) ON DELETE CASCADE
);
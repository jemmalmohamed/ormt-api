CREATE TABLE IF NOT EXISTS domaine_analytique (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    titre VARCHAR(255) NOT NULL,
    description TEXT NULL,
    apropos TEXT NULL,
    image_url VARCHAR(500) NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    source_theme_key VARCHAR(255) NULL,
    metadata_json TEXT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_domaine_analytique_source_theme_key
    ON domaine_analytique (source_theme_key);

CREATE TABLE IF NOT EXISTS categorie_analytique (
    id BIGSERIAL PRIMARY KEY,
    domaine_analytique_id BIGINT NOT NULL,
    tbd_dashboard_id BIGINT NULL,
    nom VARCHAR(255) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    description TEXT NULL,
    slug VARCHAR(255) NOT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INT NOT NULL DEFAULT 0,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_categorie_analytique_domaine_analytique
        FOREIGN KEY (domaine_analytique_id) REFERENCES domaine_analytique (id) ON DELETE CASCADE,
    CONSTRAINT fk_categorie_analytique_tbd_dashboard
        FOREIGN KEY (tbd_dashboard_id) REFERENCES tbd_dashboard (id) ON DELETE SET NULL,
    CONSTRAINT uk_categorie_analytique_nom UNIQUE (domaine_analytique_id, nom),
    CONSTRAINT uk_categorie_analytique_slug UNIQUE (domaine_analytique_id, slug)
);

CREATE INDEX IF NOT EXISTS idx_categorie_analytique_domaine
    ON categorie_analytique (domaine_analytique_id, ordre, libelle);

CREATE TABLE IF NOT EXISTS espace_domaine_analytique (
    id BIGSERIAL PRIMARY KEY,
    id_espace BIGINT NOT NULL,
    id_domaine_analytique BIGINT NOT NULL,
    ordre INT NOT NULL DEFAULT 0,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_espace_domaine_analytique_espace
        FOREIGN KEY (id_espace) REFERENCES espace (id) ON DELETE CASCADE,
    CONSTRAINT fk_espace_domaine_analytique_domaine_analytique
        FOREIGN KEY (id_domaine_analytique) REFERENCES domaine_analytique (id) ON DELETE CASCADE,
    CONSTRAINT uk_espace_domaine_analytique UNIQUE (id_espace, id_domaine_analytique)
);

CREATE INDEX IF NOT EXISTS idx_espace_domaine_analytique_espace
    ON espace_domaine_analytique (id_espace, ordre);

CREATE TABLE IF NOT EXISTS tableau_bord_domaine_analytique (
    id BIGSERIAL PRIMARY KEY,
    id_tableau_bord BIGINT NOT NULL,
    id_domaine_analytique BIGINT NOT NULL,
    ordre INT NOT NULL DEFAULT 0,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_tableau_bord_domaine_analytique_tableau_bord
        FOREIGN KEY (id_tableau_bord) REFERENCES tableau_bord (id) ON DELETE CASCADE,
    CONSTRAINT fk_tableau_bord_domaine_analytique_domaine_analytique
        FOREIGN KEY (id_domaine_analytique) REFERENCES domaine_analytique (id) ON DELETE CASCADE,
    CONSTRAINT uk_tableau_bord_domaine_analytique UNIQUE (id_tableau_bord, id_domaine_analytique)
);

CREATE INDEX IF NOT EXISTS idx_tableau_bord_domaine_analytique_tb
    ON tableau_bord_domaine_analytique (id_tableau_bord, ordre);

CREATE TABLE IF NOT EXISTS domaine_analytique_section (
    id BIGSERIAL PRIMARY KEY,
    domaine_analytique_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    titre VARCHAR(255) NULL,
    content_json TEXT NULL,
    ordre INT NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_domaine_analytique_section_domaine
        FOREIGN KEY (domaine_analytique_id) REFERENCES domaine_analytique (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_domaine_analytique_section_owner
    ON domaine_analytique_section (domaine_analytique_id, ordre);

CREATE TABLE IF NOT EXISTS categorie_analytique_section (
    id BIGSERIAL PRIMARY KEY,
    categorie_analytique_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    titre VARCHAR(255) NULL,
    content_json TEXT NULL,
    ordre INT NOT NULL DEFAULT 0,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_categorie_analytique_section_categorie
        FOREIGN KEY (categorie_analytique_id) REFERENCES categorie_analytique (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_categorie_analytique_section_owner
    ON categorie_analytique_section (categorie_analytique_id, ordre);

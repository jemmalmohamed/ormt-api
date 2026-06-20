CREATE TABLE IF NOT EXISTS tableau_bord_v2_categorie (
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
    CONSTRAINT fk_tb_v2_categorie_tb_domaine
        FOREIGN KEY (tb_domaine_id) REFERENCES tb_domaine (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tb_v2_categorie_domaine
    ON tableau_bord_v2_categorie (tb_domaine_id, ordre, libelle);

CREATE TABLE IF NOT EXISTS tableau_bord_v2 (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    titre VARCHAR(255) NOT NULL,
    sous_titre VARCHAR(255) NULL,
    description TEXT NULL,
    source VARCHAR(255) NULL,
    periode_label VARCHAR(255) NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    categorie_id BIGINT NULL,
    theme_json TEXT NULL,
    settings_json TEXT NULL,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_tb_v2_categorie
        FOREIGN KEY (categorie_id) REFERENCES tableau_bord_v2_categorie (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tb_v2_public_list ON tableau_bord_v2 (status, actif, categorie_id, titre);

CREATE TABLE IF NOT EXISTS tableau_bord_v2_widget (
    id BIGSERIAL PRIMARY KEY,
    dashboard_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    titre VARCHAR(255) NULL,
    sous_titre VARCHAR(255) NULL,
    description TEXT NULL,
    ordre INT NULL DEFAULT 0,
    section_key VARCHAR(120) NULL,
    x_coord INT NULL,
    y_coord INT NULL,
    width_units INT NULL,
    height_units INT NULL,
    config_json TEXT NULL,
    style_json TEXT NULL,
    data_source_type VARCHAR(40) NULL,
    indicateur_id BIGINT NULL,
    graphe_configuration_id BIGINT NULL,
    chiffre_cle_id BIGINT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_tb_v2_widget_dashboard
        FOREIGN KEY (dashboard_id) REFERENCES tableau_bord_v2 (id) ON DELETE CASCADE,
    CONSTRAINT fk_tb_v2_widget_indicateur
        FOREIGN KEY (indicateur_id) REFERENCES indicateur (id) ON DELETE SET NULL,
    CONSTRAINT fk_tb_v2_widget_graphe_configuration
        FOREIGN KEY (graphe_configuration_id) REFERENCES graphe_configuration (id) ON DELETE SET NULL,
    CONSTRAINT fk_tb_v2_widget_chiffre_cle
        FOREIGN KEY (chiffre_cle_id) REFERENCES chiffre_cle (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tb_v2_widget_dashboard_ordre
    ON tableau_bord_v2_widget (dashboard_id, ordre, id);

CREATE TABLE IF NOT EXISTS tableau_bord_v2_widget_item (
    id BIGSERIAL PRIMARY KEY,
    widget_id BIGINT NOT NULL,
    libelle VARCHAR(255) NULL,
    valeur VARCHAR(255) NULL,
    unite VARCHAR(80) NULL,
    description TEXT NULL,
    ordre INT NULL DEFAULT 0,
    config_json TEXT NULL,
    style_json TEXT NULL,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_tb_v2_widget_item_widget
        FOREIGN KEY (widget_id) REFERENCES tableau_bord_v2_widget (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tb_v2_widget_item_widget_ordre
    ON tableau_bord_v2_widget_item (widget_id, ordre, id);

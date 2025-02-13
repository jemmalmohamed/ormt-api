-- Domaine
CREATE TABLE IF NOT EXISTS domaine (
    id BIGSERIAL PRIMARY KEY,
    titre varchar(255) NOT NULL,
    description varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);

-- Sous domaine
CREATE TABLE IF NOT EXISTS sous_domaine (
    id BIGSERIAL PRIMARY KEY,
    id_domaine BIGINT NOT NULL,
    titre varchar(255) NOT NULL,
    description varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_sous_domaine_domaine FOREIGN KEY (id_domaine) REFERENCES domaine(id)
);

-- Periodicite
CREATE TABLE IF NOT EXISTS periodicite (
    id BIGSERIAL PRIMARY KEY,
    code varchar(50) NOT NULL,
    libelle varchar(255) NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);

-- Indicateur
CREATE TABLE IF NOT EXISTS indicateur (
    id BIGSERIAL PRIMARY KEY,
    id_sous_domaine BIGINT NOT NULL,
    id_periodicite BIGINT NOT NULL,
    titre varchar(255) NOT NULL,
    description varchar(255) NULL,
    unite_calcul varchar(50) NULL,
    source_donnees varchar(255) NULL,
    regle_calcul varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_indicateur_sous_domaine FOREIGN KEY (id_sous_domaine) REFERENCES sous_domaine(id),
    CONSTRAINT fk_indicateur_periodicite FOREIGN KEY (id_periodicite) REFERENCES periodicite(id)
);

-- Attribut
CREATE TABLE IF NOT EXISTS attribut (
    id BIGSERIAL PRIMARY KEY,
    nom varchar(255) NOT NULL,
    description varchar(255) NULL,
    type_donnee varchar(50) NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL
);

-- Valeur Attribut
CREATE TABLE IF NOT EXISTS valeur_attribut (
    id BIGSERIAL PRIMARY KEY,
    id_attribut BIGINT NOT NULL,
    valeur varchar(255) NOT NULL,
    description varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_valeur_attribut_attribut FOREIGN KEY (id_attribut) REFERENCES attribut(id)
);

-- Donnee Indicateur
CREATE TABLE IF NOT EXISTS donnee_indicateur (
    id BIGSERIAL PRIMARY KEY,
    id_indicateur BIGINT NOT NULL,
    valeur numeric(15,2) NOT NULL,
    annee int4 NOT NULL,
    periode int4 NOT NULL DEFAULT 1,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_donnee_indicateur_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur(id)
);

-- Donnee Attribut
CREATE TABLE IF NOT EXISTS donnee_attribut (
    id BIGSERIAL PRIMARY KEY,
    id_donnee BIGINT NOT NULL,
    id_valeur_attribut BIGINT NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_donnee_attribut_donnee FOREIGN KEY (id_donnee) REFERENCES donnee_indicateur(id),
    CONSTRAINT fk_donnee_attribut_valeur FOREIGN KEY (id_valeur_attribut) REFERENCES valeur_attribut(id)
);

-- Index
CREATE INDEX idx_donnee_indicateur_date ON donnee_indicateur(annee, periode);
CREATE INDEX idx_donnee_attribut_donnee ON donnee_attribut(id_donnee);
CREATE INDEX idx_donnee_attribut_valeur ON donnee_attribut(id_valeur_attribut);
CREATE INDEX idx_valeur_attribut_attribut ON valeur_attribut(id_attribut);
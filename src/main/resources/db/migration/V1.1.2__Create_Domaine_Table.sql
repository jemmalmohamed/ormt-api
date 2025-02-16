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



-- Indicateur
CREATE TABLE IF NOT EXISTS indicateur (
    id BIGSERIAL PRIMARY KEY,
    id_sous_domaine BIGINT NOT NULL,
    nom varchar(255) NOT NULL,
    description varchar(255) NULL,
    abreviation varchar(50) NULL,
    categorie varchar(50) NULL,
    type_tb varchar(50) NULL,
    unite_calcul varchar(50) NULL,
    source varchar(255) NULL,
    regle_calcul varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_indicateur_sous_domaine FOREIGN KEY (id_sous_domaine) REFERENCES sous_domaine(id)
);
 
-- Dimension indicateur (relation entre indicateur et ses dimensions)
CREATE TABLE IF NOT EXISTS indicateur_dimension (
    id BIGSERIAL PRIMARY KEY,
    id_indicateur BIGINT NOT NULL,
    nom varchar(255) NOT NULL,
    type varchar(50) NOT NULL,
    description varchar(255) NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_indicateur_dimension_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur(id)
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
-- Donnee Indicateur avec ses valeurs des dimensions 
CREATE TABLE IF NOT EXISTS donnee_indicateur (
    id BIGSERIAL PRIMARY KEY,
    id_indicateur BIGINT NOT NULL,
    id_periodicite BIGINT NOT NULL,
    valeur varchar(255) NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_donnee_indicateur_indicateur FOREIGN KEY (id_indicateur) REFERENCES indicateur(id),
    CONSTRAINT fk_donnee_indicateur_periodicite FOREIGN KEY (id_periodicite) REFERENCES periodicite(id)
);



-- Valeurs des dimensions pour chaque donnée
CREATE TABLE IF NOT EXISTS donnee_valeur_dimension (
    id BIGSERIAL PRIMARY KEY,
    id_donnee BIGINT NOT NULL,
    id_indicateur_dimension BIGINT NOT NULL,
    valeur varchar(255) NOT NULL,
    status_code int4 NULL,
    created_date timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
    version int8 NULL,
    created_by varchar(255) NULL,
    last_modified_by varchar(255) NULL,
    CONSTRAINT fk_donnee_valeur_dimension_donnee FOREIGN KEY (id_donnee) REFERENCES donnee_indicateur(id),
    CONSTRAINT fk_donnee_valeur_dimension_indicateur_dimension FOREIGN KEY (id_indicateur_dimension) REFERENCES indicateur_dimension(id)
);

-- Index
CREATE INDEX idx_donnee_indicateur_date ON donnee_indicateur(valeur);
CREATE INDEX idx_donnee_valeur_dimension_donnee ON donnee_valeur_dimension(id_donnee);
CREATE INDEX idx_donnee_valeur_dimension_indicateur ON donnee_valeur_dimension(id_indicateur_dimension);
CREATE INDEX idx_indicateur_dimension_indicateur ON indicateur_dimension(id_indicateur);
-- Create ControleApplication table
CREATE TABLE controle_application (
    id BIGSERIAL PRIMARY KEY,
    nombre_visites_cati INTEGER,
    nombre_infractions INTEGER,
    nombre_entreprises_visitees INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create SanteSecuriteTravail table
CREATE TABLE sante_securite_travail (
    id BIGSERIAL PRIMARY KEY,
    nombre_visites_sst INTEGER,
    nombre_entreprises_visitees_sst INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create ClimatSocial table
CREATE TABLE climat_social (
    id BIGSERIAL PRIMARY KEY,
    nombre_conflits_individuels INTEGER,
    nombre_greves_evitees INTEGER,
    nombre_greves_declenchees INTEGER,
    nombre_journees_perdues INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create DroitConventionnel table
CREATE TABLE droit_conventionnel (
    id BIGSERIAL PRIMARY KEY,
    nombre_protocoles_accord INTEGER,
    nombre_conventions_collectives INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create RelationsProfessionnelles table
CREATE TABLE relations_professionnelles (
    id BIGSERIAL PRIMARY KEY,
    controle_application_id BIGINT REFERENCES controle_application(id),
    sante_securite_travail_id BIGINT REFERENCES sante_securite_travail(id),
    climat_social_id BIGINT REFERENCES climat_social(id),
    droit_conventionnel_id BIGINT REFERENCES droit_conventionnel(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_controle_application_source ON controle_application(source_donnees_id);
CREATE INDEX idx_sante_securite_travail_source ON sante_securite_travail(source_donnees_id);
CREATE INDEX idx_climat_social_source ON climat_social(source_donnees_id);
CREATE INDEX idx_droit_conventionnel_source ON droit_conventionnel(source_donnees_id); 
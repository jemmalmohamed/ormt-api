 

-- Create AppareilFormation table
CREATE TABLE appareil_formation (
    id BIGSERIAL PRIMARY KEY,
    nombre_etablissements INTEGER,
    nombre_formateurs INTEGER,
    taux_encadrement FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create AccesFormation table
CREATE TABLE acces_formation (
    id BIGSERIAL PRIMARY KEY,
    nombre_stagiaires INTEGER,
    nombre_inscrits_concours INTEGER,
    nombre_places_offertes INTEGER,
    taux_affluence FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create FilieresEtProgrammes table
CREATE TABLE filieres_et_programmes (
    id BIGSERIAL PRIMARY KEY,
    nombre_filieres INTEGER,
    nombre_programmes INTEGER,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create OffreFormation table
CREATE TABLE offre_formation (
    id BIGSERIAL PRIMARY KEY,
    appareil_formation_id BIGINT REFERENCES appareil_formation(id),
    acces_formation_id BIGINT REFERENCES acces_formation(id),
    filieres_programmes_id BIGINT REFERENCES filieres_et_programmes(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_appareil_formation_source ON appareil_formation(source_donnees_id);
CREATE INDEX idx_acces_formation_source ON acces_formation(source_donnees_id);
CREATE INDEX idx_filieres_programmes_source ON filieres_et_programmes(source_donnees_id); 
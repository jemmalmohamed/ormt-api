 

-- Create TissuEconomique table
CREATE TABLE tissu_economique (
    id BIGSERIAL PRIMARY KEY,
    production_sectorielle JSONB,
    chiffre_affaires_sectoriel JSONB,
    investissement_sectoriel JSONB,
    exportation_sectorielle JSONB,
    importation_sectorielle JSONB,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create ContributionSectorielle table
CREATE TABLE contribution_sectorielle (
    id BIGSERIAL PRIMARY KEY,
    va_par_secteur JSONB,
    part_va_secteur_region JSONB,
    part_va_secteur_national JSONB,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create ContributionCroissance table
CREATE TABLE contribution_croissance (
    id BIGSERIAL PRIMARY KEY,
    pib FLOAT,
    taux_croissance_pib FLOAT,
    pib_par_tete FLOAT,
    part_va_regionale FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create Pauvrete table
CREATE TABLE pauvrete (
    id BIGSERIAL PRIMARY KEY,
    taux_pauvrete FLOAT,
    source_donnees_id BIGINT REFERENCES source_donnees(id)
);

-- Create CadreMacroEconomique table
CREATE TABLE cadre_macro_economique (
    id BIGSERIAL PRIMARY KEY,
    contribution_croissance_id BIGINT REFERENCES contribution_croissance(id),
    contribution_sectorielle_id BIGINT REFERENCES contribution_sectorielle(id),
    tissu_economique_id BIGINT REFERENCES tissu_economique(id),
    pauvrete_id BIGINT REFERENCES pauvrete(id)
);

-- Add indexes for better query performance
CREATE INDEX idx_tissu_economique_source ON tissu_economique(source_donnees_id);
CREATE INDEX idx_contribution_sectorielle_source ON contribution_sectorielle(source_donnees_id);
CREATE INDEX idx_contribution_croissance_source ON contribution_croissance(source_donnees_id);
CREATE INDEX idx_pauvrete_source ON pauvrete(source_donnees_id); 
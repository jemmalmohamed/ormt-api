-- Migration V1.1.8: Create Chart System Tables
-- Tables pour le système de configuration des graphiques

-- Table des types de graphiques disponibles
CREATE TABLE IF NOT EXISTS graphe_type (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,

    description TEXT,
    chart_js_type VARCHAR(50) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    status_code INTEGER NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

 

-- Table des configurations de graphiques sauvegardées
CREATE TABLE IF NOT EXISTS graphe_configuration (
    id BIGSERIAL PRIMARY KEY,
    indicateur_id BIGINT NOT NULL,
    graphe_type_id BIGINT NOT NULL,
    nom VARCHAR(200),
    dimension_mapping_json TEXT NOT NULL,
    chart_options_json TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    status_code INTEGER NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50), 
    CONSTRAINT fk_chart_config_indicateur FOREIGN KEY (indicateur_id) REFERENCES indicateur(id) ON DELETE CASCADE,
    CONSTRAINT fk_chart_config_graphe_type FOREIGN KEY (graphe_type_id) REFERENCES graphe_type(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_graphe_type_code ON graphe_type(code);
CREATE INDEX IF NOT EXISTS idx_graphe_type_actif ON graphe_type(actif);
CREATE INDEX IF NOT EXISTS idx_graphe_config_indicateur ON graphe_configuration(indicateur_id);
CREATE INDEX IF NOT EXISTS idx_graphe_config_default ON graphe_configuration(is_default);

-- Insertion des types de graphiques de base
INSERT INTO graphe_type (code, nom, description, chart_js_type,  actif) VALUES
('CAMEMBERT', 'Camembert', 'Graphique circulaire pour représenter des proportions', 'pie',  TRUE),
('HISTOGRAMME', 'Histogramme', 'Graphique en barres verticales', 'bar',  TRUE),
('COURBES', 'Courbes', 'Graphique linéaire simple', 'line',  TRUE),
('PYRAMIDE_AGES', 'Pyramide des âges', 'Graphique spécialisé pour la répartition par âge et genre', 'bar',  TRUE),
('CARTE', 'Carte', 'Représentation géographique des données', 'choropleth',  TRUE),
('COURBE_LINEAIRE', 'Courbe linéaire', 'Graphique linéaire pour évolutions temporelles', 'line',  TRUE),
('HISTOGRAMME_EMPILTE_EVOLUTION', 'Histogramme empilé évolution', 'Histogramme empilé pour comparer évolutions', 'bar',  TRUE),
('COURBE_EVOLUTION', 'Courbe linéaire évolution', 'Courbes multiples pour évolutions temporelles', 'line',  TRUE);

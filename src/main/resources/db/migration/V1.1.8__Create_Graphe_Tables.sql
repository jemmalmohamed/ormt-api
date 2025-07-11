-- Migration V1.1.8: Create Chart System Tables
-- Tables pour le système de configuration des graphiques

-- Table des types de graphiques disponibles
CREATE TABLE IF NOT EXISTS graphe_type (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    chart_js_type VARCHAR(50) NOT NULL,
    min_dimensions INTEGER DEFAULT 1,
    max_dimensions INTEGER DEFAULT 10,
    requires_temporal BOOLEAN DEFAULT FALSE,
    actif BOOLEAN DEFAULT TRUE,
    status_code INTEGER NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
);

-- Table des règles de mapping pour chaque type de graphique
CREATE TABLE IF NOT EXISTS graphe_mapping_rule (
    id BIGSERIAL PRIMARY KEY,
    graphe_type_id BIGINT NOT NULL,
    mapping_key VARCHAR(50) NOT NULL, -- 'xAxis', 'yAxis', 'series', 'filter', 'labels', 'values'
    is_required BOOLEAN DEFAULT FALSE,
    is_forbidden BOOLEAN DEFAULT FALSE,
    must_be_temporal BOOLEAN DEFAULT FALSE,
    must_be_geographic BOOLEAN DEFAULT FALSE,
    max_values INTEGER,
    description VARCHAR(255),
    status_code INTEGER NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_graphe_mapping_rule_graphe_type FOREIGN KEY (graphe_type_id) REFERENCES graphe_type(id) ON DELETE CASCADE
);

-- Table des configurations de graphiques sauvegardées
CREATE TABLE IF NOT EXISTS graphe_configuration (
    id BIGSERIAL PRIMARY KEY,
    indicateur_id BIGINT NOT NULL,
    graphe_type_id BIGINT NOT NULL,
    nom VARCHAR(200),
    dimension_mapping_json TEXT NOT NULL,
    chart_options_json TEXT,
    colors_json TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT FALSE,
    ordre_affichage INTEGER DEFAULT 0,
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
CREATE INDEX IF NOT EXISTS idx_graphe_mapping_rule_graphe_type ON graphe_mapping_rule(graphe_type_id);
CREATE INDEX IF NOT EXISTS idx_graphe_config_indicateur ON graphe_configuration(indicateur_id);
CREATE INDEX IF NOT EXISTS idx_graphe_config_default ON graphe_configuration(is_default);

-- Insertion des types de graphiques de base
INSERT INTO graphe_type (code, nom, description, chart_js_type, min_dimensions, max_dimensions, requires_temporal, actif) VALUES
('CAMEMBERT', 'Camembert', 'Graphique circulaire pour représenter des proportions', 'pie', 1, 1, FALSE, TRUE),
('HISTOGRAMME', 'Histogramme', 'Graphique en barres verticales', 'bar', 1, 10, FALSE, TRUE),
('COURBES', 'Courbes', 'Graphique linéaire simple', 'line', 1, 10, FALSE, TRUE),
('PYRAMIDE_AGES', 'Pyramide des âges', 'Graphique spécialisé pour la répartition par âge et genre', 'bar', 2, 2, FALSE, TRUE),
('CARTE', 'Carte', 'Représentation géographique des données', 'choropleth', 1, 3, FALSE, TRUE),
('COURBE_LINEAIRE', 'Courbe linéaire', 'Graphique linéaire pour évolutions temporelles', 'line', 1, 8, TRUE, TRUE),
('HISTOGRAMME_EVOLUTION', 'Histogramme empilé évolution', 'Histogramme empilé pour comparer évolutions', 'bar', 1, 8, TRUE, TRUE),
('COURBE_EVOLUTION', 'Courbe linéaire évolution', 'Courbes multiples pour évolutions temporelles', 'line', 1, 8, TRUE, TRUE);

-- Règles de mapping pour CAMEMBERT
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'CAMEMBERT'), 'labels', TRUE, FALSE, 'Libellés des parts du camembert'),
((SELECT id FROM graphe_type WHERE code = 'CAMEMBERT'), 'values', TRUE, FALSE, 'Valeurs des parts du camembert'),
((SELECT id FROM graphe_type WHERE code = 'CAMEMBERT'), 'xAxis', FALSE, TRUE, 'Axe X interdit pour camembert'),
((SELECT id FROM graphe_type WHERE code = 'CAMEMBERT'), 'yAxis', FALSE, TRUE, 'Axe Y interdit pour camembert'),
((SELECT id FROM graphe_type WHERE code = 'CAMEMBERT'), 'series', FALSE, TRUE, 'Séries interdites pour camembert');

-- Règles de mapping pour HISTOGRAMME
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, max_values, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME'), 'xAxis', TRUE, FALSE, NULL, 'Axe X obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME'), 'yAxis', TRUE, FALSE, NULL, 'Axe Y (valeurs) obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME'), 'series', FALSE, FALSE, 5, 'Séries optionnelles (max 5)'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME'), 'filter', FALSE, FALSE, NULL, 'Filtres optionnels');

-- Règles de mapping pour COURBES
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, max_values, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'COURBES'), 'xAxis', TRUE, FALSE, NULL, 'Axe X obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBES'), 'yAxis', TRUE, FALSE, NULL, 'Axe Y (valeurs) obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBES'), 'series', FALSE, FALSE, 8, 'Séries optionnelles (max 8)'),
((SELECT id FROM graphe_type WHERE code = 'COURBES'), 'filter', FALSE, FALSE, NULL, 'Filtres optionnels');

-- Règles de mapping pour PYRAMIDE_AGES
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'PYRAMIDE_AGES'), 'ageAxis', TRUE, FALSE, 'Dimension âge obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'PYRAMIDE_AGES'), 'genderAxis', TRUE, FALSE, 'Dimension genre obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'PYRAMIDE_AGES'), 'values', TRUE, FALSE, 'Valeurs obligatoires'),
((SELECT id FROM graphe_type WHERE code = 'PYRAMIDE_AGES'), 'series', FALSE, TRUE, 'Séries interdites pour pyramide des âges');

-- Règles de mapping pour CARTE
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, must_be_geographic, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'CARTE'), 'geographic', TRUE, FALSE, TRUE, 'Dimension géographique obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'CARTE'), 'values', TRUE, FALSE, FALSE, 'Valeurs obligatoires'),
((SELECT id FROM graphe_type WHERE code = 'CARTE'), 'series', FALSE, FALSE, FALSE, 'Séries optionnelles'),
((SELECT id FROM graphe_type WHERE code = 'CARTE'), 'filter', FALSE, FALSE, FALSE, 'Filtres optionnels');

-- Règles de mapping pour COURBE_LINEAIRE (temporel)
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, must_be_temporal, max_values, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'COURBE_LINEAIRE'), 'xAxis', TRUE, FALSE, TRUE, NULL, 'Axe X temporel obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_LINEAIRE'), 'yAxis', TRUE, FALSE, FALSE, NULL, 'Axe Y (valeurs) obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_LINEAIRE'), 'series', FALSE, FALSE, FALSE, 8, 'Séries optionnelles (max 8)'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_LINEAIRE'), 'filter', FALSE, FALSE, FALSE, NULL, 'Filtres optionnels');

-- Règles de mapping pour HISTOGRAMME_EVOLUTION (temporel)
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, must_be_temporal, max_values, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME_EVOLUTION'), 'xAxis', TRUE, FALSE, TRUE, NULL, 'Axe X temporel obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME_EVOLUTION'), 'yAxis', TRUE, FALSE, FALSE, NULL, 'Axe Y (valeurs) obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME_EVOLUTION'), 'series', FALSE, FALSE, FALSE, 6, 'Séries optionnelles (max 6)'),
((SELECT id FROM graphe_type WHERE code = 'HISTOGRAMME_EVOLUTION'), 'filter', FALSE, FALSE, FALSE, NULL, 'Filtres optionnels');

-- Règles de mapping pour COURBE_EVOLUTION (temporel)
INSERT INTO graphe_mapping_rule (graphe_type_id, mapping_key, is_required, is_forbidden, must_be_temporal, max_values, description) VALUES
((SELECT id FROM graphe_type WHERE code = 'COURBE_EVOLUTION'), 'xAxis', TRUE, FALSE, TRUE, NULL, 'Axe X temporel obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_EVOLUTION'), 'yAxis', TRUE, FALSE, FALSE, NULL, 'Axe Y (valeurs) obligatoire'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_EVOLUTION'), 'series', FALSE, FALSE, FALSE, 8, 'Séries optionnelles (max 8)'),
((SELECT id FROM graphe_type WHERE code = 'COURBE_EVOLUTION'), 'filter', FALSE, FALSE, FALSE, NULL, 'Filtres optionnels');

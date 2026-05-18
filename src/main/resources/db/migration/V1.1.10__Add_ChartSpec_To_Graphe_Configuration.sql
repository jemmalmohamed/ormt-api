ALTER TABLE graphe_configuration
    ADD COLUMN chart_spec_version INTEGER,
    ADD COLUMN chart_spec_json TEXT,
    ADD COLUMN config_system VARCHAR(30) DEFAULT 'legacy';

UPDATE graphe_configuration
SET config_system = 'legacy'
WHERE config_system IS NULL;

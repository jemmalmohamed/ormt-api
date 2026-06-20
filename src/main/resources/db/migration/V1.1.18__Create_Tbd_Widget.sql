CREATE TABLE tbd_widget (
    id              BIGSERIAL PRIMARY KEY,
    row_id          BIGINT NOT NULL REFERENCES tbd_widget_row(id) ON DELETE CASCADE,
    type            VARCHAR(20) NOT NULL DEFAULT 'CHART',
    indicateur_id   BIGINT REFERENCES indicateur(id),
    kpi_id          BIGINT,
    content_json    TEXT,
    titre           VARCHAR(255),
    ordre           INTEGER DEFAULT 0,
    size_percent    INTEGER DEFAULT 50,
    actif           BOOLEAN DEFAULT TRUE,
    status_code INT NULL,
    created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    last_modified_by VARCHAR(50)
    CONSTRAINT chk_tbd_widget_type CHECK (type IN ('CHART', 'KPI_CARD', 'EDITOR', 'TEXT')),
    CONSTRAINT chk_tbd_widget_size CHECK (size_percent BETWEEN 5 AND 100)
);
COMMENT ON COLUMN tbd_widget.type IS 'CHART=app-chart(indicateur_id) | KPI_CARD=kpi-card(kpi_id) | EDITOR=p-editor(content_json) | TEXT=texte(content_json)';
COMMENT ON COLUMN tbd_widget.size_percent IS 'Largeur % dans le splitter horizontal. Somme des widgets dans une row = 100.';
COMMENT ON COLUMN tbd_widget.kpi_id IS 'FK vers tbd_kpi — contrainte ajoutée quand le module KPI sera créé.';

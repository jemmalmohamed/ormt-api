ALTER TABLE tbd_widget
DROP CONSTRAINT IF EXISTS chk_tbd_widget_type;

ALTER TABLE tbd_widget
ADD CONSTRAINT chk_tbd_widget_type
CHECK (type IN ('CHART', 'KPI_CARD', 'EDITOR', 'TEXT', 'EMPTY'));

COMMENT ON COLUMN tbd_widget.type IS 'CHART=app-chart(indicateur_id) | KPI_CARD=kpi-card(kpi_id) | EDITOR=p-editor(content_json) | TEXT=texte(content_json) | EMPTY=spacer vide';

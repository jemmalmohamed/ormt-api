CREATE INDEX idx_tbd_section_dashboard ON tbd_section(dashboard_id);
CREATE INDEX idx_tbd_widget_row_section ON tbd_widget_row(section_id);
CREATE INDEX idx_tbd_widget_row ON tbd_widget(row_id);
CREATE INDEX idx_tbd_widget_indicateur ON tbd_widget(indicateur_id);
CREATE INDEX idx_tbd_assignation_dashboard ON tbd_assignation(dashboard_id);

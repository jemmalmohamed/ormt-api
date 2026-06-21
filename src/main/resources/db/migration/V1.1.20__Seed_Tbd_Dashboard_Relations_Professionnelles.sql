INSERT INTO tbd_dashboard (nom, titre, sous_titre, description, status, created_by)
VALUES (
    'relations-professionnelles-marrakech',
    'Relations Professionnelles et Climat Social',
    'Région Marrakech-Safi',
    'Cette analyse présente l''évolution des relations professionnelles, les mécanismes de dialogue social et les tendances sectorielles pour optimiser le climat social régional.',
    'PUBLISHED',
    'seed'
);

WITH db AS (SELECT id FROM tbd_dashboard WHERE nom = 'relations-professionnelles-marrakech')
INSERT INTO tbd_source_listing (dashboard_id, source_id, ordre)
SELECT db.id, s.id, 1 FROM db, source s WHERE s.nom ILIKE '%minist%' OR s.nom ILIKE '%inclus%' LIMIT 1;

WITH db AS (SELECT id FROM tbd_dashboard WHERE nom = 'relations-professionnelles-marrakech')
INSERT INTO tbd_section (dashboard_id, label, ordre, size_percent, created_by)
VALUES
    ((SELECT id FROM db), NULL, 1, 20, 'seed'),
    ((SELECT id FROM db), 'Indicateurs', 2, 45, 'seed'),
    ((SELECT id FROM db), 'Analyse et tendances', 3, 35, 'seed');

WITH s1 AS (SELECT id FROM tbd_section WHERE ordre = 1 AND dashboard_id = (SELECT id FROM tbd_dashboard WHERE nom = 'relations-professionnelles-marrakech'))
INSERT INTO tbd_widget_row (section_id, ordre, size_percent, created_by)
VALUES ((SELECT id FROM s1), 1, 100, 'seed');

WITH s2 AS (SELECT id FROM tbd_section WHERE ordre = 2 AND dashboard_id = (SELECT id FROM tbd_dashboard WHERE nom = 'relations-professionnelles-marrakech'))
INSERT INTO tbd_widget_row (section_id, ordre, size_percent, created_by)
VALUES
    ((SELECT id FROM s2), 1, 50, 'seed'),
    ((SELECT id FROM s2), 2, 50, 'seed');

WITH s3 AS (SELECT id FROM tbd_section WHERE ordre = 3 AND dashboard_id = (SELECT id FROM tbd_dashboard WHERE nom = 'relations-professionnelles-marrakech'))
INSERT INTO tbd_widget_row (section_id, ordre, size_percent, created_by)
VALUES ((SELECT id FROM s3), 1, 100, 'seed');

WITH r1 AS (SELECT wr.id FROM tbd_widget_row wr JOIN tbd_section s ON wr.section_id = s.id JOIN tbd_dashboard d ON s.dashboard_id = d.id WHERE d.nom = 'relations-professionnelles-marrakech' AND s.ordre = 1 AND wr.ordre = 1)
INSERT INTO tbd_widget (row_id, type, titre, ordre, size_percent, created_by)
VALUES
    ((SELECT id FROM r1), 'KPI_CARD', 'Entreprises sans conflits', 1, 25, 'seed'),
    ((SELECT id FROM r1), 'KPI_CARD', 'Entreprises avec conflits', 2, 25, 'seed'),
    ((SELECT id FROM r1), 'KPI_CARD', 'Conflits individuels', 3, 25, 'seed'),
    ((SELECT id FROM r1), 'KPI_CARD', 'Total conflits 2023', 4, 25, 'seed');

WITH r21 AS (SELECT wr.id FROM tbd_widget_row wr JOIN tbd_section s ON wr.section_id = s.id JOIN tbd_dashboard d ON s.dashboard_id = d.id WHERE d.nom = 'relations-professionnelles-marrakech' AND s.ordre = 2 AND wr.ordre = 1)
INSERT INTO tbd_widget (row_id, type, titre, ordre, size_percent, created_by)
VALUES
    ((SELECT id FROM r21), 'CHART', 'Évolution conflits sociaux 2021–2023', 1, 65, 'seed'),
    ((SELECT id FROM r21), 'CHART', 'Grèves déclenchées vs évitées', 2, 35, 'seed');

WITH r22 AS (SELECT wr.id FROM tbd_widget_row wr JOIN tbd_section s ON wr.section_id = s.id JOIN tbd_dashboard d ON s.dashboard_id = d.id WHERE d.nom = 'relations-professionnelles-marrakech' AND s.ordre = 2 AND wr.ordre = 2)
INSERT INTO tbd_widget (row_id, type, titre, ordre, size_percent, created_by)
VALUES
    ((SELECT id FROM r22), 'CHART', 'Conflits par secteur et taille 2024', 1, 50, 'seed'),
    ((SELECT id FROM r22), 'CHART', 'Nature des conflits', 2, 50, 'seed');

WITH r3 AS (SELECT wr.id FROM tbd_widget_row wr JOIN tbd_section s ON wr.section_id = s.id JOIN tbd_dashboard d ON s.dashboard_id = d.id WHERE d.nom = 'relations-professionnelles-marrakech' AND s.ordre = 3 AND wr.ordre = 1),
     content AS (SELECT '{"html":"<p><strong>Analyse et tendances — Données 2024</strong></p><ul><li>Stabilité générale : 92,7% des entreprises sans conflit</li><li>Secteur le plus touché : agences de voyages (15,1%)</li><li>97,1% des conflits sont individuels</li></ul>"}' AS json_content)
INSERT INTO tbd_widget (row_id, type, titre, ordre, size_percent, content_json, created_by)
VALUES
    ((SELECT id FROM r3), 'EDITOR', 'Analyse et tendances', 1, 75, (SELECT json_content FROM content), 'seed'),
    ((SELECT id FROM r3), 'KPI_CARD', 'KPI sidebar', 2, 25, NULL, 'seed');

ALTER TABLE tbd_widget_row
ADD COLUMN IF NOT EXISTS height_px INTEGER;

UPDATE tbd_widget_row
SET height_px = 200
WHERE height_px IS NULL;

ALTER TABLE tbd_widget_row
ALTER COLUMN height_px SET NOT NULL;

ALTER TABLE tbd_widget_row
ALTER COLUMN height_px SET DEFAULT 200;

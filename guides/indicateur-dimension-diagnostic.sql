-- Diagnostic des associations indicateur <-> dimension

-- 1. Doublons exacts sur la meme dimension liee au meme indicateur
SELECT
    id_indicateur,
    id_dimension,
    COUNT(*) AS duplicate_count
FROM indicateur_dimension
GROUP BY id_indicateur, id_dimension
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC, id_indicateur, id_dimension;

-- 2. Indicateurs avec plusieurs dimensions principales
SELECT
    id_indicateur,
    COUNT(*) AS principale_count
FROM indicateur_dimension
WHERE principale = TRUE
GROUP BY id_indicateur
HAVING COUNT(*) > 1
ORDER BY principale_count DESC, id_indicateur;

-- 3. Indicateurs avec plusieurs dimensions temporelles
SELECT
    id_indicateur,
    COUNT(*) AS temporelle_count
FROM indicateur_dimension
WHERE temporelle = TRUE
GROUP BY id_indicateur
HAVING COUNT(*) > 1
ORDER BY temporelle_count DESC, id_indicateur;

-- 4. Associations marquees principale et temporelle en meme temps
SELECT
    id,
    id_indicateur,
    id_dimension
FROM indicateur_dimension
WHERE principale = TRUE
  AND temporelle = TRUE
ORDER BY id_indicateur, id_dimension;

-- 5. Vue detaillee des associations pour un indicateur donne
-- Remplacer :indicateur_id par l'id cible.
SELECT
    id,
    id_indicateur,
    id_dimension,
    principale,
    temporelle,
    created_date,
    last_modified_date
FROM indicateur_dimension
WHERE id_indicateur = :indicateur_id
ORDER BY principale DESC, temporelle DESC, id_dimension;

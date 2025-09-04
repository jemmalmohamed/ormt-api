#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs.
- Ajoute seulement le nom, grapheTypeCode, isDefault=true et chartOptionsJson vide
- Basé sur le mapping des indicateurs vers les types de graphiques
- Écrase les configurations existantes pour chaque indicateur

Run from repo root or this script's directory.
"""
import json
import sys
import os
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "demande-de-travail"

# Mapping from indicator name to graphe type code (normalized upper)
INDICATEUR_TO_TYPE = {
    "productivité par secteur d'activité": "HISTOGRAMME",
    "valeur ajoutée par secteur d'activité": "CAMEMBERT",
    "masse salariale par secteur d'activité": "CAMEMBERT",
    "salaire moyen par secteur d'activité": "CAMEMBERT",
    "smig journalier": "INDICATEUR",
    "smag journalier": "INDICATEUR",
    "salaire moyen à l'année n par secteur d'activité": "INDICATEUR",
    "salaire moyen n-10 par secteur d'activité": "INDICATEUR",
    "variation productivité": "NUAGE_DU_POINT",
    "variation valeur ajoutée": "NUAGE_DU_POINT",
    "variation de la masse salariale": "NUAGE_DU_POINT",
    "emploi par genre": "COURBES",
    "emploi par âge": "COURBES",
    "emploi par diplôme": "COURBES",
    "emploi par secteur d'activité": "COURBES",
    "emploi par statut professionnel": "COURBES",
    "emploi par type d'emploi": "COURBES",
    "emploi par secteur d'emploi": "COURBES",
    "emploi par région": "CHOROPLETH",
    "emploi indépendants déclarés à la cnss par secteur d'activité": "HISTOGRAMME",
    "emploi indépendants déclarés à la cnss par région": "HISTOGRAMME",
    "salariés déclarés à la cnss par secteur d'activité": "HISTOGRAMME",
    "salariés déclarés à la cnss par région": "CHOROPLETH",
    "emploi salarié eqtp par secteur d'activité": "CAMEMBERT",
    "nombre de déclaration cnss par secteur d'activité": "HISTOGRAMME",
    "indice de rotation de salariés par poste par secteur d'activité": "INDICATEUR",
    "emploi informel par secteur d'activité": "CAMEMBERT",
    "salarié public par secteur": "CAMEMBERT",
    "nombre d'entreprises": "RADAR",
    "nombre d'entreprises entrantes": "RADAR",
    "nombre d'entreprises sortantes": "RADAR",
    "taux de survie des entreprises": "COURBES",
    "part de l'emploi informel par secteur d'activité": "COURBES",
    "part de l'emploi rémunéré par secteur d'activité": "COURBES",
    "part des actifs occupés bénéficiant d'une couverture médicale": "COURBES",
    "part des actifs occupés bénéficiant d'une couverture médicale par milieu de résidence": "COURBES",
    "part des actifs occupés bénéficiant d'une couverture médicale par genre": "COURBES",
    "part des salariés disposant d'un contrat de travail": "COURBES",
    "part de l'emploi de type occasionnel ou saisonnier": "COURBES",
    "part de l'emploi de type occasionnel ou saisonnier par milieu de résidence": "COURBES",
    "part de l'emploi de type occasionnel ou saisonnier par genre": "COURBES",
    "part des actifs occupés travaillant plus de 48h/semaine (durée du travail excessive)": "COURBES",
    "part des actifs occupés travaillant plus de 48h/semaine (durée du travail excessive) par milieu de résidence": "COURBES",
    "part des actifs occupés travaillant plus de 48h/semaine (durée du travail excessive) par genre": "COURBES",
    "taux d'affiliation des actifs occupés à une organisation syndicale ou professionnelle": "COURBES",
    "part des salariés touchant un salaire inférieur au smig par secteur d'activité": "COURBES",
    "salariés (stables) déclarés à la cnss par secteur d'activité": "COURBES",
    "création d'emploi par genre": "HISTOGRAMME",
    "création d'emploi par diplôme": "HISTOGRAMME",
    "création d'emploi par statut professionnel": "HISTOGRAMME",
    "création d'emploi par secteur d'activité -7p": "HISTOGRAMME",
    "création d'emploi par secteur d'activité -2p": "HISTOGRAMME",
    "création d'emploi par secteur d'emploi": "HISTOGRAMME",
    "création d'emploi par type d'emploi": "HISTOGRAMME",
    "création d'emploi par région": "CHOROPLETH",
    "création d'emploi eqtp par secteur d'activité": "HISTOGRAMME",
    "création d'emploi informel estimé": "HISTOGRAMME",
    "création d'emploi indépendants déclaré à la cnss par région": "CHOROPLETH",
    "création d'emploi indépendants déclaré à la cnss par secteur d'activité": "HISTOGRAMME",
    "création d'emploi salariés par région": "CHOROPLETH",
    "création d'emploi salariés par secteur d'activité": "COURBES",
}


def process_file(path: Path):
    """
    Process a single JSON file and add simple graphe configurations
    """
    with path.open(encoding="utf-8") as f:
        content = json.load(f)

    if not isinstance(content, dict) or "indicateurs" not in content:
        return False, 0

    changed = False
    added = 0

    for indicateur in content.get("indicateurs", []):
        nom = indicateur.get("nom")
        if not nom:
            continue
        type_code = INDICATEUR_TO_TYPE.get(nom)
        if not type_code:
            continue

        # Remplacer complètement les configurations existantes
        cfg_obj = {
            "nom": f"{nom} - {type_code.title()}",
            "grapheTypeCode": type_code,
            "isDefault": True,
            "chartOptionsJson": ""
        }
        
        # Écraser complètement les configurations existantes
        indicateur["grapheConfigurations"] = [cfg_obj]
        changed = True
        added += 1

    if changed:
        with path.open("w", encoding="utf-8") as f:
            json.dump(content, f, ensure_ascii=False, indent=2)

    return changed, added


def main():
    if not DOMAIN_DIR.exists():
        print(f"Domain path not found: {DOMAIN_DIR}")
        sys.exit(1)

    total_files = 0
    total_added = 0
    
    # Liste des fichiers principaux des sous-domaines
    main_files = [
        DOMAIN_DIR / "sous-domaines" / "salaire-productivite" / "salaire-productivite.json",
        DOMAIN_DIR / "sous-domaines" / "volume d'emploi" / "volume-emploi.json", 
        DOMAIN_DIR / "sous-domaines" / "tissu-economique" / "tissu-economique.json",
        DOMAIN_DIR / "sous-domaines" / "Qualité-de-l'emploi" / "Qualité-de-l'emploi.json",
        DOMAIN_DIR / "sous-domaines" / "création d'emploi" / "creation-emploi.json"
    ]
    
    for file_path in main_files:
        if file_path.exists():
            changed, added = process_file(file_path)
            if changed:
                total_files += 1
                total_added += added
                print(f"Updated {file_path.name}: +{added} config(s)")
            else:
                print(f"No changes for {file_path.name}")
        else:
            print(f"File not found: {file_path}")
    
    print(f"Done. Updated files: {total_files}, total configs added: {total_added}")


if __name__ == "__main__":
    main()

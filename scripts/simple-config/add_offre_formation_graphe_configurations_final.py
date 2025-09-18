#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Offre de formation - Version finale avec correspondance exacte.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Offre de formation"

# Mapping from exact indicator names to graphe type codes
INDICATEUR_TO_TYPE = {
    # ES-Etudiants
    "Nombre d'étudiants de l'enseignement supérieur par composante": "CAMEMBERT",
    "Nombre d'étudiants de l'enseignement supérieur par genre": "CAMEMBERT",
    "Nombre d'étudiants de l'enseignement universitaire public par domaine d'études": "HISTOGRAMME",
    "Nombre d'étudiants de l'enseignement universitaire public par genre": "CAMEMBERT",
    "Nombre d'étudiants de l'enseignement supérieur privé par domaine d'études": "HISTOGRAMME",
    "Nombre d'étudiants de l'enseignement supérieur privé par genre": "CAMEMBERT",
    "Nombre d'étudiants de la formation des cadres par domaine d'études": "HISTOGRAMME",
    "Nombre d'étudiants de la formation des cadres par genre": "CAMEMBERT",
    
    # ES-Nouveaux Inscrits
    "Nombre de nouveaux inscrits de l'enseignement supérieur par composante": "HISTOGRAMME",
    "Nombre de nouveaux inscrits de l'enseignement supérieur par genre": "CAMEMBERT",
    "Nombre de nouveaux inscrits de l'enseignement universitaire public par domaine d'études": "HISTOGRAMME",
    "Nombre de nouveaux inscrits de l'enseignement universitaire public par genre": "CAMEMBERT",
    "Nombre de nouveaux inscrits de l'enseignement supérieur privé par genre": "CAMEMBERT",
    "Nombre de nouveaux inscrits de l'enseignement supérieur privé par domaine d'études": "HISTOGRAMME",
    "Nombre de nouveaux inscrits de la formation des cadres par domaine d'études": "HISTOGRAMME",
    "Nombre de nouveaux inscrits de la formation des cadres par genre": "CAMEMBERT",
    
    # FP-Stagiaires
    "Nombre de stagiaires de la formation professionnelle par mode de formation": "HISTOGRAMME",
    "Nombre de stagiaires de la formation professionnelle par opérateur de formation": "HISTOGRAMME",
    "Nombre de stagiaires de la formation professionnelle par niveau de formation": "HISTOGRAMME",
    "Nombre de stagiaires de la formation professionnelle par région": "HISTOGRAMME",
    
    # ES-Diplômés
    "Nombre de diplômés de l'enseignement supérieur par composante": "HISTOGRAMME",
    "Nombre de diplômés de l'enseignement supérieur par genre": "CAMEMBERT",
    "Nombre de diplômés de l'enseignement universitaire public par domaine d'études": "HISTOGRAMME",
    "Nombre de diplômés de l'enseignement supérieur public par genre": "CAMEMBERT",
    "Nombre de diplômés de l'enseignement supérieur privé par domaine d'études": "HISTOGRAMME",
    "Nombre de diplômés de l'enseignement supérieur privé par genre": "CAMEMBERT",
    "Nombre de diplômés de la formation des cadres par domaine d'études": "HISTOGRAMME",
    "Nombre de diplômés de la formation des cadres par genre": "CAMEMBERT",
    
    # ES-Insertion Professionnelle
    "Taux d'insertion des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par composante": "HISTOGRAMME",
    "Taux d'insertion des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par type d'accès": "HISTOGRAMME",
    "Taux d'insertion des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par genre": "HISTOGRAMME",
    "Taux d'insertion des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par diplôme": "HISTOGRAMME",
    "Taux d'insertion des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par domaine d'études": "HISTOGRAMME",
    "Taux d'emploi des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par composante et par genre": "HISTOGRAMME",
    "Taux d'emploi des diplômés de l'enseignement universitaire public après 4 ans (Promotion 2014) par diplôme et par genre": "BARRES_GROUPEES",
    "Taux d'emploi des diplômés de l'enseignement supérieur privé après 4 ans (Promotion 2014) par diplôme et par genre": "BARRES_GROUPEES",
    
    # ES-Chômage
    "Taux de chômage des diplômés de l'enseignement supérieur après 4 ans (Promotion 2014) par composante et genre": "HISTOGRAMME",
    "Taux de chômage des diplômés de l'enseignement universitaire public après 4 ans (Promotion 2014) par type d'accès": "HISTOGRAMME",
    "Taux de chômage des diplômés de l'enseignement universitaire public après 4 ans (Promotion 2014) par diplôme et genre": "HISTOGRAMME",
    "Taux de chômage des diplômés de l'enseignement supérieur privé après 4 ans (Promotion 2014) par diplôme et genre": "HISTOGRAMME",
    
    # FP-Lauréats
    "Nombre de lauréats de la formation professionnelle par mode de formation": "HISTOGRAMME",
    "Nombre de lauréats de la formation professionnelle par opérateur de formation": "HISTOGRAMME",
    "Nombre de lauréats de la formation professionnelle par niveau de formation": "HISTOGRAMME",
    "Nombre de lauréats de la formation professionnelle par région": "HISTOGRAMME",
    
    # FP-insertion à 9 mois
    "Taux d'insertion des lauréats de la formation professionnelle à 9 mois par mode de formation": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 9 mois par genre": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 9 mois par niveau de formation": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 9 mois par région": "HISTOGRAMME",
    
    # FP-insertion à 36 mois
    "Taux d'insertion des lauréats de la formation professionnelle à 36 mois par mode de formation": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 36 mois par genre": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 36 mois par niveau de formation": "HISTOGRAMME",
    "Taux d'insertion des lauréats de la formation professionnelle à 36 mois par région": "HISTOGRAMME",
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
    unmatched = []

    print(f"Processing file: {path.name}")
    for indicateur in content.get("indicateurs", []):
        nom = indicateur.get("nom")
        if not nom:
            continue
            
        print(f"  Found indicator: '{nom}'")
        
        # Direct match first
        type_code = INDICATEUR_TO_TYPE.get(nom)
        
        if not type_code:
            unmatched.append(nom)
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
        print(f"    -> Added {type_code} configuration")

    if changed:
        with path.open("w", encoding="utf-8") as f:
            json.dump(content, f, ensure_ascii=False, indent=2)

    # Print unmatched indicators for debugging
    if unmatched:
        print(f"  Unmatched indicators in {path.name}:")
        for ind in unmatched:
            print(f"    - '{ind}'")

    return changed, added


def main():
    if not DOMAIN_DIR.exists():
        print(f"Domain path not found: {DOMAIN_DIR}")
        sys.exit(1)

    total_files = 0
    total_added = 0
    
    # Liste des fichiers principaux des sous-domaines
    subdomain_files = [
        DOMAIN_DIR / "sous-domaines" / "ES-Etudiants" / "ES-Etudiants.json",
        DOMAIN_DIR / "sous-domaines" / "ES-Nouveaux Inscrits" / "ES-Nouveaux Inscrits.json",
        DOMAIN_DIR / "sous-domaines" / "FP-Stagiaires" / "FP-Stagiaires.json",
        DOMAIN_DIR / "sous-domaines" / "ES-Diplômés" / "ES-Diplômés.json",
        DOMAIN_DIR / "sous-domaines" / "ES-Insertion Professionnelle" / "ES-Insertion Professionnelle.json",
        DOMAIN_DIR / "sous-domaines" / "ES-Chômage" / "ES-Chômage.json",
        DOMAIN_DIR / "sous-domaines" / "FP-Lauréats" / "FP-Lauréats.json",
        DOMAIN_DIR / "sous-domaines" / "FP-insertion à 9 mois" / "FP-insertion à 9 mois.json",
        DOMAIN_DIR / "sous-domaines" / "FP-insertion à 36 mois" / "FP-insertion à 36 mois.json",
    ]
    
    for file_path in subdomain_files:
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

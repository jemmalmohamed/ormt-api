#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Offre de travail - Version finale avec correspondance exacte.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Offre de travail"

# Mapping from exact indicator names to graphe type codes
INDICATEUR_TO_TYPE = {
    # Nouveaux entrants potentiels sur le marché du travail
    "Nouveaux entrants potentiels sur le MT par genre": "CAMEMBERT",
    "Nouveaux entrants potentiels sur le MT par composante": "CAMEMBERT",
    
    # Activité
    "Population en âge d’activité": "HISTOGRAMME",
    "Population inactive": "HISTOGRAMME",
    "Population active": "HISTOGRAMME",
    "Population active par diplôme et genre": "HISTOGRAMME",
    "Population active par diplôme et milieu": "HISTOGRAMME",
    "Population en active par diplôme et milieu de résidence": "HISTOGRAMME",
    "Taux d’activité selon le genre et le milieu de résidence": "COURBES",
    "Taux d’activité selon le genre": "COURBES",
    "Taux d’activité selon le groupe d’âge et le milieu de résidence": "COURBES",
    "Taux d’activité selon le groupe d’âge et le genre": "PYRAMIDE_DES_AGES",
    "Taux d’activité selon le diplôme et le milieu de résidence": "COURBES",
    "Taux d’activité selon le diplôme et le genre": "COURBES",
    "Taux d’activité selon le milieu de résidence": "COURBES",
    "Taux d’activité par région et le milieu de résidence": "COURBES",
    "Taux d’activité par région et genre": "COURBES",
    
    # Chômage
    "Population active en chômage par milieu de résidence": "HISTOGRAMME",
    "Population active en chômage par diplôme et genre": "HISTOGRAMME",
    "Population active en chômage par diplôme et milieu de résidence": "HISTOGRAMME",
    "Population active en chômage par genre": "HISTOGRAMME",
    "Population active en chômage selon la durée": "HISTOGRAMME",
    "Population active en chômage selon l’expérience": "HISTOGRAMME",
    "Population active en chômage selon les causes": "HISTOGRAMME",
    "Taux de chômage selon le milieu de résidence": "HISTOGRAMME",
    "Taux de chômage par région et genre": "CARTE",
    "Taux de chômage par région et milieu de résidence": "COURBES",
    "Taux de chômage selon le groupe d’âge": "COURBES",
    "Taux de chômage selon le diplôme": "COURBES",
    "Taux de chômage selon le genre": "COURBES",
    
    # NEET
    "Effectif des jeunes NEET par milieu de résidence": "HISTOGRAMME",
    "Effectif des jeunes NEET par genre": "HISTOGRAMME",
    "Taux de NEET par milieu de résidence": "COURBES",
    "Taux de NEET par genre": "COURBES",
    "Taux de NEET par région": "CARTE",
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

    for indicateur in content.get("indicateurs", []):
        nom = indicateur.get("nom")
        if not nom:
            continue
            
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

    if changed:
        with path.open("w", encoding="utf-8") as f:
            json.dump(content, f, ensure_ascii=False, indent=2)

    # Print unmatched indicators for debugging
    if unmatched:
        print(f"  Unmatched indicators in {path.name}:")
        for ind in unmatched:
            print(f"    - {ind}")

    return changed, added


def main():
    if not DOMAIN_DIR.exists():
        print(f"Domain path not found: {DOMAIN_DIR}")
        sys.exit(1)

    total_files = 0
    total_added = 0
    
    # Liste des fichiers principaux des sous-domaines
    subdomain_files = [
        DOMAIN_DIR / "sous-domaines" / "Nouveaux entrants potentiels sur le marché du travail" / "Nouveaux entrants potentiels sur le marché du travail.json",
        DOMAIN_DIR / "sous-domaines" / "Activité" / "Activité.json",
        DOMAIN_DIR / "sous-domaines" / "Chomage" / "Chomage.json",
        DOMAIN_DIR / "sous-domaines" / "NEET" / "NEET.json",
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

#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Relations professionnelles et climat social - Version finale avec toutes les courbes.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Relations professionnelles et climat social"

# Mapping from exact indicator names to graphe type codes
# Tous les indicateurs sont configurés avec COURBES comme demandé
INDICATEUR_TO_TYPE = {
    # Application de la loi de travail
    "Nombre de visites des inspecteurs de travail": "COURBES",
    "Nombre de observations émises": "COURBES",
    "Nombre d'observations par motif d'observation": "COURBES",
    "Nombre d'observations par secteur": "COURBES",
    "Nombre de PV": "COURBES",
    "Nombre de contraventions": "COURBES",
    "Nombre de délits": "COURBES",
    
    # Climat social
    "Nombre de conflits par région": "COURBES",
    "Nombre de grèves évitées par région": "COURBES",
    "Nombre de grèves déclenchées par région": "COURBES",
    "Nombre de grèves évitées par secteur": "COURBES",
    "Nombre de grèves déclenchées par secteur": "COURBES",
    
    # Droit conventionnel
    "Nombre de protocoles signés par région": "COURBES",
    "Nombre de conventions collectives par région": "COURBES",
    "Nombre de conventions collectives par secteur": "COURBES",
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
        DOMAIN_DIR / "sous-domaines" / "application de la loi de travail" / "application de la loi de travail.json",
        DOMAIN_DIR / "sous-domaines" / "climat social" / "application de la loi de travail.json",
        DOMAIN_DIR / "sous-domaines" / "droit conventionnel" / "droit conventionnel.json",
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

#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Cadre macro-économique - Version finale avec correspondance exacte.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Cadre macro-économique"

# Mapping from exact indicator names to graphe type codes
INDICATEUR_TO_TYPE = {
    # Investissement
    "Formation brut de capital fixe": "HISTOGRAMME",
    "Volume global de l’investissement public": "HISTOGRAMME",
    "Flux net des investissements directs étrangers au Maroc": "COURBES",
    "Flux des investissements directs marocains à l’étranger": "COURBES",
    
    # Population et conditions de vie
    "population municipale": "HISTOGRAMME",
    "incidence de la pauvreté absolue par région": "CARTE",
    "taux de pauvreté multidimensionnelle par région": "CARTE",
    "taux de vulnérabilité à la pauvreté par région": "CARTE",
    
    # Contexte économique
    "produit intérieur brut aux prix courants": "INDICATEUR",
    "taux de croissance du pib en volume": "COURBES",
    "produit intérieur brut par habitant (aux prix courants) par région en dh": "INDICATEUR",
    "valeur ajoutée par branche d’activité aux prix courants": "COURBES",
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
        DOMAIN_DIR / "sous-domaines" / "investissement" / "investissement.json",
        DOMAIN_DIR / "sous-domaines" / "population et conditions de vie" / "population et conditions de vie.json",
        DOMAIN_DIR / "sous-domaines" / "contexte économique" / "contexte-economique.json",
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

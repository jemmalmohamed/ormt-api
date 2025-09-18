#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Intermédiation sur le marché du travail.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Intermédiation sur le marché du travail"

# Mapping from exact indicator names to graphe type codes
INDICATEUR_TO_TYPE = {
    # Entretiens de positionnement
    "objectif ciblé d'entretiens de positionnement": "HISTOGRAMME",
    "nombre d'entretiens de positionnement": "HISTOGRAMME",
    "entretiens de positionnement par genre": "CAMEMBERT",
    "entretiens de positionnement par âge": "CAMEMBERT",
    "entretiens de positionnement par diplôme": "CAMEMBERT",
    "entretiens de positionnement par région": "CAMEMBERT",
    
    # Offres d'emploi recueillies
    "nombre d'offre d'emploi recueillies": "HISTOGRAMME",
    "offre d'emploi recueillies par région": "CAMEMBERT",
    "offre d'emploi recueillies par taille d'entreprise": "CAMEMBERT",
    "offre d'emploi recueillies par contrat": "CAMEMBERT",
    
    # Inscrits à l'anapec
    "nombre total des inscrits à l'anapec": "HISTOGRAMME",
    "inscrits à l'anapec par genre": "CAMEMBERT",
    "inscrits à l'anapec par âge": "CAMEMBERT",
    "inscrits à l'anapec par diplôme": "CAMEMBERT",
    "inscrits à l'anapec par région": "CAMEMBERT",
    "nombre de nouveaux inscrits à l'anapec": "HISTOGRAMME",
    "nouveaux inscrits à l'anapec par genre": "CAMEMBERT",
    "nouveaux inscrits à l'anapec par âge": "CAMEMBERT",
    "nouveaux inscrits à l'anapec par diplôme": "CAMEMBERT",
    "nouveaux inscrits à l'anapec par région": "CAMEMBERT",
    
    # Agences de recrutement privé (arp)
    "nombre des agences de recrutement privé par région": "HISTOGRAMME",
    "agences de recrutement privé par région": "CAMEMBERT",
    
    # Ateliers de recherche d'emploi
    "objectif ciblé d'ateliers de recherche d'emploi": "HISTOGRAMME",
    "nombre de bénéficiaires d'ateliers de recherche d'emploi": "HISTOGRAMME",
    "bénéficiaires d'are par genre": "CAMEMBERT",
    "bénéficiaires d'are par âge": "CAMEMBERT",
    "bénéficiaires d'are par diplôme": "CAMEMBERT",
    "bénéficiaires d'are par région": "CAMEMBERT",
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
    print(f"Looking for domain directory: {DOMAIN_DIR}")
    if not DOMAIN_DIR.exists():
        print(f"Domain path not found: {DOMAIN_DIR}")
        sys.exit(1)
    
    print(f"Domain directory found!")
    total_files = 0
    total_added = 0
    
    # Liste des fichiers principaux des sous-domaines
    subdomain_files = [
        DOMAIN_DIR / "sous-domaines" / "entretiens-positionnement" / "entretiens-positionnement.json",
        DOMAIN_DIR / "sous-domaines" / "offres-emploi-recueillies" / "offres-emploi-recueillies.json",
        DOMAIN_DIR / "sous-domaines" / "inscrits-anapec" / "inscrits-anapec.json",
        DOMAIN_DIR / "sous-domaines" / "agences de recrutement privé (arp)" / "agences de recrutement privé (arp).json",
        DOMAIN_DIR / "sous-domaines" / "ateliers-recherche-emploi" / "ateliers-recherche-emploi.json"
    ]
    
    for file_path in subdomain_files:
        print(f"Checking file: {file_path}")
        if file_path.exists():
            print(f"Processing file: {file_path}")
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

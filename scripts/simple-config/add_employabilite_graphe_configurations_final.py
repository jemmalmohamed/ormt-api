#!/usr/bin/env python3
"""
Script pour ajouter des configurations de graphiques simplifiées aux indicateurs du domaine
Employabilité et insertion professionnelle - Version finale avec correspondance exacte.
"""
import json
import sys
import os
import glob
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
DOMAIN_DIR = ROOT / "data" / "init-data" / "domaines" / "national" / "Employabilité et insertion professionnelle"

# Mapping from exact indicator names to graphe type codes
INDICATEUR_TO_TYPE = {
    # TAHFIZ
    "Nombre des salariés bénéficiaires TAHFIZ": "HISTOGRAMME",
    "Salariés bénéficiaires TAHFIZ par genre": "CAMEMBERT",
    "Salariés bénéficiaires TAHFIZ par âge": "CAMEMBERT",
    "Salariés bénéficiaires TAHFIZ par diplôme": "CAMEMBERT",
    "Salariés bénéficiaires TAHFIZ par SAE": "CAMEMBERT",
    "Salariés bénéficiaires TAHFIZ par taille d'entreprise": "CAMEMBERT",
    "Salariés bénéficiaires TAHFIZ par région": "CAMEMBERT",
    "Nombre d'entreprises bénéficiaires TAHFIZ": "HISTOGRAMME",
    "Entreprises bénéficiaires TAHFIZ par taille d'entreprise": "CAMEMBERT",
    "Entreprises bénéficiaires TAHFIZ par SAE": "CAMEMBERT",
    "Entreprises bénéficiaires TAHFIZ par région": "CAMEMBERT",
    
    # Contrats d'insertion (CI)
    "Nombre des bénéficiaires du contrats d'insertion": "HISTOGRAMME",
    "Bénéficiaires du CI par genre": "CAMEMBERT",
    "Bénéficiaires du CI par diplôme": "CAMEMBERT",
    "Bénéficiaires du CI par SAE": "CAMEMBERT",
    "Bénéficiaires du CI par région": "CAMEMBERT",
    "Nombre d'entreprises bénéficiaires du CI": "HISTOGRAMME",
    "Entreprises bénéficiaires du CI par taille d'entreprise": "CAMEMBERT",
    "Entreprises bénéficiaires du CI par SAE": "CAMEMBERT",
    "Entreprises bénéficiaires du CI par région": "CAMEMBERT",
    
    # Placement à l'international
    "Nombre des bénéficiaires du PI": "HISTOGRAMME",
    "Bénéficiaires du PI par genre": "CAMEMBERT",
    "Bénéficiaires du PI par pays d'accueil": "CAMEMBERT",
    "Bénéficiaires du PI par SAE": "CAMEMBERT",
    "Bénéficiaires du PI par type de contrat": "CAMEMBERT",
    "Bénéficiaires du PI par région": "CAMEMBERT",
    
    # Auto emploi
    "Objectif ciblé (accompagnement)": "HISTOGRAMME",
    "Nombre de porteurs de projet accompagnés": "HISTOGRAMME",
    "Porteurs de projet accompagnés par genre": "CAMEMBERT",
    "Porteurs de projet accompagnés par âge": "CAMEMBERT",
    "Porteurs de projet accompagnés par région": "CAMEMBERT",
    "Nombre d'entreprises crées": "HISTOGRAMME",
    "Entreprises crées par genre": "CAMEMBERT",
    "Entreprises crées par région": "CAMEMBERT",
    "Nombre des emplois crées": "HISTOGRAMME",
    "Emplois crées par région": "CAMEMBERT",
    
    # Emploi salarié
    "Objectif ciblé": "HISTOGRAMME",
    "Taux de réalisation de l'objectif ciblé": "HISTOGRAMME",
    "Nombre des bénéficiaires de l'emploi salarié": "HISTOGRAMME",
    "Emploi salarié par genre": "CAMEMBERT",
    "Emploi salarié par région": "CAMEMBERT",
    
    # Programmes d'amélioration de l'employabilité  
    "Objectif ciblé (hors formation en ligne)": "HISTOGRAMME",
    "Nombre de bénéficiaires des Programmes d'amélioration de l'employabilité": "HISTOGRAMME",
    "Bénéficiaires des Programmes d'amélioration de l'employabilité par composante": "CAMEMBERT",
    "Bénéficiaires de la FCE par région": "CAMEMBERT",
    "Bénéficiaires de la FQR par région": "CAMEMBERT",
    "Bénéficiaires de la FSE par région": "CAMEMBERT",
    "Bénéficiaires de la FSE par secteur d'activité économique": "CAMEMBERT",
    "Nombre des bénéficiaires du partenariat régional": "HISTOGRAMME",
    "Nombre des bénéficiaires du partenariat national": "HISTOGRAMME",
    "Nombre des bénéficiaires de la formation en ligne": "HISTOGRAMME",
    
    # Insertion des bénéficiaires du CI
     "Taux d'insertion à 3 ans par genre": "HISTOGRAMME",
    "Taux d'insertion à 3 ans par âge": "HISTOGRAMME",
    "Taux d'insertion à 3 ans par diplôme": "HISTOGRAMME",
    "Taux d'insertion à 3 ans par taille d'entreprise": "HISTOGRAMME",
    "Taux d'insertion à 3 ans par secteur d'activité économique": "HISTOGRAMME",
    "Taux d'insertion à 3 ans par région": "HISTOGRAMME",
    "Taux d'insertion à 3 ans selon la période de stage": "HISTOGRAMME",
    "Taux d'insertion à l'issue de stage par taille d'entreprise": "HISTOGRAMME",
    "Taux d'insertion à l'issue de stage par secteur d'activité économique": "HISTOGRAMME",
    # IDMAJ
    "Nombre des bénéficiaires IDMAJ, y compris le PI": "HISTOGRAMME",
    "Bénéficiaires IDMAJ par genre (y compris le PI)": "CAMEMBERT",
    "Bénéficiaires IDMAJ par âge (hors PI)": "CAMEMBERT",
    "Bénéficiaires IDMAJ par diplôme (hors PI)": "CAMEMBERT",
    "Bénéficiaires IDMAJ par type de contrat (hors PI)": "CAMEMBERT",
    "Bénéficiaires IDMAJ par secteur d'activité (hors PI)": "CAMEMBERT",
    "Bénéficiaires IDMAJ par région (hors PI)": "CAMEMBERT",
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
        DOMAIN_DIR / "sous-domaines" / "TAHFIZ" / "TAHFIZ.sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "Contrats d'Insertion (CI)" / "Contrats d'Insertion (CI).sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "Placement à l'International" / "Placement à l'International.sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "Auto emploi" / "Auto emploi.sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "Emploi salarié" / "Emploi salarié.sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "Insertion des bénéficiaires du CI" / "Insertion des bénéficiaires du CI.sousdomaine.json",
        DOMAIN_DIR / "sous-domaines" / "IDMAJ" / "IDMAJ.sousdomaine.json"
    ]
    
    # Find the Programmes file using glob pattern to handle special characters
    programmes_pattern = str(DOMAIN_DIR / "sous-domaines" / "Programmes*" / "*.sousdomaine.json")
    programmes_files = glob.glob(programmes_pattern)
    for prog_file in programmes_files:
        subdomain_files.append(Path(prog_file))
    
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

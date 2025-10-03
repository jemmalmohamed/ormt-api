#!/usr/bin/env python3
import json
import os
from pathlib import Path

# Define the target regions to keep
TARGET_REGIONS = ['marrakech-safi', 'national', 'ensemble', 'total']

def filter_json_file(file_path):
    """Filter a JSON file to keep only target regions"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        if 'data' not in data:
            print(f"Skipping {file_path}: No 'data' field found")
            return
        
        original_count = len(data['data'])
        
        # Filter data to keep only target regions
        filtered_data = []
        for entry in data['data']:
            if 'region' in entry and entry['region'] in TARGET_REGIONS:
                filtered_data.append(entry)
        
        # Update the data
        data['data'] = filtered_data
        
        # Write back to file
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        
        print(f"✅ {file_path}: {len(filtered_data)} entries kept (was {original_count})")
        
    except Exception as e:
        print(f"❌ Error processing {file_path}: {e}")

# List of files to process
files_to_process = [
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/création d'emploi/data/création d'emploi salariés par région.json",
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/tissu économique/data/nombre d'entreprises entrantes.json",
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/tissu économique/data/nombre d'entreprises sortantes.json",
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/tissu économique/data/nombre d'entreprises.json",
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/volume d'emploi/data/emploi_par_region.json",
    "data/init-data/domaines/regional/demande-de-travail/sous-domaines/volume d'emploi/data/salaries_declares_cnss_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/auto emploi/data/emplois_crees_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/auto emploi/data/entreprises_crees_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/auto emploi/data/porteurs_projet_accompagnes_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/contrats d'insertion (ci)/data/beneficiaires_ci_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/contrats d'insertion (ci)/data/entreprises_beneficiaires_ci_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/emploi salarié/data/emploi_salarie_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/idmaj/data/beneficiaires_idmaj_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/insertion des bénéficiaires du ci/data/taux_insertion_3ans_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/placement à l'international/data/beneficiaires_pi_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/programmes d'amélioration de l'employabilité/data/beneficiaires_fce_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/programmes d'amélioration de l'employabilité/data/beneficiaires_fqr_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/programmes d'amélioration de l'employabilité/data/beneficiaires_fse_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/tahfiz/data/entreprises_beneficiaires_tahfiz_par_region.json",
    "data/init-data/domaines/regional/Employabilité et insertion professionnelle/sous-domaines/tahfiz/data/salaries_beneficiaires_tahfiz_par_region.json",
    "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/agences de recrutement privé (arp)/data/agences_recrutement_prive_region.json"
]

print("🔄 Starting to filter JSON files...")
print(f"Target regions: {', '.join(TARGET_REGIONS)}")
print()

for file_path in files_to_process:
    if os.path.exists(file_path):
        filter_json_file(file_path)
    else:
        print(f"⚠️  File not found: {file_path}")

print("\n✅ Filtering complete!")
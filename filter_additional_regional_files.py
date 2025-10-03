#!/usr/bin/env python3
import json
import os

def filter_regional_data(file_path):
    """Filter JSON file to keep only marrakech-safi, national, ensemble, and total entries"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        original_count = len(data.get('data', []))
        
        # Filter data to keep only desired regions
        filtered_data = []
        for entry in data.get('data', []):
            region = entry.get('region', '').lower()
            if any(keyword in region for keyword in ['marrakech-safi', 'national', 'ensemble', 'total']):
                filtered_data.append(entry)
        
        data['data'] = filtered_data
        
        # Write back to file
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        
        new_count = len(filtered_data)
        print(f"✅ {os.path.basename(file_path)}: {new_count} entries kept (was {original_count})")
        
    except Exception as e:
        print(f"❌ Error processing {file_path}: {e}")

def main():
    # List of additional files to filter
    files_to_filter = [
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/variation de la masse salariale.json",
        "data/init-data/domaines/regional/demande-de-travail/sous-domaines/création d'emploi/data/création d'emploi par région.json",
        "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/ateliers de recherche d'emploi/data/are_region.json",
        "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/entretiens de positionnement/data/ep_region.json",
        "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/inscrits à l'anapec/data/inscrits_anapec_region.json",
        "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/inscrits à l'anapec/data/nouveaux_inscrits_anapec_region.json",
        "data/init-data/domaines/regional/Intermédiation sur le marché du travail/sous-domaines/offres d'emploi recueillies/data/offres_emploi_recueillies_region.json",
        "data/init-data/domaines/regional/Offre de formation/sous-domaines/formation professionnelle - insertion à 36 mois/data/Taux d'insertion des lauréats de la formation professionnelle à 36 mois par région.json",
        "data/init-data/domaines/regional/Offre de formation/sous-domaines/formation professionnelle - insertion à 9 mois/data/Taux d'insertion des lauréats de la formation professionnelle à 9 mois par région.json",
        "data/init-data/domaines/regional/Offre de formation/sous-domaines/formation professionnelle - lauréats/data/Nombre de lauréats de la formation professionnelle par région.json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/activité/data/Taux d'activité par région et milieu de résidence (en %).json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/chômage/data/Taux de chômage par region et genre.json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/chômage/data/Taux de chômage par region et milieu de résidence.json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/neet/data/Taux de NEET par région.json",
        "data/init-data/domaines/regional/Relations professionnelles et climat social/sous-domaines/climat social/data/Nombre de grèves déclenchées par région.json",
        "data/init-data/domaines/regional/Relations professionnelles et climat social/sous-domaines/climat social/data/Nombre de grèves évitées par région.json",
        "data/init-data/domaines/regional/Relations professionnelles et climat social/sous-domaines/climat social/data/Nombre de visites des inspecteurs de travail.json",
        "data/init-data/domaines/regional/Relations professionnelles et climat social/sous-domaines/droit conventionnel/data/Nombre de conventions collectives par région.json",
        "data/init-data/domaines/regional/Relations professionnelles et climat social/sous-domaines/droit conventionnel/data/Nombre de protocoles signés par région.json"
    ]
    
    print("Starting filtering of additional regional JSON files...")
    print("=" * 60)
    
    processed_count = 0
    for file_path in files_to_filter:
        if os.path.exists(file_path):
            filter_regional_data(file_path)
            processed_count += 1
        else:
            print(f"⚠️  File not found: {file_path}")
    
    print("=" * 60)
    print(f"Completed filtering {processed_count} additional files!")

if __name__ == "__main__":
    main()
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
    # List of the latest files to filter
    files_to_filter = [
        "data/init-data/domaines/regional/Cadre macro-économique/sous-domaines/population et conditions de vie/data/taux de pauvreté multidimensionnelle par région.json",
        "data/init-data/domaines/regional/Cadre macro-économique/sous-domaines/population et conditions de vie/data/taux de vulnérabilité à la pauvreté par région (en %).json",
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/masse salariale par secteur d'activité.json",
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/productivité par secteur d'activité.json",
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/salaire moyen par secteur d'activité.json",
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/valeur ajoutée par secteur d'activité.json",
        "data/init-data/domaines/regional/Compétitivité et salaires/sous-domaines/salaires & productivité/data/variation de la masse salariale.json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/activité/data/Taux d'activité par région et genre (en %).json",
        "data/init-data/domaines/regional/Offre de travail/sous-domaines/activité/data/Taux d'activité par région et milieu de résidence (en %).json"
    ]
    
    print("Starting filtering of latest regional JSON files...")
    print("=" * 70)
    
    processed_count = 0
    for file_path in files_to_filter:
        if os.path.exists(file_path):
            filter_regional_data(file_path)
            processed_count += 1
        else:
            print(f"⚠️  File not found: {file_path}")
    
    print("=" * 70)
    print(f"Completed filtering {processed_count} latest files!")

if __name__ == "__main__":
    main()
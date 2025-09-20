import json
import os
from pathlib import Path
import glob
from collections import OrderedDict

def add_titre_to_indicators(json_file_path):
    print(f"Processing {json_file_path}")
    with open(json_file_path, 'r', encoding='utf-8') as f:
        data = json.load(f, object_pairs_hook=OrderedDict)
    
    if 'indicateurs' in data and isinstance(data['indicateurs'], list):
        for i, indicator in enumerate(data['indicateurs']):
            if isinstance(indicator, dict) and 'nom' in indicator:
                nom_value = indicator['nom']
                # Create new ordered dict
                new_indicator = OrderedDict()
                new_indicator['nom'] = nom_value
                new_indicator['titre'] = nom_value
                # Add the rest
                for key, value in indicator.items():
                    if key not in ['nom', 'titre']:
                        new_indicator[key] = value
                data['indicateurs'][i] = new_indicator
    
    with open(json_file_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

def main():
    base_path = '/home/jemmal/dev/ormt/ormt/ormt-api/data/init-data/domaines/national'
    
    pattern = os.path.join(base_path, '**', 'sous-domaines', '**', '*.sousdomaine.json')
    files = glob.glob(pattern, recursive=True)
    print(f"Found {len(files)} files")
    for json_file in files:
        print(f"Processing: {json_file}")
        add_titre_to_indicators(json_file)
    
    print("All files processed.")

if __name__ == "__main__":
    main()

def main():
    base_path = Path('/home/jemmal/dev/ormt/ormt/ormt-api/data/init-data/domaines/national')
    
    for json_file in base_path.rglob('**/sous-domaines/**/*.sousdomaine.json'):
        if 'data/' not in str(json_file):
            print(f"Processing: {json_file}")
            add_titre_to_indicators(json_file)
    
    print("All files processed.")

if __name__ == "__main__":
    main()
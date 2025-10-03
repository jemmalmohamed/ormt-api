#!/usr/bin/env python3
import json

# Read the file
file_path = 'data/init-data/domaines/regional/Cadre macro-économique/sous-domaines/contexte économique/data/Valeur ajoutée par branche d\'activité aux prix courants (en millions de DH).json'

with open(file_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

# Filter to keep only marrakech-safi entries (no national entries found in this file)
filtered_data = []
for entry in data['data']:
    if entry['region'] in ['marrakech-safi', 'national', 'ensemble', 'total']:
        filtered_data.append(entry)

# Update the data
data['data'] = filtered_data

# Write back to file
with open(file_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f'Filtered {len(filtered_data)} entries from original data')
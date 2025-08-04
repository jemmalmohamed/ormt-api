import csv
import json
from pathlib import Path

def clean_value(value_str):
    """Clean and convert string values to integers"""
    if not value_str or value_str.strip() == '':
        return 0
    # Remove spaces and commas
    cleaned = value_str.replace(' ', '').replace(',', '').strip()
    try:
        return int(cleaned)
    except ValueError:
        return 0

def parse_csv_to_json():
    # File paths
    csv_file = Path("/home/jemmal/dev/ormt/ormt/ormt-api/data/back/Classeur1.csv")
    json_file = Path("/home/jemmal/dev/ormt/ormt/ormt-api/data/init-data/domaines/demande-de-travail/sous-domaines/tissu-economique/data/nombre d'entreprises.json")
    
    # Read CSV file with tab delimiter
    with open(csv_file, 'r', encoding='utf-8') as f:
        reader = csv.reader(f, delimiter='\t')
        rows = list(reader)
    
    # Extract header row (sectors are in columns 3 onwards)
    header = rows[0]
    sector_columns = header[3:]  # Skip annuel, taille, region columns
    
    print(f"Found sectors: {sector_columns}")
    
    data_entries = []
    
    # Process each data row (starting from row 1)
    for row_idx in range(1, len(rows)):
        row = rows[row_idx]
        
        # Extract basic info
        annuel = row[0]
        taille = row[1] 
        region = row[2].strip()  # Remove any leading/trailing spaces
        
        # Process each sector value
        for sector_idx, sector in enumerate(sector_columns):
            value_col_idx = sector_idx + 3  # Offset for annuel, taille, region
            
            if value_col_idx < len(row):
                value = clean_value(row[value_col_idx])
                
                # Create data entry
                entry = {
                    "secteur_activite": sector,
                    "taille": taille,
                    "region": region,
                    "annuel": annuel,
                    "valeur": value
                }
                data_entries.append(entry)
    
    # Create final JSON structure
    json_data = {
        "indicateur": "nombre d'entreprises",
        "data": data_entries
    }
    
    # Write to JSON file
    json_file.parent.mkdir(parents=True, exist_ok=True)
    with open(json_file, 'w', encoding='utf-8') as f:
        json.dump(json_data, f, ensure_ascii=False, indent=4)
    
    print(f"Successfully converted CSV to JSON!")
    print(f"Total entries: {len(data_entries)}")
    print(f"JSON file saved to: {json_file}")
    
    # Print first few entries for verification
    print("\nFirst 5 entries:")
    for i, entry in enumerate(data_entries[:5]):
        print(f"{i+1}. {entry}")
    
    # Print summary statistics
    years = set(entry['annuel'] for entry in data_entries)
    regions = set(entry['region'] for entry in data_entries)
    sizes = set(entry['taille'] for entry in data_entries)
    sectors = set(entry['secteur_activite'] for entry in data_entries)
    
    print(f"\nData summary:")
    print(f"Years: {sorted(years)}")
    print(f"Company sizes: {sorted(sizes)}")
    print(f"Regions: {len(regions)} regions")
    print(f"Sectors: {sectors}")

if __name__ == "__main__":
    parse_csv_to_json()

import csv
import json
import re
from pathlib import Path

def parse_csv_to_json():
    # File paths
    csv_file = Path("/home/jemmal/dev/ormt/ormt/ormt-api/data/back/Classeur1.csv")
    json_file = Path("/home/jemmal/dev/ormt/ormt/ormt-api/data/init-data/domaines/demande-de-travail/sous-domaines/tissu-economique/data/nombre d'entreprises.json")
    
    # Read CSV file
    with open(csv_file, 'r', encoding='utf-8') as f:
        reader = csv.reader(f, delimiter='\t')
        rows = list(reader)
    
    # Extract header information
    years_row = rows[0][1:]  # Skip first column
    sizes_row = rows[1][1:]
    regions_row = rows[2][1:]
    
    # Parse years (2019-2023)
    years = []
    for year in years_row:
        if year and year not in years:
            years.append(year)
    
    # Parse company sizes
    company_sizes = ["1 à 9 salariés", "10-49 salariés", "50 à 199 salariés", "200 à 499 salariés", "500 salariés et plus"]
    
    # Parse regions (clean up extra spaces)
    regions = []
    for region in regions_row:
        clean_region = region.strip()
        if clean_region and clean_region not in regions:
            regions.append(clean_region)
    
    # Data structure to hold results
    data_entries = []
    
    # Process each sector row (starting from row 3)
    for row_idx in range(3, len(rows)):
        sector = rows[row_idx][0]
        values = rows[row_idx][1:]
        
        # Track current position in the data
        col_idx = 0
        
        # For each year
        for year in years:
            # For each company size
            for size in company_sizes:
                # For each region
                for region in regions:
                    if col_idx < len(values):
                        # Clean the value (remove spaces and convert to int)
                        value_str = values[col_idx].replace(' ', '').replace(',', '')
                        try:
                            value = int(value_str) if value_str else 0
                        except ValueError:
                            value = 0
                        
                        # Create data entry
                        entry = {
                            "secteur_activite": sector,
                            "taille": size,
                            "region": region,
                            "annuel": year,
                            "valeur": value
                        }
                        data_entries.append(entry)
                        
                        col_idx += 1
    
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

if __name__ == "__main__":
    parse_csv_to_json()

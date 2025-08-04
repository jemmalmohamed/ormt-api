#!/usr/bin/env python3
"""
Script pour multiplier par 1000 les valeurs dans les fichiers HCP de création d'emploi
"""

import json
import os

# Liste des fichiers HCP à convertir (multiplier par 1000)
hcp_files = [
    "création d'emploi par genre.json",
    "création d'emploi par diplôme.json", 
    "création d'emploi par région.json",
    "création d'emploi par secteur d'activité -2p.json",
    "création d'emploi par secteur d'activité -7p.json",
    "création d'emploi par secteur d'emploi.json",
    "création d'emploi par statut professionnel.json",
    "création d'emploi par type d'emploi.json"
]

# Chemin vers le dossier des données
data_dir = "data/init-data/domaines/demande-de-travail/sous-domaines/création d'emploi/data"

def convert_file_values(file_path):
    """Multiplie par 1000 toutes les valeurs dans un fichier JSON"""
    try:
        # Lire le fichier
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Multiplier toutes les valeurs par 1000
        for item in data.get('data', []):
            if 'valeur' in item and isinstance(item['valeur'], (int, float)):
                item['valeur'] = int(item['valeur'] * 1000)
        
        # Écrire le fichier modifié
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
        
        print(f"✅ Converti: {os.path.basename(file_path)}")
        return True
        
    except Exception as e:
        print(f"❌ Erreur avec {os.path.basename(file_path)}: {e}")
        return False

def main():
    """Convertit tous les fichiers HCP"""
    print("🔄 Conversion des valeurs HCP en milliers (×1000)...")
    print("=" * 60)
    
    converted = 0
    errors = 0
    
    for filename in hcp_files:
        file_path = os.path.join(data_dir, filename)
        
        if os.path.exists(file_path):
            if convert_file_values(file_path):
                converted += 1
            else:
                errors += 1
        else:
            print(f"⚠️  Fichier non trouvé: {filename}")
            errors += 1
    
    print("=" * 60)
    print(f"📊 Résumé:")
    print(f"   ✅ Fichiers convertis: {converted}")
    print(f"   ❌ Erreurs: {errors}")
    print(f"   📁 Total traité: {converted + errors}")
    
    if converted > 0:
        print("\n🎉 Conversion terminée avec succès!")
        print("Les valeurs HCP sont maintenant en unités réelles (milliers).")

if __name__ == "__main__":
    main()

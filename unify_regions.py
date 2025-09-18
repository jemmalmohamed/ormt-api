#!/usr/bin/env python3
"""
Script pour unifier les noms de régions dans les fichiers JSON
Auteur: GitHub Copilot
Date: 15 septembre 2025
"""

import json
import os
import re
from pathlib import Path
from typing import Dict, List, Tuple

# Mapping des variations vers les noms standardisés
REGION_MAPPING = {
    # Tanger-Tétouan-Al Hoceïma
    'tanger-tetouan-al hoceima': 'tanger-tétouan-al hoceïma',
    'tanger-tétouan-al hoceïma': 'tanger-tétouan-al hoceïma',
    'tanger-tétouan-al hoceima': 'tanger-tétouan-al hoceïma',
    'tanger-tétouan-al houceima': 'tanger-tétouan-al hoceïma',
    'tanger tétouan al hoceïma': 'tanger-tétouan-al hoceïma',
    'tanger-tétouan- al hoceima': 'tanger-tétouan-al hoceïma',

    # Béni Mellal-Khénifra
    'beni mellal-khénifra': 'béni mellal-khénifra',
    'béni melle-khénifra': 'béni mellal-khénifra',
    'béni mellal-khénifra': 'béni mellal-khénifra',

    # Casablanca-Settat
    'grand casablanca-settat': 'casablanca-settat',
    'grand casablanca- settat': 'casablanca-settat',
    'grand grand casablanca-settat': 'casablanca-settat',
    'casablanca-settat': 'casablanca-settat',

    # Rabat-Salé-Kénitra
    'rabat salé kénitra': 'rabat-salé-kénitra',
    'rabat-salé- kénitra': 'rabat-salé-kénitra',
    'rabat-salé-kénitra': 'rabat-salé-kénitra',

    # Dakhla-Oued Ed-Dahab
    'dakhla-oued ed dahab': 'dakhla-oued ed-dahab',
    'dakhla-oued ed-dahab': 'dakhla-oued ed-dahab',

    # Laâyoune-Sakia El Hamra
    'laayoune-sakia al hamra': 'laâyoune-sakia el hamra',
    'laayoune-sakia el hamra': 'laâyoune-sakia el hamra',

    # Souss-Massa
    'souss massa': 'souss-massa',
    'souss-massa': 'souss-massa',

    # Autres régions (déjà cohérentes)
    'l\'oriental': 'l\'oriental',
    'fès-meknès': 'fès-meknès',
    'marrakech-safi': 'marrakech-safi',
    'drâa-tafilalet': 'drâa-tafilalet',
    'guelmim-oued noun': 'guelmim-oued noun',

    # Entrées non-régionales (laisser telles quelles)
    'national': 'national',
    'total': 'total',
    'ensemble': 'ensemble',
    'non renseigné': 'non renseigné',
    'autres (offres à l\'international)': 'autres (offres à l\'international)',
    'régions du sud': 'régions du sud',
}

def normalize_region_name(region_name: str) -> str:
    """Normalise un nom de région selon le mapping défini"""
    # Convertir en minuscules pour la recherche
    normalized = region_name.lower().strip()

    # Appliquer le mapping
    if normalized in REGION_MAPPING:
        return REGION_MAPPING[normalized]

    # Si pas trouvé dans le mapping, retourner tel quel
    return region_name

def process_json_file(file_path: Path) -> Tuple[int, List[str]]:
    """Traite un fichier JSON et retourne le nombre de modifications et la liste des changements"""
    modifications = 0
    changes = []

    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)

        # Fonction récursive pour traiter toutes les valeurs de région
        def process_data(obj):
            nonlocal modifications, changes
            if isinstance(obj, dict):
                for key, value in obj.items():
                    if key == 'region' and isinstance(value, str):
                        normalized = normalize_region_name(value)
                        if normalized != value:
                            changes.append(f"  '{value}' -> '{normalized}'")
                            obj[key] = normalized
                            modifications += 1
                    else:
                        process_data(value)
            elif isinstance(obj, list):
                for item in obj:
                    process_data(item)

        process_data(data)

        # Sauvegarder le fichier si des modifications ont été faites
        if modifications > 0:
            with open(file_path, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)

        return modifications, changes

    except Exception as e:
        print(f"Erreur lors du traitement de {file_path}: {e}")
        return 0, []

def main():
    """Fonction principale"""
    base_path = Path('/home/jemmal/dev/ormt/ormt/ormt-api/data/init-data/domaines')

    if not base_path.exists():
        print(f"Chemin {base_path} n'existe pas!")
        return

    total_files = 0
    total_modifications = 0
    all_changes = []

    print("🔍 Recherche et traitement des fichiers JSON...")
    print("=" * 60)

    # Parcourir tous les fichiers JSON
    for json_file in base_path.rglob('*.json'):
        total_files += 1
        modifications, changes = process_json_file(json_file)

        if modifications > 0:
            print(f"📄 {json_file.relative_to(base_path)}: {modifications} modification(s)")
            all_changes.extend([f"{json_file.name}: {change}" for change in changes])
            total_modifications += modifications

    print("=" * 60)
    print("📊 RAPPORT FINAL")
    print(f"📁 Fichiers analysés: {total_files}")
    print(f"🔧 Modifications totales: {total_modifications}")

    if all_changes:
        print("\n📝 DÉTAIL DES MODIFICATIONS:")
        for change in all_changes[:20]:  # Limiter à 20 pour éviter un output trop long
            print(f"  {change}")
        if len(all_changes) > 20:
            print(f"  ... et {len(all_changes) - 20} autres modifications")

    print("\n✅ Traitement terminé!")

if __name__ == "__main__":
    main()
#!/bin/bash

# Navigate to the script directory
cd "$(dirname "$0")"

# Run the Python script
python3 populate_companies_data.py

echo "Data population complete!"

#!/bin/bash

# Script to update version numbers in documentation files
# Usage: ./update-version-in-docs.sh <new_version>

set -e

if [ -z "$1" ]; then
    echo "Error: Version number required"
    echo "Usage: $0 <new_version>"
    exit 1
fi

NEW_VERSION="$1"
echo "Updating documentation to version: $NEW_VERSION"

# Files to update
FILES=(
    "README.md"
    "doc.md"
    "doc-en.md"
    "CHANGELOG.md"
)

# Update version in all documentation files
for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "Updating $file..."
        
        # Replace version in <version>X.Y.Z</version> tags
        sed -i.bak "s|<version>[0-9]\+\.[0-9]\+\.[0-9]\+</version>|<version>${NEW_VERSION}</version>|g" "$file"
        
        # Replace version in Maven coordinates (deploy-manifest-plugin:X.Y.Z)
        sed -i.bak "s|deploy-manifest-plugin:[0-9]\+\.[0-9]\+\.[0-9]\+|deploy-manifest-plugin:${NEW_VERSION}|g" "$file"
        
        # Replace version in artifact names (deploy-manifest-plugin-X.Y.Z)
        sed -i.bak "s|deploy-manifest-plugin-[0-9]\+\.[0-9]\+\.[0-9]\+|deploy-manifest-plugin-${NEW_VERSION}|g" "$file"
        
        # Replace version in URLs and paths
        sed -i.bak "s|/[0-9]\+\.[0-9]\+\.[0-9]\+/|/${NEW_VERSION}/|g" "$file"
        
        # Remove backup file
        rm -f "${file}.bak"
        echo "✅ Updated $file"
    else
        echo "⚠️  File not found: $file"
    fi
done

echo "✅ All documentation files updated to version $NEW_VERSION"

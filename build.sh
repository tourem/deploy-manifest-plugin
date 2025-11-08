#!/bin/bash

# Build script for Maven Project Descriptor Generator

set -e

echo "🔨 Building Maven Project Descriptor Generator..."
echo ""

mvn clean package

echo ""
echo "✅ Build complete!"
echo "📦 JAR location: target/maven-project-descriptor-1.0-SNAPSHOT.jar"
echo ""
echo "Run with: ./run.sh /path/to/maven/project"


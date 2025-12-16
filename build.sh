#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Директории для install
for dir in events-contract matchmaking-api; do
    if [ -d "$dir" ] && [ -f "$dir/mvnw" ]; then
        cd "$dir"
        chmod +x mvnw
        ./mvnw install
        cd ..
    fi
done

# Директории для clean package
for dir in audit-service matchmaking-rest statistics-service; do
    if [ -d "$dir" ] && [ -f "$dir/mvnw" ]; then
        cd "$dir"
        chmod +x mvnw
        ./mvnw clean package -DskipTests
        cd ..
    fi
done

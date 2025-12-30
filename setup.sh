#!/usr/bin/env bash
set -e

echo "=== Sillicat Dev Setup ==="

# Variables
MCP_DIR="MavenMCP-1.8.9"
SILLICAT_DIR="sillicat-dist"
OPTIFINE_RAR="Optifine_SRC_1.8.9_HD_U_M6_pre2.rar"
DEV_DIR="sillicat-prepped"

# Clean previous runs
rm -rf "$MCP_DIR" "$SILLICAT_DIR" Optifine-SRC "$DEV_DIR"

# Download sources
echo "Downloading MCP 1.8.9..."
git clone https://github.com/Marcelektro/MavenMCP-1.8.9.git "$MCP_DIR"

echo "Downloading Optifine 1.8.9 sources..."
wget -O "$OPTIFINE_RAR" "https://github.com/Hexeption/Optifine-SRC/raw/refs/heads/master/Optifine%20SRC%20Version%20%5B1.8.9%20HD%20U%20M6%20pre2%5D.rar"

# Extract Optifine
echo "Extracting Optifine..."
mkdir Optifine-SRC
unrar x "$OPTIFINE_RAR" Optifine-SRC/

# Setup Optifine sources
rm -rf "$MCP_DIR/src/main/java/net"
cp -r Optifine-SRC/net "$MCP_DIR/src/main/java/"
rm -rf "$MCP_DIR/src/main/resources/assets"
cp -r Optifine-SRC/assets "$MCP_DIR/src/main/resources/assets"

# Setup Sillicat
echo "Downloading Sillicat..."
git clone https://github.com/amiliecoding/sillicat-dist "$SILLICAT_DIR"
cp -r "$SILLICAT_DIR/sillicat" "$MCP_DIR/src/main/java/"
rm "$MCP_DIR/pom.xml"
cp "$SILLICAT_DIR/pom.xml" "$MCP_DIR/pom.xml"

# Apply patches safely
echo "Applying patches..."
PATCH_DIR="$(pwd)/$SILLICAT_DIR/sillicat/patches"

for patch_file in "$PATCH_DIR"/*.patch; do
    echo "Applying $(basename "$patch_file")..."
    patch -p0 -d "$MCP_DIR/src/main/java" < "$patch_file"
done

# Move prepared MCP to dev folder
mv "$MCP_DIR" "$DEV_DIR"

# Cleanup temporary files
echo "Cleaning up temporary files..."
rm -rf "$SILLICAT_DIR" Optifine-SRC "$OPTIFINE_RAR"

echo "=== Sillicat development environment ready at $DEV_DIR ==="
echo "Open $DEV_DIR in IntelliJ as a Maven project."

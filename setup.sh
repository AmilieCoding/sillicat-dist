git clone https://github.com/Marcelektro/MavenMCP-1.8.9.git ../
wget https://github.com/Hexeption/Optifine-SRC/raw/refs/heads/master/Optifine%20SRC%20Version%20%5B1.8.9%20HD%20U%20M6%20pre2%5D.rar ../

rm -rf ../MavenMCP-1.8.9/src/main/java/net
mkdir Optifine-SRC
mv 'Optifine SRC Version [1.8.9 HD U M6 pre2].rar' Optifine-SRC
unrar x ../Optifine-SRC/'Optifine SRC Version [1.8.9 HD U M6 pre2].rar'
cp -r ../Optifine-SRC/net ../MavenMCP-1.8.9/src/main/java/net
rm -rf ../MavenMCP-1.8.9/src/main/resources/assets
cp -r ../Optifine-SRC/assets ../MavenMCP-1.8.9/src/main/resources/assets

git clone https://github.com/amiliecoding/sillicat-dist
cp -r ../sillicat-dist/sillicat ../MavenMCP-1.8.9/src/main/java/

patch -p0 <../sillicat-dist/patches/Minecraft.patch

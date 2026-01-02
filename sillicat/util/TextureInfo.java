package sillicat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TextureInfo {
    public final int w, h;

    public TextureInfo(int w, int h) {
        this.w = w;
        this.h = h;
    }

    private static final Map<ResourceLocation, TextureInfo> CACHE = new ConcurrentHashMap<>();

    public static TextureInfo of(ResourceLocation rl) {
        TextureInfo cached = CACHE.get(rl);
        if (cached != null) return cached;

        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(rl);
            try (InputStream in = res.getInputStream()) {
                BufferedImage img = ImageIO.read(in);
                TextureInfo info = new TextureInfo(img.getWidth(), img.getHeight());
                CACHE.put(rl, info);
                return info;
            }
        } catch (Exception e) {
            System.out.println("TextureInfo failed for " + rl + " : " + e);
            TextureInfo info = new TextureInfo(256, 256);
            CACHE.put(rl, info);
            return info;
        }
    }
}

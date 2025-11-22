package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFrames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class DynamicSkinManager {

    static DynamicTexture dynamicSkin;
    static ResourceLocation dynamicSkinLoc;
    static NativeImage vanillaSkin;

    public static boolean initialized = false;

    public static final int PlayerTextureWidth = 64;
    public static final int PlayerTextureHeight = 64;

    public static CompletableFuture<Void> initialize() {
        return VanillaSkin.loadVanillaSkin().thenAccept(img -> {
            initialized = true;

            vanillaSkin = img;

            dynamicSkin = new DynamicTexture(vanillaSkin);
            dynamicSkinLoc = Minecraft.getInstance()
                    .getTextureManager()
                    .register("dynamic_skin", dynamicSkin);
        });
    }

    public static ResourceLocation getDynamicSkinTextureLocation() {
        return dynamicSkinLoc;
    }

    public static void updateHead(NativeImage head) {
        if (dynamicSkin == null) return;

        NativeImage img = dynamicSkin.getPixels();
        if (img == null)
        {
            EyeFrames.EYEFRAMES_LOGGER.error("Failed to load dynamic player head");
            assert false;
        }

        for (int y = 0; y < PlayerHead.HeadTextureHeight; y++) {
            for (int x = 0; x < PlayerHead.HeadTextureWidth; x++) {
                img.setPixelRGBA(x, y, head.getPixelRGBA(x, y));
            }
        }

        dynamicSkin.upload();
    }
}
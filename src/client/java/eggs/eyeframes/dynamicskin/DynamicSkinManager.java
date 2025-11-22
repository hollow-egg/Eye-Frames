package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFrames;
import eggs.eyeframes.EyeFramesClient;

import java.util.concurrent.CompletableFuture;

import static eggs.eyeframes.EyeFramesClient.*;

public class DynamicSkinManager {
    static NativeImage vanillaSkin;

    public static boolean initialized = false;

    public static CompletableFuture<Void> initialize() {
        initialized = true;

        return reset();
    }

    public static CompletableFuture<Void> reset(){
        return VanillaSkin.loadVanillaSkin().thenAccept(img -> {
            vanillaSkin = img;

            EyeFramesClient.getDynamicSkin().setPixels(vanillaSkin);
            EyeFramesClient.getDynamicSkin().upload();
        });
    }

    public static void updateHead(NativeImage head) {
        NativeImage img = EyeFramesClient.getDynamicSkin().getPixels();
        if (img == null)
        {
            EyeFrames.EYEFRAMES_LOGGER.error("Failed to load dynamic player head");
            assert false;
        }

        for (int y = 0; y < HeadTextureHeight; y++) {
            for (int x = 0; x < HeadTextureWidth; x++) {
                img.setPixelRGBA(x, y, head.getPixelRGBA(x, y));
            }
        }

        EyeFramesClient.getDynamicSkin().upload();
    }

    public static NativeImage crop(NativeImage source, int x, int y, int width, int height) {
        NativeImage out = new NativeImage(width, height, true);

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                out.setPixelRGBA(px, py, source.getPixelRGBA(x + px, y + py));
            }
        }

        return out;
    }
}
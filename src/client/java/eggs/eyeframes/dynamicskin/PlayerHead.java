package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;

import java.util.HashMap;

public class PlayerHead {
    public static final int HeadTextureWidth = 64;
    public static final int HeadTextureHeight = 16;

    private static final HashMap<String, NativeImage> headStates = new HashMap<>();
    private static String currentState = "default";

    public static void reset(){
        headStates.clear();
        NativeImage img = crop(VanillaSkin.getVanillaSkin(), 0, 0, HeadTextureWidth, HeadTextureHeight);
        headStates.put(currentState, img);
    }

    public static NativeImage getTexture(){
        return getTexture(currentState);
    }
    public static NativeImage getTexture(String state){
        if (headStates.isEmpty()) {
            reset();
        }
        return headStates.getOrDefault(state, VanillaSkin.getVanillaSkin());
    }
    public static void setState(String state){
        currentState = state;
    }

    public static NativeImage crop(NativeImage source, int x, int y, int width, int height) {
        NativeImage out = new NativeImage(width, height, true);

        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int color = source.getPixelRGBA(x + px, y + py);
                out.setPixelRGBA(px, py, color);
            }
        }

        return out;
    }
}

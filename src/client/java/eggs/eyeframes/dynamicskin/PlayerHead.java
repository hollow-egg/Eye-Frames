package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFrames;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHead {

    public static final int HeadTextureWidth = 64;
    public static final int HeadTextureHeight = 16;

    private static final HashMap<String, NativeImage> headStates = new HashMap<>();
    private static String currentState = "default";

    public static boolean initialized = false;

    private static ModelPart headModel = null;

    public static void initialize() {
        initialized = true;
        reset();
    }

    public static void reset() {
        if (!initialized) return;

        headStates.clear();
        NativeImage base = DynamicSkinManager.vanillaSkin;
        NativeImage head = crop(base, 0, 0, HeadTextureWidth, HeadTextureHeight);
        headStates.put("default", head);
    }

    public static NativeImage getTexture() {
        return getTexture(currentState);
    }

    public static NativeImage getTexture(String state) {
        if (!initialized) {
            EyeFrames.EYEFRAMES_LOGGER.error("PlayerHead used before initialization");
            assert false;
        }

        return headStates.getOrDefault(state, headStates.get("default"));
    }

    public static void setState(String state) {
        currentState = state;
    }

    public static void putState(String state, NativeImage head) {
        headStates.put(state, head);
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

    public static ModelPart getHeadModel() {
        if (headModel != null) return headModel;

        EnumSet<Direction> allFaces = EnumSet.allOf(Direction.class);

        ModelPart.Cube headCube = new ModelPart.Cube(
                0, 0,// uv
                -4.0f, -8.0f, -4.0f,
                8.0f, 8.0f, 8.0f,
                0, 0, 0,
                false,
                64, 64,// texture size
                allFaces
        );

        headModel = new ModelPart(List.of(headCube), Map.of());

        return headModel;
    }
}
package eggs.eyeframes.dynamicplayerhead;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.texture.Dumpable;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class DynamicPlayerHeadManager {
    private static final ConcurrentHashMap<UUID, DynamicTexture> textures = new ConcurrentHashMap<>();

    public static void setTexture(UUID playerID, NativeImage image) {
        DynamicTexture tex = new DynamicTexture(image);
        textures.put(playerID, tex);
    }

    public static Dumpable getTexture(UUID playerID) {
        return textures.get(playerID);
    }
}
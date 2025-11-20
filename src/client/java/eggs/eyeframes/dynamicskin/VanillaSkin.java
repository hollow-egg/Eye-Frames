package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class VanillaSkin {
    private static final ResourceLocation steve = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public static NativeImage getVanillaSkin() {
        NativeImage skinTextureBase;
        try {
            skinTextureBase = NativeImage.read(Minecraft.getInstance().getResourceManager().open(getVanillaSkinLocation()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return skinTextureBase;
    }
    public static ResourceLocation getVanillaSkinLocation(){
        if (Minecraft.getInstance().player == null)
            return steve; //fallback

        return Minecraft.getInstance()
                .getSkinManager()
                .getInsecureSkin(Minecraft.getInstance().player.getGameProfile())
                .texture();
    }
}

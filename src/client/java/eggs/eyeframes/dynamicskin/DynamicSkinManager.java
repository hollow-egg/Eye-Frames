package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import static eggs.eyeframes.dynamicskin.PlayerHead.HeadTextureHeight;
import static eggs.eyeframes.dynamicskin.PlayerHead.HeadTextureWidth;

public class DynamicSkinManager {

    static DynamicTexture DynamicSkinTexture = null;
    static ResourceLocation DynamicSkinTextureLocation = null;

    public static ResourceLocation getDynamicSkinTextureLocation(){
        if (DynamicSkinTextureLocation == null) {
            updateHeadParts(PlayerHead.getTexture());
        }
        return DynamicSkinTextureLocation;
    }

    public static void updateHeadParts(NativeImage customHead) {
        Minecraft mc = Minecraft.getInstance();

        NativeImage target;
        if (DynamicSkinTextureLocation == null)
            target = VanillaSkin.getVanillaSkin();
        else
            target = DynamicSkinTexture.getPixels();

        assert target != null;
        for (int y = 0; y < HeadTextureHeight; y++) {
            for (int x = 0; x < HeadTextureWidth; x++) {
                int rgba = customHead.getPixelRGBA(x, y);
                target.setPixelRGBA(x, y, rgba);
            }
        }

        if (DynamicSkinTexture != null) {
            DynamicSkinTexture.upload();
        }
        else {
            DynamicSkinTexture = new DynamicTexture(target);
            ResourceLocation playerSkin = VanillaSkin.getVanillaSkinLocation();
            DynamicSkinTextureLocation = mc.getTextureManager().register("dynamic_skin_" + playerSkin.getPath(), DynamicSkinTexture);
        }
    }
}

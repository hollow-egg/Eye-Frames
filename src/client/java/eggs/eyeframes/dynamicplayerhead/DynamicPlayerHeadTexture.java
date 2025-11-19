package eggs.eyeframes.dynamicplayerhead;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import static eggs.eyeframes.EyeFrames.*;

public class DynamicPlayerHeadTexture {

    public static final String PLAYER_HEAD_PREFIX = "player_head";
    private static ResourceLocation dynamicTextureID = null;
    private static DynamicTexture dynamicTexture;

    private static void upload(NativeImage img){
        if (dynamicTextureID != null)
            dynamicTexture.close();

        dynamicTexture = new DynamicTexture(img);
        dynamicTextureID = Minecraft.getInstance().getTextureManager().register(PLAYER_HEAD_PREFIX, dynamicTexture);
    }

    public static void set(ResourceLocation textureID){
        LOGGER.info("Setting to: {}", textureID.toString());
        Minecraft client = Minecraft.getInstance();

        NativeImage head = new NativeImage(PlayerHeadTextureWidth, PlayerHeadTextureHeight, false);

        try {
            // Load the texture's bytes
            byte[] fullSkinBytes = client.getResourceManager()
                    .open(textureID)
                    .readAllBytes();

            NativeImage fullSkin = NativeImage.read(new ByteArrayInputStream(fullSkinBytes));

            if (fullSkin.getWidth() < PlayerHeadTextureWidth || fullSkin.getHeight() < PlayerHeadTextureHeight){
                throw new Exception("Incorrect player skin size detected!");
            }

            for (int y = 0; y < PlayerHeadTextureHeight; y++) {
                for (int x = 0; x < PlayerHeadTextureWidth; x++) {
                    head.setPixelRGBA(x, y, fullSkin.getPixelRGBA(x, y));
                }
            }

        } catch (Exception e) {
            LOGGER.error("Failed to load: {}", textureID);
            LOGGER.error(e.toString());

            //fallback
            for (int y = 0; y < PlayerHeadTextureHeight; y++) {
                for (int x = 0; x < PlayerHeadTextureWidth; x++) {
                    head.setPixelRGBA(x, y, 0xFFFF00FF);
                }
            }
        }

        upload(head);
    }

    public static ResourceLocation get() {
        if (dynamicTextureID == null){
            reset();
        }

        return dynamicTextureID;
    }

    public static void reset() {
        Minecraft client = Minecraft.getInstance();
        GameProfile profile = client.getGameProfile();

        ResourceLocation skinID = client.getSkinManager().getInsecureSkin(profile).texture();
        set(skinID);
    }

    private static void setDefaultHeadTexture() {
        ResourceLocation steveId = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");
        set(steveId);
    }
}
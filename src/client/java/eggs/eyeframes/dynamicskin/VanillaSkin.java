package eggs.eyeframes.dynamicskin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class VanillaSkin {

    public static CompletableFuture<NativeImage> loadVanillaSkin() {
        Minecraft mc = Minecraft.getInstance();
        SkinManager skins = mc.getSkinManager();

        //if player does not exist (in menu)
        if (mc.player == null) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return NativeImage.read(mc.getResourceManager().open(DefaultPlayerSkin.getDefaultTexture()));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load default skin", e);
                }
            });
        }

        //if player exists (in game)
        GameProfile profile = mc.player.getGameProfile();
        return skins.getOrLoad(profile).thenApply(playerSkin -> {
            ResourceLocation tex = playerSkin.texture();
            try {
                return NativeImage.read(mc.getResourceManager().open(tex));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load player skin", e);
            }
        });
    }
}

package eggs.eyeframes.dynamicskin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFrames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.server.packs.resources.ResourceManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VanillaSkin {

    public static CompletableFuture<NativeImage> loadVanillaSkin() {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        GameProfile profile = minecraft.getGameProfile();

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = fetchMojangTextures(profile);

                MinecraftProfileTexture skinTexture = textures.get(MinecraftProfileTexture.Type.SKIN);
                if (skinTexture != null) {
                    URI url = new URI(skinTexture.getUrl());
                    return NativeImage.read(url.toURL().openStream());
                } else {
                    // fallback
                    return NativeImage.read(resourceManager.open(DefaultPlayerSkin.get(profile).texture()));
                }
            } catch (Exception e) {
                EyeFrames.EYEFRAMES_LOGGER.error(e.getMessage());
                try {
                    // double fallback :)
                    return NativeImage.read(resourceManager.open(DefaultPlayerSkin.get(profile).texture()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> fetchMojangTextures(GameProfile profile) {
        try {
            String uuidNoHyphen = profile.getId().toString().replace("-", "");
            URI url = new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidNoHyphen + "?unsigned=false");
            InputStreamReader reader = new InputStreamReader(url.toURL().openStream());

            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();

            JsonObject property = json.getAsJsonArray("properties").get(0).getAsJsonObject();
            String encoded = property.get("value").getAsString();

            String decodedJson = new String(Base64.getDecoder().decode(encoded));
            JsonObject texturesJson = JsonParser.parseString(decodedJson).getAsJsonObject().getAsJsonObject("textures");

            Map<String, String> metadata = Map.of("signature", property.get("signature").getAsString());

            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = new HashMap<>();

            if (texturesJson.has("SKIN")) {
                JsonObject skin = texturesJson.getAsJsonObject("SKIN");
                textures.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(
                        skin.get("url").getAsString(), metadata
                ));
            }
            if (texturesJson.has("CAPE")) {
                JsonObject cape = texturesJson.getAsJsonObject("CAPE");
                textures.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(
                        cape.get("url").getAsString(), metadata
                ));
            }
            if (texturesJson.has("ELYTRA")) {
                JsonObject elytra = texturesJson.getAsJsonObject("ELYTRA");
                textures.put(MinecraftProfileTexture.Type.ELYTRA, new MinecraftProfileTexture(
                        elytra.get("url").getAsString(), metadata
                ));
            }

            return textures;

        } catch (Exception e) {
            EyeFrames.EYEFRAMES_LOGGER.error(e.getMessage());
            return Map.of();
        }
    }
}
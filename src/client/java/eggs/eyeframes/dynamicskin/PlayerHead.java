package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFramesClient;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.HashMap;

import static eggs.eyeframes.EyeFramesClient.HeadTextureHeight;
import static eggs.eyeframes.EyeFramesClient.HeadTextureWidth;
import static eggs.eyeframes.dynamicskin.DynamicSkinManager.crop;

public class PlayerHead {
    private static final HashMap<String, NativeImage> headStates = new HashMap<>();
    private static String currentState = "default";

    public static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            reset();
        }
    }

    public static void reset() {
        if (!initialized) return;

        headStates.clear();
        NativeImage base = DynamicSkinManager.vanillaSkin;
        NativeImage head = crop(base, 0, 0, HeadTextureWidth, HeadTextureHeight);
        EyeFramesClient.getDynamicHead().setPixels(head);
        EyeFramesClient.getDynamicHead().upload();
        headStates.put("default", head);
    }

    public static NativeImage getTexture(String state) {
        return headStates.getOrDefault(state, headStates.get("default"));
    }

    public static void setState(String state) {
        currentState = state;
        EyeFramesClient.getDynamicHead().setPixels(headStates.getOrDefault(state, headStates.get("default")));
        EyeFramesClient.getDynamicHead().upload();
    }
    public static String getState(){
        return currentState;
    }
    public static void putState(String state, NativeImage head) {
        headStates.put(state, head);
    }

    public static ModelPart createHeadModel(int u, int v, int w, int h) {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(u, v)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, w, h).bakeRoot().getChild("head");
    }
}
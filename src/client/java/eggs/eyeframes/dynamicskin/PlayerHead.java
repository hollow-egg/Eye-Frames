package eggs.eyeframes.dynamicskin;

import com.mojang.blaze3d.platform.NativeImage;
import eggs.eyeframes.EyeFramesClient;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import static eggs.eyeframes.EyeFramesClient.HeadTextureHeight;
import static eggs.eyeframes.EyeFramesClient.HeadTextureWidth;
import static eggs.eyeframes.dynamicskin.DynamicSkinManager.crop;

public class PlayerHead {
    private static final int frameCount = 8;
    private static final NativeImage[] headframes = new NativeImage[frameCount];
    private static int currentState = 0;

    public static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            NativeImage base = DynamicSkinManager.vanillaSkin;
            NativeImage head = crop(base, 0, 0, HeadTextureWidth, HeadTextureHeight);
            EyeFramesClient.getDynamicHead().setPixels(head);
            EyeFramesClient.getDynamicHead().upload();
            for (int i = 0; i < frameCount; ++i){
                headframes[i] = new NativeImage(HeadTextureWidth, HeadTextureHeight, false);
                headframes[i].copyFrom(head);
            }
        }
    }

    public static void reset() {
        if (!initialized) return;

        NativeImage base = DynamicSkinManager.vanillaSkin;
        NativeImage head = crop(base, 0, 0, HeadTextureWidth, HeadTextureHeight);
        EyeFramesClient.getDynamicHead().setPixels(head);
        EyeFramesClient.getDynamicHead().upload();
        headframes[currentState] = new NativeImage(HeadTextureWidth, HeadTextureHeight, false);
        headframes[currentState].copyFrom(head);
    }

    public static NativeImage getTexture(int state) {
        return headframes[state];
    }

    public static void saveState(){
        headframes[currentState] = new NativeImage(HeadTextureWidth, HeadTextureHeight, false);
        headframes[currentState].copyFrom(EyeFramesClient.getDynamicHead().getPixels());
    }

    public static void setState(int state) {
        saveState();
        while (state < 0)
            state+=frameCount;
        currentState = state%frameCount;
        EyeFramesClient.getDynamicHead().setPixels(headframes[currentState]);
        EyeFramesClient.getDynamicHead().upload();
    }
    public static int getState(){
        return currentState;
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
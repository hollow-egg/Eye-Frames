package eggs.eyeframes;

import com.mojang.blaze3d.platform.InputConstants;
import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.hud.Hud;
import eggs.eyeframes.tools.Input;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import static eggs.eyeframes.EyeFrames.MOD_ID;

public class EyeFramesClient implements ClientModInitializer {
	private static final String Category = "Eye Frames";

    //constants
    public static final int PlayerTextureWidth = 64;
    public static final int PlayerTextureHeight = 64;
    public static final int HeadTextureWidth = 64;
    public static final int HeadTextureHeight = 16;
    //assets
    public static final ResourceLocation DynamicSkinLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "dynamic_skin");
    public static final ResourceLocation DynamicHeadLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, "dynamic_head");
    private static DynamicTexture DYNAMIC_SKIN;
    private static DynamicTexture DYNAMIC_HEAD;

	@Override
	public void onInitializeClient() {
		Input.addKeybinding("control", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, Category);
		ClientTickEvents.END_CLIENT_TICK.register(Hud::tickEvent);
		HudRenderCallback.EVENT.register(Hud::renderEvent);

        //register textures
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            DYNAMIC_SKIN = new DynamicTexture(PlayerTextureWidth, PlayerTextureHeight, true);
            DYNAMIC_HEAD = new DynamicTexture(HeadTextureWidth, HeadTextureHeight, true);

            client.getTextureManager().register(DynamicSkinLocation, DYNAMIC_SKIN);
            client.getTextureManager().register(DynamicHeadLocation, DYNAMIC_HEAD);
        });

        //join event
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> DynamicSkinManager.initialize().thenRun(
        ()->{
            PlayerHead.initialize();
            DynamicSkinManager.updateHead(DYNAMIC_HEAD.getPixels());
        }));
	}

    public static DynamicTexture getDynamicSkin() { return DYNAMIC_SKIN; }
    public static DynamicTexture getDynamicHead() { return DYNAMIC_HEAD; }
}
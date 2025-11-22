package eggs.eyeframes;

import com.mojang.blaze3d.platform.InputConstants;
import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.hud.Hud;
import eggs.eyeframes.tools.Input;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.lwjgl.glfw.GLFW;

public class EyeFramesClient implements ClientModInitializer {
	private static final String Category = "Eye Frames";

	@Override
	public void onInitializeClient() {
		Input.addKeybinding("control", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, Category);
		ClientTickEvents.END_CLIENT_TICK.register(Hud::tickEvent);
		HudRenderCallback.EVENT.register(Hud::renderEvent);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            DynamicSkinManager.initialize().thenRun(() -> {
                if (!PlayerHead.initialized)
                    PlayerHead.initialize();
                DynamicSkinManager.updateHead(PlayerHead.getTexture());
            });
        });
	}
}
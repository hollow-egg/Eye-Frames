package eggs.eyeframes;

import eggs.eyeframes.screens.hud.Hud;
import eggs.eyeframes.tools.Input;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class EyeFramesClient implements ClientModInitializer {
	private static final String Category = "Eye Frames";

	@Override
	public void onInitializeClient() {
		Input.addKeybinding("control", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, Category);
		ClientTickEvents.END_CLIENT_TICK.register(Hud::tickEvent);
		HudRenderCallback.EVENT.register(Hud::renderEvent);
	}
}
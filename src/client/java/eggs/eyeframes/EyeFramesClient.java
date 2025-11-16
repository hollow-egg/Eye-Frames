package eggs.eyeframes;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EyeFramesClient implements ClientModInitializer {
	private static Identifier ExpressionIcon;
	private static final int IconScale = 16;
	public static final int SkinTextureSize = 64;
	private static KeyBinding controlEyesKeyBind;
	private static boolean showIcon = false;

	@Override
	public void onInitializeClient() {

		controlEyesKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.eyeframes.control",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT_ALT,
				KeyBinding.UI_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;

			boolean pressed = controlEyesKeyBind.isPressed();

			if (pressed && !showIcon) { //unlock mouse
				client.mouse.unlockCursor();
			}
			else if (!pressed && showIcon) { //lock mouse
				client.mouse.lockCursor();
			}

			showIcon = pressed;
		});

		HudRenderCallback.EVENT.register((context, tick) -> {
			if (showIcon) {
				GameProfile profile = MinecraftClient.getInstance().getGameProfile();
				if (profile != null) {
					var skin = MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile);
					ExpressionIcon = skin.texture();
				}
				int x = context.getScaledWindowWidth() / 2 - IconScale / 2;
				int y = context.getScaledWindowHeight() - IconScale - 30;
				//hud icon
				context.drawTexture(
						ExpressionIcon,
						x, y,
						IconScale, IconScale,
						IconScale, IconScale,
						IconScale * 8, IconScale * 8
				);
				//draw border
				context.drawBorder(
						x - 1, y - 1,
						IconScale + 2, IconScale + 2,
						0xFFFFFFFF
				);
			}
		});
	}
}
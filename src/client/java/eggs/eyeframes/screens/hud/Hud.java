package eggs.eyeframes.screens.hud;

import com.mojang.authlib.GameProfile;
import eggs.eyeframes.tools.Input;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

import static eggs.eyeframes.EyeFrames.SkinTextureSize;

public class Hud {
    private static boolean showOverlay = false;
    private static final int ICON_SCALE = 16;
    private static Identifier cachedTexture = null;

    private static TexturedHudButton iconPreview;
    private static ArrayList<SimpleHudButton> OverlayButtons;
    private static final int overlayButtonColor = 0x50FFFFFF;

    public static void tickEvent(MinecraftClient client) {
        if (client.player == null) return;

        boolean pressed = Input.isKeybindingPressed("control");

        //state has changed
        if (pressed != showOverlay) {
            showOverlay = pressed;

            if (showOverlay) {
                client.mouse.unlockCursor();
                cacheSkinTexture(client);
                //icon preview
                createIconPreviewButton(client);
                createOverlayButtons(client);
            } else {
                client.mouse.lockCursor();
            }
        }
    }

    private static void cacheSkinTexture(MinecraftClient client) {
        GameProfile profile = client.getGameProfile();
        if (profile != null) {
            cachedTexture = client.getSkinProvider().getSkinTextures(profile).texture();
        }
    }

    private static void createIconPreviewButton(MinecraftClient client) {
        int x = client.getWindow().getScaledWidth() / 2 - ICON_SCALE / 2;
        int y = client.getWindow().getScaledHeight() - ICON_SCALE - 30;
        iconPreview = new TexturedHudButton(
                cachedTexture,
                x, y,
                ICON_SCALE, ICON_SCALE, //width/height
                8, 8, //uv
                8, 8, //regionSize
                SkinTextureSize, SkinTextureSize
        );
    }

    private static void createOverlayButtons(MinecraftClient client) {
        OverlayButtons = new ArrayList<>();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int size = 30;
        int radius = 45;

        double centerX = width / 2.0;
        double centerY = height / 2.0;

        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);

            double bx = centerX + Math.cos(angle) * radius - size / 2.0;
            double by = centerY + Math.sin(angle) * radius - size / 2.0;

            OverlayButtons.add(new SimpleHudButton(
                    (int) bx, (int) by,
                    size, size,
                    overlayButtonColor
            ));
        }
    }


    public static void renderEvent(DrawContext context, RenderTickCounter tick) {
        if (!showOverlay || iconPreview == null) return;

        var client = MinecraftClient.getInstance();

        double mouseX = client.mouse.getX() * context.getScaledWindowWidth() / client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * context.getScaledWindowHeight() / client.getWindow().getHeight();

        for (SimpleHudButton button : OverlayButtons)
            button.render(context, mouseX, mouseY, tick);
        iconPreview.render(context, mouseX, mouseY, tick);
    }
}

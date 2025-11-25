package eggs.eyeframes.screens.hud;

import eggs.eyeframes.EyeFramesClient;
import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.widgets.PlayerHeadPreviewHudButton;
import eggs.eyeframes.screens.widgets.SimpleHudButton;
import eggs.eyeframes.tools.Input;
import java.util.ArrayList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
public class Hud {
    private static boolean showOverlay = false;
    private static final int ICON_SCALE = 16;

    private static PlayerHeadPreviewHudButton iconPreview = null;
    private static ArrayList<SimpleHudButton> OverlayButtons;
    private static final int overlayButtonColor = 0x50FFFFFF;

    public static void tickEvent(Minecraft client) {
        if (client.player == null) return;

        boolean pressed = Input.isKeybindingPressed("control");

        if (iconPreview == null){
            createIconPreviewButton(client);
        }

        //state has changed
        if (pressed != showOverlay) {
            showOverlay = pressed;

            if (showOverlay) {
                client.mouseHandler.releaseMouse();
                //icon preview
                createIconPreviewButton(client);
                createOverlayButtons(client);
            } else {
                client.mouseHandler.grabMouse();
            }
        }
    }

    private static void createIconPreviewButton(Minecraft client) {
        int x = client.getWindow().getGuiScaledWidth() / 2 - ICON_SCALE / 2;
        int y = client.getWindow().getGuiScaledHeight() - ICON_SCALE - 30;
        iconPreview = new PlayerHeadPreviewHudButton(
                x, y,
                ICON_SCALE, ICON_SCALE
        );
    }

    private static void createOverlayButtons(Minecraft client) {
        OverlayButtons = new ArrayList<>();
        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();

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


    public static void renderEvent(GuiGraphics context, DeltaTracker tick) {
        if (showOverlay) {

            var client = Minecraft.getInstance();

            double mouseX = client.mouseHandler.xpos() * context.guiWidth() / client.getWindow().getScreenWidth();
            double mouseY = client.mouseHandler.ypos() * context.guiHeight() / client.getWindow().getScreenHeight();

            for (int i = 0; i < OverlayButtons.size(); ++i) {
                OverlayButtons.get(i).render(context, mouseX, mouseY, tick);
                if (i != PlayerHead.getState() && OverlayButtons.get(i).isHovered(mouseX, mouseY)) {
                    PlayerHead.setState(i);
                    DynamicSkinManager.updateHead(EyeFramesClient.getDynamicHead().getPixels());
                }
            }
            iconPreview.render(context, mouseX, mouseY, tick);
        }
        else if (iconPreview != null)
            iconPreview.render(context, -1, -1, tick);
    }
}

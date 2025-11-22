package eggs.eyeframes.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import static eggs.eyeframes.EyeFramesClient.*;

@Environment(EnvType.CLIENT)
public class PlayerHeadPreviewHudButton implements HudButton {

    private final int x, y, width, height;

    public PlayerHeadPreviewHudButton(int x, int y,
                                      int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public void render(GuiGraphics context, double mouseX, double mouseY, DeltaTracker tickDelta) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Bottom layer
        int u = 8;
        int v = 8;
        context.blit(
                DynamicHeadLocation,
                x, y,
                width, height,
                (float)u, (float)v,
                8, 8,
                HeadTextureWidth, HeadTextureHeight
        );

        // Top layer
        u+=32;
        int offset = 1;
        context.blit(
                DynamicHeadLocation,
                x - offset, y - offset,
                width + offset * 2, width + offset * 2,
                (float)u, (float)v,
                8, 8,
                HeadTextureWidth, HeadTextureHeight
        );

        RenderSystem.disableBlend();

        if (isHovered(mouseX, mouseY)) {
            context.renderOutline(x - 1, y - 1,
                    width + 2, height + 2,
                    0xFFFFFFFF);
        }
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }
}

package eggs.eyeframes.screens.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
public class SimpleHudButton implements HudButton {

    private final int x, y, width, height;
    private final int color;

    public SimpleHudButton(int x, int y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void render(GuiGraphics context, double mouseX, double mouseY, DeltaTracker tickDelta) {
        context.fill(x, y, x + width, y + height, color);

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

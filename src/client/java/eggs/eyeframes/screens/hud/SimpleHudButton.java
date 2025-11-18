package eggs.eyeframes.screens.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

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
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickDelta) {
        context.fill(x, y, x + width, y + height, color);

        if (isHovered(mouseX, mouseY)) {
            context.drawBorder(x - 1, y - 1,
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

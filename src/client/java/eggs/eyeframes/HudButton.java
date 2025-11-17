package eggs.eyeframes;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface HudButton {
    void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickDelta);
    boolean isHovered(double mouseX, double mouseY);
}

package eggs.eyeframes.screens.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface HudButton {
    void render(GuiGraphics context, double mouseX, double mouseY, DeltaTracker tickDelta);
    boolean isHovered(double mouseX, double mouseY);
}

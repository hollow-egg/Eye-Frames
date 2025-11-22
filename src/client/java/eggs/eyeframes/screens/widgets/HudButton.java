package eggs.eyeframes.screens.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
public interface HudButton {
    void render(GuiGraphics context, double mouseX, double mouseY, DeltaTracker tickDelta);
    boolean isHovered(double mouseX, double mouseY);
}

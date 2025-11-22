package eggs.eyeframes.screens.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class IconButton extends AbstractWidget {

    private ResourceLocation texture;
    private final Runnable onClick;
    private final String label;

    public IconButton(int x, int y, int width, int height,
                      ResourceLocation texture,
                      Runnable onClick, String label) {

        super(x, y, width, height, Component.literal(""));
        this.texture = texture;
        this.onClick = onClick;
        this.label = label;
    }
    //no label
    public IconButton(int x, int y, int width, int height,
                      ResourceLocation texture,
                      Runnable onClick) {

        super(x, y, width, height, Component.literal(""));
        this.texture = texture;
        this.onClick = onClick;
        this.label = null;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.blit(texture, getX(), getY(),
                0, 0,
                getWidth(), getHeight(),
                getWidth(), getHeight());

        if (label != null) {
            Font renderer = Minecraft.getInstance().font;
            context.drawCenteredString(
                    renderer,
                    label,
                    getX() + this.width / 2,
                    getY() + (this.height - renderer.lineHeight) / 2,
                    0xFFFFFF
            );
        }

        if (active && isHovered()) {
            context.renderOutline(getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
        }
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onClick.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
}

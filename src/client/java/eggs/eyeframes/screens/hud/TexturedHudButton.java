package eggs.eyeframes.screens.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TexturedHudButton implements HudButton {

    private final int x, y, width, height, u, v, regionSizeX, regionSizeY, texWidth, texHeight;
    private final Identifier texture;

    public TexturedHudButton(Identifier texture, int x, int y, int w, int h, int u, int v, int regionSizeX, int regionSizeY, int texW, int texH) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.regionSizeX = regionSizeX;
        this.regionSizeY = regionSizeY;
        this.texWidth = texW;
        this.texHeight = texH;
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickDelta) {
        context.drawTexture(
                texture,
                x, y,
                width, height,
                (float)u, (float)v,
                regionSizeX, regionSizeY,
                texWidth, texHeight
        );

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

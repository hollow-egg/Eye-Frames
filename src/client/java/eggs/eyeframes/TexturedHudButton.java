package eggs.eyeframes;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class TexturedHudButton implements HudButton {

    private final int x, y, width, height, u, v, texW, texH;
    private final Identifier texture;

    public TexturedHudButton(Identifier texture, int x, int y, int w, int h, int u, int v, int texW, int texH) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.texW = texW;
        this.texH = texH;
    }

    @Override
    public void render(DrawContext context, double mouseX, double mouseY, RenderTickCounter tickDelta) {
        context.drawTexture(
                texture,
                x, y,
                u, v,
                width, height,
                texW, texH
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

package eggs.eyeframes.tools.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButton extends ClickableWidget {

    private final Identifier texture;
    private final Runnable onClick;
    private final String label;

    public IconButton(int x, int y, int width, int height,
                      Identifier texture,
                      Runnable onClick, String label) {

        super(x, y, width, height, Text.literal(""));
        this.texture = texture;
        this.onClick = onClick;
        this.label = label;
    }
    //no label
    public IconButton(int x, int y, int width, int height,
                      Identifier texture,
                      Runnable onClick) {

        super(x, y, width, height, Text.literal(""));
        this.texture = texture;
        this.onClick = onClick;
        this.label = null;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(texture, getX(), getY(),
                0, 0,
                getWidth(), getHeight(),
                getWidth(), getHeight());

        if (label != null) {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(
                    renderer,
                    label,
                    getX() + this.width / 2,
                    getY() + (this.height - renderer.fontHeight) / 2,
                    0xFFFFFF
            );
        }

        if (isHovered()) {
            context.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onClick.run();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}

package eggs.eyeframes.screens;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static eggs.eyeframes.EyeFramesClient.SkinTextureSize;

@Environment(EnvType.CLIENT)
public class Face extends Screen {

    private final Screen parent;
    private Identifier skinTexture;

    public Face(Screen parent){
        super(Text.literal("Face"));
        this.parent = parent;
    }

    private void addPixelButton(int u, int v, int x, int y, int size){
        this.addDrawableChild(new ClickableWidget(x, y, size, size, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                if (skinTexture != null) {
                    context.drawTexture(
                            skinTexture,
                            x, y,
                            u * SkinTextureSize, v * SkinTextureSize,
                            size, size,
                            SkinTextureSize * SkinTextureSize, SkinTextureSize * SkinTextureSize
                    );
                }
                if (isHovered()){
                    context.drawBorder(x, y, size, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
        });
    }

    @Override
    protected void init(){
        super.init();

        GameProfile profile = MinecraftClient.getInstance().getGameProfile();
        if (profile != null) {
            var skin = MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile);
            skinTexture = skin.texture();
        }

        int pixelSize = 16;
        int offsetX = this.width / 2 - pixelSize * 8 / 2;
        int offsetY = this.height / 2 - pixelSize * 8 / 2;
        for (int i = 0; i < SkinTextureSize; ++i){
            addPixelButton(
                    i % 8 + 8, i / 8 + 8,
                    i % 8 * pixelSize + offsetX, i / 8 * pixelSize + offsetY,
                    pixelSize);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        super.render(context, mouseX, mouseY, delta);
        String text = Text.translatable("screens.eyeframes.face").getString();
        int x = this.width / 2 - textRenderer.getWidth(text) / 2;
        int y = 8;
        context.drawText(textRenderer, text, x, y, 0xFFFFFFFF, true);
    }

    @Override
    public void close(){
        assert client != null;
        client.setScreen(parent);
    }
}

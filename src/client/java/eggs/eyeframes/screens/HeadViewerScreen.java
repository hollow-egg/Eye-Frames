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

@Environment(EnvType.CLIENT)
public class HeadViewerScreen extends Screen {
    private final Screen parent;
    public static Identifier SkinTexture;
    public enum State{
        Left(0),
        Face(1),
        Right(2),
        Back(3),
        Top(4),
        Bottom(5);
        private final int index;

        State(int index) {
            this.index = index;
        }
    }
    private State state = State.Face;
    private static final Identifier rightArrowTexture = Identifier.of("minecraft", "textures/gui/sprites/widget/page_forward.png");
    private static final Identifier leftArrowTexture = Identifier.of("minecraft", "textures/gui/sprites/widget/page_backward.png");
    private static final Identifier buttonTexture = Identifier.of("minecraft", "textures/gui/sprites/widget/button.png");
    private static final int ImageScale = 100;

    public HeadViewerScreen(Screen parent) {
        super(Text.literal("Head Viewer"));
        this.parent = parent;
    }

    private static void cacheSkinTexture(MinecraftClient client) {
        GameProfile profile = client.getGameProfile();
        if (profile != null) {
            SkinTexture = client.getSkinProvider().getSkinTextures(profile).texture();
        }
    }

    @Override
    protected void init(){
        assert client != null;
        cacheSkinTexture(client);

        int size = 20;
        int x = this.width / 2 + ImageScale / 2 + size;
        int y = this.height / 2;

        //right arrow
        this.addDrawableChild(new ClickableWidget(x, y, size, size, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.drawTexture(
                        rightArrowTexture,
                        this.getX(), this.getY(),
                        0, 0,
                        size, size,
                        size, size
                );
                if (isHovered()){
                    context.drawBorder(this.getX(), this.getY(), size, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

            @Override
            public void onClick(double mouseX, double mouseY){
                int temp = state.index;
                temp = (temp+1)%4;
                state = State.values()[temp];
            }
        });
        //left arrow
        x = this.width / 2 - ImageScale / 2 - size * 2;
        this.addDrawableChild(new ClickableWidget(x, y, size, size, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.drawTexture(
                        leftArrowTexture,
                        this.getX(), this.getY(),
                        0, 0,
                        size, size,
                        size, size
                );
                if (isHovered()){
                    context.drawBorder(this.getX(), this.getY(), size, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

            @Override
            public void onClick(double mouseX, double mouseY){
                int temp = state.index;
                --temp;
                if (temp < 0)
                    temp = 3;
                state = State.values()[temp];
            }
        });
        //top button
        x = this.width / 2 - size / 2;
        y = size * 2 - size / 2;
        this.addDrawableChild(new ClickableWidget(x, y, size, size, Text.literal("^")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.drawTexture(
                        buttonTexture,
                        this.getX(), this.getY(),
                        0, 0,
                        size, size,
                        size, size
                );
                if (isHovered()){
                    context.drawBorder(this.getX(), this.getY(), size, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

            @Override
            public void onClick(double mouseX, double mouseY){
                state = State.Top;
            }
        });
        //bottom button
        y = this.height / 2 + ImageScale / 2 + size;
        this.addDrawableChild(new ClickableWidget(x, y, size, size, Text.literal("v")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.drawTexture(
                        buttonTexture,
                        this.getX(), this.getY(),
                        0, 0,
                        size, size,
                        size, size
                );
                if (isHovered()){
                    context.drawBorder(this.getX(), this.getY(), size, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

            @Override
            public void onClick(double mouseX, double mouseY){
                state = State.Bottom;
            }
        });
        //done button
        y = this.height - size;
        x -= size / 2;
        this.addDrawableChild(new ClickableWidget(x, y, size * 2, size, Text.literal("Done")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.drawTexture(
                        buttonTexture,
                        this.getX(), this.getY(),
                        0, 0,
                        this.getWidth(), this.getHeight(),
                        this.getWidth(), this.getHeight()
                );
                Text doneMessage = Text.translatable("gui.done");
                context.drawText(textRenderer, doneMessage, this.getX() + size - textRenderer.getWidth(doneMessage) / 2, this.getY() + size / 4, 0xFFFFFFFF, true);
                if (isHovered()){
                    context.drawBorder(this.getX(), this.getY(), size * 2, size, 0xFFFFFFFF);
                }
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

            @Override
            public void onClick(double mouseX, double mouseY){
                close();
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Text title = Text.translatable("screens.eyeframes.head_viewer");
        context.drawText(textRenderer, title, this.width / 2 - textRenderer.getWidth(title) / 2, textRenderer.fontHeight, 0xFFFFFFFF, true);

        //skin
        int u = 0;
        int v = 0;
        switch (state){
            case Left -> v = 1;
            case Face -> {
                u = 1;
                v = 1;
            }
            case Right -> {
                u = 2;
                v = 1;
            }
            case Back -> {
                u = 3;
                v = 1;
            }
            case Top -> u = 1;
            case Bottom -> u = 2;
        }
        int size = ImageScale;
        int x = this.width / 2 - size / 2;
        int y = this.height / 2 - size / 2;
        if (SkinTexture != null) {
            context.drawTexture( //bottom layer
                    SkinTexture,
                    x, y,
                    size * u, size * v,
                    size, size,
                    size * 8, size * 8
            );
            size+=2;
            context.drawTexture( //top layer
                    SkinTexture,
                    x, y,
                    size * (u + 4), size * v,
                    size, size,
                    size * 8, size * 8
            );
        }
    }

    @Override
    public void close(){
        assert client != null;
        client.setScreen(parent);
    }
}


package eggs.eyeframes.screens;

import com.mojang.authlib.GameProfile;
import eggs.eyeframes.tools.widgets.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.swing.*;

import static eggs.eyeframes.EyeFrames.MOD_ID;

@Environment(EnvType.CLIENT)
public class HeadEditorScreen extends Screen {

    private final Screen parent;

    public static Identifier SkinTexture;

    public enum State {
        Left(0), Face(1), Right(2), Back(3), Top(4), Bottom(5);
        public final int index;

        State(int index) { this.index = index; }
    }

    private State state = State.Face;

    private static final Identifier RIGHT_ARROW = Identifier.of("minecraft", "textures/gui/sprites/widget/page_forward.png");
    private static final Identifier LEFT_ARROW  = Identifier.of("minecraft", "textures/gui/sprites/widget/page_backward.png");
    private static final Identifier BUTTON_TEX  = Identifier.of("minecraft", "textures/gui/sprites/widget/button.png");

    private static final int ICON_SIZE = 20;
    private static final int IMAGE_SCALE = 100;

    public HeadEditorScreen(Screen parent) {
        super(Text.literal("Head Editor"));
        this.parent = parent;
    }

    private static void cacheSkinTexture(MinecraftClient client) {
        GameProfile profile = client.getGameProfile();
        if (profile != null) {
            SkinTexture = client.getSkinProvider().getSkinTextures(profile).texture();
        }
    }

    @Override
    protected void init() {
        assert client != null;
        //cacheSkinTexture(client);
        SkinTexture = Identifier.of(MOD_ID, "textures/temp/yolk.png"); //TODO remove

        int cx = width / 2;
        int cy = height / 2;

        // Right arrow
        addDrawableChild(new IconButton(
                cx + IMAGE_SCALE / 2 + ICON_SIZE,
                cy - ICON_SIZE / 2,
                ICON_SIZE, ICON_SIZE,
                RIGHT_ARROW,
                () -> state = State.values()[(state.index + 1) % 4]
        ));

        // Left arrow
        addDrawableChild(new IconButton(
                cx - IMAGE_SCALE / 2 - ICON_SIZE * 2,
                cy - ICON_SIZE / 2,
                ICON_SIZE, ICON_SIZE,
                LEFT_ARROW,
                () -> state = State.values()[(state.index + 3) % 4]
        ));

        // Top
        addDrawableChild(new IconButton(
                cx - ICON_SIZE / 2,
                ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> state = State.Top,
                "^"
        ));

        // Bottom
        addDrawableChild(new IconButton(
                cx - ICON_SIZE / 2,
                cy + IMAGE_SCALE / 2 + ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> state = State.Bottom,
                "v"
        ));

        // Done button
        addDrawableChild(new IconButton(
                cx - ICON_SIZE,
                height - ICON_SIZE,
                ICON_SIZE * 2, ICON_SIZE,
                BUTTON_TEX,
                this::close,
                Text.translatable("gui.done").getString()
        ));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Text title = Text.translatable("screens.eyeframes.head_viewer");
        context.drawText(textRenderer, title,
                width / 2 - textRenderer.getWidth(title) / 2,
                10,
                0xFFFFFFFF,
                true);

        renderHead(context);
    }

    private void renderHead(DrawContext context) {
        if (SkinTexture == null) return;

        int u = switch (state) {
            case Left -> 0;
            case Face, Top -> 1;
            case Right, Bottom -> 2;
            case Back -> 3;
        };
        int v = switch (state) {
            case Top, Bottom -> 0;
            default -> 1;
        };

        int size = IMAGE_SCALE;
        int x = width / 2 - size / 2;
        int y = height / 2 - size / 2;

        // Bottom layer
        context.drawTexture(
                SkinTexture, x, y,
                u * size, v * size,
                size, size,
                size * 8, size * 8
        );

        // Top layer
        int offset = 2;
        int overlay = size + offset * 2;
        context.drawTexture(
                SkinTexture, x - offset / 2, y - offset / 2,
                overlay * (u + 4), overlay * v,
                overlay, overlay,
                overlay * 8, overlay * 8
        );
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }
}
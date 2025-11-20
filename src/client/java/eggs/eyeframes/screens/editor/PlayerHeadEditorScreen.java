package eggs.eyeframes.screens.editor;

import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.options.widgets.IconButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static eggs.eyeframes.EyeFrames.*;

@Environment(EnvType.CLIENT)
public class PlayerHeadEditorScreen extends Screen {

    private final Screen parent;

    public enum State {
        Left(0), Face(1), Right(2), Back(3), Top(4), Bottom(5);
        public final int index;

        State(int index) { this.index = index; }
    }

    private State state = State.Face;

    private static final ResourceLocation RIGHT_ARROW = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/page_forward.png");
    private static final ResourceLocation LEFT_ARROW  = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/page_backward.png");
    private static final ResourceLocation BUTTON_TEX  = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button.png");

    private static final int ICON_SIZE = 20;
    private static final int IMAGE_SCALE = 100;

    public PlayerHeadEditorScreen(Screen parent) {
        super(Component.literal("Head Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        // Right arrow
        addRenderableWidget(new IconButton(
                cx + IMAGE_SCALE / 2 + ICON_SIZE, cy - ICON_SIZE / 2,
                ICON_SIZE, ICON_SIZE,
                RIGHT_ARROW,
                () -> state = State.values()[(state.index + 1) % 4]
        ));

        // Left arrow
        addRenderableWidget(new IconButton(
                cx - IMAGE_SCALE / 2 - ICON_SIZE * 2, cy - ICON_SIZE / 2,
                ICON_SIZE, ICON_SIZE,
                LEFT_ARROW,
                () -> state = State.values()[(state.index + 3) % 4]
        ));

        // Top
        addRenderableWidget(new IconButton(
                cx - ICON_SIZE / 2, ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> state = State.Top,
                "^"
        ));

        // Bottom
        addRenderableWidget(new IconButton(
                cx - ICON_SIZE / 2, cy + IMAGE_SCALE / 2 + ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> state = State.Bottom,
                "v"
        ));

        // Done button
        addRenderableWidget(new IconButton(
                cx - ICON_SIZE, height - ICON_SIZE,
                ICON_SIZE * 2, ICON_SIZE,
                BUTTON_TEX,
                this::onClose,
                Component.translatable("gui.done").getString()
        ));

        // Reset button
        addRenderableWidget(new IconButton(
                width - ICON_SIZE * 2, ICON_SIZE,
                ICON_SIZE * 2, ICON_SIZE,
                BUTTON_TEX,
                PlayerHead::reset,
                "Reset"
        ));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Component title = Component.translatable("screens.eyeframes.head_viewer");
        context.drawString(font, title,
                width / 2 - font.width(title) / 2,
                10,
                0xFFFFFFFF,
                true);

        renderHead(context, mouseX, mouseY);
    }

    private void renderHead(GuiGraphics context, int mouseX, int mouseY) {
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

        //real uvs
        u*=8;
        v*=8;

        int size = IMAGE_SCALE;
        int x = width / 2 - size / 2;
        int y = height / 2 - size / 2;

        ResourceLocation tex = DynamicSkinManager.getDynamicSkinTextureLocation();
        // Bottom layer
        context.blit(
                tex,
                x, y,
                size, size,
                (float)u, (float)v,
                8, 8,
                PlayerHeadTextureWidth, PlayerHeadTextureHeight
        );

        // Top layer
        u+=32;
        int offset = 2;
        int overlay = size + offset * 2;
        context.blit(
                tex,
                x - offset, y - offset,
                overlay, overlay,
                (float)u, (float)v,
                8, 8,
                PlayerHeadTextureWidth, PlayerHeadTextureHeight
        );

        context.renderOutline(mouseX, mouseY,
                size / 8, size / 8,
                0xFFFFFFFF);

        int px = u + mouseX / 32;
        int py = v + mouseY / 32;
        if (px <= 64 && py <= 16) {
            PlayerHead.getTexture().setPixelRGBA(px, py, 0xFFFF00FF);
            DynamicSkinManager.updateHeadParts(PlayerHead.getTexture());
        }
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }
}
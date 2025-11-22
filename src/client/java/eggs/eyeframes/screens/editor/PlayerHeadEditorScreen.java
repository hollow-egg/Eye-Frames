package eggs.eyeframes.screens.editor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.options.widgets.IconButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import static eggs.eyeframes.dynamicskin.PlayerHead.HeadTextureHeight;
import static eggs.eyeframes.dynamicskin.PlayerHead.HeadTextureWidth;

@Environment(EnvType.CLIENT)
public class PlayerHeadEditorScreen extends Screen {

    private final Screen parent;

    private float rotX = 0;
    private float rotY = 0;
    private boolean rotating = true;
    private boolean penTool = false;

    private static final int ICON_SIZE = 20;
    private static final int IMAGE_SCALE = 100;
    private static boolean mouseDown = false;
    private static final ResourceLocation BUTTON_TEX = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button.png");
    private static final ResourceLocation BUTTON_DISABLED_TEX = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button_disabled.png");

    public PlayerHeadEditorScreen(Screen parent) {
        super(Component.literal("Head Editor"));
        this.parent = parent;
    }

    private int debugMouseX = 0;
    private int debugMouseY = 0;

    @Override
    protected void init() {
        // Done button
        addRenderableWidget(new IconButton(
                width / 2 - ICON_SIZE, height - ICON_SIZE,
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
                () ->{
                    PlayerHead.reset();
                    DynamicSkinManager.updateHead(PlayerHead.getTexture());
                },
                "Reset"
        ));

        if (!DynamicSkinManager.initialized) {
            DynamicSkinManager.initialize().thenRun(PlayerHead::initialize);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Component title = Component.translatable("screens.eyeframes.head_viewer");
        context.drawString(font, title, width / 2 - font.width(title) / 2, 10, 0xFFFFFFFF, true);

        render3DPlayerHead(context, mouseX, mouseY);

        String coords = String.format("Mouse: (%d, %d)", debugMouseX, debugMouseY);
        context.drawString(font, coords, 10, height - 20, 0xFFFFFF00, true);
    }

    private void render3DPlayerHead(GuiGraphics context, int mouseX, int mouseY) {
        if (!DynamicSkinManager.initialized) return;

        int centerX = width / 2 - 50;
        int centerY = height / 2 + 20;

        PoseStack poseStack = context.pose();
        poseStack.pushPose();

        poseStack.translate(centerX, centerY, IMAGE_SCALE);
        float scale = IMAGE_SCALE;
        poseStack.scale(scale, scale, scale);

        Quaternionf rotation = new Quaternionf().rotateXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), 0);
        poseStack.mulPose(rotation);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(DynamicSkinManager.getDynamicSkinTextureLocation()));

        int light = 0xF000F0;
        int overlay = OverlayTexture.NO_OVERLAY;

        PlayerHead.getHeadModel().render(
                poseStack,
                vertexConsumer,
                light,
                overlay,
                0xFFFFFFFF
        );

        bufferSource.endBatch();
        poseStack.popPose();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double deltaX, double deltaY) {
        if (rotating) {
            rotY -= (float) (deltaX * 0.5f);
            rotX += (float) (deltaY * 0.5f);
            rotX = Math.max(-90f, Math.min(90f, rotX));
            return true;
        }
        else if (penTool) {
            editPixel(x, y);
        }
        return super.mouseDragged(x, y, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double x, double y, int i) {
        if (i == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mouseDown = true;
            if (penTool) {
                editPixel(x, y);
            }
        }

        return super.mouseClicked(x, y, i);
    }

    @Override
    public boolean mouseReleased(double x, double y, int i) {
        if (i == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mouseDown = false;
            rotating = false;
        }
        return super.mouseReleased(x, y, i);
    }

    private void editPixel(double mouseX, double mouseY) {
        int px=(int)mouseX;
        int py=(int)mouseY;

        debugMouseX = px;
        debugMouseY = py;

        if (px >= 0 && px < HeadTextureWidth &&
            py >= 0 && py < HeadTextureHeight) {
            PlayerHead.getTexture().setPixelRGBA(px, py, 0xFFFF00FF);
            DynamicSkinManager.updateHead(PlayerHead.getTexture());
        }
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }
}
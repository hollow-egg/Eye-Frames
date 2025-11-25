package eggs.eyeframes.screens.editor;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import eggs.eyeframes.EyeFramesClient;
import eggs.eyeframes.dynamicskin.DynamicSkinManager;
import eggs.eyeframes.dynamicskin.PlayerHead;
import eggs.eyeframes.screens.widgets.Slider;
import eggs.eyeframes.screens.widgets.IconButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import static eggs.eyeframes.EyeFramesClient.*;

@Environment(EnvType.CLIENT)
public class PlayerHeadEditorScreen extends Screen {

    private final Screen parent;

    private float rotX = 0;
    private float rotY = 180;

    private static final int ICON_SIZE = 20;
    private static final int IMAGE_SCALE = 5;
    private static final int MODEL_SCALE = 100;
    private static boolean mouseDown = false;
    private static final ResourceLocation BUTTON_TEX = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button.png");
    private static final ResourceLocation BUTTON_TEX_DISABLED = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button_disabled.png");

    public PlayerHeadEditorScreen(Screen parent) {
        super(Component.literal("Head Editor"));
        this.parent = parent;
    }

    private int pixelMouseX = 0;
    private int pixelMouseY = 0;
    private boolean hoveringImage = false;
    private boolean hoveringModel = false;

    private int selectedColor = 0xFFFFFFFF;
    private int previewColor = 0xFFFFFFFF;
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private Slider alphaSlider;

    private IconButton pickerButton;

    private static final ModelPart BaseModel = PlayerHead.createHeadModel(0, 0, HeadTextureWidth, HeadTextureHeight);
    private static final ModelPart HatModel = PlayerHead.createHeadModel(HeadTextureWidth / 2, 0, HeadTextureWidth, HeadTextureHeight);

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
                ()-> DynamicSkinManager.reset().thenRun(PlayerHead::reset),
                "Reset"
        ));
        // Save button
        addRenderableWidget(new IconButton(
                ICON_SIZE, ICON_SIZE,
                ICON_SIZE * 2, ICON_SIZE,
                BUTTON_TEX,
                () -> DynamicSkinManager.updateHead(getDynamicHead().getPixels()),
                "Save"
        ));
        // Picker button
        pickerButton = new IconButton(
                ICON_SIZE, ICON_SIZE * 2,
                ICON_SIZE * 2, ICON_SIZE,
                BUTTON_TEX,
                ()->{
                    pickerButton.active = false;
                    pickerButton.setTexture(BUTTON_TEX_DISABLED);
                },
                "Pick");
        addRenderableWidget(pickerButton);
        // Up Frame button
        addRenderableWidget(new IconButton(
                width / 2 + 50, ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> {
                    PlayerHead.setState(PlayerHead.getState()+1);
                    DynamicSkinManager.updateHead(getDynamicHead().getPixels());
                },
                ">"
        ));
        // Down Frame button
        addRenderableWidget(new IconButton(
                width / 2 - 50 - ICON_SIZE, ICON_SIZE,
                ICON_SIZE, ICON_SIZE,
                BUTTON_TEX,
                () -> {
                    PlayerHead.setState(PlayerHead.getState()-1);
                    DynamicSkinManager.updateHead(getDynamicHead().getPixels());
                },
                "<"
        ));
        redSlider = new Slider(50, height - 40, 100, 10, "R", 255, value -> {
            updateSelectedColor();
        });
        greenSlider = new Slider(50, height - 30, 100, 10, "G", 255, value -> {
            updateSelectedColor();
        });
        blueSlider = new Slider(50, height - 20, 100, 10, "B", 255, value -> {
            updateSelectedColor();
        });
        alphaSlider = new Slider(50, height - 10, 100, 10, "A", 255, value -> {
            updateSelectedColor();
        });
        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);
        addRenderableWidget(alphaSlider);

        if (!DynamicSkinManager.initialized) {
            DynamicSkinManager.initialize().thenRun(PlayerHead::initialize);
        }
    }

    private void updateSelectedColor() {
        selectedColor = (alphaSlider.getValue() << 24) | (blueSlider.getValue() << 16) | (greenSlider.getValue() << 8) | redSlider.getValue();
        previewColor = (alphaSlider.getValue() << 24) | (redSlider.getValue() << 16) | (greenSlider.getValue() << 8) | blueSlider.getValue();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Component title = Component.translatable("screens.eyeframes.head_viewer");
        context.drawString(font, title, width / 2 - font.width(title) / 2, 10, 0xFFFFFFFF, true);

        int w = HeadTextureWidth / 2 * IMAGE_SCALE;
        int h = HeadTextureHeight * IMAGE_SCALE;
        int x = 3 * width / 4 - w / 2;
        int y = height / 2 - h / 2 - h / 2;

        pixelMouseX = (mouseX - x) / IMAGE_SCALE;
        pixelMouseY = (mouseY - y) / IMAGE_SCALE;

        if (mouseX > x && mouseY > y + h) {
            pixelMouseX += 32;
            pixelMouseY -= 16;
        }

        Font renderer = Minecraft.getInstance().font;
        String message = String.format("{%d,%d}", pixelMouseX, pixelMouseY);
        context.drawCenteredString(
                renderer,
                message,
                renderer.width(message) / 2, height - renderer.lineHeight,
                0xFFFFFF00
        );

        //frame counter
        message = String.format("Frame: %d", PlayerHead.getState());
        context.drawCenteredString(
                renderer,
                message,
                width / 2, renderer.lineHeight * 2,
                0xFFFFFF00
        );

        render3DPlayerHead(context);
        render2DPlayerHead(context, x, y, w, h);

        //selection
        hoveringModel = (mouseX < width / 2) && !(mouseDown && hoveringImage) || (mouseDown && hoveringModel);
        hoveringImage = (mouseX < x + w) && ((mouseDown && hoveringImage) || (mouseX >= x && mouseY >= y && mouseY < y + h * 2));

        //color preview
        context.fill(
                0, width / 2 - 10,
                20, width / 2 + 10,
                previewColor
        );
    }

    private void render3DPlayerHead(GuiGraphics context) {
        if (!DynamicSkinManager.initialized) return;

        int centerX = width / 4;
        int centerY = height / 2 + 20;

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(DynamicHeadLocation));
        Quaternionf rotation = new Quaternionf().rotateXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), 0);
        int light = 0xF000F0;
        int overlay = OverlayTexture.NO_OVERLAY;

        PoseStack poseStack = context.pose();
        poseStack.pushPose();

        poseStack.translate(centerX, centerY, MODEL_SCALE);
        poseStack.scale(-MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        poseStack.mulPose(rotation);

        BaseModel.render(
                poseStack,
                vertexConsumer,
                light,
                overlay,
                0xFFFFFFFF
        );

        poseStack.popPose();
        poseStack.pushPose();

        poseStack.translate(centerX, centerY + MODEL_SCALE / 32F, MODEL_SCALE);
        poseStack.scale(-MODEL_SCALE - MODEL_SCALE / 8F, MODEL_SCALE + MODEL_SCALE / 8F, MODEL_SCALE + MODEL_SCALE / 8F);
        poseStack.mulPose(rotation);

        VertexConsumer hatConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(DynamicHeadLocation));

        HatModel.render(
                poseStack,
                hatConsumer,
                light,
                overlay,
                0xFFFFFFFF
        );

        bufferSource.endBatch();
        poseStack.popPose();
    }

    private void render2DPlayerHead(GuiGraphics context, int x, int y, int w, int h){

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.blit(
                DynamicHeadLocation,
                x, y,
                w, h,
                0, 0,
                HeadTextureWidth / 2, HeadTextureHeight,
                HeadTextureWidth, HeadTextureHeight
        );

        context.blit(
                DynamicHeadLocation,
                x, y + h,
                w, h,
                HeadTextureWidth / 2F, 0,
                HeadTextureWidth / 2, HeadTextureHeight,
                HeadTextureWidth, HeadTextureHeight
        );

        RenderSystem.disableBlend();

        if (hoveringImage && !(hoveringModel && mouseDown) &&
            pixelMouseX >= 0 && pixelMouseX < HeadTextureWidth &&
            pixelMouseY >= 0 && pixelMouseY < HeadTextureHeight) {
            int tempX;
            int tempY;
            if (pixelMouseX < HeadTextureWidth / 2){
                tempX = x + pixelMouseX * IMAGE_SCALE;
                tempY = y + pixelMouseY * IMAGE_SCALE;
            }
            else{
                tempX = x + (pixelMouseX - HeadTextureWidth / 2) * IMAGE_SCALE;
                tempY = y + (pixelMouseY + HeadTextureHeight) * IMAGE_SCALE;
            }
            context.renderOutline(
                    tempX, tempY,
                    w * 2 / HeadTextureWidth, h / HeadTextureHeight,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(x, y, button, deltaX, deltaY)) {
            return true;
        }

        if (hoveringModel) {
            rotY -= (float) (deltaX * 0.5f);
            rotX -= (float) (deltaY * 0.5f);
            rotX = Math.max(-90f, Math.min(90f, rotX));
            return true;
        } else if (hoveringImage) {
            editPixel();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double x, double y, int i) {
        if (i == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            mouseDown = true;
            if (hoveringImage)
                editPixel();
        }

        return super.mouseClicked(x, y, i);
    }

    @Override
    public boolean mouseReleased(double x, double y, int i) {
        if (i == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            mouseDown = false;
        return super.mouseReleased(x, y, i);
    }

    private void editPixel() {
        NativeImage img = EyeFramesClient.getDynamicHead().getPixels();
        assert img != null;
        if (pixelMouseX >= 0 && pixelMouseX < HeadTextureWidth &&
            pixelMouseY >= 0 && pixelMouseY < HeadTextureHeight) {
            if (!pickerButton.active){
                selectedColor = EyeFramesClient.getDynamicHead().getPixels().getPixelRGBA(pixelMouseX, pixelMouseY);
                redSlider.setValue(selectedColor           & 0xFF);
                greenSlider.setValue((selectedColor >> 8)  & 0xFF);
                blueSlider.setValue((selectedColor >> 16)  & 0xFF);
                alphaSlider.setValue((selectedColor >> 24) & 0xFF);
                updateSelectedColor();

                pickerButton.active = true;
                pickerButton.setTexture(BUTTON_TEX);
            }
            else {
                img.setPixelRGBA(pixelMouseX, pixelMouseY, selectedColor);
                EyeFramesClient.getDynamicHead().upload();
            }
        }
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }
}
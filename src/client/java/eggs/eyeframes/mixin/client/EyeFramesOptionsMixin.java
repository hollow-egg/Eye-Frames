package eggs.eyeframes.mixin.client;

import eggs.eyeframes.screens.PlayerHeadEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eggs.eyeframes.EyeFrames.MOD_ID;

@Mixin(SkinCustomizationScreen.class)
public abstract class EyeFramesOptionsMixin extends Screen {
    @Unique
    private static final ResourceLocation eyeTex = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/eye_closed.png");
    @Unique
    private static final ResourceLocation eyeTexHovered = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/eye_open.png");

    protected EyeFramesOptionsMixin(Component title) {
        super(title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void addOptionsButton$EyeFrames(CallbackInfo ci) {
        int x = 16;
        int y = 8;
        int w = 16;
        int h = 16;

        AbstractWidget EyeFramesButton = new AbstractWidget(x, y, w, h, Component.literal("")) {
            @Override
            public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
                //is it hovered
                ResourceLocation tex;
                if (isHovered())
                    tex = eyeTexHovered;
                else
                    tex = eyeTex;

                context.blit(
                        tex,
                        this.getX(),
                        this.getY(),
                        0, 0,
                        w, h,
                        w, h
                );
            }

            @Override
            public void onClick(double mouseX, double mouseY){
                Minecraft.getInstance().setScreen(new PlayerHeadEditorScreen(Minecraft.getInstance().screen));
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput builder) {}
        };
        this.addRenderableWidget(EyeFramesButton);
    }
}

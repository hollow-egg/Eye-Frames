package eggs.eyeframes.mixin.client;

import eggs.eyeframes.screens.Face;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eggs.eyeframes.EyeFrames.MOD_ID;

@Mixin(SkinOptionsScreen.class)
public abstract class EyeFramesOptionsMixin extends Screen {
    protected EyeFramesOptionsMixin(Text title) {
        super(title);
    }

    @Inject(method = "addOptions", at = @At("TAIL"))
    private void addEyeFramesButton(CallbackInfo ci) {
        final Identifier eyeTex = Identifier.of(MOD_ID, "textures/gui/eye_closed.png");
        final Identifier eyeTexHovered = Identifier.of(MOD_ID, "textures/gui/eye_open.png");

        int x = 16;
        int y = 8;
        int w = 16;
        int h = 16;

        ClickableWidget EyeFramesButton = new ClickableWidget(x, y, w, h, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                //is it hovered
                Identifier tex;
                if (isHovered())
                    tex = eyeTexHovered;
                else
                    tex = eyeTex;

                context.drawTexture(
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
                MinecraftClient.getInstance().setScreen(new Face(MinecraftClient.getInstance().currentScreen));
            }

            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
        };
        this.addDrawableChild(EyeFramesButton);
    }
}

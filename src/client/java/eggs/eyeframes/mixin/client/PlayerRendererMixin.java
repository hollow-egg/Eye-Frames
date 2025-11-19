package eggs.eyeframes.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eggs.eyeframes.EyeFrames.MOD_ID;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin{

    @Unique
    private static final ResourceLocation CUSTOM_SKIN = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/temp/hollow_egg.png");

    @Unique
    private boolean useCustomSkin = false;

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("RETURN"), cancellable = true)
    private void eyeframes$ReplaceTexture(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> ci) {
        if (useCustomSkin) {
            ci.setReturnValue(CUSTOM_SKIN);
        }
    }

    @Shadow
    public abstract void render(LivingEntity entity, float f, float g, PoseStack matrices, MultiBufferSource source, int light);

    @Unique
    private boolean alreadyRun = false; //avoids stack overflow (cuz of recursion)

    @Inject(method = "setModelProperties",
            at = @At("TAIL"))
    private void eyeframes$SwitchModels(AbstractClientPlayer player, CallbackInfo ci) {
        PlayerRenderer renderer = (PlayerRenderer) (Object) this;
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();

        if (!alreadyRun) { //must draw body first so first person hand draws correctly (idk why)
            //disable only head
            model.head.visible = false;
            model.hat.visible = false;
            useCustomSkin = false;
        }
        else {
            //disable everything but head
            model.setAllVisible(false);
            model.head.visible = true;
            model.hat.visible = player.isModelPartShown(PlayerModelPart.HAT); //default settings for hat
            useCustomSkin = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL"))
    private void eyeframes$ManagePasses(AbstractClientPlayer player, float f, float g, PoseStack matrices,
                              MultiBufferSource bufferSource, int light, CallbackInfo ci) {

        //should run twice, once with just the head, once with just the body (with default changes)
        if (!alreadyRun) {
            alreadyRun = true;
            render(player, f, g, matrices, bufferSource, light);
            alreadyRun = false;
        }
    }
}
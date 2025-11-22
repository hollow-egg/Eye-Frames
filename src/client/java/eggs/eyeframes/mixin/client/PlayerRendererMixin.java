package eggs.eyeframes.mixin.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eggs.eyeframes.dynamicskin.DynamicSkinManager.getDynamicSkinTextureLocation;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin{
    @Inject(method = "getTextureLocation(Lnet/minecraft/client/player/AbstractClientPlayer;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("RETURN"), cancellable = true)
    private void eyeframes$ReplaceTexture(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> ci) {
        ci.setReturnValue(getDynamicSkinTextureLocation());
    }
}
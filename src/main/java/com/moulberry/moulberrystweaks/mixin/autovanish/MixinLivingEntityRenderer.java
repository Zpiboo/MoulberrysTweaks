package com.moulberry.moulberrystweaks.mixin.autovanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.moulberrystweaks.AutoVanishPlayers;
import com.moulberry.moulberrystweaks.ext.TranslucentAlphaExt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer extends EntityRenderer<LivingEntity, LivingEntityRenderState> {

    @Shadow
    public abstract Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState);

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("RETURN"))
    public void extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        // Note: TranslucentAlphaExt is only implemented for AvatarRenderState, so this only affects avatars
        if (state instanceof TranslucentAlphaExt ext) {
            if (AutoVanishPlayers.isEnabled && entity != Minecraft.getInstance().getCameraEntity() /* TODO: make sure ingame that this is sufficient (there is also .asLivingEntity()) */) { // Enabled
                Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                double distanceSq = entity.getBoundingBox().distanceToSqr(camera.position());
                if (distanceSq <= 0.2*0.2) {
                    ext.moulberrystweaks$setTranslucentAlpha(0x00);
                } else if (distanceSq < 5*5) {
                    ext.moulberrystweaks$setTranslucentAlpha((int)(distanceSq/25 * (0xFF - 0x20) + 0x20));
                } else {
                    ext.moulberrystweaks$setTranslucentAlpha(0xFF);
                }
            } else {
                ext.moulberrystweaks$setTranslucentAlpha(0xFF);
            }
        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(LivingEntity entity, LivingEntityRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof TranslucentAlphaExt ext) {
            int translucentAlpha = ext.moulberrystweaks$getTranslucentAlpha();
            if (translucentAlpha <= 0) {
                ci.cancel();
            }
        }
    }

    @WrapOperation(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/ARGB;multiply(II)I"
            )
    )
    public int submit_tintedColor(int lhs, int rhs, Operation<Integer> original, @Local(argsOnly = true) LivingEntityRenderState state) {
        int argb = original.call(lhs, rhs);
        if (state instanceof TranslucentAlphaExt ext) {
            int oldAlpha = (argb >> 24) & 0xFF;
            int newAlpha = Math.min(oldAlpha, ext.moulberrystweaks$getTranslucentAlpha());
            argb &= 0xFFFFFF;
            argb |= newAlpha << 24;
        }
        return argb;
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    public void getRenderType(LivingEntityRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing, CallbackInfoReturnable<RenderType> cir) {
        if (isBodyVisible && state instanceof TranslucentAlphaExt ext && ext.moulberrystweaks$getTranslucentAlpha() < 0xFF) {
            cir.setReturnValue(RenderTypes.entityTranslucentCullItemTarget(this.getTextureLocation(state)));
        }
    }

}

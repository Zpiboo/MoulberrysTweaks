package com.moulberry.moulberrystweaks.mixin.autovanish;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.moulberrystweaks.ext.TranslucentAlphaExt;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {

    @WrapOperation(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            )
    )
    public void submit_submitModel(
            SubmitNodeCollector instance,

            Model<? super Object> model,
            Object stateObj,
            PoseStack poseStack,
            RenderType renderType,
            int lightCoords,
            int overlayCoords,
            int outlineColor,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,

            Operation<Void> original,
            @Local AvatarRenderState state
    ) {
        if (state instanceof TranslucentAlphaExt ext) {
            int translucentAlpha = ext.moulberrystweaks$getTranslucentAlpha();
            if (translucentAlpha < 0xFF) {
                instance.submitModel(model, stateObj, poseStack, renderType, lightCoords, overlayCoords, 0xFFFFFF | (translucentAlpha << 24), null, outlineColor, crumblingOverlay);
                return;
            }
        }

        original.call(instance, model, stateObj, poseStack, renderType, lightCoords, overlayCoords, outlineColor, crumblingOverlay);
    }

    @WrapOperation(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
            )
    )
    public RenderType render_entitySolid(Identifier texture, Operation<RenderType> original, @Local(argsOnly = true) AvatarRenderState state) {
        if (state instanceof TranslucentAlphaExt ext && ext.moulberrystweaks$getTranslucentAlpha() < 0xFF) {
            return RenderTypes.armorTranslucent(texture);
        } else {
            return original.call(texture);
        }
    }

}

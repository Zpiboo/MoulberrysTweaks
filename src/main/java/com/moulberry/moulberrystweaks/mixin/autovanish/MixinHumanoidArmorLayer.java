package com.moulberry.moulberrystweaks.mixin.autovanish;

import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.moulberrystweaks.ext.TranslucentAlphaExt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer {

    @Shadow
    @Final
    private EquipmentLayerRenderer equipmentRenderer;

    @Inject(method = "renderArmorPiece", at = @At("HEAD"))
    public void renderHead(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, HumanoidRenderState state, CallbackInfo ci) {
        if (state instanceof TranslucentAlphaExt ext1 && this.equipmentRenderer instanceof TranslucentAlphaExt ext2) {
            ext2.moulberrystweaks$setTranslucentAlpha(ext1.moulberrystweaks$getTranslucentAlpha());
        }
    }

    @Inject(method = "renderArmorPiece", at = @At("RETURN"))
    public void renderReturn(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack itemStack, EquipmentSlot slot, int lightCoords, HumanoidRenderState state, CallbackInfo ci) {
        if (this.equipmentRenderer instanceof TranslucentAlphaExt ext2) {
            ext2.moulberrystweaks$setTranslucentAlpha(0xFF);
        }
    }

}

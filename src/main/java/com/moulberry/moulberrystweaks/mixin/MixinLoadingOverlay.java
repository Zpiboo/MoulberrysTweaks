package com.moulberry.moulberrystweaks.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.moulberry.moulberrystweaks.MoulberrysTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public class MixinLoadingOverlay {

    @Shadow
    private long fadeOutStart;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (MoulberrysTweaks.config.loadingOverlay.fast && this.fadeOutStart != -1L) {
            this.minecraft.setOverlay(null);
        }
    }

    @WrapWithCondition(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;clearColorOverride:I", opcode = Opcodes.PUTFIELD))
    public boolean renderShouldClear(GuiRenderState instance, int value) {
        return !MoulberrysTweaks.config.loadingOverlay.transparent || Minecraft.getInstance().player == null;
    }

    @WrapWithCondition(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
    public boolean renderShouldBlit(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int srcWidth, int srcHeight, int textureWidth, int textureHeight, int color) {
        return !MoulberrysTweaks.config.loadingOverlay.transparent || Minecraft.getInstance().player == null;
    }

    @WrapOperation(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fadeIn:Z"))
    public boolean renderShouldFadeIn(LoadingOverlay instance, Operation<Boolean> original) {
        if (MoulberrysTweaks.config.loadingOverlay.transparent && Minecraft.getInstance().player != null) {
            return false;
        }
        return original.call(instance);
    }

}

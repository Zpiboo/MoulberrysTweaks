package com.moulberry.moulberrystweaks.mixin.debugshape;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.moulberrystweaks.debugrender.DebugRenderManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow @Final private LevelTargetBundle targets;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/OptionsRenderState;cloudStatus:Lnet/minecraft/client/CloudStatus;",
                    opcode = Opcodes.GETFIELD
            )
    )
    public void renderLevelPost(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci, @Local FrameGraphBuilder frameGraphBuilder) {
        if (!DebugRenderManager.hasShapesToRender()) {
            return;
        }

        FramePass framePass = frameGraphBuilder.addPass("moulberrys_tweaks_mod_pass");
        this.targets.main = framePass.readsAndWrites(this.targets.main);
        if (this.targets.translucent != null) {
            this.targets.translucent = framePass.readsAndWrites(this.targets.translucent);
        }
        framePass.executes(() -> {
            this.renderBuffers.bufferSource().endBatch();

            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(modelViewMatrix);  // TODO: i have absolutely no idea what im doing with this (frustumMatrix -> modelViewMatrix)

            // Set model view stack to identity
            var modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.identity();

            DebugRenderManager.renderWorld(poseStack, Minecraft.getInstance().gameRenderer.getMainCamera());  // TODO: im not sure abt this either... (removed camera param -> fetch main camera from mc.gameRenderer)

            this.renderBuffers.bufferSource().endBatch();

            // Pop model view stack
            modelViewStack.popMatrix();
        });
    }

}

package com.moulberry.moulberrystweaks.debugrender;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.OptionalDouble;
import java.util.function.Function;

public class CustomRenderTypes {

    public record RenderParams(boolean depth, boolean wireframe, boolean cull) {}

    public static RenderType debugTriangleStrip(boolean depth, boolean wireframe, boolean cull) {
        return DEBUG_TRIANGLE_STRIP.apply(new RenderParams(depth, wireframe, cull && !wireframe));
        // debugrender:add -> sphere, cube, line_strip
        // debugrender:remove
        // debugrender:clear
        // debugrender:clear_namespace
    }

    private static final Function<RenderParams, RenderType> DEBUG_TRIANGLE_STRIP = Util.memoize(params -> {
        String name = "debug_triangle_strip";
        if (!params.depth) {
            name += "_nodepth";
        }
        if (!params.cull) {
            name += "_nocull";
        }
        if (params.wireframe) {
            name += "_wireframe";
        }

        DepthStencilState depthStencilState = new DepthStencilState(
                params.depth ? CompareOp.LESS_THAN_OR_EQUAL : CompareOp.ALWAYS_PASS,
                !params.wireframe,
                -1, -1
        );
        var pipeline = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath("moulberrystweaks", "pipeline/"+name))
                    .withDepthStencilState(depthStencilState)
                    .withPolygonMode(params.wireframe ? PolygonMode.WIREFRAME : PolygonMode.FILL)
                    .withCull(params.cull)
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
                    .build()
        );

        return RenderType.create(
            "moulberrystweaks/"+name,
            RenderSetup.builder(pipeline)
                    .bufferSize(1536)
                    .createRenderSetup()
        );
    });

    public static final RenderType DEBUG_LINE = RenderType.create(
        "moulberrystweaks/debug_line",
        RenderSetup.builder(RenderPipelines.LINES)
                .bufferSize(1536)
                .createRenderSetup()
    );


    private static final RenderPipeline PIPELINE_LINES_WITHOUT_DEPTH = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath("moulberrystweaks", "pipeline/lines_without_depth"))
                .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
                .build()
    );

    public static final RenderType DEBUG_LINE_WITHOUT_DEPTH = RenderType.create(
        "moulberrystweaks/debug_line_without_depth",
        RenderSetup.builder(PIPELINE_LINES_WITHOUT_DEPTH)
                .bufferSize(1536)
                .createRenderSetup()
    );

    private static final RenderPipeline PIPELINE_LINE_STRIP = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath("moulberrystweaks", "pipeline/line_strip"))
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.DEBUG_LINE_STRIP)
                    .build()
    );

    public static final RenderType LINE_STRIP = RenderType.create(
        "moulberrystweaks/line_strip",
        RenderSetup.builder(PIPELINE_LINE_STRIP)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup()
    );

    private static final RenderPipeline PIPELINE_LINE_STRIP_WITHOUT_DEPTH = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath("moulberrystweaks", "pipeline/line_strip_without_depth"))
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES)
                .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
                .build()
    );

    public static final RenderType LINE_STRIP_WITHOUT_DEPTH = RenderType.create(
        "moulberrystweaks/line_strip_without_depth",
        RenderSetup.builder(PIPELINE_LINE_STRIP_WITHOUT_DEPTH)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup()
    );

    private static final RenderPipeline PIPELINE_DEBUG_LINE_STRIP_WITHOUT_DEPTH = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath("moulberrystweaks", "pipeline/debug_line_strip_without_depth"))
                .withLocation("pipeline/debug_line_strip")
                .withVertexShader("core/position_color")
                .withFragmentShader("core/position_color")
                .withCull(false)
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP)
                .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
                .build()
    );

    public static final RenderType DEBUG_LINE_STRIP_WITHOUT_DEPTH = RenderType.create(
        "moulberrystweaks/debug_line_strip_without_depth",
        RenderSetup.builder(PIPELINE_DEBUG_LINE_STRIP_WITHOUT_DEPTH)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup()
    );

}

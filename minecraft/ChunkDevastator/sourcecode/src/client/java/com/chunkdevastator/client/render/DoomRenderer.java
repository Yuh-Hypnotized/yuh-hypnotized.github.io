package com.chunkdevastator.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import com.chunkdevastator.ChunkDevastatorConstants;
import com.chunkdevastator.client.ClientDoomRegistry;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Renders the doom warning box (outline + translucent fill) around the chunk
 * column, using the 26.2 rendering model:
 *
 *   extraction phase  -> LevelExtractionEvents.END_EXTRACTION  (build immutable state)
 *   drawing phase     -> LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN (submit geometry)
 *   GPU submission    -> custom RenderPipeline + StagedVertexBuffer + RenderPass
 *
 * Ported from the official Fabric 26.2 guide: docs.fabricmc.net/develop/rendering/world
 * The countdown NUMBER is drawn as a HUD element (GuiGraphicsExtractor#text), not as
 * world-space text: 26.2 replaced Font#drawInBatch with a pipeline/glyph system whose
 * world-space API is not yet documented for mods.
 */
public final class DoomRenderer {
    private static final Identifier PIPELINE_ID = Identifier.fromNamespaceAndPath(ChunkDevastatorConstants.MOD_ID, "pipeline/doom_box");

    private static final RenderPipeline DOOM_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(PIPELINE_ID)
                    .withDepthStencilState(Optional.empty()) // visible through terrain
                    .build());

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final StagedVertexBuffer STAGED_BUFFER =
            new StagedVertexBuffer(() -> "ChunkDevastator Dooms", RenderType.SMALL_BUFFER_SIZE);

    private final ClientDoomRegistry registry;
    private List<DoomRenderState> renderStates = List.of();

    public DoomRenderer(ClientDoomRegistry registry) {
        this.registry = registry;
    }

    /** LevelExtractionEvents.END_EXTRACTION: snapshot this frame's dooms (immutable, thread-safe). */
    public void onExtract(LevelExtractionContext context) {
        ClientLevel level = context.level();
        List<DoomRenderState> states = new ArrayList<>();
        for (ClientDoomRegistry.Entry doom : registry.visible(level)) {
            states.add(DoomRenderState.from(doom, level));
        }
        this.renderStates = List.copyOf(states);
    }

    /** LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN: draw every visible doom box. */
    public void onRender(LevelRenderContext context) {
        if (renderStates.isEmpty()) {
            return;
        }
        VertexFormat formatBinding = DOOM_PIPELINE.getVertexFormatBinding(0);
        assert formatBinding != null;

        PrimitiveTopology primitive = DOOM_PIPELINE.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = STAGED_BUFFER.appendDraw(formatBinding, primitive,
                primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        renderBoxes(context, draw);

        STAGED_BUFFER.upload();
        StagedVertexBuffer.ExecuteInfo info = STAGED_BUFFER.getExecuteInfo(draw);
        if (info != null) {
            draw(Minecraft.getInstance(), info);
        }
        STAGED_BUFFER.endFrame();
    }

    private void renderBoxes(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer builder = STAGED_BUFFER.getVertexBuilder(draw);
        for (DoomRenderState state : renderStates) {
            renderBox(matrices.last().pose(), builder, state);
        }

        matrices.popPose();
    }

    // ------------------------------------------------------------------
    // Geometry: 4 corner posts + top/bottom edge loops (thin quads) and
    // 4 translucent fill faces, one chunk column each.
    // ------------------------------------------------------------------
    private static void renderBox(Matrix4fc matrix, VertexConsumer buffer, DoomRenderState s) {
        float t = 0.25F; // outline thickness
        float minX = s.minX();
        float maxX = s.maxX();
        float minZ = s.minZ();
        float maxZ = s.maxZ();
        float minY = s.minY();
        float maxY = s.maxY();

        // Corner posts
        post(matrix, buffer, minX, minX + t, minZ, minZ + t, minY, maxY, s);
        post(matrix, buffer, maxX - t, maxX, minZ, minZ + t, minY, maxY, s);
        post(matrix, buffer, minX, minX + t, maxZ - t, maxZ, minY, maxY, s);
        post(matrix, buffer, maxX - t, maxX, maxZ - t, maxZ, minY, maxY, s);
        // Top / bottom edge loops
        edgeLoop(matrix, buffer, minX, maxX, minZ, maxZ, maxY, maxY + t, s);
        edgeLoop(matrix, buffer, minX, maxX, minZ, maxZ, minY - t, minY, s);
        // Translucent fill faces
        fillFace(matrix, buffer, minX, maxX, minZ, minY, maxY, s);
        fillFace(matrix, buffer, minX, maxX, maxZ, minY, maxY, s);
        fillFaceZ(matrix, buffer, minX, minZ, maxZ, minY, maxY, s);
        fillFaceZ(matrix, buffer, maxX, minZ, maxZ, minY, maxY, s);
    }

    private static void post(Matrix4fc m, VertexConsumer b, float x0, float x1, float z0, float z1,
                             float y0, float y1, DoomRenderState s) {
        quad(m, b, x0, y0, z0, x0, y1, z0, x1, y1, z1, x1, y0, z1, s, s.edgeAlpha());
        quad(m, b, x1, y0, z1, x1, y1, z1, x0, y1, z0, x0, y0, z0, s, s.edgeAlpha());
    }

    private static void edgeLoop(Matrix4fc m, VertexConsumer b, float minX, float maxX, float minZ, float maxZ,
                                 float y0, float y1, DoomRenderState s) {
        quad(m, b, minX, y0, minZ, minX, y1, minZ, maxX, y1, minZ, maxX, y0, minZ, s, s.edgeAlpha());
        quad(m, b, maxX, y0, minZ, maxX, y1, minZ, maxX, y1, maxZ, maxX, y0, maxZ, s, s.edgeAlpha());
        quad(m, b, maxX, y0, maxZ, maxX, y1, maxZ, minX, y1, maxZ, minX, y0, maxZ, s, s.edgeAlpha());
        quad(m, b, minX, y0, maxZ, minX, y1, maxZ, minX, y1, minZ, minX, y0, minZ, s, s.edgeAlpha());
    }

    /** Vertical face at constant Z. */
    private static void fillFace(Matrix4fc m, VertexConsumer b, float x0, float x1, float z,
                                 float y0, float y1, DoomRenderState s) {
        quad(m, b, x0, y0, z, x0, y1, z, x1, y1, z, x1, y0, z, s, s.fillAlpha());
    }

    /** Vertical face at constant X. */
    private static void fillFaceZ(Matrix4fc m, VertexConsumer b, float x, float z0, float z1,
                                  float y0, float y1, DoomRenderState s) {
        quad(m, b, x, y0, z0, x, y1, z0, x, y1, z1, x, y0, z1, s, s.fillAlpha());
    }

    private static void quad(Matrix4fc m, VertexConsumer b,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             DoomRenderState s, float alpha) {
        b.addVertex(m, x0, y0, z0).setColor(s.r(), s.g(), s.b(), alpha);
        b.addVertex(m, x1, y1, z1).setColor(s.r(), s.g(), s.b(), alpha);
        b.addVertex(m, x2, y2, z2).setColor(s.r(), s.g(), s.b(), alpha);
        b.addVertex(m, x3, y3, z3).setColor(s.r(), s.g(), s.b(), alpha);
    }

    // ------------------------------------------------------------------
    // GPU submission (pattern from the Fabric 26.2 docs).
    // ------------------------------------------------------------------
    private static void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();
        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> ChunkDevastatorConstants.MOD_ID + " doom box rendering",
                        colorTexture, Optional.empty(), mainTarget.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(DOOM_PIPELINE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }

    // ------------------------------------------------------------------
    // Frame state + colors.
    // ------------------------------------------------------------------
    private record DoomRenderState(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                   float r, float g, float b, float edgeAlpha, float fillAlpha) {
        static DoomRenderState from(ClientDoomRegistry.Entry doom, ClientLevel level) {
            ChunkPos pos = new ChunkPos(doom.chunkX(), doom.chunkZ());
            int color = warningColor(doom.remainingSeconds());
            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            return new DoomRenderState(
                    pos.getMinBlockX(), level.getMinY(), pos.getMinBlockZ(),
                    pos.getMaxBlockX() + 1, level.getMaxY(), pos.getMaxBlockZ() + 1,
                    r, g, b, 0.35F, 0.12F);
        }
    }

    /** 5 discrete color steps: t=5 yellow -> t=1 red (SPEC 5.2). Shared with the HUD countdown. */
    public static int warningColor(float remainingSeconds) {
        int index = 5 - (int) Math.ceil(remainingSeconds);
        if (index < 0) {
            index = 0;
        }
        if (index > 4) {
            index = 4;
        }
        return ChunkDevastatorConstants.WARNING_COLORS[index];
    }
}

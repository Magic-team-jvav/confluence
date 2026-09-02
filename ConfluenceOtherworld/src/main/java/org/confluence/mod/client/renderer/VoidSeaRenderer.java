package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.common.util.VoidSeaHelper;
import org.joml.Matrix4f;

import static org.confluence.mod.client.util.ClientVoidSeaConstants.*;

public class VoidSeaRenderer {
    private static TextureTarget sceneDepth;

    public static void render(RenderLevelStageEvent event, Minecraft minecraft, LocalPlayer player) {
        ClientLevel level = minecraft.level;
        if (level == null
                || !VoidSeaHelper.isEnd(player.level())
                || !VoidSeaHelper.isDimensionalOverlapEffect(player)) {
            return;
        }

//        if (LibUtils.isModLoaded("iris") && VoidSeaIrisCompat.isRenderingShadowPass()) {
//            return;
//        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 cameraPosition = event.getCamera().getPosition();
        float renderY = VoidSeaHelper.getHeight(partialTick);
        float renderDistance = minecraft.gameRenderer.getRenderDistance();
        if (Math.abs(renderY - (float) cameraPosition.y) > renderDistance) {
            return;
        }
        float radius = Math.max(MIN_RENDER_RADIUS, minecraft.options.renderDistance().get() * CHUNK_SIZE + RENDER_DISTANCE_MARGIN);
        try {
            prepareSceneDepth(minecraft);
            drawSea(cameraPosition, renderY, radius, level.getGameTime() % GAME_TIME_CYCLE + partialTick);
            renderSubmergedSurface(event, minecraft, cameraPosition, renderY);
        } catch (RuntimeException exception) {
//            Confluence.LOGGER.warn("Failed to render the void sea.", exception);
        }
    }

    private static void drawSea(Vec3 cameraPosition, float renderY, float radius, float gameTime) {
        ShaderInstance shader = ModRenderer.getVoidSeaShader();
        if (shader == null) {
            return;
        }

        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }
        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }
        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }
        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }
        shader.safeGetUniform("GameTime").set(gameTime);
        shader.safeGetUniform("LayerCount").set(VoidSeaRenderSettings.getLayerCount());
        shader.safeGetUniform("DetailAlpha").set(VoidSeaRenderSettings.getDetailAlpha());
        shader.safeGetUniform("DetailBrightness").set(VoidSeaRenderSettings.getDetailBrightness());
        shader.safeGetUniform("FlowSpeed").set(VoidSeaRenderSettings.getFlowSpeed());
        shader.safeGetUniform("LayerScaleStep").set(VoidSeaRenderSettings.getLayerScaleStep());
        shader.safeGetUniform("Hue").set(VoidSeaRenderSettings.getHue());
        shader.safeGetUniform("HueStep").set(VoidSeaRenderSettings.getHueStep());
        shader.safeGetUniform("Saturation").set(VoidSeaRenderSettings.getSaturation());
        shader.safeGetUniform("FlickerIntensity").set(VoidSeaRenderSettings.getFlickerIntensity());
        shader.safeGetUniform("FlickerSpeed").set(VoidSeaRenderSettings.getFlickerSpeed());
        shader.safeGetUniform("ScreenSize").set((float) sceneDepth.width, (float) sceneDepth.height);
        shader.safeGetUniform("EdgeColor").set(EDGE_COLOR);
        shader.safeGetUniform("EdgeCoreWidth").set(EDGE_CORE_WIDTH);
        shader.safeGetUniform("EdgeGlowWidth").set(EDGE_GLOW_WIDTH);
        float edgeStrengthMultiplier = cameraPosition.y < renderY ? UNDERWATER_EDGE_STRENGTH_MULTIPLIER : 1.0F;
        shader.safeGetUniform("EdgeCoreStrength").set(EDGE_CORE_STRENGTH * edgeStrengthMultiplier);
        shader.safeGetUniform("EdgeGlowStrength").set(EDGE_GLOW_STRENGTH * edgeStrengthMultiplier);
        shader.setSampler("Sampler2", RenderSystem.getShaderTexture(2));
        shader.setSampler("DepthSampler", sceneDepth.getDepthTextureId());

        BufferBuilder builder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        addSeaMesh(builder, renderY - (float) cameraPosition.y, radius, (float) cameraPosition.x, (float) cameraPosition.z);
        ModRenderTypes.SEA_RENDER_TYPE.draw(builder.buildOrThrow());
    }

    private static void prepareSceneDepth(Minecraft minecraft) {
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        if (sceneDepth == null) {
            sceneDepth = new TextureTarget(mainTarget.width, mainTarget.height, true, Minecraft.ON_OSX);
        } else if (sceneDepth.width != mainTarget.width
                || sceneDepth.height != mainTarget.height) {
            sceneDepth.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
        }
        sceneDepth.copyDepthFrom(mainTarget);
        mainTarget.bindWrite(false);
    }

    private static void renderSubmergedSurface(RenderLevelStageEvent event, Minecraft minecraft, Vec3 cameraPosition, float renderY) {
        ShaderInstance shader = ModRenderer.getVoidSeaSubmergedSurfaceShader();
        if (shader == null) {
            return;
        }

        minecraft.getMainRenderTarget().bindWrite(true);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        shader.setSampler("DepthSampler", sceneDepth.getDepthTextureId());
        shader.safeGetUniform("InverseProjMat").set(new Matrix4f(event.getProjectionMatrix()).invert());
        shader.safeGetUniform("InverseViewMat").set(new Matrix4f(event.getModelViewMatrix()).invert());
        shader.safeGetUniform("SeaRelativeY").set(renderY - (float) cameraPosition.y);
        shader.safeGetUniform("SubmergedColor").set(SUBMERGED_SURFACE_COLOR);
        float submergedSurfaceStrengthMultiplier = cameraPosition.y < renderY ? UNDERWATER_SUBMERGED_SURFACE_STRENGTH_MULTIPLIER : 1.0F;
        shader.safeGetUniform("SubmergedStrength").set(SUBMERGED_SURFACE_STRENGTH * submergedSurfaceStrengthMultiplier);
        shader.apply();
        BufferBuilder buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        buffer.addVertex(0.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 0.0F, 0.0F);
        buffer.addVertex(1.0F, 1.0F, 0.0F);
        buffer.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(buffer.buildOrThrow());
        shader.clear();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void addSeaMesh(BufferBuilder builder, float y, float radius, float cameraX, float cameraZ) {
        if (radius <= SIMPLE_MESH_RADIUS) {
            addLayerQuad(builder, y, -radius, -radius, radius, radius, cameraX, cameraZ);
            return;
        }

        addLayerQuad(builder, y, -SIMPLE_MESH_RADIUS, -SIMPLE_MESH_RADIUS, SIMPLE_MESH_RADIUS, SIMPLE_MESH_RADIUS, cameraX, cameraZ);
        float innerRadius = SIMPLE_MESH_RADIUS;
        float cellSize = INITIAL_RING_CELL_SIZE;
        while (innerRadius < radius) {
            float outerRadius = Math.min(innerRadius + cellSize, radius);
            addSeaRing(builder, y, innerRadius, outerRadius, cellSize, cameraX, cameraZ);
            innerRadius = outerRadius;
            cellSize *= RING_CELL_SIZE_MULTIPLIER;
        }
    }

    private static void addSeaRing(BufferBuilder builder, float y, float innerRadius, float outerRadius, float cellSize, float cameraX, float cameraZ) {
        for (float x = -outerRadius; x < outerRadius; x += cellSize) {
            float nextX = Math.min(x + cellSize, outerRadius);
            addLayerQuad(builder, y, x, innerRadius, nextX, outerRadius, cameraX, cameraZ);
            addLayerQuad(builder, y, x, -outerRadius, nextX, -innerRadius, cameraX, cameraZ);
        }
        for (float z = -innerRadius; z < innerRadius; z += cellSize) {
            float nextZ = Math.min(z + cellSize, innerRadius);
            addLayerQuad(builder, y, -outerRadius, z, -innerRadius, nextZ, cameraX, cameraZ);
            addLayerQuad(builder, y, innerRadius, z, outerRadius, nextZ, cameraX, cameraZ);
        }
    }

    private static void addLayerQuad(BufferBuilder builder, float y, float minX, float minZ, float maxX, float maxZ, float cameraX, float cameraZ) {
        addLayerVertex(builder, minX, y, minZ, cameraX, cameraZ);
        addLayerVertex(builder, minX, y, maxZ, cameraX, cameraZ);
        addLayerVertex(builder, maxX, y, maxZ, cameraX, cameraZ);
        addLayerVertex(builder, maxX, y, minZ, cameraX, cameraZ);
    }

    private static void addLayerVertex(BufferBuilder builder, float x, float y, float z, float cameraX, float cameraZ) {
        float tileSize = VoidSeaRenderSettings.getTileSize();
        builder.addVertex(x, y, z).setColor(SEA_COLOR.x, SEA_COLOR.y, SEA_COLOR.z, VoidSeaRenderSettings.getBaseAlpha()).setUv((cameraX + x) / tileSize, (cameraZ + z) / tileSize).setLight(LightTexture.FULL_BRIGHT);
    }
}

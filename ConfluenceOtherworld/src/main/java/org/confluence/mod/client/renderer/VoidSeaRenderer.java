package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.util.VoidSeaHelper;

public class VoidSeaRenderer {
    /// 虚空海着色器资源。
    public static final ResourceLocation SEA_SHADER_ID = Confluence.asResource("void_sea");
    /// 海面基础颜色。
    public static final int COLOR = 0x22000000;
    /// 中心细分网格半径（单位：格）。
    public static final float SIMPLE_MESH_RADIUS = 8.0F * 16.0F;
    /// 首层环带单元尺寸（单位：格）。
    public static final float INITIAL_RING_CELL_SIZE = 16.0F;
    /// 最小海面渲染半径（单位：格）。
    public static final float MIN_RENDER_RADIUS = 256.0F;
    /// 视距外扩范围（单位：格）。
    public static final float RENDER_DISTANCE_MARGIN = 32.0F;
    /// 纹理动画循环时长（单位：刻）。
    public static final long GAME_TIME_CYCLE = 24000L;

    public static void render(RenderLevelStageEvent event, Minecraft minecraft, LocalPlayer player) {
        ClientLevel level = minecraft.level;
        if (level == null
                || level.dimension() != Level.END
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
        float radius = Math.max(MIN_RENDER_RADIUS, minecraft.options.renderDistance().get() * 16.0F + RENDER_DISTANCE_MARGIN);
        try {
            drawSea(cameraPosition, renderY, radius, level.getGameTime() % GAME_TIME_CYCLE + partialTick);
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

        BufferBuilder builder = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX_COLOR);
        float y = renderY - (float) cameraPosition.y;
        addSeaMesh(builder, y, radius, (float) cameraPosition.x, (float) cameraPosition.z);
        ModRenderTypes.SEA_RENDER_TYPE.draw(builder.buildOrThrow());
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
            cellSize *= 2.0F;
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
        builder.addVertex(x, y, z).setUv((cameraX + x) / tileSize, (cameraZ + z) / tileSize).setColor(COLOR >> 16 & 255, COLOR >> 8 & 255, COLOR & 255, (int) (VoidSeaRenderSettings.getBaseAlpha() * 255.0F));
    }
}

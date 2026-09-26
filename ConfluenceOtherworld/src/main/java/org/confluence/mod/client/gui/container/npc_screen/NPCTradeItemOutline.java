package org.confluence.mod.client.gui.container.npc_screen;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.inventory.Slot;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.npc.trade.NPCTradeMenu;
import org.joml.Matrix4f;

/// 只采集商品图标的透明轮廓；描边不涉及槽位、数量或玩家背包。
public final class NPCTradeItemOutline implements AutoCloseable {
    private static final int COLUMNS = 9;
    private static final int ROWS = 4;
    private static final int PADDING = 2;
    private static final int CELL_SIZE = 16 + PADDING * 2;
    private static final int ATLAS_WIDTH = COLUMNS * CELL_SIZE;
    private static final int ATLAS_HEIGHT = ROWS * CELL_SIZE;
    private static final float OUTLINE_ALPHA = 0.45F;
    private static ShaderInstance shader;

    private TextureTarget atlas;
    private int atlasScale;
    private boolean prepared;

    public static void setShader(ShaderInstance instance) {
        shader = instance;
    }

    private boolean isPrepared() {
        return prepared && shader != null;
    }

    /// 每帧只切换一次离屏目标，保证动画贴图、模型和附魔光效仍实时更新。
    public void prepare(GuiGraphics graphics, NPCTradeMenu menu, int imageWidth) {
        prepared = false;
        if (shader == null || menu.slots.stream().noneMatch(slot -> menu.isOfferSlot(slot.index) && slot.hasItem()))
            return;
        Minecraft minecraft = Minecraft.getInstance();
        int scale = Math.max(1, (int) Math.ceil(minecraft.getWindow().getGuiScale()));
        graphics.flush();
        if (atlas == null || atlasScale != scale) {
            close();
            atlasScale = scale;
            atlas = new TextureTarget(ATLAS_WIDTH * scale, ATLAS_HEIGHT * scale, true, Minecraft.ON_OSX);
            atlas.setClearColor(0, 0, 0, 0);
        }
        RenderSystem.backupProjectionMatrix();
        try {
            atlas.clear(Minecraft.ON_OSX);
            atlas.bindWrite(true);
            RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, ATLAS_WIDTH, ATLAS_HEIGHT, 0, 1000, 21000), VertexSorting.ORTHOGRAPHIC_Z);
            GuiGraphics capture = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
            for (Slot slot : menu.slots) {
                if (!menu.isOfferSlot(slot.index) || !slot.hasItem()) continue;
                int x = slot.index % COLUMNS * CELL_SIZE + PADDING;
                int y = slot.index / COLUMNS * CELL_SIZE + PADDING;
                capture.renderItem(slot.getItem(), x, y, slot.x + slot.y * imageWidth);
            }
            capture.flush();
            prepared = true;
        } finally {
            minecraft.getMainRenderTarget().bindWrite(true);
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    /// 在背景阶段合成一像素轮廓，随后由原版绘制商品及数量。
    public void render(GuiGraphics graphics, Slot slot) {
        if (!isPrepared()) return;
        ModRarity rarity = ModRarity.getRarity(slot.getItem());
        if (rarity == null) return;
        graphics.flush();
        int color = rarity.color();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = OUTLINE_ALPHA;
        int column = slot.index % COLUMNS;
        int row = slot.index / COLUMNS;
        float u0 = (float) (column * CELL_SIZE) / ATLAS_WIDTH;
        float u1 = (float) ((column + 1) * CELL_SIZE) / ATLAS_WIDTH;
        /// 帧缓冲纹理的纵轴与 GUI 相反。
        float v0 = 1.0F - (float) (row * CELL_SIZE) / ATLAS_HEIGHT;
        float v1 = 1.0F - (float) ((row + 1) * CELL_SIZE) / ATLAS_HEIGHT;
        float x = slot.x - PADDING;
        float y = slot.y - PADDING;
        Matrix4f matrix = graphics.pose().last().pose();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, atlas.getColorTextureId());
        shader.safeGetUniform("OutlineStep").set(1.0F / ATLAS_WIDTH, 1.0F / ATLAS_HEIGHT);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        try {
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            buffer.vertex(matrix, x, y, 150).uv(u0, v0).color(red, green, blue, alpha).endVertex();
            buffer.vertex(matrix, x, y + CELL_SIZE, 150).uv(u0, v1).color(red, green, blue, alpha).endVertex();
            buffer.vertex(matrix, x + CELL_SIZE, y + CELL_SIZE, 150).uv(u1, v1).color(red, green, blue, alpha).endVertex();
            buffer.vertex(matrix, x + CELL_SIZE, y, 150).uv(u1, v0).color(red, green, blue, alpha).endVertex();
            BufferUploader.drawWithShader(buffer.end());
        } finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    /// 关闭界面或改变 GUI 缩放时释放离屏纹理。
    @Override
    public void close() {
        prepared = false;
        if (atlas != null) {
            atlas.destroyBuffers();
            atlas = null;
        }
    }
}

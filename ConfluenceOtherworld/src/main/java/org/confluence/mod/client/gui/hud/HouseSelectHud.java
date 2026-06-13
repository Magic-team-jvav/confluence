package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.util.LibRenderUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.GuiSprite;
import org.confluence.mod.network.c2s.HouseSelectPacketC2S;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.client.PortGuiLayer;

public class HouseSelectHud implements PortGuiLayer {
    private static final GuiSprite crosshair = new GuiSprite(Confluence.asResource("hud/house_select/crosshair"), 15, 15);
    private static final Component tip1 = Component.translatable("message.confluence.house_select.tip1");
    private static final Component tip2 = Component.translatable("message.confluence.house_select.tip2");
    private static final Component check = Component.translatable("message.confluence.house_select.check");
    private static final GuiSprite[] sprites = Util.make(new GuiSprite[AvailableHouseSelectPacketS2C.size], a -> {
        ResourceLocation i = Confluence.asResource("hud/house_select/npc_head");
        int w = 128, h = 128;
        a[0] = new GuiSprite(i, w, h, 0, 0, 8, 12);
        a[1] = new GuiSprite(i, w, h, 9, 0, 12, 11);
        a[2] = new GuiSprite(i, w, h, 22, 0, 15, 14);
        a[3] = new GuiSprite(i, w, h, 38, 0, 14, 11);
        a[4] = new GuiSprite(i, w, h, 53, 0, 9, 13);
        a[5] = new GuiSprite(i, w, h, 63, 0, 13, 11);
        a[6] = new GuiSprite(i, w, h, 77, 0, 11, 11);
        a[7] = new GuiSprite(i, w, h, 89, 0, 14, 13);
        a[8] = new GuiSprite(i, w, h, 104, 0, 11, 10);
        a[9] = new GuiSprite(i, w, h, 116, 0, 11, 9);

        a[10] = null;
        a[11] = null;
        a[12] = new GuiSprite(i, w, h, 31, 15, 15, 15);
        a[13] = null;
        a[14] = new GuiSprite(i, w, h, 59, 15, 11, 11);
        a[15] = null;
        a[16] = new GuiSprite(i, w, h, 99, 15, 10, 10);
        a[17] = new GuiSprite(i, w, h, 110, 15, 16, 12);

        a[18] = null;
        a[19] = null;
        a[20] = new GuiSprite(i, w, h, 30, 37, 15, 15);
        a[21] = new GuiSprite(i, w, h, 46, 37, 12, 12);
        a[22] = null;
        a[23] = null;
        a[24] = new GuiSprite(i, w, h, 94, 37, 14, 14);
    });

    public static boolean inSelectHUD = false;
    private static boolean[] available = new boolean[AvailableHouseSelectPacketS2C.size];
    private static int selected = -1;
    private static Component regionText;

    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        if (!inSelectHUD) return;

        Minecraft minecraft = Minecraft.getInstance();
        MouseHandler handler = minecraft.mouseHandler;
        Font font = minecraft.font;
        int halfW = guiGraphics.guiWidth() / 2;
        int x = halfW;
        int y = guiGraphics.guiHeight() / 2;
        Window window = minecraft.getWindow();
        if (InputConstants.isKeyDown(window.getWindow(), InputConstants.KEY_ESCAPE)) {
            inSelectHUD = false;
            selected = -1;
            handler.grabMouse();
        } else if (Screen.hasAltDown()) {
            handler.releaseMouse();
            int m = x + 24 * 6;
            int mouseX = (int) (handler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth());
            int mouseY = (int) (handler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight());
            for (int i = 0; i < available.length; i++) {
                if (!available[i]) continue;
                EntityType<?> type = AvailableHouseSelectPacketS2C.getTypes()[i];
                if (type == null) continue;
                GuiSprite sprite = sprites[i];
                if (sprite == null) continue;

                sprite.setPos(x, y).renderAligned(guiGraphics, 24, 24);
                if (mouseX >= x && mouseX < x + 24 && mouseY >= y && mouseY < y + 24) {
                    Component name;
                    if (i == 0) {
                        name = check;
                    } else {
                        name = type.getDescription();
                    }
                    guiGraphics.renderTooltip(font, name, mouseX, mouseY);

                    if (handler.isLeftPressed() && i != AvailableHouseSelectPacketS2C.traveling_merchant) {
                        selected = i;
                    }
                }

                x += 24;
                if (x >= m) {
                    x = halfW;
                    y += 24;
                }
            }
        } else {
            if (selected < 0) {
                crosshair.setPos(x - 7, y - 7).render(guiGraphics);
            } else {
                sprites[selected].setPos(x - 12, y - 12).renderAligned(guiGraphics, 24, 24);
            }
            guiGraphics.drawString(font, tip1, x - font.width(tip1) / 2, y + 15 - font.lineHeight, 0xFFFFFF);
            guiGraphics.drawString(font, tip2, x - font.width(tip2) / 2, y + 15, 0xFFFFFF);
            handler.grabMouse();
        }

        if (regionText != null) {
            guiGraphics.drawCenteredString(font, regionText, halfW, y + 32, 0xFFFFFF);
        }
    }

    public static void selectHouse(Player player) {
        if (selected < 0) return;
        BlockHitResult result = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);
        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        IHouseDetector detect = IHouseDetector.detect(pos, player.level());
        if (selected == 0 && !detect.isError()) {
            for (BlockPos blockPos : detect.list()) {
                DebugBlocksHelper.Singleton().addDebugBlock(blockPos, new DebugBlocksHelper.DebugInfo(255, 255, 30, 100));
            }
            DebugBlocksHelper.Singleton().addDebugBlock(pos, new DebugBlocksHelper.DebugInfo(255, 0, 120, 120));
        }
        HouseSelectPacketC2S.sendToServer(selected, pos);
    }

    public static void handlePacket(boolean[] values) {
        available = values;
    }

    private static final WorldBorder worldBorder;

    static {
        worldBorder = new WorldBorder();
        worldBorder.setSize(256);
    }

    public static void updatePlayerRegionAt(LocalPlayer player) {
        if (inSelectHUD) {
            ChunkPos pos = player.chunkPosition();
            int x = (pos.x + 8) >> 4 << 4;
            int z = (pos.z + 8) >> 4 << 4;
            if (worldBorder.getCenterX() != x || worldBorder.getCenterZ() != z) {
                regionText = Component.literal("Region(x=[" + (x - 8) + ", " + (x + 7) + "], z=[" + (z - 8) + ", " + (z + 7) + "])");
                worldBorder.setCenter(x * 16, z * 16);
            }
        }
    }

    /// [net.minecraft.client.renderer.LevelRenderer#renderWorldBorder]
    public static void renderRegionInWorld(Minecraft minecraft) {
        if (!inSelectHUD) return;
        Camera camera = minecraft.gameRenderer.getMainCamera();
        boolean isBlendDisabled = !LibRenderUtils.isBlendEnabled();
        if (isBlendDisabled) {
            RenderSystem.enableBlend();
        }
        boolean isDepthTestDisabled = !LibRenderUtils.isDepthTestEnabled();
        if (isDepthTestDisabled) {
            RenderSystem.enableDepthTest();
        }
        int[] blendFunc = LibRenderUtils.getBlendFunc();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        boolean depthMask = Minecraft.useShaderTransparency();
        boolean currentDepthMask = LibRenderUtils.getCurrentDepthMask();
        if (currentDepthMask != depthMask) {
            RenderSystem.depthMask(depthMask);
        }
        @Nullable ShaderInstance shader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        float[] polygonOffset = LibRenderUtils.getPolygonOffset();
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        boolean isPolygonOffsetDisabled = !LibRenderUtils.isPolygonOffsetEnabled();
        if (isPolygonOffsetDisabled) {
            RenderSystem.enablePolygonOffset();
        }
        boolean cullEnabled = LibRenderUtils.isCullEnabled();
        if (cullEnabled) {
            RenderSystem.disableCull();
        }
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float x = (float) camera.getPosition().x;
        float z = (float) camera.getPosition().z;
        int a = (int) (Mth.clamp(Math.pow(1.0 - worldBorder.getDistanceToBorder(x, z) / 256, 4.0), 0.0, 1.0) * 51);
        float depthFar = minecraft.gameRenderer.getDepthFar();
        float minX = (float) worldBorder.getMinX();
        float maxX = (float) worldBorder.getMaxX();
        float minZ = (float) worldBorder.getMinZ();
        float maxZ = (float) worldBorder.getMaxZ();
        float minDx = minX - x;
        float maxDx = maxX - x;
        float minDz = minZ - z;
        float maxDz = maxZ - z;

        builder.vertex(maxDx, -depthFar, minDz).color(153, 255, 0, a);
        builder.vertex(maxDx, -depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(maxDx, depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(maxDx, depthFar, minDz).color(153, 255, 0, a);

        builder.vertex(minDx, -depthFar, minDz).color(153, 255, 0, a);
        builder.vertex(minDx, -depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(minDx, depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(minDx, depthFar, minDz).color(153, 255, 0, a);

        builder.vertex(minDx, -depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(maxDx, -depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(maxDx, depthFar, maxDz).color(153, 255, 0, a);
        builder.vertex(minDx, depthFar, maxDz).color(153, 255, 0, a);

        builder.vertex(minDx, -depthFar, minDz).color(153, 255, 0, a);
        builder.vertex(maxDx, -depthFar, minDz).color(153, 255, 0, a);
        builder.vertex(maxDx, depthFar, minDz).color(153, 255, 0, a);
        builder.vertex(minDx, depthFar, minDz).color(153, 255, 0, a);

        BufferUploader.drawWithShader(builder.end());

        if (cullEnabled) {
            RenderSystem.enableCull();
        }
        RenderSystem.polygonOffset(polygonOffset[0], polygonOffset[1]);
        if (isPolygonOffsetDisabled) {
            RenderSystem.disablePolygonOffset();
        }
        if (isBlendDisabled) {
            RenderSystem.disableBlend();
        }
        RenderSystem.blendFuncSeparate(blendFunc[0], blendFunc[1], blendFunc[2], blendFunc[3]);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (isDepthTestDisabled) {
            RenderSystem.disableDepthTest();
        }
        if (currentDepthMask != depthMask) {
            RenderSystem.depthMask(currentDepthMask);
        }
        if (shader != null) {
            RenderSystem.setShader(() -> shader);
        }
    }
}

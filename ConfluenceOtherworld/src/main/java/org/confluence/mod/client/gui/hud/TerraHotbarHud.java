package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.common.menu.TerraInventoryActions;

import static org.confluence.mod.common.menu.TerraInventoryLayout.*;

/// 泰拉风格快捷栏：贴在屏幕左上角，格子带编号，上方显示选中物品的名字。
/// 位置与泰拉背包第一行完全一致，打开背包时背包从快捷栏“向下展开”。
public class TerraHotbarHud implements LayeredDraw.Layer {
    public static final ResourceLocation SLOTS = Confluence.asResource("textures/gui/container/terra_inventory.png");
    public static final int SLOTS_WIDTH = 256;
    public static final int SLOTS_HEIGHT = 64;
    /// 经验条紧贴快捷栏下方，宽度与快捷栏相同（9 格）
    public static final int XP_BAR_Y = SCREEN_TOP + INV_Y + CELL;
    public static final int XP_BAR_WIDTH = 9 * CELL;

    /// 原版状态效果图标在 y=1 起画；左上角模式下整体下移到快捷栏（和经验条）下方——泰拉的增益图标也在快捷栏下面
    public static int effectsOffsetY() {
        return org.confluence.mod.client.ClientConfigs.terrariaExperienceBar ? XP_BAR_Y + 16 : SCREEN_TOP + INV_Y + CELL + GAP - 1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || player.isSpectator() || !org.confluence.mod.client.ClientConfigs.terrariaInterface) return;
        if (org.confluence.mod.client.gui.container.TerraScreenMovement.appliesTo(minecraft.screen)) return; // 界面自己会画这一行
        Inventory inventory = player.getInventory();
        Font font = minecraft.font;
        int left = SCREEN_MARGIN;
        int top = SCREEN_TOP;

        RenderSystem.enableBlend();
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            int x = left + inventoryX(i);
            int y = top + inventoryY(0);
            ItemStack stack = inventory.getItem(i);
            guiGraphics.blit(SLOTS, x - 1, y - 1, TerraInventoryActions.isFavorite(stack) ? 72 : 0, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            if (i == inventory.selected) {
                guiGraphics.blit(SLOTS, x - 1, y - 1, 0, 18, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            }
            guiGraphics.renderItem(player, stack, x, y, i + 1);
            guiGraphics.renderItemDecorations(font, stack, x, y);
            renderSlotNumber(guiGraphics, font, i, x, y);
        }

        // 副手（泰拉没有，原版有）：快捷栏右侧
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.isEmpty()) {
            int x = left + inventoryX(Inventory.getSelectionSize()) + GAP;
            int y = top + inventoryY(0);
            guiGraphics.blit(SLOTS, x - 1, y - 1, 18, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            guiGraphics.renderItem(player, offhand, x, y, 0);
            guiGraphics.renderItemDecorations(font, offhand, x, y);
        }
        RenderSystem.disableBlend();

        if (org.confluence.mod.client.ClientConfigs.terrariaExperienceBar && minecraft.gameMode != null && minecraft.gameMode.hasExperience()) {
            renderExperience(guiGraphics, font, player, left + INV_X, top + INV_Y + CELL);
        }

        ItemStack selected = inventory.getSelected();
        if (!selected.isEmpty()) {
            Component name = Component.empty().append(selected.getHoverName()).withStyle(selected.getRarity().color());
            guiGraphics.drawString(font, name, left + INV_X, top - 10, 0xFFFFFF, true);
        }
    }

    /// 泰拉风格经验条：与饱食度条同一套配色（深色边框 + 底色 + 顶部高光），等级用白字阴影写在条下方，
    /// 和“生命/魔力”标签一致，不再使用原版的绿色描边数字。
    private static final int XP_BAR_HEIGHT = 5;

    private static void renderExperience(GuiGraphics guiGraphics, Font font, LocalPlayer player, int x, int y) {
        int right = x + XP_BAR_WIDTH;
        guiGraphics.fill(x, y, right, y + XP_BAR_HEIGHT, 0xFF2A2233);
        guiGraphics.fill(x + 1, y + 1, right - 1, y + XP_BAR_HEIGHT - 1, 0xFF4A3F57);
        int inner = XP_BAR_WIDTH - 2;
        int filled = Math.round(inner * Math.clamp(player.experienceProgress, 0.0F, 1.0F));
        if (filled > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + filled, y + XP_BAR_HEIGHT - 1, 0xFF4FA82A);
            guiGraphics.fill(x + 1, y + 1, x + 1 + filled, y + 2, 0xFF9BE35C);
        }
        if (player.experienceLevel > 0) {
            Component level = Component.translatable("gui.confluence.level", player.experienceLevel);
            guiGraphics.drawString(font, level, x, y + XP_BAR_HEIGHT + 2, 0xFFFFFF, true);
        }
    }

    /// 泰拉的格子编号：1..9（第 10 格为 0，这里只有 9 格）。画在物品之上（物品的 z 约为 150–250）
    public static void renderSlotNumber(GuiGraphics guiGraphics, Font font, int index, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 1, y + 1, 300);
        guiGraphics.pose().scale(0.75F, 0.75F, 1.0F);
        guiGraphics.drawString(font, String.valueOf((index + 1) % 10), 0, 0, 0xFFFFFF, true);
        guiGraphics.pose().popPose();
    }
}

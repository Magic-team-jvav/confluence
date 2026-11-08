package org.confluence.mod.client.gui.hud;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.client.ClientConfigs;

/// 泰拉界面下坐骑（马、猪、骆驼……）的血量：原版在底部快捷栏上方画一排心，泰拉界面没有底部快捷栏，
/// 改成右上角饱食度条下面的一条细条（同样 5 像素高），左边一个缩小的原版“坐骑心”图标。
public class TerraMountHud implements LayeredDraw.Layer {
    private static final int BAR_WIDTH = 60;
    private static final int BAR_HEIGHT = 5;
    private static final int ICON_SIZE = 7;
    /// 生命值下面：饱食度条（2 + 5）之后
    private static final int BELOW_HEALTH = 9;

    public static boolean active() {
        return ClientConfigs.terrariaInterface && mount() != null;
    }

    /// 信息饰品文字要让出的高度
    public static int height() {
        return active() ? BAR_HEIGHT + 2 : 0;
    }

    private static LivingEntity mount() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return null;
        return minecraft.player.getVehicle() instanceof LivingEntity living && living.showVehicleHealth() ? living : null;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!ClientConfigs.terrariaInterface || minecraft.options.hideGui) return;
        LivingEntity mount = mount();
        if (mount == null) return;
        int right = guiGraphics.guiWidth() - 128 + ClientConfigs.healthOffsetX + 100;
        int y = TerraStyleHealthHud.lastLegacyBottom + BELOW_HEALTH;
        int barX = right - BAR_WIDTH;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(barX - ICON_SIZE - 2, y - 1, 0.0F);
        guiGraphics.pose().scale(ICON_SIZE / 9.0F, ICON_SIZE / 9.0F, 1.0F);
        guiGraphics.blitSprite(ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full"), 0, 0, 9, 9);
        guiGraphics.pose().popPose();
        guiGraphics.fill(barX, y, right, y + BAR_HEIGHT, 0xFF2A2233);
        guiGraphics.fill(barX + 1, y + 1, right - 1, y + BAR_HEIGHT - 1, 0xFF4A3F57);
        int inner = BAR_WIDTH - 2;
        int filled = Math.round(inner * Math.clamp(mount.getHealth() / mount.getMaxHealth(), 0.0F, 1.0F));
        if (filled > 0) {
            guiGraphics.fill(barX + 1, y + 1, barX + 1 + filled, y + BAR_HEIGHT - 1, 0xFFB83838);
            guiGraphics.fill(barX + 1, y + 1, barX + 1 + filled, y + 2, 0xFFE86A6A);
        }
    }
}

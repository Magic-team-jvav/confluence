package org.confluence.mod.client.gui.hud;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.common.init.TCItems;

/// 泰拉界面下的信息饰品文字（手表、天气收音机、深度计……）：
/// 一行一个，左边是饰品小图标，白字带阴影、没有灰色底；排在右上角生命值（和饱食度条）下面，
/// 右边与心对齐，像泰拉里那样挨着魔力星那一列。原来 TerraCurio 的 info_hud 在泰拉界面下不画。
public class TerraInfoHud implements LayeredDraw.Layer {
    public static final ResourceLocation TERRA_CURIO_LAYER = ResourceLocation.fromNamespaceAndPath("terra_curio", "info_hud");
    private static final int ICON = 7;
    private static final int ICON_GAP = 3;
    private static final int LINE_HEIGHT = 10;
    /// 生命值下面：饱食度条（2 + 5）再空一点
    private static final int BELOW_HEALTH = 12;
    private static ResourceLocation[] icons;

    public static boolean active() {
        return ClientConfigs.terrariaInterface && ClientConfigs.terraStyleHealth;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!active() || minecraft.options.hideGui || minecraft.getDebugOverlay().showDebugScreen() || minecraft.player == null) return;
        Int2ObjectMap<Component> information = InformationHandler.getInformation();
        if (information.isEmpty()) return;
        if (icons == null) {
            icons = TCItems.FULL_INFO.stream().map(TooltipComponentsValue.Storage::texture).toArray(ResourceLocation[]::new);
        }
        Font font = minecraft.font;
        // 与 TerraStyleHealthHud / TerraStyleFoodHud 的右边一致
        int right = guiGraphics.guiWidth() - 128 + ClientConfigs.healthOffsetX + 100;
        int top = TerraStyleHealthHud.lastLegacyBottom + BELOW_HEALTH + TerraMountHud.height(); // 骑着坐骑时让出坐骑血条
        for (Int2ObjectMap.Entry<Component> entry : information.int2ObjectEntrySet()) {
            Component info = entry.getValue();
            int textLeft = right - font.width(info);
            int key = entry.getIntKey();
            if (key >= 0 && key < icons.length) {
                guiGraphics.blit(icons[key], textLeft - ICON_GAP - ICON, top + 1, 0, 0, ICON, ICON, ICON, ICON);
            }
            guiGraphics.drawString(font, info, textLeft, top, 0xFFFFFF, true);
            top += LINE_HEIGHT;
        }
    }
}

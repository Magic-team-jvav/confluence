package org.confluence.mod.client.gui.hud;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.client.ClientConfigs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/// 泰拉风格的拾取文字：捡起物品时在角色头顶（第一人称为准星上方）弹出“名称 (数量)”，
/// 颜色为稀有度颜色，慢慢上升并淡出；短时间内捡到同一种物品会合并数量并重新弹一下。
public class TerraPickupTextHud implements LayeredDraw.Layer {
    private static final long LIFETIME = 2200L;
    private static final long FADE = 600L;
    private static final long MERGE_WINDOW = 1600L;
    private static final long POP = 150L;
    private static final int MAX_ENTRIES = 6;
    /// 第一行离准星的高度（在呼吸气泡上方）
    private static final int ABOVE_CROSSHAIR = 50;
    private static final int LINE_HEIGHT = 11;
    private static final List<Entry> ENTRIES = new ArrayList<>();

    /// 由 ClientPacketListener 的拾取包调用（主线程）
    public static void onPickup(ItemStack picked, int amount) {
        if (!ClientConfigs.terrariaInterface || picked.isEmpty() || amount <= 0) return;
        long now = System.currentTimeMillis();
        for (Entry entry : ENTRIES) {
            if (now - entry.spawn < MERGE_WINDOW && ItemStack.isSameItemSameComponents(entry.stack, picked)) {
                entry.count += amount;
                entry.spawn = now;
                return;
            }
        }
        ENTRIES.add(new Entry(picked.copyWithCount(1), amount, now));
        while (ENTRIES.size() > MAX_ENTRIES) ENTRIES.removeFirst();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (ENTRIES.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        ENTRIES.removeIf(entry -> now - entry.spawn > LIFETIME);
        if (minecraft.options.hideGui || minecraft.player == null || !ClientConfigs.terrariaInterface) return;
        Font font = minecraft.font;
        int centerX = guiGraphics.guiWidth() / 2;
        int baseY = guiGraphics.guiHeight() / 2 - ABOVE_CROSSHAIR;
        // 最新的在最下面，旧的往上排
        int line = 0;
        for (Iterator<Entry> it = ENTRIES.reversed().iterator(); it.hasNext(); line++) {
            Entry entry = it.next();
            long age = now - entry.spawn;
            float rise = Mth.clamp(age / (float) LIFETIME, 0.0F, 1.0F) * 8.0F;
            int alpha = age > LIFETIME - FADE ? (int) (255 * (LIFETIME - age) / (float) FADE) : 255;
            if (alpha < 8) continue;
            float scale = age < POP ? 1.0F + 0.25F * (1.0F - age / (float) POP) : 1.0F;
            Component text = entry.text();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(centerX, baseY - line * LINE_HEIGHT - rise, 0.0F);
            guiGraphics.pose().scale(scale, scale, 1.0F);
            guiGraphics.drawString(font, text, -font.width(text) / 2, 0, (alpha << 24) | 0xFFFFFF, true);
            guiGraphics.pose().popPose();
        }
    }

    private static final class Entry {
        final ItemStack stack;
        int count;
        long spawn;

        Entry(ItemStack stack, int count, long spawn) {
            this.stack = stack;
            this.count = count;
            this.spawn = spawn;
        }

        Component text() {
            MutableComponent name = Component.empty().append(stack.getHoverName());
            if (count > 1) name.append(" (" + count + ")");
            ChatFormatting color = stack.getRarity().color();
            return name.withStyle(color == null ? ChatFormatting.WHITE : color);
        }
    }
}

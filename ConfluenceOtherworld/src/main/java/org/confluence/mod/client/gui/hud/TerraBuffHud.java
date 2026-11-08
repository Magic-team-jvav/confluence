package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.container.TerraChestScreen;

import java.util.ArrayList;
import java.util.List;

import static org.confluence.mod.common.menu.TerraInventoryLayout.*;

/// 泰拉风格的增益栏：快捷栏下方一排带边框的方形图标，图标下写剩余时间（“5 м”“30 с”），
/// 不闪烁、没有原版的大底框。背包打开时由 ExtraInventoryScreen 画在合成列表下方，
/// 在那里悬停显示名称与说明，右键取消增益（减益不能取消，和泰拉一样）。
public class TerraBuffHud implements LayeredDraw.Layer {
    public static final int ICON = 18;
    public static final int CELL_W = 20;
    public static final int CELL_H = 27;
    public static final int PER_ROW = 8;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!ClientConfigs.terrariaInterface) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || player.isSpectator()) return;
        // 泰拉背包与箱子界面自己处理（盖住了快捷栏下方的位置）
        if (org.confluence.mod.client.gui.container.TerraScreenMovement.appliesTo(minecraft.screen)) return;
        render(guiGraphics, minecraft.font, visibleEffects(player), SCREEN_MARGIN + INV_X, TerraHotbarHud.effectsOffsetY());
    }

    /// 需要显示的效果：与原版一致，隐藏 showIcon=false 的；增益在前、减益在后，同类按剩余时间
    public static List<MobEffectInstance> visibleEffects(net.minecraft.world.entity.LivingEntity player) {
        List<MobEffectInstance> list = new ArrayList<>();
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance.showIcon()) list.add(instance);
        }
        list.sort((a, b) -> {
            int ca = a.getEffect().value().getCategory() == MobEffectCategory.HARMFUL ? 1 : 0;
            int cb = b.getEffect().value().getCategory() == MobEffectCategory.HARMFUL ? 1 : 0;
            if (ca != cb) return Integer.compare(ca, cb);
            return a.compareTo(b);
        });
        return list;
    }

    public static void render(GuiGraphics guiGraphics, Font font, List<MobEffectInstance> effects, int left, int top) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableBlend();
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance instance = effects.get(i);
            int x = left + (i % PER_ROW) * CELL_W;
            int y = top + (i / PER_ROW) * CELL_H;
            boolean harmful = instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL;
            // 边框沿用泰拉背包格子：增益普通格，减益特殊格
            guiGraphics.blit(TerraHotbarHud.SLOTS, x, y, harmful ? 18 : 0, 0, ICON, ICON, TerraHotbarHud.SLOTS_WIDTH, TerraHotbarHud.SLOTS_HEIGHT);
            TextureAtlasSprite sprite = minecraft.getMobEffectTextures().get(instance.getEffect());
            guiGraphics.blit(x + 1, y + 1, 0, 16, 16, sprite);
            Component time = timeText(instance);
            if (time != null) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(x + ICON / 2.0F - font.width(time) * 0.375F, y + ICON + 1, 0.0F);
                guiGraphics.pose().scale(0.75F, 0.75F, 1.0F);
                guiGraphics.drawString(font, time, 0, 0, 0xFFFFFF, true);
                guiGraphics.pose().popPose();
            }
        }
        RenderSystem.disableBlend();
    }

    /// 泰拉的写法：不足 1 分钟写秒，不足 1 小时写分钟，否则写小时；无限时长不写
    public static Component timeText(MobEffectInstance instance) {
        if (instance.isInfiniteDuration()) return null;
        int seconds = Math.max(0, instance.getDuration() / 20);
        if (seconds < 60) return Component.translatable("gui.confluence.buff_time.seconds", seconds);
        if (seconds < 3600) return Component.translatable("gui.confluence.buff_time.minutes", seconds / 60);
        return Component.translatable("gui.confluence.buff_time.hours", seconds / 3600);
    }

    /// 鼠标下的效果序号，没有则 -1
    public static int indexAt(int count, int left, int top, double mouseX, double mouseY) {
        for (int i = 0; i < count; i++) {
            int x = left + (i % PER_ROW) * CELL_W;
            int y = top + (i / PER_ROW) * CELL_H;
            if (mouseX >= x && mouseX < x + ICON && mouseY >= y && mouseY < y + ICON) return i;
        }
        return -1;
    }

    /// 悬停提示：名称（含等级）、剩余时间、模组的效果说明（tooltip.effect.<命名空间>.<路径>.N），可取消时加一行提示
    public static List<Component> tooltip(MobEffectInstance instance) {
        List<Component> lines = new ArrayList<>();
        MobEffect effect = instance.getEffect().value();
        boolean harmful = effect.getCategory() == MobEffectCategory.HARMFUL;
        MutableComponent name = Component.translatable(effect.getDescriptionId());
        if (instance.getAmplifier() > 0) {
            name.append(" ").append(Component.translatable("enchantment.level." + (instance.getAmplifier() + 1)));
        }
        lines.add(name.withStyle(harmful ? ChatFormatting.RED : ChatFormatting.WHITE));
        String base = "tooltip." + effect.getDescriptionId();
        for (int i = 0; i < 8 && I18n.exists(base + "." + i); i++) {
            lines.add(Component.translatable(base + "." + i).withStyle(ChatFormatting.GRAY));
        }
        Component time = timeText(instance);
        if (time != null) lines.add(time.copy().withStyle(ChatFormatting.GRAY));
        if (!harmful) lines.add(Component.translatable("gui.confluence.buff_cancel").withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }

}

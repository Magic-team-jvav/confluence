package org.confluence.mod.client.gui.hud;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

/// 泰拉风格的怪物血条：受伤（血量不满）的生物脚下出现一条小血条，颜色随血量从绿到黄到红。
/// 泰拉是 2D 的，血条总画在精灵下方；这里在世界渲染时把生物脚下的点投影到屏幕上，再在 HUD 里画，
/// 这样血条不会被地面挡住。看不见的生物（隔着墙）不画。Boss 用底部的 Boss 血条，这里不画。
public class TerraMobHealthBars implements LayeredDraw.Layer {
    private static final double MAX_DISTANCE = 32.0;
    private static final int MAX_BARS = 48;
    private static final int BAR_WIDTH = 28;
    private static final int BAR_HEIGHT = 4;
    /// 脚下再往下多少屏幕像素
    private static final int BELOW_FEET = 3;
    private static final List<Bar> BARS = new ArrayList<>();

    private record Bar(float x, float y, float fraction, float scale) {}

    /// AFTER_ENTITIES 阶段调用：算出本帧每条血条的屏幕位置
    public static void collect(RenderLevelStageEvent event) {
        BARS.clear();
        if (!ClientConfigs.terrariaInterface) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.options.hideGui) return;
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Matrix4f matrix = new Matrix4f(event.getProjectionMatrix()).mul(event.getModelViewMatrix());
        float guiWidth = minecraft.getWindow().getGuiScaledWidth();
        float guiHeight = minecraft.getWindow().getGuiScaledHeight();
        Vector4f point = new Vector4f();
        for (Entity entity : minecraft.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || !shouldShow(living, player)) continue;
            Vec3 pos = living.getPosition(partialTick);
            double distance = pos.distanceTo(cameraPos);
            if (distance > MAX_DISTANCE || !event.getFrustum().isVisible(living.getBoundingBoxForCulling())) continue;
            if (!player.hasLineOfSight(living)) continue;
            point.set((float) (pos.x - cameraPos.x), (float) (pos.y - cameraPos.y), (float) (pos.z - cameraPos.z), 1.0F);
            matrix.transform(point);
            if (point.w <= 0.05F) continue; // 在镜头后面
            float screenX = (point.x / point.w + 1.0F) * 0.5F * guiWidth;
            float screenY = (1.0F - point.y / point.w) * 0.5F * guiHeight;
            if (screenX < -BAR_WIDTH || screenX > guiWidth + BAR_WIDTH || screenY < -BAR_HEIGHT || screenY > guiHeight + BAR_HEIGHT) continue;
            float fraction = Mth.clamp(living.getHealth() / living.getMaxHealth(), 0.0F, 1.0F);
            // 远处的血条稍微小一点，近处保持原大小
            float scale = (float) Mth.clampedMap(distance, 8.0, MAX_DISTANCE, 1.0, 0.6);
            BARS.add(new Bar(screenX, screenY, fraction, scale));
            if (BARS.size() >= MAX_BARS) break;
        }
    }

    private static boolean shouldShow(LivingEntity living, LocalPlayer player) {
        if (living == player || !living.isAlive() || living.isInvisibleTo(player)) return false;
        if (living instanceof Player || living instanceof ArmorStand) return false;
        if (living instanceof BaseBoss || living.getType().is(Tags.EntityTypes.BOSSES)) return false;
        return living.getHealth() < living.getMaxHealth();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (BARS.isEmpty() || !ClientConfigs.terrariaInterface || Minecraft.getInstance().options.hideGui) return;
        for (Bar bar : BARS) {
            int width = Math.max(10, Math.round(BAR_WIDTH * bar.scale));
            int left = Math.round(bar.x - width / 2.0F);
            int top = Math.round(bar.y) + BELOW_FEET;
            int fill = Math.max(1, Math.round((width - 2) * bar.fraction));
            guiGraphics.fill(left, top, left + width, top + BAR_HEIGHT, 0xC0201818); // 外框
            guiGraphics.fill(left + 1, top + 1, left + width - 1, top + BAR_HEIGHT - 1, 0xC0503A3A); // 空的部分
            int color = color(bar.fraction);
            guiGraphics.fill(left + 1, top + 1, left + 1 + fill, top + BAR_HEIGHT - 1, color);
            guiGraphics.fill(left + 1, top + 1, left + 1 + fill, top + 2, brighter(color)); // 上沿高光
        }
    }

    /// 满血绿 → 半血黄 → 残血红（与泰拉一致）
    private static int color(float fraction) {
        int red;
        int green;
        if (fraction > 0.5F) {
            red = Math.round(255 * (1.0F - fraction) * 2.0F);
            green = 220;
        } else {
            red = 255;
            green = Math.round(220 * fraction * 2.0F);
        }
        return 0xFF000000 | Mth.clamp(red, 0, 255) << 16 | Mth.clamp(green, 0, 255) << 8 | 0x20;
    }

    private static int brighter(int color) {
        int r = Math.min(255, ((color >> 16) & 255) + 60);
        int g = Math.min(255, ((color >> 8) & 255) + 60);
        int b = Math.min(255, (color & 255) + 60);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}

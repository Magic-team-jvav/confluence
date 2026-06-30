package org.confluence.mod.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/// 玩家手部位置计算工具。
/// NeoForge 1.21.1 无现成 API，通过肩膀基准 + 视线方向 + 侧向偏移推算。
public final class HandPositionUtils {
    private HandPositionUtils() {}

    /// 获取玩家主手掌（物品实际持握点）的世界坐标。
    /// 手掌在肩膀下方约 12px(0.75格) + 前方 0.4格物品渲染偏移处。
    /// 此位置是 ItemInHandRenderer 中物品的实际渲染原点。
    public static Vec3 getPalmPosition(Player player, float partialTick) {
        Vec3 shoulder = player.getEyePosition(partialTick).add(0.0, -0.25, 0.0);
        Vec3 look = player.getViewVector(partialTick);
        Vec3 side = look.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
        double sideOffset = rightHanded ? 0.35 : -0.35;

        Vec3 palmPos = shoulder
                .add(0.0, -0.75, 0.0)
                .add(look.scale(0.4))
                .add(side.scale(sideOffset));

        if (player.isShiftKeyDown()) {
            palmPos = palmPos.add(look.scale(-0.12)).add(0.0, -0.2, 0.0);
        }

        float attack = player.getAttackAnim(partialTick);
        if (attack > 0.0f) {
            float arc = Mth.sin(attack * Mth.PI);
            palmPos = palmPos.add(look.scale(arc * 0.45)).add(0.0, arc * 0.15, 0.0);
        }

        if (player.isUsingItem()) {
            int duration = player.getUseItem().getUseDuration(player);
            float progress = duration > 0 ? player.getUseItemRemainingTicks() / (float) duration : 0f;
            float t = 1.0f - progress;
            palmPos = palmPos.add(look.scale(t * 0.25)).add(0.0, t * 0.12, 0.0);
        }

        return palmPos;
    }

    /// 通用手部（手掌）位置，自动处理 Player 检查。
    public static Vec3 getHandPosition(LivingEntity owner) {
        if (owner instanceof Player player) {
            return getPalmPosition(player, 1.0F);
        }
        return new Vec3(owner.getX(), owner.getEyeY() - 0.65, owner.getZ());
    }
}

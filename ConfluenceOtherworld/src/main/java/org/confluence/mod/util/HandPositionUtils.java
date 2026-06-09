package org.confluence.mod.util;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 玩家手部位置计算工具。
 * NeoForge 1.21.1 无现成 API，通过肩膀基准 + 身体朝向(yBodyRot) + 侧向偏移推算。
 */
public final class HandPositionUtils {
    private HandPositionUtils() {}

    /**
     * 获取玩家主手掌（物品实际持握点）的世界坐标，等效于 {@code getPalmPosition(player, partialTick, null)}。
     */
    public static Vec3 getPalmPosition(@Nullable Player player, float partialTick) {
        return getPalmPosition(player, partialTick, null);
    }

    /**
     * 获取玩家主手掌（物品实际持握点）的世界坐标。
     * 所有身体偏移均乘以 {@code player.getScale()} 以适配缩放后的玩家模型。
     * 此位置是 ItemInHandRenderer 中物品的实际渲染原点。
     *
     * @param player      玩家
     * @param partialTick 部分 tick
     * @param localOffset 手掌局部坐标系下的位移 (侧向, 上下, 前后)，可为 {@code null}
     * @return 世界坐标
     */
    public static Vec3 getPalmPosition(@Nullable Player player, float partialTick, @Nullable Vec3 localOffset) {
        // 基于身体朝向计算水平前向和侧向，忽略视角俯仰
        float yBodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        float yawRad = yBodyRot * Mth.DEG_TO_RAD;
        float cosYaw = Mth.cos(yawRad);
        float sinYaw = Mth.sin(yawRad);
        // MC 坐标系：forward = (-sinYaw, 0, cosYaw), side = forward × UP
        Vec3 forward = new Vec3(-sinYaw, 0, cosYaw);
        Vec3 side = forward.cross(new Vec3(0.0, 1.0, 0.0));

        float scale = player.getScale();
        Vec3 shoulder = player.getEyePosition(partialTick).add(0.0, -0.25 * scale, 0.0);
        boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
        double sideOffset = (rightHanded ? 0.35 : -0.35) * scale;

        Vec3 palmPos = shoulder
                .add(0.0, -0.75 * scale, 0.0)
                .add(forward.scale(0.4 * scale))
                .add(side.scale(sideOffset));

        if (player.isShiftKeyDown()) {
            palmPos = palmPos.add(forward.scale(-0.12 * scale)).add(0.0, -0.1875F * scale, 0.0);
        }

        if (player.isUsingItem()) {
            int duration = player.getUseItem().getUseDuration(player);
            float progress = duration > 0 ? player.getUseItemRemainingTicks() / (float) duration : 0f;
            float t = 1.0f - progress;
            palmPos = palmPos.add(forward.scale(t * 0.25 * scale)).add(0.0, t * 0.12 * scale, 0.0);
        }

        if (localOffset != null) {
            palmPos = palmPos
                    .add(side.scale(localOffset.x))
                    .add(0.0, localOffset.y, 0.0)
                    .add(forward.scale(localOffset.z));
        }
        return palmPos;
    }

    /**
     * 通用手部（手掌）位置，自动处理 Player 检查。
     */
    public static Vec3 getHandPosition(LivingEntity owner) {
        if (owner instanceof Player player) {
            return getPalmPosition(player, 1.0F);
        }
        return new Vec3(owner.getX(), owner.getEyeY() - 0.65, owner.getZ());
    }
}

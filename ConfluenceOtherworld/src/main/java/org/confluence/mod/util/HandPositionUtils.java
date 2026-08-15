package org.confluence.mod.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 玩家手部位置计算工具。
 *
 * <p>这里按肩膀基准、身体朝向和侧向偏移估算物品实际持握点，供链锤、悠悠球等
 * 需要从玩家手部发出连接线或弹幕的武器复用。身体朝向比视线方向更稳定，不会因为玩家
 * 抬头/低头导致连接线从头顶或脚下穿出。</p>
 */
public final class HandPositionUtils {
    private HandPositionUtils() {}

    /**
     * 获取玩家主手掌，也就是物品实际持握点的世界坐标。
     */
    public static Vec3 getPalmPosition(Player player, float partialTick) {
        return getPalmPosition(player, partialTick, null);
    }

    /**
     * 获取玩家主手掌，也就是物品实际持握点的世界坐标。
     *
     * @param player      玩家
     * @param partialTick 部分 tick
     * @param localOffset 手掌局部偏移，按“侧向、上下、前后”解释；传入 {@code null} 表示不追加偏移
     */
    public static Vec3 getPalmPosition(Player player, float partialTick, Vec3 localOffset) {
        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        float yawRad = bodyYaw * Mth.DEG_TO_RAD;
        float cosYaw = Mth.cos(yawRad);
        float sinYaw = Mth.sin(yawRad);
        Vec3 forward = new Vec3(-sinYaw, 0.0, cosYaw);
        Vec3 side = forward.cross(new Vec3(0.0, 1.0, 0.0));

        Vec3 shoulder = player.getEyePosition(partialTick).add(0.0, -0.25, 0.0);
        boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
        double sideOffset = rightHanded ? 0.35 : -0.35;

        Vec3 palmPos = shoulder
                .add(0.0, -0.75, 0.0)
                .add(forward.scale(0.4))
                .add(side.scale(sideOffset));

        if (player.isShiftKeyDown()) {
            palmPos = palmPos.add(forward.scale(-0.12)).add(0.0, -0.1875, 0.0);
        }

        if (player.isUsingItem()) {
            int duration = player.getUseItem().getUseDuration(player);
            float progress = duration > 0 ? player.getUseItemRemainingTicks() / (float) duration : 0f;
            float t = 1.0f - progress;
            palmPos = palmPos.add(forward.scale(t * 0.25)).add(0.0, t * 0.12, 0.0);
        }

        if (localOffset != null) {
            palmPos = palmPos
                    .add(side.scale(localOffset.x))
                    .add(0.0, localOffset.y, 0.0)
                    .add(forward.scale(localOffset.z));
        }

        return palmPos;
    }

    /** 获取任意活体实体的手部近似位置；玩家使用更精确的主手位置，其余实体退回眼部下方。 */
    public static Vec3 getHandPosition(LivingEntity owner) {
        if (owner instanceof Player player) {
            return getPalmPosition(player, 1.0F);
        }
        return new Vec3(owner.getX(), owner.getEyeY() - 0.65, owner.getZ());
    }
}

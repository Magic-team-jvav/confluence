package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VampireBatSummon extends SummonInstance {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 35.0F;
    private int attackTick;
    private int attackDuration;
    private final Set<UUID> hitEntities = new HashSet<>();
    private boolean returning;
    private boolean attackingThisTick;
    private Vec3 attackStart = Vec3.ZERO;
    private Vec3 attackEnd = Vec3.ZERO;
    private Vec3 arcSide = Vec3.ZERO;

    public VampireBatSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        super(Confluence.asResource("vampire_bat"), owner, slotCost, stats, pose);
        addGoal(1, new SummonGoal<VampireBatSummon>(this) {
            @Override
            public boolean canUse() {
                return true;
            }

            @Override
            public void tick() {
                moveBat();
            }
        });
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), owner().position(), 62.5);
    }

    private Vec3 perch() {
        double angle = (order() % 7 - 3) * Math.PI / 8.0;
        Vec3 side = Vec3.directionFromRotation(0.0F, owner().yBodyRot + 90.0F);
        return owner().position().add(side.scale(Math.sin(angle) * 1.5)).add(0.0, 2.0 + Math.cos(angle) * 0.6, 0.0);
    }

    @Override
    protected boolean usesOwnerRecovery() {
        // 往返轨迹自行回到主人身边，通用传送会截断一次攻击。
        return false;
    }

    @Override
    protected void onTargetChanged(LivingEntity previousTarget, LivingEntity currentTarget) {
        if (previousTarget != currentTarget && attackDuration > 0) {
            attackDuration = 0;
            returning = true;
        }
    }

    private void moveBat() {
        attackingThisTick = false;
        Vec3 home = perch();
        if (attackDuration > 0 && (target() == null || !target().isAlive())) {
            attackDuration = 0;
            returning = true;
        }
        if (attackDuration == 0) {
            Vec3 offset = home.subtract(position());
            moveTo(position().add(offset.normalize().scale(Math.min(offset.length(), 1.2))));
            if (offset.lengthSqr() > 0.25) return;
            returning = false;
            if (target() == null) return;
            attackStart = home;
            attackEnd = targetPosition();
            Vec3 direction = attackEnd.subtract(home);
            arcSide = direction.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
            if (arcSide.lengthSqr() < 0.01) arcSide = new Vec3(1.0, 0.0, 0.0);
            arcSide = arcSide.scale(Math.min(1.5, direction.length() * 0.15));
            // Wiki 的 66 个 Terraria tick 对应 22 个 Minecraft tick。
            attackDuration = 22;
            attackTick = 0;
            hitEntities.clear();
        } else {
            attackingThisTick = true;
            double phase = (double) ++attackTick / attackDuration;
            attackStart = home;
            attackEnd = targetPosition();
            Vec3 point = attackStart.lerp(attackEnd, (1.0 - Math.cos(phase * Math.PI * 2.0)) * 0.5)
                    .add(arcSide.scale(Math.sin(phase * Math.PI * 2.0)));
            moveTo(point);
            if (attackTick >= attackDuration) {
                attackDuration = 0;
                returning = true;
            }
        }
    }

    private void moveTo(Vec3 point) {
        Vec3 movement = point.subtract(position());
        float yaw = currentPose().yaw();
        if (movement.horizontalDistanceSqr() > 0.0001) {
            float desired = (float) Math.toDegrees(Math.atan2(-movement.x, movement.z));
            yaw += Mth.clamp(Mth.wrapDegrees(desired - yaw), -25.0F, 25.0F);
        }
        advanceTo(new SummonPose(point, yaw, 0.0F, 0.0F));
    }

    @Override
    protected void afterPathAdvance(SummonPose older, SummonPose previous, SummonPose current) {
        if (!attackingThisTick || target() == null) return;
        Vec3 movement = current.position().subtract(previous.position());
        int steps = Math.max(1, (int) Math.ceil(movement.length() / 0.2));
        for (int i = 0; i <= steps; i++) {
            Vec3 point = previous.position().add(movement.scale((double) i / steps));
            hurtTouchingTargets(AABB.ofSize(point, 0.8, 0.6, 0.8), 62.5, 1.0F, hitEntities);
        }
    }

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(attackDuration == 0 || returning, SummonAnimation.FLY, 0, 0, 0.0F, 1.0F, 1.0F);
    }
}

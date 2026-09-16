package org.confluence.mod.common.summon.flying;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

import java.util.List;

public final class DeadlySphereSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 40.0F;
    private static final ResourceLocation[] FORMS = {
            Confluence.asResource("deadly_sphere_spikes"), Confluence.asResource("deadly_sphere_flames"), Confluence.asResource("deadly_sphere_blade")
    };
    private int form;
    private int attacks;
    private int dashTicks;
    private int pauseTicks;
    private boolean dashingThisTick;
    private Vec3 dashVelocity = Vec3.ZERO;

    public DeadlySphereSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose pose) {
        super(Confluence.asResource("deadly_sphere"), owner, slotCost, stats, pose, 0.8, 0.8);
        addGoal(1, new SummonGoal<DeadlySphereSummon>(this) {
            @Override
            public boolean canUse() {
                return true;
            }

            @Override
            public void tick() {
                moveSphere();
            }
        });
    }

    @Override
    protected LivingEntity findTarget() {
        if (position().distanceToSqr(owner().position()) > 93.75 * 93.75) return null;
        LivingEntity target = SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 50.0);
        return target != null && SummonTargetCache.hasVisibleTarget(owner().serverLevel(), owner(), position(), 50.0, target)
                ? target : null;
    }

    @Override
    protected double ownerRecoveryDistanceSqr() {
        return 93.75 * 93.75;
    }

    private void moveSphere() {
        dashingThisTick = false;
        if (target() == null) {
            dashTicks = 0;
            pauseTicks = 0;
            moveToward(owner().position().add(0.0, 2.0, 0.0), 0.15, 1.2);
        } else if (pauseTicks > 0) {
            pauseTicks--;
            moveBy(velocity().scale(0.5), currentPose().yaw(), currentPose().pitch());
        } else if (dashTicks > 0) {
            dashTicks--;
            dashingThisTick = true;
            moveBy(dashVelocity);
            if (dashTicks == 0) {
                pauseTicks = 3;
                if (++attacks == 3) {
                    attacks = 0;
                    form = (form + 1) % FORMS.length;
                    pauseTicks += 5;
                }
            }
        } else {
            dashVelocity = targetPosition().subtract(position()).normalize().scale(1.0);
            // 当前刻已经执行了第一步，余下计数必须扣除这一刻。
            dashTicks = (form == 0 ? 7 : 5) - 1;
            dashingThisTick = true;
            moveBy(dashVelocity);
        }
    }

    @Override
    protected void afterPathAdvance(SummonPose older, SummonPose previous, SummonPose current) {
        if (target() == null || !dashingThisTick) return;
        Vec3 movement = current.position().subtract(previous.position());
        int steps = Math.max(1, (int) Math.ceil(movement.length() / 0.2));
        for (int i = 0; i <= steps; i++) {
            Vec3 point = previous.position().add(movement.scale((double) i / steps)).add(0.0, 0.4, 0.0);
            hurtTouchingTargets(AABB.ofSize(point, 0.8, 0.8, 0.8), 50.0, 1.0F);
        }
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource source) {
        return 4;
    }

    @Override
    public SummonVisualState visualState() {
        return dashingThisTick ? new SummonVisualState(false, SummonAnimation.MELEE_ATTACK, 0, 0, 0.0F, 1.0F, 1.0F) : SummonVisualState.DEFAULT;
    }

    @Override
    public void appendRenderParts(List<SummonRenderPart> output) {
        output.add(new SummonRenderPart(uuid(), FORMS[form], currentPose(), visualState(), order()));
    }
}

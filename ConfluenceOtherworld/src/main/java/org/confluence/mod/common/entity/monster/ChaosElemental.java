package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/// 通过奔跑、短距离瞬移和接触攻击追逐玩家的混沌精。
///
/// 混沌精属于战士型敌怪，不会施放法师弹幕。它只在奔跑受阻时推进传送周期；取得新目标、
/// 受到玩家或其召唤物造成的伤害、或接触玩家都会延后传送；
/// 环境与机关伤害不会干扰传送计时。
public final class ChaosElemental extends BaseWarriorMonster {
    private static final int MIN_TELEPORT_DISTANCE = 4;
    private static final int MAX_TELEPORT_DISTANCE = 20;
    private static final int TELEPORT_ATTEMPTS = 16;
    private static final int TELEPORT_DELAY = 100;
    private static final int TELEPORT_SUPPRESSION = 60;
    private int teleportCooldown;
    @Nullable
    private LivingEntity previousTarget;

    public ChaosElemental(EntityType<? extends ChaosElemental> type, Level level) {
        super(type, level, 0.0, LandAnimationProfile.WALK_ONLY, LandSoundProfile.ROUTINE, 1.2, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) {
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            teleportCooldown = 0;
            previousTarget = null;
            return;
        }
        if (target != previousTarget) {
            previousTarget = target;
            teleportCooldown = TELEPORT_DELAY;
            return;
        }
        if (teleportCooldown > 0) {
            // 顺畅奔跑时不推进瞬移计时；只有受阻或转身减速时才逐步接近瞬移。
            if (getDeltaMovement().horizontalDistanceSqr() < 0.01) --teleportCooldown;
            return;
        }
        if (getDeltaMovement().horizontalDistanceSqr() >= 0.01) return;
        teleportCooldown = tryTeleportAround(target) ? TELEPORT_DELAY : 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && source.getEntity() instanceof Player) {
            teleportCooldown = TELEPORT_SUPPRESSION;
        }
        return accepted;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean damaged = super.doHurtTarget(target);
        if (damaged && target instanceof Player) {
            teleportCooldown = TELEPORT_SUPPRESSION;
        }
        return damaged;
    }

    private boolean tryTeleportAround(LivingEntity target) {
        BlockPos center = target.blockPosition();
        int minimumDistanceSquared = MIN_TELEPORT_DISTANCE * MIN_TELEPORT_DISTANCE;
        int maximumDistanceSquared = MAX_TELEPORT_DISTANCE * MAX_TELEPORT_DISTANCE;
        for (int attempt = 0; attempt < TELEPORT_ATTEMPTS; ++attempt) {
            int xOffset = random.nextIntBetweenInclusive(-MAX_TELEPORT_DISTANCE, MAX_TELEPORT_DISTANCE);
            int zOffset = random.nextIntBetweenInclusive(-MAX_TELEPORT_DISTANCE, MAX_TELEPORT_DISTANCE);
            int horizontalDistanceSquared = xOffset * xOffset + zOffset * zOffset;
            if (horizontalDistanceSquared < minimumDistanceSquared || horizontalDistanceSquared > maximumDistanceSquared) {
                continue;
            }
            BlockPos candidate = center.offset(xOffset, random.nextIntBetweenInclusive(-5, 5), zOffset);
            Vec3 destination = findStandingPosition(candidate);
            if (destination == null) {
                continue;
            }
            navigation.stop();
            teleportTo(destination.x, destination.y, destination.z);
            playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    @Nullable
    private Vec3 findStandingPosition(BlockPos origin) {
        BlockPos.MutableBlockPos cursor = origin.above(5).mutable();
        for (int offset = 0; offset <= 10; ++offset) {
            BlockPos feet = cursor.immutable();
            if (level().hasChunkAt(feet) && level().getBlockState(feet.below()).isFaceSturdy(level(), feet.below(), Direction.UP) && !level().getFluidState(feet).is(FluidTags.LAVA)) {
                Vec3 destination = Vec3.atBottomCenterOf(feet);
                AABB movedBox = getBoundingBox().move(destination.x - getX(), destination.y - getY(), destination.z - getZ());
                if (level().noCollision(this, movedBox)) {
                    return destination;
                }
            }
            cursor.move(Direction.DOWN);
        }
        return null;
    }
}

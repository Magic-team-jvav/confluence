package org.confluence.mod.common.summon.sword;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonCollision;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonVisualState;

/**
 * 六种材质召唤剑共用的运行实例。
 *
 * <p>这里保留 1.21 侧的追击、斜劈和背部编队行为，材质差异只由 {@link Kind} 负责。
 * 召唤剑属于同一行为分组，多把剑共享连续序号，方便服务端与客户端保持一致的背部排列。</p>
 */
public final class SummonSword extends SummonInstance {
    private static final ResourceLocation GROUP_KEY = Confluence.asResource("summon_sword");
    private static final double SEARCH_RANGE = 40.0;
    private static final AABB ATTACK_BOX = new AABB(-0.75, -0.75, -0.75, 0.75, 0.75, 1.5);
    private final Kind kind;
    private final SwordSlashGoal slashGoal;
    private float damageMultiplier = 1.0F;
    private boolean followingOwner;
    private int spinTicks;

    public SummonSword(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose, Kind kind) {
        super(kind.type(), owner, slotCost, snapshot, initialPose);
        this.kind = kind;
        this.slashGoal = new SwordSlashGoal(this);
        addGoal(0, slashGoal);
        addGoal(1, new SwordAttackGoal(this));
        addGoal(2, new SwordFollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), SEARCH_RANGE);
    }

    @Override
    protected double ownerRecoveryDistanceSqr() {
        return 16.0 * 16.0;
    }

    @Override
    protected void beforeGoalTick() {
        slashGoal.updateCooldown();
        spinTicks = Math.max(0, spinTicks - 1);
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (!hasValidTarget()) {
            return;
        }
        for (SummonCollision.Hit hit : SummonCollision.sweep(owner().level(), previousPreviousPose, previousPose,
                currentPose, ATTACK_BOX, candidate -> candidate == target()
                        || SummonTargetCache.isValidTarget(owner(), candidate, SEARCH_RANGE * 2.0, false))) {
            if (hurtTarget(hit.target(), damageMultiplier)) {
                kind.applyHitEffect(owner(), hit.target());
            }
        }
    }

    boolean hasValidTarget() {
        LivingEntity target = target();
        return target != null && target.isAlive() && !target.isRemoved() && target.level() == owner().level();
    }

    void moveTo(SummonPose pose) {
        setPath("summon_sword_move", java.util.List.of(pose));
    }

    SummonPose aimAt(Vec3 position, Vec3 direction) {
        Vec3 normal = direction.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        if (normal.lengthSqr() < 1.0E-6) {
            normal = new Vec3(1.0, 0.0, 0.0);
        }
        return poseFromAxes(position, direction, normal);
    }

    SummonPose followPose(Vec3 position, Vec3 targetPosition) {
        int sequence = order() + 1;
        Vec3 forward = Vec3.directionFromRotation(0.0F, owner().yBodyRot).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 lookPosition = position.subtract(forward.scale(5.0))
                .add(0.0, -8.0 - (sequence - 1) / 2.0, 0.0)
                .add(position.subtract(targetPosition).scale(20.0));
        Vec3 direction = lookPosition.subtract(position);
        return direction.lengthSqr() < 1.0E-6 ? currentPose() : aimAt(position, direction);
    }

    void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    void setFollowingOwner(boolean followingOwner) {
        this.followingOwner = followingOwner;
    }

    void beginPostSlashSpin() {
        spinTicks = 15;
    }

    @Override
    public SummonVisualState visualState() {
        if (slashGoal.isSlashing()) {
            return new SummonVisualState(followingOwner, SummonAnimation.SLASH, slashGoal.slashTicks(), 10, 0.0F, 1.0F, 1.0F);
        }
        if (spinTicks > 0) {
            return new SummonVisualState(followingOwner, SummonAnimation.SPIN_X, 15 - spinTicks, 15, 360.0F, 1.0F, 1.0F);
        }
        return new SummonVisualState(followingOwner, SummonAnimation.NONE, 0, 0, 0.0F, 1.0F, 1.0F);
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public ResourceLocation groupKey() {
        return GROUP_KEY;
    }

    public enum Kind {
        WOODEN("summon_wooden_sword", 0x714C11), STONE("summon_stone_sword", 0x8E9797),
        IRON("summon_iron_sword", 0xE6F0F3), GOLDEN("summon_golden_sword", 0xE3D529),
        DIAMOND("summon_diamond_sword", 0x17CFC1), NETHERITE("summon_netherite_sword", 0x8136D2);

        private final ResourceLocation type;
        private final int trailColor;

        Kind(String path, int trailColor) {
            this.type = Confluence.asResource(path);
            this.trailColor = trailColor;
        }

        void applyHitEffect(ServerPlayer owner, LivingEntity target) {
            switch (this) {
                case WOODEN -> target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
                case STONE ->
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                case IRON -> owner.heal(0.5F);
                case GOLDEN -> target.setSecondsOnFire(5);
                case DIAMOND ->
                        target.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), 100, 2));
                case NETHERITE ->
                        target.addEffect(new MobEffectInstance(ModEffects.HELLFIRE.get(), 100, 2));
            }
        }

        public ResourceLocation type() {
            return type;
        }

        public int trailColor() {
            return trailColor;
        }
    }
}

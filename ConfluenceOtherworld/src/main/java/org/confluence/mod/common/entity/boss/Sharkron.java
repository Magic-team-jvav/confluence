package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.entity.monster.Shark;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;
import java.util.UUID;

public class Sharkron extends Shark implements BossOwnedEntity {
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swimming");
    private static final int LIFETIME_TICKS = 240;
    private static final int INITIAL_FLIGHT = 11;
    // 在目标侧下方寻找可游达的起跳点，留出向前扑击的距离。
    private static final int APPROACH_DISTANCE = 5, APPROACH_DEPTH = 3, PATH_RETRY_TICKS = 10;
    private static final double LEAP_ALIGNMENT = Math.cos(Math.toRadians(10));
    // 超过可跃击的水面高度时继续水下巡游，避免为碰不到的目标停在起跳点。
    private static final double LEAP_REACH_ABOVE_WATER = 5.0;
    private static final int LEAP_COOLDOWN = 32;
    private static final double LEAP_SPEED = 0.95, LEAP_LIFT = 0.95;
    private DukeFishron owner;
    private UUID ownerId;
    private boolean launched;
    private int nextLeap;
    private boolean enteredWater;
    private int remainingLife = LIFETIME_TICKS;

    public Sharkron(EntityType<? extends Sharkron> type, Level level) {
        super(type, level);
    }

    void launch(DukeFishron owner, Vec3 position, Vec3 velocity) {
        this.owner = owner;
        ownerId = owner.getUUID();
        launched = true;
        setPos(position);
        setNoGravity(true);
        setDeltaMovement(velocity);
        LibEntityUtils.turnEntityTowards(this, velocity, 180, 180);
    }

    @Override
    public DukeFishron getBossOwner() {
        return owner != null && owner.isAlive() ? owner : null;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(-1, new BreachGoal());
    }

    @Override
    protected boolean isValidAquaticTarget(LivingEntity target) {
        return target.isInWaterOrBubble() || !isInWaterOrBubble();
    }

    @Override
    protected boolean flopsOnLand() {
        return !isInWater() && getTarget() == null && (owner == null || owner.getTarget() == null);
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel server) {
            if (remainingLife-- <= 0) {
                discard();
                return;
            }
            if (owner != null && !owner.isAlive()) {
                if (!owner.isRemoved() || owner.getRemovalReason().shouldDestroy()) ownerId = null;
                owner = null;
            }
            if (owner == null && ownerId != null && tickCount % 20 == 0 && server.getEntity(ownerId) instanceof DukeFishron duke && duke.isAlive()) {
                if (duke.trackShark(this)) owner = duke;
                else ownerId = null;
            }
            enteredWater |= isInWater();
            setNoGravity(launched && !enteredWater && tickCount < INITIAL_FLIGHT);
            if (getTarget() == null && owner != null && owner.getTarget() != null && canAttack(owner.getTarget()))
                setTarget(owner.getTarget());
            if (!isInWater() && getDeltaMovement().lengthSqr() > 0.001)
                LibEntityUtils.turnEntityTowards(this, getDeltaMovement(), 15, 10);
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RemainingLife", remainingLife);
        if (ownerId != null) tag.putUUID("FishronOwner", ownerId);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        remainingLife = tag.contains("RemainingLife") ? tag.getInt("RemainingLife") : LIFETIME_TICKS;
        ownerId = tag.hasUUID("FishronOwner") ? tag.getUUID("FishronOwner") : null;
        setNoGravity(false);
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return !isInWater();
    }

    private final class BreachGoal extends Goal {
        private BlockPos launchPoint;
        private Path approach;
        private LivingEntity prey;
        private int retryAt;

        private boolean hasSwimmingTarget() {
            LivingEntity target = getTarget();
            return target != null && target.isAlive() && target.isInWaterOrBubble();
        }

        private BreachGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (hasSwimmingTarget() || tickCount < nextLeap || tickCount < retryAt) return false;
            LivingEntity target = getTarget();
            if (target == null && owner != null) target = owner.getTarget();
            if (target == null)
                target = level().getNearestPlayer(getX(), getY(), getZ(), getAttributeValue(Attributes.FOLLOW_RANGE), true);
            if (target == null || !target.isAlive() || target.isInWaterOrBubble()
                    || target instanceof Player player && (player.isSpectator() || player.isCreative()
                    && (owner == null || !owner.isDeveloperTarget(player)))) return false;
            prey = target;
            if (onGround() && !isInWater()) return true;
            if (!isInWater()) return false;
            retryAt = tickCount + PATH_RETRY_TICKS;
            Vec3 side = position().subtract(target.position()).multiply(1, 0, 1).normalize();
            Vec3 staging = target.position().add(side.scale(APPROACH_DISTANCE));
            BlockPos column = BlockPos.containing(staging);
            if (!level().hasChunkAt(column)) return false;
            BlockPos surface = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, column).below();
            if (!level().getFluidState(surface).is(FluidTags.WATER)
                    || target.getY() > surface.getY() + 1 + LEAP_REACH_ABOVE_WATER) return false;
            launchPoint = surface;
            for (int depth = 1; depth < APPROACH_DEPTH && level().getFluidState(launchPoint.below()).is(FluidTags.WATER); depth++)
                launchPoint = launchPoint.below();
            approach = getNavigation().createPath(launchPoint, 0);
            return approach != null && approach.canReach();
        }

        @Override
        public void start() {
            if (approach != null) getNavigation().moveTo(approach, 1.2);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = prey;
            return target != null && target.isAlive() && !hasSwimmingTarget() && !target.isInWaterOrBubble() && tickCount >= nextLeap && (onGround() && !isInWater() || launchPoint != null
                    && isInWater() && (!getNavigation().isDone() || launchPoint.distToCenterSqr(position()) <= 2.25)
                    && launchPoint.distToCenterSqr(target.getX(), launchPoint.getY(), target.getZ()) < 144);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = prey;
            if (target == null || tickCount < nextLeap) return;
            if ((isInWater() || !onGround()) && (launchPoint == null || launchPoint.distToCenterSqr(position()) > 2.25))
                return;
            getNavigation().stop();
            Vec3 horizontal = target.position().subtract(position()).multiply(1, 0, 1).normalize();
            if (horizontal.lengthSqr() > 0) {
                boolean aligned = Vec3.directionFromRotation(0, yBodyRot).dot(horizontal) >= LEAP_ALIGNMENT;
                faceCombatDirection(horizontal, 20, 0);
                if (!aligned) return;
            }
            setDeltaMovement(horizontal.scale(LEAP_SPEED).add(0, LEAP_LIFT, 0));
            hasImpulse = true;
            nextLeap = tickCount + LEAP_COOLDOWN;
        }

        @Override
        public void stop() {
            getNavigation().stop();
            prey = null;
            approach = null;
            launchPoint = null;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "swim", 0, state -> state.setAndContinue(SWIM)));
    }
}

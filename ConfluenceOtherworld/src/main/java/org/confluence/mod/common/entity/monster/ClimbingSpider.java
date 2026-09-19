package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.EnemyWalkNodeEvaluator;
import org.confluence.mod.common.entity.projectile.SpiderWebSpit;
import org.confluence.mod.common.init.entity.ModEntities;

public class ClimbingSpider extends BaseWarriorMonster {
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(ClimbingSpider.class, EntityDataSerializers.BOOLEAN);

    private final Kind kind;
    private int spitTicks;

    public ClimbingSpider(EntityType<? extends ClimbingSpider> type, Level level, Kind kind) {
        super(type, level, 0.0, kind == Kind.WALL ? LandAnimationProfile.NONE : LandAnimationProfile.WALK_IDLE, LandSoundProfile.ROUTINE, 1.0, true);
        this.kind = kind;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CLIMBING, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level) {
            @Override
            protected PathFinder createPathFinder(int maxVisitedNodes) {
                nodeEvaluator = new EnemyWalkNodeEvaluator();
                return new PathFinder(nodeEvaluator, maxVisitedNodes);
            }
        };
    }

    @Override
    public boolean onClimbable() {
        return entityData.get(CLIMBING);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        entityData.set(CLIMBING, horizontalCollision);
        setSpecialState(CombatState.CLIMBING, kind == Kind.BLACK_RECLUSE && onClimbable());
        LivingEntity target = getTarget();
        if (kind == Kind.WALL || isNoAi() || !isAlive() || target == null || !target.isAlive() || !canAttack(target) || hasEffect(LibEffects.CONFUSED.get())
                || !LibUtils.isAtLeastExpert(level(), blockPosition()) || !getSensing().hasLineOfSight(target))
            return;
        if (++spitTicks < stateParameters(CombatState.SPITTING).attackInterval()) return;
        spitTicks = 0;
        SpiderWebSpit spit = ModEntities.SPIDER_WEB_SPIT.get().create(level());
        if (spit == null) return;
        Vec3 offset = target.getEyePosition().subtract(getEyePosition());
        Vec3 aim = offset.add(0.0, offset.horizontalDistance() * 0.12, 0.0).normalize().scale(0.8);
        spit.configure(this, getEyePosition(), aim, LibUtils.isMaster(level(), blockPosition()) ? 54.0F : 36.0F, 80);
        if (level().addFreshEntity(spit)) playSound(SoundEvents.SPIDER_AMBIENT, 0.8F, 1.4F);
        else spit.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean damaged = super.hurt(source, amount);
        if (damaged && !level().isClientSide) spitTicks -= 7 + random.nextInt(14);
        return damaged;
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 multiplier) {
        if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, multiplier);
    }

    public enum CombatState {CLIMBING, SPITTING}

    public enum Kind {
        WALL, BLACK_RECLUSE, JUNGLE
    }
}

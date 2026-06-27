package org.confluence.mod.common.entity.boss.skeletronprime;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.ISharedFlagControllerHolder;
import org.confluence.terraentity.api.entity.ITrackType;
import org.confluence.terraentity.entity.ai.goal.behavior.BTCommonRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.AngleLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TimeCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.decoration.RepeatUntilNode;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.AttributeModifierAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.FlyTowardTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.LookAtTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.SyncFlagAction;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.util.SharedFlagController;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.registries.track.variant.SimpleTrack;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkeletronPrime extends AbstractTerraBossBase implements ISharedFlagControllerHolder, Boss {


    protected static final EntityDataAccessor<Integer> DATA_SHARE_FLAG = SynchedEntityData.defineId(SkeletronPrime.class, EntityDataSerializers.INT);

    private final SharedFlagController controller;
    public final SharedFlagController.SharedFlag spinFlag;


    List<SkeletronPrimePart> parts;
    public int spinTicks = 0;
    public int spinTransitionTick = 5;

    public SkeletronPrime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        parts = new ArrayList<>();
        this.controller = new SharedFlagController(this.getEntityData(), DATA_SHARE_FLAG);
        this.spinFlag = this.controller.registerFlag();

    }

    @Override
    public void tick() {
        super.tick();
        if(this.isSpinning()) {
            this.spinTicks++;
            this.spinTransitionTick = 5;
        }else if(this.spinTransitionTick > 0) {
            this.spinTicks = 0;
            this.spinTransitionTick--;
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(5, new SkeletronPrimeBT(this));


        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHARE_FLAG, 0);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new FlyingPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
    }

    public void setPart(SkeletronPrimePart part) {
        parts.add(part);
    }

    public boolean isSpinning() {
        return this.controller.getFlag(this.spinFlag);
    }

    @Override
    public SharedFlagController getSharedFlagController() {
        return controller;
    }

    private static class SkeletronPrimeBT extends BTCommonRoot<SkeletronPrime> {

        public SkeletronPrimeBT(SkeletronPrime mob) {
            super(mob);
        }

        @Override
        protected BTNode createStageTrigger() {
            return BTFactory.selector()
                    .addChild(new LookAtTargetAction(this.mob))

                    ;
        }

        @Override
        protected BTNode createAttackBehavior() {
            return BTFactory.selector()
                    .addWithCondition(TimeCondition.isDay(this.mob.level()), BTFactory.sequence()
                            .addChild(new SyncFlagAction<>(this.mob, this.mob.spinFlag, true))
                            .addChild(new AttributeModifierAction.Add(this.mob, LibAttributes.getAttackDamage(), TerraEntity.space("day"), 999, AttributeModifier.Operation.ADD_VALUE))
                            .addChild(BTFactory.withTimer(99999,new FlyTowardTargetAction(mob, 2.0f)))
                    )
                    .addWithCondition(TimeCondition.isNight(this.mob.level()), BTFactory.sequence()
                            .addChild(new SyncFlagAction<>(this.mob, this.mob.spinFlag, false))
                            .addChild(new AttributeModifierAction.Remove(this.mob, LibAttributes.getAttackDamage(), TerraEntity.space("day")))
                            .addChild(BTFactory.withTimer(200, BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ALL)
                                    .addChild(new LerpTrackAction(mob))
                                    .addChild(BTFactory.infinite(BTFactory.sequence()
                                            .addChild(BTFactory.wait(30))
                                            .addChild(new RepeatUntilNode(BTStatus.FAILURE, new AngleLowerThanCondition(mob, Math.PI * 0.3f)))
                                            .addChild(BTFactory.withTimer(20, new SlowdownAction(this.mob, 0.8)))
                                    ))
                            ))
                            .addChild(new SyncFlagAction<>(this.mob, this.mob.spinFlag, true))
                            .addChild(BTFactory.withTimer(50,new FlyTowardTargetAction(mob, 0.8f)))
                            .addChild(new SyncFlagAction<>(this.mob, this.mob.spinFlag, false))

                            .addChild(BTFactory.withTimer(10)))
            ;
        }

        private static class LerpTrackAction extends BTNode {

            Mob mob;
            ITrackType tracker;

            public LerpTrackAction(Mob mob, ITrackType tracker) {
                this.mob = mob;
                this.tracker = tracker;
            }
            public LerpTrackAction(Mob mob) {
                this(mob, new SimpleTrack(Math.PI, 1.1, 0.12, Optional.of(2.5d), 0.3));
            }

            @Override
            public BTStatus execute() {
                LivingEntity target = mob.getTarget();
                if(target == null) {
                    return BTStatus.FAILURE;
                }
                Vec3 motion = this.mob.getDeltaMovement();
                Vec3 dir = target.position().subtract(this.mob.position());
                double angle = TEUtils.angleBetween(motion, dir);

                Vec3 movement = this.tracker.calDeltaMovement(motion, dir, angle);
                if(movement.equals(motion)) {
                    return BTStatus.SUCCESS;
                }
                mob.setDeltaMovement(movement);

                return BTStatus.RUNNING;
            }
        }

        private static class SlowdownAction extends BTNode {
            Mob mob;
            final double efficiency;
            public SlowdownAction(Mob mob, double efficiency) {
                this.mob = mob;
                this.efficiency = efficiency;
            }

            @Override
            public BTStatus execute() {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(efficiency));
//                System.out.println("slowdown: " + mob.tickCount);
                return BTStatus.RUNNING;
            }
        }
    }




    @Override
    public void addSkills() {
    }


    @Override
    public void firstSpawn() {

        if(level() instanceof ServerLevel serverLevel) {
            for(int i=0;i<4;i++) {
                int finalI = i;
                SkeletronPrimePart part = TEUtils.spawnEntity(
                        ()->new SkeletronPrimePart(TEBossEntities.SKELETRON_PRIME_PART.get(),serverLevel, this, finalI) ,
                        serverLevel,
                        position().offsetRandom(this.random, 3));
                this.parts.add(part);
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof SkeletronPrimePart){
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.parts.stream().filter(LivingEntity::isAlive).forEach(Entity::kill);
    }
}

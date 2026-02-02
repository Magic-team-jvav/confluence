package org.confluence.terraentity.entity.boss.thetwins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.api.entity.IAutoLeaveMob;
import org.confluence.terraentity.api.entity.ISharedFlagControllerHolder;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.goal.behavior.BTBossTwoStageRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.Blackboard;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.IBlackboardHolder;
import org.confluence.terraentity.entity.ai.goal.behavior.blackboard.KeyType;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.HealthLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.*;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.entity.util.SharedFlagController;
import org.confluence.terraentity.init.TEParticles;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 魔焰眼
 */
public class Spazmatism extends AbstractTerraBossBase implements ISharedFlagControllerHolder, FlyingAnimal, IBlackboardHolder, OwnableEntity, Boss, IAutoLeaveMob {

    private static final EntityDataAccessor<Integer> DATA_SHARE_FLAG = SynchedEntityData.defineId(Spazmatism.class, EntityDataSerializers.INT);
    private static final RawAnimation move1 = RawAnimation.begin().thenLoop("type_1");
    private static final RawAnimation move2 = RawAnimation.begin().thenLoop("type_2");
    private static final RawAnimation switching = RawAnimation.begin().thenPlay("switching");
    private static final RawAnimation run1 = RawAnimation.begin().thenLoop("type_1_run");
    private static final RawAnimation run2 = RawAnimation.begin().thenLoop("type_2_run");

    private static final float rangeDamageFactor = 1.0f;

    protected SharedFlagController sharedFlagController;
    protected SharedFlagController.SharedFlag move_1_Flag;
    protected SharedFlagController.SharedFlag run_1_Flag;
    protected SharedFlagController.SharedFlag switch_Flag;
    protected SharedFlagController.SharedFlag move_2_Flag;
    protected SharedFlagController.SharedFlag run_2_Flag;

    private final Blackboard blackboard;
    private final SkillParams skillParams;

    public UUID ownerUUID;


    public Spazmatism(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.sharedFlagController = new SharedFlagController(this.entityData, DATA_SHARE_FLAG);
        move_1_Flag = sharedFlagController.registerFlag();
        run_1_Flag = sharedFlagController.registerFlag();
        switch_Flag = sharedFlagController.registerFlag();
        move_2_Flag = sharedFlagController.registerFlag();
        run_2_Flag = sharedFlagController.registerFlag();

        this.blackboard = Blackboard.create();
        this.blackboard.put(KeyType.STAGE, 1);
        // 由于行为树是延迟创建的，所以在goal注册以后再初始化也没问题
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.THE_TWINS_PARAMS).spazmatismParams();
        this.xpReward = skillParams.xpReward;

    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void doLeave() {
        this.setDeltaMovement(0, 5, 0);
    }


    public record SkillParams(int xpReward, float moveSpeed1, float moveSpeed2, float dashSpeed1, float dashSpeed2,
                              int shootCount1, int shootCount2, int shootInterval1, int shootInterval2,
                              int dashCount1, int dashCount2, int dashInterval1, int dashInterval2,
                              int dashDuration1, int dashDuration2,
                              float followDistance) {

        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                Codec.FLOAT.fieldOf("move_speed_1").forGetter(SkillParams::moveSpeed1),
                Codec.FLOAT.fieldOf("move_speed_2").forGetter(SkillParams::moveSpeed2),
                Codec.FLOAT.fieldOf("dash_speed_1").forGetter(SkillParams::dashSpeed1),
                Codec.FLOAT.fieldOf("dash_speed_2").forGetter(SkillParams::dashSpeed2),
                Codec.INT.fieldOf("shoot_count_1").forGetter(SkillParams::shootCount1),
                Codec.INT.fieldOf("shoot_count_2").forGetter(SkillParams::shootCount2),
                Codec.INT.fieldOf("shoot_interval_1").forGetter(SkillParams::shootInterval1),
                Codec.INT.fieldOf("shoot_interval_2").forGetter(SkillParams::shootInterval2),
                Codec.INT.fieldOf("dash_count_1").forGetter(SkillParams::dashCount1),
                Codec.INT.fieldOf("dash_count_2").forGetter(SkillParams::dashCount2),
                Codec.INT.fieldOf("dash_interval_1").forGetter(SkillParams::dashInterval1),
                Codec.INT.fieldOf("dash_interval_2").forGetter(SkillParams::dashInterval2),
                Codec.INT.fieldOf("dash_duration_1").forGetter(SkillParams::dashDuration1),
                Codec.INT.fieldOf("dash_duration_2").forGetter(SkillParams::dashDuration2),
                Codec.FLOAT.fieldOf("follow_distance").forGetter(SkillParams::followDistance)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams() {
            return new SkillParams(1500, 1.5f, 1f, 1f, 2f,
                    5, 33, 20, 3,
                    5, 5, 10, 10,
                    10, 10,
                    7);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHARE_FLAG, 0);

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, this.createBT());
    }

    protected BTRoot createBT(){
        return new SpazmatismBT(this);
    }

    @Override
    protected void registerRandomStrollGoal() {

        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10, 1f));
    }

    @Override
    public boolean isFlying() {
        return true;
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

    @Override
    public SharedFlagController getSharedFlagController() {
        return sharedFlagController;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public Blackboard getBlackboard() {
        return this.blackboard;
    }

    private static class SpazmatismBT extends BTBossTwoStageRoot<Spazmatism> {

        public SpazmatismBT(Spazmatism mob) {
            super(mob);
        }

        @Override
        protected Condition createStageCondition() {
            return new HealthLowerThanCondition(this.mob, 0.5f);
        }

        @Override
        protected SequenceNode switchPre(SequenceNode sequence) {
            return sequence
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "switching", mob.switch_Flag, true))
                    .addChild(BTFactory.wait(10))
                    ;
        }

        @Override
        protected SequenceNode switchPost(SequenceNode sequence) {
            return sequence
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "switching", mob.switch_Flag, false))
                    .addChild(BTFactory.wait(20))
                    ;
        }

        @Override
        protected BTNode createStageOneAttack() {
            return BTFactory.sequence()
                    .addChild(BTFactory.withTimer(50)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance, this.mob.skillParams.moveSpeed1, 1f))
                            .addChild(new LookAtTargetAction(mob))
                    )
                    // 平行射击
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance, this.mob.skillParams.moveSpeed1, 1f))
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount1, BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval1))
                                    .addChild(new IntervalShootAction(mob))
                            ))
                    )
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run1", mob.run_1_Flag, true))
                    // 冲刺
                    .addChild(BTFactory.repeater(this.mob.skillParams.dashCount1, BTFactory.sequence()
                            .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                    .addChild(BTFactory.wait(this.mob.skillParams.dashInterval1))
                                    .addChild(new LookAtTargetAction(mob))
                            )
                            .addChild(BTFactory.withTimer(this.mob.skillParams.dashDuration1, new DashAction(mob, this.mob.skillParams.dashSpeed1)))
                    ))
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run1", mob.run_1_Flag, false))
                    ;
        }

        @Override
        protected BTNode createStageTwoAttack() {
            return BTFactory.sequence()
                    .addChild(BTFactory.withTimer(50)
                            .addChild(new FlyTowardTargetAction(mob, this.mob.skillParams.moveSpeed2))
                            .addChild(new LookAtTargetAction(mob))
                    )
                    // 跟随射击
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new FlyTowardTargetAction(mob, this.mob.skillParams.moveSpeed2))
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount2, BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval2))
                                    .addChild(new ContinueShootAction(mob, 10))
                            ))
                    )
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run2", mob.run_2_Flag, true))
                    // 冲刺
                    .addChild(BTFactory.repeater(this.mob.skillParams.dashCount2, BTFactory.sequence()
                            .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                    .addChild(BTFactory.wait(this.mob.skillParams.dashInterval2))
                                    .addChild(new LookAtTargetAction(mob))
                            )
                            .addChild(BTFactory.withTimer(this.mob.skillParams.dashDuration2, new DashAction(mob, this.mob.skillParams.dashSpeed2)))
                    ))
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run2", mob.run_2_Flag, false))
                    ;
        }

        /**
         * 一阶段远程攻击
         */
        private static class IntervalShootAction extends ShootAction<Spazmatism> {

            public IntervalShootAction(Spazmatism mob) {
                super(mob);
            }

            @Override
            protected void shoot(LivingEntity target) {
                var entity = TEProjectileEntities.FIRE_BOUND_PROJ.get().create(mob.level());
                if (entity != null) {
                    entity.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0f, 1.5f, 10.0f);
                    entity.setOwner(mob);
                    entity.setPos(mob.getX(), mob.getY()  + mob.getBbHeight() * 0.5f, mob.getZ());
                    entity.setDamage((float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * rangeDamageFactor);
                    mob.level().addFreshEntity(entity);
                }
            }
        }

        /**
         * 二阶段远程攻击
         */
        protected static class ContinueShootAction extends ShootAction<Spazmatism> {
            int range;

            public ContinueShootAction(Spazmatism mob, int range) {
                super(mob);
                this.range = range;
            }

            @Override
            protected void shoot(LivingEntity target) {
                Vec3 eyePos = mob.getBoundingBox().getCenter();
                Vec3 dir = mob.getForward().normalize();
                eyePos = eyePos.add(dir);
//                Vec3 end = eyePos.add(dir.scale(range));

                float step = 1f;
                Set<LivingEntity> hurt = new HashSet<>();
                float boxSize = 0.5f;
                for (float i = 0; i < range; i += step) {
                    Vec3 pos = eyePos.add(dir.scale(i));
                    ((ServerLevel) this.mob.level()).sendParticles(TEParticles.FIRE_BOUND.get(), pos.x, pos.y, pos.z, 3, 0.3, 0.3, 0.3, 0.2);
                    AABB box = new AABB(pos.add(-boxSize, -boxSize, -boxSize), pos.add(boxSize, boxSize, boxSize));
                    for (LivingEntity e : mob.level().getEntitiesOfClass(LivingEntity.class, box)) {
                        if (e != mob) {
                            hurt.add(e);
                        }
                    }
                }

                for (LivingEntity e : hurt) {
                    if (this.mob.canAttack(e)) {
                        e.hurt(this.mob.damageSources().inFire(), (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE) * rangeDamageFactor);
                        e.setRemainingFireTicks(100);
                    }
                }

            }
        }

    }


    @Override
    public void addSkills() {
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5,
                state -> state.setAndContinue(this.getStage() == 1 ? move1 : (this.getStage() == 3) ? move2 : switching))
                .triggerableAnim("run1", run1)
                .triggerableAnim("switching", switching)
                .triggerableAnim("run2", run2)
        );

    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof Spazmatism) && super.canAttack(entity);
    }

    @Override
    public boolean shouldShowBossBar() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(this.getOwnerUUID() != null){
            compound.putUUID("ownerUUID", this.getOwnerUUID());
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("ownerUUID")) {
            this.ownerUUID = tag.getUUID("ownerUUID");
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if(level() instanceof ServerLevel serverLevel && this.getOwnerUUID() != null) {
            if(serverLevel.getEntity(ownerUUID) instanceof TheTwins theTwins) {
                theTwins.onPartDie(this);
            }
        }
        super.die(damageSource);
    }

    @Override
    public boolean shouldShowMessage() {
        return false;
    }
}

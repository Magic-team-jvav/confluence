package org.confluence.terraentity.entity.boss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.TaskScheduler;
import org.confluence.terraentity.api.entity.ISharedFlagControllerHolder;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.DistanceLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.LandRandomStrollAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.MoveToTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.SyncFlagAction;
import org.confluence.terraentity.entity.util.SharedFlagController;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

public class Deerclops extends AbstractTerraBossBase implements Boss, ISharedFlagControllerHolder {

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("Walk");
    private static final RawAnimation STAND = RawAnimation.begin().thenLoop("Stand");
    private static final RawAnimation ICE = RawAnimation.begin().thenPlay("Ice");
    private static final RawAnimation ROAR = RawAnimation.begin().thenPlay("Roar");
    private static final RawAnimation ROARING = RawAnimation.begin().thenLoop("Roaring");
    private static final EntityDataAccessor<Integer> DATA_SHARE_FLAG = SynchedEntityData.defineId(Deerclops.class, EntityDataSerializers.INT);
    DeerSharedFlagController sharedFlagController;
    BlockPos destroyChestPos;

    TaskScheduler scheduler;

    SkillParams skillParams;

    int attackDamage;
    int attackRange;
    int rangeDamage;
    int thrownIceCount;

    public int transitionTicks = 20;

    public Deerclops(EntityType<? extends AbstractTerraBossBase> type, Level level) {
        super(type, level);
        this.sharedFlagController = new DeerSharedFlagController(this.entityData, DATA_SHARE_FLAG);
        setNoGravity(false);
        scheduler = new TaskScheduler(0);
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.DEERCLOPS_PARAMS);
        this.attackDamage = skillParams.attackDamage;
        this.attackRange = skillParams.attackRange;
        this.rangeDamage = skillParams.rangeDamage;
        this.thrownIceCount = skillParams.thrownIceCount;
        this.xpReward = skillParams.xpReward;
    }

    public Deerclops(Level level) {
        this(TEBossEntities.DEERCLOPS.get(), level);
    }

    @Override
    public SharedFlagController getSharedFlagController() {
        return sharedFlagController;
    }

    public record SkillParams(int xpReward, int attackDamage, int attackRange, int rangeDamage, int thrownIceCount, int blackHandDamage) {
        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                Codec.INT.fieldOf("attack_damage").forGetter(SkillParams::attackDamage),
                Codec.INT.fieldOf("attack_range").forGetter(SkillParams::attackRange),
                Codec.INT.fieldOf("range_damage").forGetter(SkillParams::rangeDamage),
                Codec.INT.fieldOf("thrown_ice_count").forGetter(SkillParams::thrownIceCount),
                Codec.INT.fieldOf("black_hand_damage").forGetter(SkillParams::blackHandDamage)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams(){
            return new SkillParams(1500,10, 10, 10, 20, 10);
        }
    }


    private static class DeerSharedFlagController extends SharedFlagController {
        SharedFlag attackFlag = this.registerFlag();
        SharedFlag roarFlag = this.registerFlag();
        SharedFlag roaringFlag = this.registerFlag();
        SharedFlag invulnerableFlag = this.registerFlag();

        public DeerSharedFlagController(SynchedEntityData entityData, EntityDataAccessor<Integer> DATA_SHARE_FLAG) {
            super(entityData, DATA_SHARE_FLAG);
        }
        private boolean isAttacking(){
            return this.getFlag(attackFlag);
        }
        private boolean isRoar(){
            return this.getFlag(roarFlag);
        }
        private boolean isRoaring(){
            return this.getFlag(roaringFlag);
        }
    }

    /**
     * 距离目标过远时无敌
     */
    public boolean isFarForInvulnerable(){
        return this.sharedFlagController.getFlag(sharedFlagController.invulnerableFlag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new DeerclposBT(this));

    }

    @Override
    public void tick(){
        super.tick();
        if(!level().isClientSide) {
            this.scheduler.tick(1L);
        }else{
            this.setYBodyRot(this.getYRot());
            if(this.isFarForInvulnerable()) {
                if(this.transitionTicks < 30) {
                    this.transitionTicks++;
                }
            }else{
                if(this.transitionTicks > 0) {
                    this.transitionTicks--;
                }
            }
        }

    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new GroundPathNavigation(this, level){
            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                BlockPos blockpos = pos.below();
                BlockState state = this.level.getBlockState(blockpos);
                return state.isSolidRender(this.level, blockpos) || state.is(Tags.Blocks.GLASS_BLOCKS);
            }
        };
    }

    @Override
    protected void registerRandomStrollGoal(){
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10, 1f));
    }

    @Override
    protected MoveControl createMoveControl() {
        return new MoveControl(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHARE_FLAG, 0);

    }


    private static class WalkToChestGoal extends BTNode {
        Deerclops mob;
        BlockPos targetPos;

        private WalkToChestGoal(Deerclops mob) {
            this.mob = mob;
        }

        @Override
        public BTStatus execute() {
            if(this.mob.getTarget() != null) {
                return BTStatus.FAILURE;
            }

            if(this.targetPos != null) {
                // 当有攻击目标时
                if(!(this.mob.level().getBlockEntity(targetPos) instanceof ChestBlockEntity)) {
                    // 当箱子被销毁
                    this.targetPos = null;
                    return BTStatus.FAILURE;
                }

                // 正在向别的目标移动时，向目标移动
                this.mob.navigation.moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);

                double distance = this.mob.distanceToSqr(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ());
                if(distance <= 20) {
                    mob.destroyChestPos = this.targetPos;
                    this.targetPos = null;
                    return BTStatus.SUCCESS;
                }
                return BTStatus.RUNNING;
            }

            this.targetPos = TEUtils.findNearbyBlockEntity(this.mob.level(), this.mob.blockPosition(), 1,(pos, entity)-> entity instanceof ChestBlockEntity);
            if(this.targetPos != null) {
                return BTStatus.RUNNING;
            }

            return BTStatus.FAILURE;
        }

    }

    private static class DestroyChestGoal extends BTNode {
        Deerclops mob;
        int animTick;
        private DestroyChestGoal(Deerclops mob) {
            this.mob = mob;
        }

        @Override
        public BTStatus execute() {
            if(mob.destroyChestPos == null) {
                return BTStatus.FAILURE;
            }
            animTick++;
            if(animTick == 7) {
                if(this.mob.level().getBlockEntity(this.mob.destroyChestPos) instanceof ChestBlockEntity) {
                    this.mob.level().destroyBlock(this.mob.destroyChestPos, true);
                }
                mob.doMeleeAttack();
            }
            if(animTick > 15) {
                return BTStatus.SUCCESS;
            }
            return BTStatus.RUNNING;
        }

        @Override
        public void start() {
            super.start();
            animTick = 0;
            mob.sharedFlagController.setFlag(mob.sharedFlagController.attackFlag, true);
            mob.triggerAnim("Controller", "Ice");
        }

        @Override
        public void stop() {
            super.stop();
            animTick = 0;
            mob.sharedFlagController.setFlag(mob.sharedFlagController.attackFlag, false);
            this.mob.destroyChestPos = null;
            mob.stopTriggeredAnim("Controller", "Ice");
        }
    }

    private static class DeerclposBT extends BTRoot<Deerclops> {

        public DeerclposBT(Deerclops mob) {
            super(mob);

        }

        @Override
        protected @NotNull BTNode createBehaviorTree() {
            return BTFactory.infinite(BTFactory.selector()
                    .addWithCondition(new TargetExistCondition(mob), BTFactory.sequence()
                            .addChild(BTFactory.withTimer(10, new RoarPre()))
                            .addChild(BTFactory.withTimer(10, new Roaring()))
                            .addChild(BTFactory.condition(new TargetExistCondition(mob), BTFactory.infinite(BTFactory.sequence()
                                    .addChild(BTFactory.selector()
                                            // 距离过远则无敌
                                            .addWithCondition(new DistanceLowerThanCondition(this.mob, 20), new SyncFlagAction<>(this.mob, this.mob.sharedFlagController.invulnerableFlag, false))
                                            .addChild(new SyncFlagAction<>(this.mob, this.mob.sharedFlagController.invulnerableFlag, true))
                                    )
                                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                            .addChild(new MoveToTargetAction(this.mob, 7, 20))
                                            .addChild(BTFactory.wait(30)))
                                    .addChild(BTFactory.condition(new DistanceLowerThanCondition(this.mob, 20),BTFactory.withTimer(15, new IceAttack().setDesc("巨鹿攻击行为集成"))))
                            )))
                    )
                    .addWithCondition(Condition.not(new TargetExistCondition(mob)), BTFactory.infinite(BTFactory.selector()
                            .addChild(BTFactory.sequence()
                                    .addChild(new WalkToChestGoal(mob))
                                    .addChild(new DestroyChestGoal(mob))
                            )
                            .addChild(new LandRandomStrollAction(mob, 1.0f, 50))
                    ))
            );
        }

        private class IceAttack extends BTNode {

            int tick = 0;
            @Override
            public void start() {
                super.start();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.attackFlag, true);
                mob.navigation.stop();
                mob.triggerAnim("Controller", "Ice");
                tick = 0;
            }

            @Override
            public void stop() {
                super.stop();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.attackFlag, false);
                mob.stopTriggeredAnim("Controller", "Ice");
                tick = 0;
            }

            @Override
            public BTStatus execute() {
                tick++;

                if(mob.getTarget() == null) {
                    return BTStatus.SUCCESS;
                }

                if(tick == 12 ) {
                    double horizonDist = mob.position().subtract(mob.getTarget().position()).multiply(1,0,1).length();
                    if(horizonDist >= mob.attackRange) {
                        // 远程攻击
                        for(int i=0;i<mob.thrownIceCount;i++) {
                            var entity = TEProjectileEntities.THROWN_ICE_PROJECTILE.get().create(mob.level());
                            if(entity != null) {
                                entity.setPos(
                                        mob.getX() + mob.getRandom().nextDouble() * 2 - 1,
                                        mob.getY() + mob.getRandom().nextDouble() * 2 + 1,
                                        mob.getZ()+ mob.getRandom().nextDouble() * 2 - 1);
                                entity.setOwner(mob);
                                entity.setDamage(mob.rangeDamage);
                                mob.level().addFreshEntity(entity);
                            }
                        }
                    }else{

                        if(mob.getTarget().getY() - mob.getY() > 5) {
                            // 召唤黑手
                            mob.doBlackHandAttack(mob.getTarget());
                        }else{
                            // 召唤冰刺
                            mob.doMeleeAttack();
                        }
                    }

                }else if(tick < 12) {
                    mob.lookAt(mob.getTarget(), 30, 30);
                    mob.getLookControl().setLookAt(mob.getTarget());

                }

                return BTStatus.RUNNING;
            }
        }


        private class RoarPre extends BTNode {

            @Override
            public void start() {
                super.start();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.roarFlag, true);
                mob.triggerAnim("Controller", "Roar");
            }

            @Override
            public void stop() {
                super.stop();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.roarFlag, false);
                mob.stopTriggeredAnim("Controller", "Roar");
            }

            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }
        }

        private class Roaring extends BTNode {

            @Override
            public void start() {
                super.start();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.roaringFlag, true);
                mob.triggerAnim("Controller", "Roaring");
            }

            @Override
            public void stop() {
                super.stop();
                DeerclposBT.this.mob.sharedFlagController.setFlag(DeerclposBT.this.mob.sharedFlagController.roaringFlag, false);
                mob.stopTriggeredAnim("Controller", "Roaring");
            }

            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }
        }

    }

    protected void doMeleeAttack(){
        for(int i=0;i<attackRange;i++) {
            int finalI = i;
            Vec3 dir = TEUtils.rotToDir(this.getYRot(), this.getXRot()).multiply(1,0,1);
            Vec3 pos = this.position();
            this.scheduler.schedule(()->{
                for(int j=0;j < finalI * 2 + 1;j++){
                    this.createMeleeIcePillar(j-3, Math.max(j, 5), pos, dir);
                }
            }, i );
        }
    }

    protected void doBlackHandAttack(LivingEntity target){
        Vec3 center = target.position();
        for(int i=0;i<4;i++) {
            Vec3 related = TEUtils.sphere(5, this.random.nextFloat() * 6.28f, this.random.nextFloat() * 3.14f);

            Vec3 absolutePos = center.add(related);
            var entity = TEProjectileEntities.SHADOW_HAND.get().create(this.level());
            if(entity != null) {
                entity.setOwner(this);
                entity.setDamage(this.skillParams.blackHandDamage);
                entity.setPos(absolutePos);
                this.level().addFreshEntity(entity);
            }
        }

    }

    protected void createMeleeIcePillar(int i,int horizon,  Vec3 center, Vec3 direction){
        var entity = TEProjectileEntities.ICE_PILLAR.get().create(this.level());
        if(entity != null) {
            Vec3 end = center.add(direction.scale(i)).offsetRandom(this.random, 0.5f);
            Vec3 horizontal = direction.cross(new Vec3(0,1,0)).normalize();
            end = end.add(horizontal.scale((this.random.nextDouble() - 0.5) * horizon));
            entity.setPos(end);
            entity.setOwner(this);
            entity.setDamage(this.attackDamage);
            this.level().addFreshEntity(entity);
        }

    }

    @Override
    public void addSkills() {
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5,
                state -> state.setAndContinue(state.isMoving() ? WALK : STAND))
                .triggerableAnim("Ice", ICE)
                .triggerableAnim("Roar", ROAR)
                .triggerableAnim("Roaring", ROARING)
        );

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 距离过远不可被攻击
        if(this.isFarForInvulnerable()) {
            return !TEUtils.isPassInvulnerableDamageSource(source, damageSources());
        }
        return super.hurt(source, amount);
    }
}

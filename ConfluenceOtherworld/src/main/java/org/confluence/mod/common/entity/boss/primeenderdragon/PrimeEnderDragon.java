package org.confluence.mod.common.entity.boss.primeenderdragon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.LibAttributes;
import org.confluence.terraentity.entity.ai.goal.behavior.BTBossTwoStageRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.WeightNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.AngleLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.DistanceLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.decoration.RepeatUntilNode;
import org.confluence.terraentity.entity.ai.goal.behavior.decoration.ReverseNode;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.AnimCtrlAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.ConditionAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.SyncFlagAction;
import org.confluence.terraentity.entity.ai.motion.DragonMovement;
import org.confluence.terraentity.entity.boss.BaseBehaviorTreeMob;
import org.confluence.terraentity.entity.util.SharedFlagController;
import org.confluence.terraentity.utils.CircularArrayBuffer;
import org.confluence.terraentity.utils.Easing;
import org.confluence.terraentity.utils.SmoothFloat;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;


public class PrimeEnderDragon extends BaseBehaviorTreeMob implements FlyingAnimal, Boss {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.new");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation FLY2 = RawAnimation.begin().thenLoop("fly2");
    private static final RawAnimation DOWN = RawAnimation.begin().thenPlay("down");
    private static final RawAnimation DOWN2 = RawAnimation.begin().thenPlay("down2");
    private static final RawAnimation SKILL1 = RawAnimation.begin().thenPlay("skill1");

    private final SharedFlagController.SharedFlag landFlag = sharedFlagController.registerFlag();
    private final SharedFlagController.SharedFlag laserFlag = sharedFlagController.registerFlag();

    private final PrimeEnderDragonPart[] subEntities;
    public final PrimeEnderDragonPart head;

    private final PrimeEnderDragonPart body;
    private final PrimeEnderDragonPart tail1;
    private final PrimeEnderDragonPart tail2;
    private final PrimeEnderDragonPart tail3;
    private final PrimeEnderDragonPart wing1;
    private final PrimeEnderDragonPart wing2;

    private static final float _turnSpeedBase = 0.7f;
    private static final float _turnSpeedInertia = 0.2f;

    private final CircularArrayBuffer<double[]> positionsBuffer;
    private final SmoothFloat laser = new SmoothFloat(20, 20);

    protected DragonMovement movement;

    public PrimeEnderDragon(EntityType<? extends PrimeEnderDragon> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.movement = new DragonMovement(this, _turnSpeedBase, true);
        this.movement.offsetY = 2;
        this.positionsBuffer = new CircularArrayBuffer<>(new double[64][6]);

        this.head = new PrimeEnderDragonPart(this, 2.0F, 2.0F);
        this.body = new PrimeEnderDragonPart(this, 6.0F, 3.0F);
        this.tail1 = new PrimeEnderDragonPart(this, 2.0F, 2.0F);
        this.tail2 = new PrimeEnderDragonPart(this, 2.0F, 2.0F);
        this.tail3 = new PrimeEnderDragonPart(this, 2.0F, 2.0F);
        this.wing1 = new PrimeEnderDragonPart(this, 7.0F, 2.0F);
        this.wing2 = new PrimeEnderDragonPart(this, 7.0F, 2.0F);
        this.subEntities = new PrimeEnderDragonPart[]{this.head, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);

        // todo：for debug
        this.getAttribute(LibAttributes.getAttackDamage()).setBaseValue(0.1);
    }


    @Override
    public void setId(int id) {
        super.setId(id);
        for(int i = 0; i < this.subEntities.length; ++i) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return this.movement.shouldMove? 0 : super.getDefaultGravity();
    }

    @Override
    protected void registerRandomStrollGoal() {
//        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 10, 1f));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

    }

    @Override
    public void addSkills() {}

    @Override
    public boolean isFlying() {
        return false;
    }

    protected BTRoot<PrimeEnderDragon> createBT(){
        return new PrimeEnderDragonBT(this);
    }


    private static class PrimeEnderDragonBT extends BTBossTwoStageRoot<PrimeEnderDragon> {

        public PrimeEnderDragonBT(PrimeEnderDragon mob) {
            super(mob);
        }

        @Override
        protected BTNode createWonderBehavior() {
            return BTFactory.sequence()
                    .addChild(new RandomStrollAction())
                    .addChild(new ParallelNode(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ALL)
                            .addChild(BTFactory.waitRandom(100, 200))
                            .addChild(new RepeatUntilNode(BTStatus.SUCCESS, new ConditionAction(new DistanceToTargetPosLowerThanCondition(10))))
                    );
        }

        @Override
        protected Condition createStageCondition() {
            return ()->false;
        }

        @Override
        protected SequenceNode switchPre(SequenceNode sequence) {
            return BTFactory.sequence();
        }

        @Override
        protected SequenceNode switchPost(SequenceNode sequence) {
            return BTFactory.sequence();
        }

        @Override
        protected BTNode createStageOneAttack() {
            return BTFactory.sequence()
                    .addChild(BTFactory.wait(20))
                    // 技能一：冲向目标
                    .addChild(BTFactory.sequence()
                            .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ALL)
                                    // 每隔一段时间重新定位目标位置
                                    .addChild(BTFactory.infinite(BTFactory.sequence()
                                            .addChild(BTFactory.wait(20))
                                            .addChild(new DashToTargetAction())
                                    ))
                                    // 直到满足条件后退出技能一
                                    .addChild(BTFactory.sequence()
                                            .addChild(new RepeatUntilNode(BTStatus.SUCCESS, new ConditionAction(
                                                    Condition.and(new AngleLowerThanCondition(mob, Math.PI / 4), new DistanceLowerThanCondition(mob, 12)))))
                                    )
                                    .addChild(BTFactory.infinite(BTFactory.sequence()
                                            .addChild(new ConditionAction(
                                                    Condition.and(new AngleLowerThanCondition(mob, Math.PI / 6), new DistanceLowerThanCondition(mob, 20)))
                                            )
                                            // // 技能三：激光攻击, 概率发射激光
                                            .addChild(new WeightNode()
                                                    .addChild(5, new SyncFlagAction<>(mob, mob.laserFlag, true))
                                                    .addChild(1, BTFactory.wait(0))
                                            )
                                            .addChild(BTFactory.wait(20))
                                    ))

                            )
                            // 惯性冲刺一段时间
                            .addChild(new SetTurnSpeedAction(_turnSpeedInertia))
                            .addChild(BTFactory.wait(20))
                            .addChild(new SetTurnSpeedAction(_turnSpeedBase))
                            .addChild(new RandomStrollAction())
                            .addChild(BTFactory.wait(20))
                            .addChild(new SyncFlagAction<>(mob, mob.laserFlag, false))
                    )

                    // 技能四: 着陆并进行横扫
                    .addChild(new WeightNode()
                            .addChild(10, BTFactory.sequence()

                                .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                        .addChild(new ReverseNode(BTFactory.wait(100)))
                                        .addChild(new RepeatUntilNode(BTStatus.SUCCESS, new ConditionAction(new DistanceToTargetPosLowerThanCondition(10))))
                                        .addChild(BTFactory.infinite(BTFactory.sequence()
                                                .addChild(BTFactory.wait(20))
                                                .addChild(new DashToTargetAction())
                                        ))
                                )
                                .addChild(new SetFlyingAction(false))
                                .addChild(new AnimCtrlAction<>(this.mob, "Controller", "land", this.mob.landFlag, true))
                                    // TODO 横扫
                                .addChild(BTFactory.wait(50))
                                .addChild(new AnimCtrlAction<>(this.mob, "Controller", "land", this.mob.landFlag, false))
                                .addChild(new SetFlyingAction(true)))
                            .addChild(1, BTFactory.wait(20))
                    )

                    .addChild(BTFactory.repeater(2, BTFactory.sequence()
                            .addChild(this.createWonderBehavior())
                            // TODO 技能二：发射龙息弹
                            .addChild(new ShootDragonFireAction())
                    ))


                    ;
        }

        @Override
        protected BTNode createStageTwoAttack() {
            return BTFactory.sequence();
        }

        private class DashToTargetAction extends BTNode {
            @Override
            public BTStatus execute() {
                if(PrimeEnderDragonBT.this.mob.getTarget() != null) {
                    PrimeEnderDragonBT.this.mob.movement.targetPos = PrimeEnderDragonBT.this.mob.getTarget().position();
                    return BTStatus.SUCCESS;
                }

                return BTStatus.FAILURE;
            }
        }

        private class ShootDragonFireAction extends BTNode {
            @Override
            public BTStatus execute() {
                if(PrimeEnderDragonBT.this.mob.getTarget() != null) {
                    var proj = EntityType.DRAGON_FIREBALL.create(PrimeEnderDragonBT.this.mob.level());
                    if(proj != null) {
                        proj.setOwner(PrimeEnderDragonBT.this.mob);
                        proj.moveTo(PrimeEnderDragonBT.this.mob.head.position());
                        Vec3 targetPos = PrimeEnderDragonBT.this.mob.getTarget().getBoundingBox().getCenter();
                        proj.shoot(targetPos.x - proj.getX(), targetPos.y - proj.getY(), targetPos.z - proj.getZ(), 1, 0);
                        PrimeEnderDragonBT.this.mob.level().addFreshEntity(proj);
                    }
                    return BTStatus.SUCCESS;
                }
                return BTStatus.FAILURE;
            }
        }

        private class RandomStrollAction extends BTNode {
            @Override
            public BTStatus execute() {
                PrimeEnderDragonBT.this.mob.movement.targetPos = AirRandomPos.getPosTowards(PrimeEnderDragonBT.this.mob, 100, 30, 5, PrimeEnderDragonBT.this.mob.blockPosition().getBottomCenter(), Mth.PI * 0.1f);
                return BTStatus.SUCCESS;
            }
        }

        private class SetTurnSpeedAction extends BTNode {
            private final float turnSpeed;
            public SetTurnSpeedAction(float turnSpeed) {
                this.turnSpeed = turnSpeed;
            }

            @Override
            public BTStatus execute() {
                PrimeEnderDragonBT.this.mob.movement.turnSpeed = turnSpeed;
                return BTStatus.SUCCESS;
            }
        }

        private class FindLandPosAction extends BTNode {
            @Override
            public BTStatus execute() {
                for(int i=0;i<4;i++) {
                    Vec3 pos = LandRandomPos.getPos(PrimeEnderDragonBT.this.mob, 30, 50);
                    if(pos != null) {
                        PrimeEnderDragonBT.this.mob.movement.targetPos = pos;
                        return BTStatus.SUCCESS;
                    }
                }
                return BTStatus.FAILURE;
            }
        }

        private class SetFlyingAction extends BTNode {
            private final boolean shouldMove;
            public SetFlyingAction(boolean shouldMove) {
                this.shouldMove = shouldMove;
            }
            @Override
            public BTStatus execute() {
                PrimeEnderDragonBT.this.mob.movement.shouldMove = shouldMove;
                PrimeEnderDragonBT.this.mob.setNoGravity(shouldMove);
                PrimeEnderDragonBT.this.mob.noPhysics = shouldMove;
                PrimeEnderDragonBT.this.mob.setDeltaMovement(PrimeEnderDragonBT.this.mob.getDeltaMovement().scale(0.5f));
                return BTStatus.SUCCESS;
            }
        }

        private class DistanceToTargetPosLowerThanCondition implements Condition {

            private final float distance;
            public DistanceToTargetPosLowerThanCondition(float distance) {
                this.distance = distance;
            }
            @Override
            public boolean check() {
                if(PrimeEnderDragonBT.this.mob.movement.targetPos == null) {
                    return false;
                }
                return PrimeEnderDragonBT.this.mob.getBoundingBox().getCenter().distanceToSqr(PrimeEnderDragonBT.this.mob.movement.targetPos) < this.distance * this.distance;
            }
        }

    }



    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isDeadOrDying()) {
            float f7 = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f9 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f10 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.level()
                    .addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f7, this.getY() + 2.0 + (double)f9, this.getZ() + (double)f10, 0.0, 0.0, 0.0);
        } else {
            if (this.isNoAi()) {

            } else {

                this.positionsBuffer.next();
                double[] currentPos = this.positionsBuffer.get();

                currentPos[0] = this.getYRot();
                currentPos[1] = this.getX();
                currentPos[2] = this.getY();
                currentPos[3] = this.getZ();

                if (this.level().isClientSide) {
//                    if (this.lerpSteps > 0) {
//                        this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
//                        this.lerpSteps--;
//                    }

                } else {
                    this.movement.tickAI();
                    this.doLaserAttack();

                }

//                this.laserTick = Mth.clamp(this.laserTick + (this.getFlag(this.laserFlag)? 1 : -1), 0, this.laserPreTick);
                this.laser.update(this.getFlag(this.laserFlag)? 1 : -1);
                this.tickParts();

            }
        }

    }

    public float getLaserRange(float partialTick) {
        return this.laser.get(partialTick, Easing.EASE_IN_QUAD::ease);
    }

    private void doLaserAttack() {
        float range = this.getLaserRange(0.5f);
        if(range <= 3) {
            return;
        }

        var t = this.getLatencyPos(1, 0.5f);
        var to = this.getLatencyPos(4, 0.5f);
        Vec3 dir = new Vec3(t[1] - to[1], t[2] - to[2], t[3] - to[3]);
        Vec3 start = this.head.getBoundingBox().getCenter();
        Vec3 end = start.add(dir);
        var livings = TEUtils.getAABBAngleTargets(start, end, this.level(), this, range, 20, e->{
            return e.distanceToSqr(start) <= range * range;
        });
        for(var living : livings) {
            living.hurt(this.damageSources().magic(), 5);
//            living.addDeltaMovement(new Vec3(0,2,0));
            living.setRemainingFireTicks(100);
        }

    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        for(var part : this.subEntities) {
            part.refreshDimensions();
        }

    }

    private void tickParts() {
        Vec3[] odlPos = new Vec3[this.subEntities.length];

        for(int j = 0; j < this.subEntities.length; ++j) {
            odlPos[j] = new Vec3(this.subEntities[j].getX(), this.subEntities[j].getY(), this.subEntities[j].getZ());
        }

        float yaw = (180 + this.getYRot()) * ((float)Math.PI / 180F);
        float yawX = Mth.sin(yaw);
        float yawY = Mth.cos(yaw);
        this.body.tickPart(yawX * 0.5F, 2.0F, -yawY * 0.5F);
        this.wing1.tickPart(yawY * 4.5F, 4.0F, yawX * 4.5F);
        this.wing2.tickPart(yawY * -4.5F, 4.0F, yawX * -4.5F);

        float offsetY = (float)(this.getLatencyPos(5, 1.0F)[2] - this.getLatencyPos(10, 1.0F)[2]) * 10.0F * ((float)Math.PI / 180F);
        float offsetYX = Mth.cos(offsetY);
        float offsetYZ = Mth.sin(offsetY);

        float headYawX = Mth.sin(yaw  - this.movement.yRotA * 0.01F);
        float headYawY = Mth.cos(yaw  - this.movement.yRotA * 0.01F);
        float offsetHeadY = this.getHeadYOffset();
        this.head.tickPart(headYawX * 4.5F * offsetYX, 2 + offsetHeadY + offsetYZ * 6.5F, -headYawY * 4.5F * offsetYX);

        double[] adouble = this.getLatencyPos(5, 1.0F);

        for(int k = 0; k < 3; ++k) {
            PrimeEnderDragonPart enderdragonpart = null;
            if (k == 0) {
                enderdragonpart = this.tail1;
            }

            if (k == 1) {
                enderdragonpart = this.tail2;
            }

            if (k == 2) {
                enderdragonpart = this.tail3;
            }

            double[] adouble1 = this.getLatencyPos(7 + k * 2, 1.0F);
            float yawTail = (float) ((this.getYRot() + Mth.wrapDegrees(adouble1[0] - adouble[0]) ) * ((float)Math.PI / 180F));
            float yawXTail = Mth.sin(yawTail);
            float yawYTail = Mth.cos(yawTail);
            float factor = 1.5F;
            float lengthOffset = (float)(k + 3) * 2.0F;
            enderdragonpart.tickPart(
                    (yawX * factor + yawXTail * lengthOffset) * offsetYX,
                    adouble1[2] - adouble[2] - (lengthOffset + 1.5F) * offsetYZ + 3F,
                    -(yawY * factor + yawYTail * lengthOffset) * offsetYX);
        }

        for(int l = 0; l < this.subEntities.length; ++l) {
            this.subEntities[l].xo = odlPos[l].x;
            this.subEntities[l].yo = odlPos[l].y;
            this.subEntities[l].zo = odlPos[l].z;
            this.subEntities[l].xOld = odlPos[l].x;
            this.subEntities[l].yOld = odlPos[l].y;
            this.subEntities[l].zOld = odlPos[l].z;
        }
    }

    private float getHeadYOffset() {
        if (false) {
            return -1.0F;
        } else {
            double[] yO = this.getLatencyPos(5, 1.0F);
            double[] y = this.getLatencyPos(0, 1.0F);
            return (float)(y[2] - yO[2]);
        }
    }

    // 改自末影龙
    public double[] getLatencyPos(int bufferIndexOffset, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        double[] pi = this.positionsBuffer.get(bufferIndexOffset);
        double[] pj = this.positionsBuffer.get(bufferIndexOffset + 1);

        // yaw
        double[] adouble = new double[6];
        double d0 = pi[0];
        double d1 = Mth.wrapDegrees(pj[0] - d0);
        adouble[0] = d0 + d1 * (double)partialTicks;

        // x
        d0 = pi[1];
        d1 = pj[1] - d0;
        adouble[1] = d0 + d1 * (double)partialTicks;

        // y
        d0 = pi[2];
        d1 = pj[2] - d0;
        adouble[2] = d0 + d1 * (double)partialTicks;

        // z
        d0 = pi[3];
        d1 = pj[3] - d0;
        adouble[3] = d0 + d1 * (double)partialTicks;
//
//        // 计算XZ平面法向量
//        d0 = this.positions[i][4];
//        d1 = this.positions[j][4] - d0;
//        adouble[4] = d0 + d1 * (double)partialTicks;
//
//        d0 = this.positions[i][5];
//        d1 = this.positions[j][5] - d0;
//        adouble[5] = d0 + d1 * (double)partialTicks;


//        adouble[3] = Mth.lerp(partialTicks, this.positions[i][2], this.positions[j][2]);

        return adouble;
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 20, state->{
            return state.setAndContinue(FLY);
        }).triggerableAnim("land", DOWN));

    }


    public boolean hurt(PrimeEnderDragonPart part, DamageSource source, float damage) {


        if (part != this.head) {
            damage = damage / 4.0F + Math.min(damage, 1.0F);
        }

        if (damage < 0.01F) {
            return false;
        } else {
            if (source.getEntity() instanceof Player || source.is(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)) {
                float f = this.getHealth();
                this.reallyHurt(source, damage);

            }

            return true;
        }

    }

    protected boolean reallyHurt(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?> @NotNull [] getParts() {
        return this.subEntities;
    }

//    @Override
//    public void checkDespawn() {
//    }
    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

}

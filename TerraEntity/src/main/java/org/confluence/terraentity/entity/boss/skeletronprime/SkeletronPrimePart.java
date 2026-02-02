package org.confluence.terraentity.entity.boss.skeletronprime;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.TargetExistCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.DashAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.LookAtTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.ShootAction;
import org.confluence.terraentity.entity.ai.motion.DashComponent;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SkeletronPrimePart extends AbstractMonster implements GeoEntity, OwnableEntity {

    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(SkeletronPrimePart.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_PART_TYPE = SynchedEntityData.defineId(SkeletronPrimePart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(SkeletronPrimePart.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SkeletronPrime owner;
    int partType;


    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(10);
    }

    public SkeletronPrimePart(EntityType<? extends Monster> entityType, Level level) {
        this(entityType, level, null, 0);
    }

    public SkeletronPrimePart(EntityType<? extends Monster> entityType, Level level, SkeletronPrime owner, int partType) {
        super(entityType, level);
        this.owner = owner;
        this.partType = partType;
        this.entityData.set(DATA_PART_TYPE, partType);
        if(owner != null) {
            this.entityData.set(DATA_OWNER_ID, owner.getId());
        }
        this.noPhysics = true;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new SkeletronPrimePartBT(this));


    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    private static class SkeletronPrimePartBT extends BTRoot<SkeletronPrimePart> {


        static Vec3[] pinnedPos = {
                new Vec3(-1, 1,  0), // 左上，激光
                new Vec3(-1, -1, 0), // 左下，锯子
                new Vec3(1,  -1, 0), // 右下，钳子
                new Vec3(1,  1,  0)  // 右上，加农炮
         };
        public SkeletronPrimePartBT(SkeletronPrimePart mob) {
            super(mob);
        }

        @Override
        protected @NotNull BTNode createBehaviorTree() {


            return BTFactory.infinite(BTFactory.selector()
                    // 游走
                    .addWithCondition(Condition.not(new TargetExistCondition(mob)), this.createWanderBehavior())
                    // 攻击
                    .addWithCondition(new TargetExistCondition(mob), this.createAttackBehavior())

            );
        }

        private BTNode createWanderBehavior() {
            BTNode followBT = this.pinBehavior(1, 6);
            return BTFactory.sequence()
                       .addChild(followBT);
        }

        private FollowOwnerAction pinBehavior(float speed, float distance, FollowOwnerAction.LookType lookType){
            return new FollowOwnerAction(this.mob, pinnedPos[this.mob.partType].scale(distance), this.mob.owner,  speed, ()->this.mob.owner, lookType);
        }

        private FollowOwnerAction pinBehavior(float speed, float distance){
            return new FollowOwnerAction(this.mob, pinnedPos[this.mob.partType].scale(distance), this.mob.owner,  speed, ()->this.mob.owner, FollowOwnerAction.LookType.OWNER_FRONT);
        }

        private BTNode createAttackBehavior() {
            if(this.mob.partType == 0) {
                return BTFactory.selector()
                        .addWithCondition(()->!this.mob.owner.isSpinning(), BTFactory.selector()
                                .addWithCondition(()->this.mob.distanceTo(this.mob.owner) > 15, BTFactory.withTimer(30,
                                        this.pinBehavior(1.2f, 6, FollowOwnerAction.LookType.MOVE_BACK)
                                ))
                                .addChild(BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(30, new LookAtTargetAction(this.mob))
                                                .addChild(BTFactory.infinite(this.pinBehavior(0.6f, 6)))
                                        )
                                        .addChild(new LaserShootAction(this.mob))
                                )
                        )
                        .addWithCondition(()->this.mob.owner.isSpinning(), BTFactory.sequence()
                                .addChild(BTFactory.withTimer(10, new LookAtTargetAction(this.mob))
                                        .addChild(BTFactory.infinite(this.pinBehavior(0.6f, 6)))
                                )
                                .addChild(new LaserShootAction(this.mob))
                        );
            }else if(this.mob.partType == 3) {
                return BTFactory.selector()
                        .addWithCondition(()->!this.mob.owner.isSpinning(), BTFactory.selector()
                                .addWithCondition(()->this.mob.distanceTo(this.mob.owner) > 15, BTFactory.withTimer(30,
                                        this.pinBehavior(1f, 7, FollowOwnerAction.LookType.MOVE_BACK)
                                ))
                                .addChild(BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(30, new LookAtSkyBehavior(this.mob))
                                                .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 7, FollowOwnerAction.LookType.SKY)))
                                        )
                                        .addChild(new CannonShootAction(this.mob))
                                )
                        )
                        .addWithCondition(()->this.mob.owner.isSpinning(), BTFactory.sequence()
                                .addChild(BTFactory.withTimer(10, new LookAtSkyBehavior(this.mob))
                                        .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 7, FollowOwnerAction.LookType.SKY)))
                                )
                                .addChild(new CannonShootAction(this.mob))
                        );
            }else if(this.mob.partType == 2) {
                return BTFactory.selector()
                        .addWithCondition(()->!this.mob.owner.isSpinning(), BTFactory.selector()
                                .addWithCondition(()->this.mob.distanceTo(this.mob.owner) > 30, BTFactory.withTimer(30,
                                        this.pinBehavior(1.5f, 5, FollowOwnerAction.LookType.MOVE_BACK)
                                ))
                                .addChild(BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(30, new LookAtTargetAction(this.mob))
                                                .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 5)))
                                        )
                                        .addChild(BTFactory.repeater(2, BTFactory.sequence()
                                                .addChild(BTFactory.withTimer(5, new LookAtTargetAction(this.mob)))
                                                .addChild(BTFactory.withTimer(10, new DashAction(mob, 2.0f)))
                                                .addChild(BTFactory.withTimer(30, this.pinBehavior(1f, 2, FollowOwnerAction.LookType.MOVE_BACK)))
                                        ))
                                )
                        )
                        .addWithCondition(()->this.mob.owner.isSpinning(), BTFactory.sequence()
                                .addChild(BTFactory.withTimer(10, new LookAtTargetAction(this.mob))
                                        .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 5)))
                                )
                                .addChild(BTFactory.repeater(3, BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(5, new LookAtTargetAction(this.mob)))
                                        .addChild(BTFactory.withTimer(10, new DashAction(mob, 2.5f)))
                                        .addChild(BTFactory.withTimer(10, this.pinBehavior(1.5f, 2, FollowOwnerAction.LookType.MOVE_BACK)))
                                ))

                        );
            }else if(this.mob.partType == 1) {
                return BTFactory.selector()
                        .addWithCondition(()->!this.mob.owner.isSpinning(), BTFactory.selector()
                                .addWithCondition(()->this.mob.distanceTo(this.mob.owner) > 30, BTFactory.withTimer(30,
                                        this.pinBehavior(1.5f, 5, FollowOwnerAction.LookType.MOVE_BACK)
                                ))
                                .addChild(BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(30, new LookAtTargetAction(this.mob))
                                                .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 5)))
                                        )
                                        .addChild(BTFactory.repeater(2, BTFactory.sequence()
                                                .addChild(BTFactory.withTimer(5, new LookAtTargetAction(this.mob)))
                                                .addChild(BTFactory.withTimer(10, new DashAction(mob, 1.0f)))
                                                .addChild(BTFactory.withTimer(30, this.pinBehavior(0.3f, 2, FollowOwnerAction.LookType.MOVE_BACK)))
                                        ))
                                )
                        )
                        .addWithCondition(()->this.mob.owner.isSpinning(), BTFactory.sequence()
                                .addChild(BTFactory.withTimer(15, new LookAtTargetAction(this.mob))
                                        .addChild(BTFactory.infinite(this.pinBehavior(0.4f, 5)))
                                )
                                .addChild(BTFactory.repeater(2, BTFactory.sequence()
                                        .addChild(BTFactory.withTimer(5, new LookAtTargetAction(this.mob)))
                                        .addChild(BTFactory.withTimer(10, new DashAction(mob, 1.5f)))
                                        .addChild(BTFactory.withTimer(10, this.pinBehavior(0.5f, 2, FollowOwnerAction.LookType.MOVE_BACK)))
                                ))

                        );
            }
            return BTFactory.sequence()


                    ;
        }

        private static class LaserShootAction extends ShootAction<SkeletronPrimePart> {

            public LaserShootAction(SkeletronPrimePart mob) {
                super(mob);
            }

            @Override
            protected void shoot(LivingEntity target) {
                var entity = TEProjectileEntities.TRAIL_PROJECTILE.get().create(mob.level());
                if (entity != null) {
                    entity.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0f, 1.5f, 10.0f);
                    entity.setOwner(mob);
                    entity.setPos(mob.getX(), mob.getY()  + mob.getBbHeight() * 0.5f, mob.getZ());
                    entity.setDamage((float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    mob.level().addFreshEntity(entity);
                }
            }
        }

        private static class CannonShootAction extends ShootAction<SkeletronPrimePart> {

            public CannonShootAction(SkeletronPrimePart mob) {
                super(mob);
            }

            @Override
            protected void shoot(LivingEntity target) {
                // todo 加农炮弹幕
                var entity = EntityType.SNOWBALL.create(mob.level());
                if (entity != null) {
                    entity.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0f, 1.5f, 10.0f);
                    entity.setOwner(mob);
                    entity.setPos(mob.getX(), mob.getY()  + mob.getBbHeight() * 0.5f, mob.getZ());
//                    entity.setDamage((float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    mob.level().addFreshEntity(entity);
                }
            }
        }

        private static class LookAtSkyBehavior extends BTNode {

            Mob mob;
            private LookAtSkyBehavior(Mob mob) {
                this.mob = mob;
            }

            @Override
            public BTStatus execute() {
                Vec3 targetPos = this.mob.position().add(0,10,0);
                this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, targetPos);
                this.mob.getLookControl().setLookAt(targetPos);
                return BTStatus.RUNNING;
            }
        }

        static class FollowOwnerAction extends BTNode {
            Vec3 pinnedPos;
            private final Mob mob;
            private Entity followed;
            private final DashComponent component;
            private final float speed;
            private final Supplier<Entity> ownerSupplier;

            final LookType lookType;

            enum LookType implements BiFunction<Mob, Entity, Vec3> {
                SKY{
                    @Override
                    public Vec3 apply(Mob mob, Entity entity) {
                        return mob.position().add(0,10,0);
                    }
                },
                OWNER {
                    @Override
                    public Vec3 apply(Mob mob, Entity entity) {
                        return entity.position().add(0,1,0);
                    }
                },
                OWNER_FRONT {
                    @Override
                    public Vec3 apply(Mob mob, Entity entity) {
                        return entity.position().add(entity.getLookAngle()
                                .multiply(1,0,1).normalize().scale(10));
                    }
                },
                MOVE_BACK {
                    @Override
                    public Vec3 apply(Mob mob, Entity entity) {
                        return mob.position().add(mob.getDeltaMovement().normalize().scale(20));
                    }
                }


            }

            FollowOwnerAction(Mob mob, Vec3 pinnedPos, Entity followed, float speed, Supplier<Entity> ownerSupplier, LookType lookType) {
                this.mob = mob;
                this.pinnedPos = pinnedPos;
                this.followed = followed;
                this.component = new DashComponent(mob);
                this.speed = speed;
                this.ownerSupplier = ownerSupplier;
                this.lookType = lookType;
            }

            @Override
            public BTStatus execute() {
                if(followed == null) {
                    this.followed = ownerSupplier.get();
                    if(this.followed == null) {
                        return BTStatus.FAILURE;
                    }
                }

                if(this.lookType != null) {
                    Vec3 lookPos = this.lookType.apply(mob, followed);
                    this.mob.getLookControl().setLookAt(lookPos);
                    this.mob.lookAt(EntityAnchorArgument.Anchor.EYES, lookPos);
                }

                Vector3f dir = pinnedPos.toVector3f();
                new Quaternionf()
                        .rotateY(-this.followed.getYHeadRot() * Mth.DEG_TO_RAD)
                        .transform(dir);
                Vec3 targetPos = followed.position().add(new Vec3(dir));
                Vec3 moveDir = targetPos.subtract(mob.position());

                if(moveDir.length() < this.speed) {
                    this.mob.setPos(targetPos);
                    this.mob.setDeltaMovement(Vec3.ZERO);
                    return BTStatus.SUCCESS;
                }
                component.setDirection(moveDir);
                component.uniformMove(this.speed);

                return BTStatus.RUNNING;
            }
        }



    }

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if(this.owner != null) {
            return this.owner.getTarget();
        }
        return super.getTarget();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide) {
            if(tickCount == 1) {
                if(owner == null && entityData.get(DATA_OWNER).isPresent()) {
                    owner = (SkeletronPrime) level().getEntities().get(entityData.get(DATA_OWNER).get());
                    if(owner != null) {
                        owner.setPart(this);
                        entityData.set(DATA_OWNER_ID, owner.getId());
                    }
                }
                if(owner == null) {
                    // 正常情况下不会出现
                    discard();
                }
            }
//            this.setYRot(this.getYHeadRot());
        }else{
            this.setYBodyRot(this.getYHeadRot());
            if(this.owner == null) {
                this.owner = (SkeletronPrime) level().getEntity(entityData.get(DATA_OWNER_ID));
            }
        }

    }

    public void setOwner(SkeletronPrime owner) {
        this.owner = owner;
        entityData.set(DATA_OWNER, Optional.of(owner.getUUID()));
    }

    public int getPartType() {
        return this.entityData.get(DATA_PART_TYPE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
        builder.define(DATA_PART_TYPE, 0);
        builder.define(DATA_OWNER_ID, 0);

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        // 从服务端同步id
//        if(level().isClientSide && key == DATA_OWNER) {
//            this.owner = (SkeletronPrime) this.level().getEntity(this.entityData.get(DATA_OWNER_ID));
//        }

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if(owner != null) {
            compound.putUUID("Owner", owner.getUUID());
        }
        compound.putInt("PartType", partType);

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (level() instanceof ServerLevel) {
            if (tag.contains("Owner")) {
                entityData.set(DATA_OWNER, Optional.of(tag.getUUID("Owner")));
            }
            if(tag.contains("PartType")) {
                entityData.set(DATA_PART_TYPE, tag.getInt("PartType"));
                this.partType = tag.getInt("PartType");
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER).orElse(null);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if(entity instanceof SkeletronPrime || entity instanceof SkeletronPrimePart){
            return false;
        }
        return super.canAttack(entity);
    }

}

package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.api.entity.IMinion;
import org.confluence.mod.common.entity.monster.prefab.AbstractPrefab;
import org.confluence.mod.common.entity.monster.prefab.AttributeBuilder;
import org.confluence.mod.common.init.ModSoundEvents;
import org.joml.Vector3f;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * 饿鬼
 */
public class TheHungry extends AbstractMonster implements IMinion, Boss.BossPart {
    Mob owner;
    protected Vec3 initPos = Vec3.ZERO;
    public Vec3 lastInitPos = Vec3.ZERO;
    public boolean needLastPos = false;  //是否需要记录lastInitPos
    boolean isFree = false;

    Vec3 initDir = Vec3.ZERO;
    int phase = 0;
    int _phase = 60;

    float forwardSpeed = 0.25f; // 向前速度
    float forwardFreq = 0.15f; // 向前频率
    float backSpeed = 0.15f; // 返回起始点速度
    float backLen = this.getTarget()==null?5:10;  // 距离起始点方向的距离
    float minDis = 8.0f; // 距离起始点的最小距离
    float maxDis = 64.0f; // 距离起始点的最大距离
    float v_speed = 1.15f;   // 回到起始方向的速度
    int switchTime = 5; // 切换方向的时间

    private static final EntityDataAccessor<Vector3f> DATA_TRIGGER =  SynchedEntityData.defineId(TheHungry.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> DATA_IS_FREE = SynchedEntityData.defineId(TheHungry.class, EntityDataSerializers.BOOLEAN);

    public TheHungry(EntityType<? extends Monster> type, Level level, AttributeBuilder builder) {
        super(type, level, builder.setController((state,e)->{
            state.add(DefaultAnimations.genericIdleController(e));
        }));
        int ranDir = this.getRandom().nextInt(7);
        this.minDis += ranDir;
        this.maxDis += ranDir;
        this.noPhysics = true;
        this.collisionProperties = new CollisionProperties(5,20,0.3f);
    }

    public TheHungry(Level level,boolean isFree) {
        this(TEMonsterEntities.THE_HUNGRY.get(), level,new AbstractPrefab().getPrefab());
        this.setFree(isFree);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < 32 * 32;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(pSource.getEntity()!= null && pSource.getEntity().getType().is(TETags.EntityTypes.FLESH_ALLIANCE))
            return false;
        return super.hurt(pSource, pAmount);
    }

    @Override
    public void move(MoverType pType, Vec3 pos) {
        this.setPos(this.getX() + pos.x, this.getY() + pos.y, this.getZ() + pos.z);
    }

    public float getMaxDis(){
        return maxDis;
    }

    /**
     * 检查是否超出反圆形范围
     * 水平方向距离限制最短，垂直方向范围更大
     * @return true表示超出范围，需要回退
     */
    private boolean isOutOfRange() {
        Vec3 relativePos = position().subtract(initPos);
        double horizontalDistance = Math.sqrt(relativePos.x * relativePos.x + relativePos.z * relativePos.z);
        double verticalDistance = Math.abs(relativePos.y);
        double maxDis = this.getMaxDis();
        double maxHorizontalDis = maxDis;
        double maxVerticalDis = maxDis * 2.0;

        // 使用椭圆方程：水平距离/水平限制 + 垂直距离/垂直限制 > 1 时超出范围
        return horizontalDistance / maxHorizontalDis + verticalDistance / maxVerticalDis > 1.0;
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if(this.owner == null)
            return super.canAttack(entity);
        else return this.owner.canAttack(entity);
    }

    protected Vec3 initDirection(LivingEntity owner){
        return owner.getForward().normalize();
    }

    @Override
    public void tick() {
        if ((this.owner == null || !this.owner.isAlive()) && !this.level().isClientSide && this.tickCount % 60 == 0) {
            if (this.isFree) {
                this.hurt(this.damageSources().starve(), 1.0F);
            } else {
                this.discard();
            }
            return;
        }

        super.tick();
        phase = (phase + 1) % _phase;

        if (!this.level().isClientSide) {
            if (this.owner instanceof LivingEntity wall && !this.isFree) {
                Vec3 testDir = this.initDirection(wall);
                if (this.initDir == null || !this.initDir.equals(testDir)) {
                    initDir = testDir;
                }
            } else if (this.isFree) {
                Vec3 pos = position();
                setTarget(this.level().getNearestPlayer(pos.x, pos.y, pos.z, 40, true));
                TEUtils.updateEntityRotation(this, this.getDeltaMovement().multiply(1, -1, 1));
            }
        }

        Vec3 currentInitPos = this.initPos;
        Vec3 currentInitDir = this.initDir;

        if (currentInitPos != null && currentInitDir != null) {
            processMovement(currentInitPos, currentInitDir);
        }
    }

    @Override
    protected void pushEntities() {
    }

    private void processMovement(Vec3 initPos, Vec3 initDir) {
        LivingEntity target = getTarget();
        boolean hasTarget = target != null;
        boolean isFree = this.isFree;
        boolean targetInRange = hasTarget && !isOutOfRange();
        float flag = hasTarget ? 2.0f : 1.0f;

        Vec3 speed = Vec3.ZERO;
        Vec3 finalSpeed;

        if (hasTarget) {
            Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5f, 0);

            this.lookControl.setLookAt(target, 200, 85);
            this.lookAt(target, 200, 85);

            double tx = targetPos.x - initPos.x;
            double ty = targetPos.y - initPos.y;
            double tz = targetPos.z - initPos.z;

            Vec3 currentPos = position();
            double dx = targetPos.x - currentPos.x;
            double dy = targetPos.y - currentPos.y;
            double dz = targetPos.z - currentPos.z;

            double distToTarget = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distToTarget > 0) {
                dx /= distToTarget;
                dy /= distToTarget;
                dz /= distToTarget;
            }

            double sx = initPos.x - currentPos.x;
            double sy = initPos.y - currentPos.y;
            double sz = initPos.z - currentPos.z;
            double distToStart = Math.sqrt(sx * sx + sy * sy + sz * sz);
            if (distToStart > 0) {
                sx /= distToStart;
                sy /= distToStart;
                sz /= distToStart;
            }

            double mixedX = dx + sx * 0.3f;
            double mixedY = dy + sy * 0.3f;
            double mixedZ = dz + sz * 0.3f;
            double mixedLen = Math.sqrt(mixedX * mixedX + mixedY * mixedY + mixedZ * mixedZ);
            if (mixedLen > 0) {
                mixedX /= mixedLen;
                mixedY /= mixedLen;
                mixedZ /= mixedLen;
            }

            double velocityScale = isFree ? v_speed : v_speed * (targetInRange ? 2.0f : 1.0f);
            speed = new Vec3(mixedX * velocityScale, mixedY * velocityScale, mixedZ * velocityScale);

            if (!isFree) {
                double newDirLen = Math.sqrt(tx * tx + ty * ty + tz * tz);
                if (newDirLen > 0) {
                    double newDirX = tx / newDirLen;
                    double newDirY = ty / newDirLen;
                    double newDirZ = tz / newDirLen;

                    if (this.initDir != null) {
                        this.initDir = this.initDir.lerp(new Vec3(newDirX, newDirY, newDirZ), 0.3f);
                    } else {
                        this.initDir = new Vec3(newDirX, newDirY, newDirZ);
                    }
                }
            }
        } else if (!isFree) {
            if (--switchTime <= 0) {
                switchTime = random.nextInt(20) + 10;

                if (this.owner instanceof WallOfFlesh wall) {
                    Vec3 baseDir = wall.getForward().normalize();
                    this.setYRot((float) Math.toDegrees(Math.atan2(-baseDir.x, baseDir.z)));

                    double rx = Math.random() - 0.5f;
                    double ry = Math.random() - 0.5f;
                    double rz = Math.random() - 0.5f;
                    double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
                    if (len > 0) {
                        rx /= len;
                        ry /= len;
                        rz /= len;
                    }

                    Vec3 testDir = new Vec3(rx, ry, rz);
                    Vec3 currentPos = position();
                    BlockPos testPos = BlockPos.containing(
                            testDir.x * 5 + currentPos.x,
                            testDir.y * 5 + currentPos.y,
                            testDir.z * 5 + currentPos.z
                    );

                    if (level().getBlockState(testPos).isAir() && testPos.getY() > level().getMinBuildHeight()) {
                        if (initDir != null) {
                            initDir = initDir.lerp(testDir, 0.4f);
                        } else {
                            initDir = testDir;
                        }
                    }
                }
            }
        }

        Vec3 forward;
        if (!isFree) {
            double sinFactor = tickCount * forwardFreq * flag;
            float sinValueX = (float) Math.sin(sinFactor * 0.7f);
            float sinValueY = (float) Math.sin(sinFactor * 1.3f);
            float sinValueZ = (float) Math.sin(sinFactor);
            float shakeMultiplier = targetInRange ? 0.3f : 1.0f;
            double shakeX = sinValueX * forwardSpeed * 0.35f * shakeMultiplier;
            double shakeY = sinValueY * forwardSpeed * 0.45f * shakeMultiplier;
            double shakeZ = sinValueZ * forwardSpeed * 0.3f * shakeMultiplier;
            double forwardMultiplier = forwardSpeed * (1.0f + (targetInRange ? 0.5f : 0.0f));
            double dirX = initDir.x * forwardMultiplier + shakeX;
            double dirY = initDir.y * forwardMultiplier + shakeY;
            double dirZ = initDir.z * forwardMultiplier + shakeZ;

            forward = new Vec3(dirX, dirY, dirZ);
        } else {
            double forwardMultiplier = forwardSpeed * 1.25;
            double dirX = initDir.x * forwardMultiplier;
            double dirY = initDir.y * forwardMultiplier;
            double dirZ = initDir.z * forwardMultiplier;

            forward = new Vec3(dirX, dirY, dirZ);
        }
        Vec3 v_back;
        if (isFree) {
            v_back = Vec3.ZERO;
        } else if (isOutOfRange()) {
            double minDisX = initPos.x + initDir.x * minDis;
            double minDisY = initPos.y + initDir.y * minDis;
            double minDisZ = initPos.z + initDir.z * minDis;

            Vec3 currentPos = position();
            double backX = minDisX - currentPos.x;
            double backY = minDisY - currentPos.y;
            double backZ = minDisZ - currentPos.z;

            double backLen = Math.sqrt(backX * backX + backY * backY + backZ * backZ);
            if (backLen > 0) {
                double scale = backSpeed * 2.0f / backLen;
                v_back = new Vec3(backX * scale, backY * scale, backZ * scale);
            } else {
                v_back = Vec3.ZERO;
            }
        } else {
            double backOffset = backLen * 0.5f * (2.25f + Math.sin(tickCount * 0.05 * flag));
            double backPosX = initPos.x + initDir.x * (minDis + backOffset);
            double backPosY = initPos.y + initDir.y * (minDis + backOffset);
            double backPosZ = initPos.z + initDir.z * (minDis + backOffset);

            Vec3 currentPos = position();
            double backX = backPosX - currentPos.x;
            double backY = backPosY - currentPos.y;
            double backZ = backPosZ - currentPos.z;

            float backMultiplier = (!hasTarget || isOutOfRange()) ? 1.0f : 0.5f;
            double scale = backSpeed * backMultiplier;
            v_back = new Vec3(backX * scale, backY * scale, backZ * scale);
        }

        double finalX = speed.x + forward.x + v_back.x;
        double finalY = speed.y + forward.y + v_back.y;
        double finalZ = speed.z + forward.z + v_back.z;

        double len = Math.sqrt(finalX * finalX + finalY * finalY + finalZ * finalZ);
        if (len > 0.35f) {
            double scale = 0.35f / len;
            finalSpeed = new Vec3(finalX * scale, finalY * scale, finalZ * scale);
        } else {
            finalSpeed = new Vec3(finalX, finalY, finalZ);
        }

        this.setDeltaMovement(finalSpeed);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.set(DATA_TRIGGER, Vec3.ZERO.toVector3f());
        this.entityData.set(DATA_OWNER_UUID, Optional.empty());
        this.entityData.set(DATA_IS_FREE, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("initPos")) {
            Vector3f initPos = new Vector3f(tag.getFloat("initPosX"), tag.getFloat("initPosY"), tag.getFloat("initPosZ"));
            this.entityData.set(DATA_TRIGGER, initPos);
            this.initPos = new Vec3(initPos);
        }
        if(tag.contains("isFree")) {
            this.entityData.set(DATA_IS_FREE, tag.getBoolean("isFree"));
            this.isFree = tag.getBoolean("isFree");
        }
        minion_readData(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(initPos!= null) {
            tag.putFloat("initPosX", (float) initPos.x);
            tag.putFloat("initPosY", (float) initPos.y);
            tag.putFloat("initPosZ", (float) initPos.z);
        }
        tag.putBoolean("isFree", this.isFree);
        minion_saveData(tag);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(level().isClientSide) {
            if (key == DATA_TRIGGER) {
                this.initPos = new Vec3(this.entityData.get(DATA_TRIGGER));
            }
            if (key == DATA_IS_FREE) {
                this.isFree = this.entityData.get(DATA_IS_FREE);
            }
        }
    }

    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        setNoGravity(true);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        if(this.initPos == null){
            return super.getBoundingBoxForCulling().inflate(10);
        }
        return new AABB(this.position(), this.initPos);
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        boolean flag = this.owner instanceof WallOfFlesh && this.owner.isAlive();
        if (owner instanceof WallOfFlesh &&!level().isClientSide && flag && !this.isFree && this.getInitPos() != null) {
            TheHungry hungry = new TheHungry(level(), true) {
                @Override
                protected boolean shouldDropLoot() {
                    return false;
                }
            };
            hungry.setNoGravity(true);
            hungry.setPos(this.position());
            hungry.setRot(this.getYRot(), this.getXRot());
            hungry.minion_setOwner(this.owner);
            level().addFreshEntity(hungry);
        }
    }

    public Vec3 getInitPos(){
        return initPos;
    }

    public void setInitPos(Vector3f initPos){
        if(!isFree) {
            this.entityData.set(DATA_TRIGGER, initPos);
            this.initPos = new Vec3(initPos);
        }
    }

    // 对于不是运动的情景，不需要更新服务端位置
    public void setClientInitPos(Vector3f initPos){
        if(needLastPos) this.lastInitPos = this.initPos;
        this.initPos = new Vec3(initPos);
    }

    public Vec3 getDir(){
        return this.owner instanceof WallOfFlesh wall?wall.getForward():Vec3.ZERO;
    }

    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(TheHungry.class, EntityDataSerializers.OPTIONAL_UUID);

    @Override
    public EntityDataAccessor<Optional<UUID>> getDATA_OWNER_UUID() {
        return DATA_OWNER_UUID;
    }

    public void minion_setOwner(Entity owner) {
        if (owner instanceof Mob wall) {
            minion_setOwnerUUID(owner.getUUID());
            this.owner = wall;
        }
    }

    @Override
    public boolean shouldBeSaved(){
        if(owner!=null){
            return owner.shouldBeSaved();
        }
        return super.shouldBeSaved();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, state -> state.setAndContinue(RawAnimation.begin().thenLoop("bait"))));
    }

    private final AnimatableInstanceCache CACHE = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public boolean isFree() {
        return this.entityData.get(DATA_IS_FREE);
    }

    public void setFree(boolean free) {
        this.isFree = free;
        this.entityData.set(DATA_IS_FREE, free);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.THE_HUNGRY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.THE_HUNGRY_DEATH.get();
    }
}

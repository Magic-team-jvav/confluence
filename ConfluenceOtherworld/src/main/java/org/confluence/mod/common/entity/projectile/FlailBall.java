package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.flail.FlailItem;
import org.confluence.mod.mixed.IPlayer;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terraentity.api.entity.IOriented;
import org.confluence.terraentity.mixin.accessor.EntityAccessor;
import org.confluence.terraentity.utils.OBB;

// 原作者：@viola
public class FlailBall extends Projectile implements IOriented, Immunity {
    public long frameCount = 0;
    private final FlailItem item;
    private InteractionHand hand;
    public float offset;
    public float radians;

    public static final EntityDataAccessor<Float> DATA_OFFSET = SynchedEntityData.defineId(FlailBall.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(FlailBall.class, EntityDataSerializers.INT);

    public static final int PHASE_SPIN = 0;
    public static final int PHASE_THROWN = 1;
    public static final int PHASE_RETRACT = 2;
    public static final int PHASE_FORCE_RETRACT = 3;
    public static final int PHASE_STAY = 4;

    public FlailBall(Level pLevel, FlailItem flailItem) {
        super(ModEntities.FLAIL_BALL.get(), pLevel);
        setNoGravity(true);
        noPhysics = true;
        setXRot(-90);
        this.item = flailItem;
    }

    public FlailBall(Level pLevel, Entity owner, InteractionHand hand, FlailItem flailItem) {
        this(pLevel, flailItem);
        setOwner(owner);
        radians = owner.getYRot() * Mth.DEG_TO_RAD;
        this.hand = hand;
        offset = hand == InteractionHand.MAIN_HAND ? -0.6f : 0.6f;
        getEntityData().set(DATA_OFFSET, offset);
        setPos(owner.position().add(offset * Mth.cos(radians), 0, offset * Mth.sin(radians)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OFFSET, -0.6f);
        builder.define(DATA_PHASE, PHASE_SPIN);
    }

    @Override
    public void tick() {
        Entity owner = getOwner();
        int phase = getPhase();
        if (owner == null || owner.isRemoved()  // 没有owner
                || (!level().isClientSide() && item == null)  // 没有物品
                || (!level().isClientSide() && owner instanceof LivingEntity living && living.getItemInHand(hand).getItem() != item)   // 切换物品
                || position().distanceToSqr(owner.position()) >= 30 * 30) {  //距离太远，自动消失的距离是固定的 TODO: 调整数值
            discard();
            if (owner instanceof IPlayer fp) {
                fp.confluence$setFlailBall(null);
            }
            return;
        }
        super.tick();
        Vec3 motion = getDeltaMovement();
        move(MoverType.SELF, motion);
        switch (phase) {
            case PHASE_SPIN -> {
                float offset = getEntityData().get(DATA_OFFSET);
                float radians = (float) Math.toRadians(owner.getYRot());
                setPos(owner.position().add(offset * Mth.cos(radians), 0, offset * Mth.sin(radians)));
            }
            case PHASE_THROWN -> {
                // TODO: 反弹
//                if(direction == null){
//                    direction = Vec3.directionFromRotation(owner.getXRot() - 3, owner.getYRot() - 3).scale(3); // TODO: 发射速度，近战速度加成
//                }
//                setDeltaMovement(direction.add(owner.getDeltaMovement()));
                noPhysics = false;

            }
            case PHASE_STAY -> {
                noPhysics = false;
                setNoGravity(false);
//                Vec3 newMotion = new Vec3(motion.x * 0.7, Math.min(motion.y, 0), motion.z * 0.7);
//                this.setDeltaMovement(newMotion);
            }
            case PHASE_RETRACT, PHASE_FORCE_RETRACT -> {
                noPhysics = true;
                setNoGravity(true);
                setDeltaMovement(owner.position().add(0, 1.5, 0).subtract(position()).normalize().scale(3)); // TODO: 时间决定速度
            }
        }
        if (position().distanceToSqr(owner.position()) > 16 * 16) {  // TODO: 抛出时间固定，时间决定速度和距离
            setPhase(PHASE_RETRACT);
        }
        refreshDimensions();
        if (!level().isClientSide()) {
            OBB obb = getOrientedBoundingBox();
            //包围盒要大一点
            AABB border = getBoundingBox().inflate(2);
            var entities = level().getEntitiesOfClass(LivingEntity.class, border, EntitySelector.NO_SPECTATORS
                    .and(e -> {
                        if (e instanceof Player) return true;
                        return obb.inflate(10).collide(e.getBoundingBox(), motion, e.getDeltaMovement());
                    }));

            for (LivingEntity living : entities) {
                if (living == owner) {
                    if (phase == PHASE_RETRACT || phase == PHASE_FORCE_RETRACT) {
                        if (owner instanceof IPlayer fp) {
                            fp.confluence$setFlailBall(null);
                        }
                        discard();
                    }
                    continue;
                } else if (!LibUtils.canHitEntity(living, owner)) continue;

                living.hurt(damageSources().mobAttack(owner instanceof LivingEntity lo ? lo : null), item.damage); // TODO: 不同阶段伤害不同
//                living.knockback();
                // TODO: 击退
            }
        }
    }


    @Override
    public void move(MoverType pType, Vec3 motion) {
        int phase = getPhase();
        if (phase == PHASE_THROWN) {
            Vec3 collide = ((EntityAccessor) this).callCollide(motion);
            if (collide.x != motion.x) {
                motion = new Vec3(-motion.x / 2, motion.y, motion.z);
            }
            if (collide.y != motion.y) {
                motion = new Vec3(motion.x, -motion.y / 2, motion.z);
            }
            if (collide.z != motion.z) {
                motion = new Vec3(motion.x, motion.y, -motion.z / 2);
            }
        } else if (phase == PHASE_STAY) {
            motion = new Vec3(motion.x * 0.7, Math.min(0, motion.y - 0.4), motion.z * 0.7);
        }
//        if(!isNoGravity()){
//            motion = motion.add(0, -0.4, 0);
//        }
        setDeltaMovement(motion);
        super.move(pType, motion);
    }

    public int getPhase() {
        return getEntityData().get(DATA_PHASE);
    }

    public void setPhase(int phase) {
        if (phase < PHASE_SPIN || phase > PHASE_STAY) {
            throw new IllegalArgumentException("Invalid phase");
        }
        getEntityData().set(DATA_PHASE, phase);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        if (getPhase() == PHASE_SPIN) {
            return super.getDimensions(pPose);
        } else {
            //包围盒要大一点
            return EntityDimensions.fixed(0.75f, 0.75f);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        Confluence.LOGGER.info("{}", pResult);
        super.onHitBlock(pResult);
    }

    @Override
    public OBB getOrientedBoundingBox() {
        Vec3 pos = position();
        Entity owner = getOwner();
        if (owner == null || getEntityData().get(DATA_PHASE) != PHASE_SPIN) {
            return new OBB(getBoundingBox()).updateVertex();
        }
        float offset = getEntityData().get(DATA_OFFSET) / -2;
        float radians = (float) Math.toRadians(owner.getYRot());
        return new OBB(pos.add(offset * Mth.cos(radians), 1, offset * Mth.sin(radians)), 0.7, 2, 3.6, 0, owner.getYRot()).updateVertex();
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return 7;
    }
}

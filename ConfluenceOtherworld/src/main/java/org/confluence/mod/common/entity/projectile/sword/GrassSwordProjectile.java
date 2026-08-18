package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModDamageTypes;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GrassSwordProjectile extends SwordProjectile {
    static final float POISON_CHANCE = 0.5F;
    static final int POISON_DURATION = 100;
    static final int POISON_AMPLIFIER = 1;

    protected static final EntityDataAccessor<Float> DATA_YAW = SynchedEntityData.defineId(GrassSwordProjectile.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_PITCH = SynchedEntityData.defineId(GrassSwordProjectile.class, EntityDataSerializers.FLOAT);
    private float yawSpeed;
    private float pitchSpeed;

    public GrassSwordProjectile(EntityType<GrassSwordProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        survivesBlockHit = true;
        remainingHits = 99999;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (level().isClientSide) {
            if (data == DATA_YAW) {
                this.yawSpeed = this.entityData.get(DATA_YAW);
            } else if (data == DATA_PITCH) {
                this.pitchSpeed = this.entityData.get(DATA_PITCH);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_YAW, 0.0f);
        this.entityData.define(DATA_PITCH, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 5) {
            Vec3 motion = getDeltaMovement();
            float yaw = Mth.HALF_PI - (float) Mth.atan2(motion.z, motion.x);
            float pitch = (float) -Mth.atan2(motion.y,
                    Math.sqrt(motion.x * motion.x + motion.z * motion.z));
            Quaternionf q = new Quaternionf()
                    .rotateY(yaw)
                    .rotateX(pitch)
                    .rotateX(-0.1f)
                    .rotateY(yawSpeed);

            Vec3 transformed = new Vec3(q.transform(new Vector3f(0, 0, 1)));
            setDeltaMovement(transformed);
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        super.shootFromRotation(shooter, x, y, z, velocity, inaccuracy);
        this.yawSpeed = (float) (Math.random() * 0.5f + 0.5f);
        this.pitchSpeed = (float) (-Math.random() * 0.05f - 0.05f);
        this.entityData.set(DATA_YAW, yawSpeed);
        this.entityData.set(DATA_PITCH, pitchSpeed);
    }

    /// 恢复 1.21 草剑剑气的专属命中效果。该参数只属于草剑，不进入通用剑气组件，
    /// 以免其他剑气为了一个特例承担额外字段和序列化分支。
    @Override
    protected void applyHitEffect(Entity target) {
        applyPoisonForRoll(target, getRandom().nextFloat());
    }

    /// 按给定随机值应用中毒，包级入口用于精确验证五成概率的边界。
    ///
    /// @return 是否成功向生物写入中毒效果
    boolean applyPoisonForRoll(Entity target, float roll) {
        if (!(target instanceof LivingEntity living) || roll < 0.0F || roll >= POISON_CHANCE) {
            return false;
        }
        return living.addEffect(new MobEffectInstance(
                MobEffects.POISON,
                POISON_DURATION,
                POISON_AMPLIFIER));
    }

    @Override
    public DamageSource damageSource() {
        return ModDamageTypes.of(level(), DamageTypes.GENERIC, this, getOwner());
    }
}

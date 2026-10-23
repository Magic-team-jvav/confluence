package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;

/// 欧泊法杖的洪流弹幕。
///
/// 出手时受重力下坠，砸到地面后贴着地面向前奔涌一段时间，然后折返奔向施法者；
/// 折返途中伤害翻倍。
public class OpalStaffProjectile extends AbstractManaProjectile {
    /// 洪流弹幕的粒子。
    public static final ResourceLocation TORRENT_PARTICLE = Confluence.asResource("opal_torrent");
    private static final double GRAVITY = 0.09;
    private static final double SURGE_SPEED = 0.55;
    private static final int SURGE_TICKS = 15;
    private static final double RETURN_SPEED = 0.85;
    private static final int MAX_AGE = 160;

    private Phase phase = Phase.FALLING;
    private int surgeTicks;
    private Vec3 surgeDirection = Vec3.ZERO;

    public OpalStaffProjectile(EntityType<OpalStaffProjectile> entityType, Level level) {
        super(entityType, level);
        // 大范围洪流使用 ParticleStorm 自定义粒子，而不是通用水流粒子。
        withParticle(TORRENT_PARTICLE);
    }

    public OpalStaffProjectile(LivingEntity living) {
        this(ModEntities.OPAL_STAFF_PROJECTILE.get(), living.level());
    }

    public Phase getPhase() {
        return phase;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Vec3 motion = getDeltaMovement();
        switch (phase) {
            case FALLING -> setDeltaMovement(motion.x, motion.y - GRAVITY, motion.z);
            case SURGE -> {
                surgeTicks++;
                setDeltaMovement(surgeDirection.scale(SURGE_SPEED));
                if (surgeTicks > SURGE_TICKS) {
                    this.phase = Phase.RETURNING;
                }
            }
            case RETURNING -> {
                LivingEntity owner = getLivingOwner();
                if (owner == null) {
                    discard();
                    return;
                }
                Vec3 target = owner.position().add(0.0, owner.getBbHeight() * 0.5, 0.0);
                Vec3 direction = target.subtract(position());
                if (direction.lengthSqr() < 2.25) {
                    discard();
                    return;
                }
                setDeltaMovement(direction.normalize().scale(RETURN_SPEED));
            }
        }
        doSimpleMove();
        doAgeCheck(MAX_AGE);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (phase != Phase.FALLING) return;
        Vec3 motion = getDeltaMovement();
        Vec3 horizontal = new Vec3(motion.x, 0.0, motion.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = new Vec3(0.0, 0.0, 1.0);
        }
        this.surgeDirection = horizontal.normalize();
        this.phase = Phase.SURGE;
        this.surgeTicks = 0;
        setDeltaMovement(surgeDirection.scale(SURGE_SPEED));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.5, 0.2);
            doDiscardInMaxPenetrate(12);
        }
    }

    /// 折返途中伤害翻倍。
    @Override
    public float getCalculatedDamage() {
        float base = super.getCalculatedDamage();
        return phase == Phase.RETURNING ? base * 2.0F : base;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Phase", phase.name());
        compound.putInt("SurgeTicks", surgeTicks);
        compound.putDouble("SurgeX", surgeDirection.x);
        compound.putDouble("SurgeZ", surgeDirection.z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.phase = Phase.valueOf(compound.getString("Phase"));
        this.surgeTicks = compound.getInt("SurgeTicks");
        this.surgeDirection = new Vec3(compound.getDouble("SurgeX"), 0.0, compound.getDouble("SurgeZ"));
    }

    public enum Phase {
        /// 出手下坠。
        FALLING,
        /// 贴地奔涌。
        SURGE,
        /// 折返施法者，伤害翻倍。
        RETURNING
    }
}

package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.EmptyEntity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

import java.util.UUID;

/// 海床崩裂者砸出的水柱。
///
/// 普通满蓄力攻击召唤出的水柱只是把范围内的敌怪快速托举起来，随后立刻消散；
/// 跳劈（暴击）召唤的水柱更高、更持久，并每秒对柱内敌怪造成一半锤子伤害。
///
/// 视觉完全交给 ParticleStorm 的自定义粒子（{@code confluence:seabed_water_column} /
/// {@code confluence:seabed_water_column_tall}），本实体自身不渲染任何几何体。
public class SeabedWaterColumnEntity extends EmptyEntity {
    /// 普通水柱的粒子。
    public static final ResourceLocation PARTICLE = Confluence.asResource("seabed_water_column");
    /// 跳劈水柱的粒子（更高、更久）。
    public static final ResourceLocation PARTICLE_TALL = Confluence.asResource("seabed_water_column_tall");

    private static final double RADIUS = 2.5;
    private UUID ownerUuid;
    private float damage;
    private int duration;
    private double columnHeight;
    private double liftSpeed;
    private boolean damageOverTime;
    private int lastDamageTick = -20;
    private boolean emitterSent;

    public SeabedWaterColumnEntity(EntityType<? extends SeabedWaterColumnEntity> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    public static SeabedWaterColumnEntity create(Level level, @Nullable Player owner, Vec3 position, float damage, boolean critical) {
        SeabedWaterColumnEntity column = new SeabedWaterColumnEntity(ModEntities.SEABED_WATER_COLUMN.get(), level);
        column.setOwner(owner);
        column.setPos(position.x, position.y, position.z);
        column.damage = damage;
        if (critical) {
            column.columnHeight = 6.0;
            column.duration = 100;
            column.liftSpeed = 1.6;
            column.damageOverTime = true;
        } else {
            column.columnHeight = 3.0;
            column.duration = 15;
            column.liftSpeed = 1.0;
        }
        return column;
    }

    public void setOwner(@Nullable Player owner) {
        this.ownerUuid = owner == null ? null : owner.getUUID();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (!emitterSent) {
            // 由服务端统一下发 ParticleStorm 发射器，客户端无需渲染实体本体。
            this.emitterSent = true;
            EmitterCreationPacketS2C.sendToAll(damageOverTime ? PARTICLE_TALL : PARTICLE, position().toVector3f(), MolangExp.EMPTY, null);
        }
        if (tickCount > duration) {
            discard();
            return;
        }
        AABB area = new AABB(
                getX() - RADIUS, getY() - 0.5, getZ() - RADIUS,
                getX() + RADIUS, getY() + columnHeight, getZ() + RADIUS
        );
        for (Entity entity : level().getEntities(this, area)) {
            if (!(entity instanceof LivingEntity living) || living.isSpectator()) continue;
            if (entity.getUUID().equals(ownerUuid) || entity instanceof Player) continue;
            if (!LibEntityUtils.canHit(getOwnerPlayer(), entity)) continue;
            Vec3 motion = living.getDeltaMovement();
            living.setDeltaMovement(motion.x, Math.min(motion.y + liftSpeed * 0.35, liftSpeed * 1.5), motion.z);
            living.hurtMarked = true;
            if (damageOverTime && tickCount - lastDamageTick >= 20) {
                living.invulnerableTime = 0;
                living.hurt(createDamageSource(), damage * 0.5F);
            }
        }
        if (damageOverTime && tickCount - lastDamageTick >= 20) {
            this.lastDamageTick = tickCount;
        }
    }

    private DamageSource createDamageSource() {
        Player player = getOwnerPlayer();
        if (player != null) {
            return player.damageSources().playerAttack(player);
        }
        return damageSources().magic();
    }

    private @Nullable Player getOwnerPlayer() {
        return ownerUuid == null ? null : level().getPlayerByUUID(ownerUuid);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.damage = compound.getFloat("Damage");
        this.duration = compound.getInt("Duration");
        this.columnHeight = compound.getDouble("ColumnHeight");
        this.liftSpeed = compound.getDouble("LiftSpeed");
        this.damageOverTime = compound.getBoolean("DamageOverTime");
        if (compound.hasUUID("Owner")) {
            this.ownerUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("Damage", damage);
        compound.putInt("Duration", duration);
        compound.putDouble("ColumnHeight", columnHeight);
        compound.putDouble("LiftSpeed", liftSpeed);
        compound.putBoolean("DamageOverTime", damageOverTime);
        if (ownerUuid != null) {
            compound.putUUID("Owner", ownerUuid);
        }
    }
}

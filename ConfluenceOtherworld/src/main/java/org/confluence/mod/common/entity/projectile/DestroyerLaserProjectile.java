package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.data.map.CreatureDefinition.ProjectileOverrides;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.entity.boss.TheDestroyerProbe;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;

/// 毁灭者与探针共用的直线激光，实体和方块碰撞均按整段位移检测。
public final class DestroyerLaserProjectile extends Projectile {
    /// 激光最长寿命，单位 tick。
    private static final int LIFETIME = 60;
    private float damage;

    public DestroyerLaserProjectile(EntityType<? extends DestroyerLaserProjectile> type, Level level) {super(type, level);}

    public static void fire(LivingEntity owner, Vec3 origin, Vec3 aim, float speed) {
        if (owner.level().isClientSide || aim.distanceToSqr(origin) < 0.001) return;
        DestroyerLaserProjectile laser = new DestroyerLaserProjectile(ModEntities.DESTROYER_LASER.get(), owner.level());
        laser.setOwner(owner);
        laser.setPos(origin);
        if (owner instanceof Mob mob) {
            ProjectileOverrides overrides = ProjectileOverrides.get(mob, laser.getType());
            laser.damage = overrides.damageOr(0);
            speed = overrides.speedOr(speed);
        }
        if (laser.damage <= 0) return;
        Vec3 direction = aim.subtract(origin);
        laser.shoot(direction.x, direction.y, direction.z, speed, 0);
        if (owner.level().addFreshEntity(laser) && owner instanceof TheDestroyerProbe) {
            owner.level().playSound(null, origin.x, origin.y, origin.z, ModSoundEvents.ITEM_DEATH_LASER.get(),
                    SoundSource.HOSTILE, 0.7F, 1.0F);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount >= LIFETIME) {
            discard();
            return;
        }
        Vec3 from = position(), to = from.add(getDeltaMovement());
        if (!level().isClientSide) {
            if (!(getOwner() instanceof LivingEntity owner) || !owner.isAlive() || owner.isRemoved()) {
                discard();
                return;
            }
            HitResult block = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            Vec3 end = block.getType() == HitResult.Type.MISS ? to : block.getLocation();
            EntityHitResult hit = ProjectileUtil.getEntityHitResult(level(), this, from, end,
                    getBoundingBox().expandTowards(getDeltaMovement()).inflate(0.3), this::canHitEntity);
            if (hit != null) {
                hit.getEntity().hurt(damageSources().mobProjectile(this, owner), damage);
                discard();
                return;
            }
            if (block.getType() != HitResult.Type.MISS) {
                discard();
                return;
            }
        }
        setPos(to);
        updateRotation();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity target) || entity instanceof TheDestroyer || entity instanceof TheDestroyerProbe)
            return false;
        return super.canHitEntity(entity) && getOwner() instanceof Mob owner && owner.canAttack(target);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putInt("Age", tickCount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("Damage");
        tickCount = tag.getInt("Age");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {return new ClientboundAddEntityPacket(this, entity);}
}

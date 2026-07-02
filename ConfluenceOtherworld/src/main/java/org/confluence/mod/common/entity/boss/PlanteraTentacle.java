package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 世纪之花触手——从方块表面伸出攻击玩家，短时间后消失。
 */
public class PlanteraTentacle extends Entity implements GeoEntity {
    private static final float DAMAGE = 8.0F;
    private static final int LIFETIME = 100;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity master;
    private Vec3 targetPos;
    private int age;

    public PlanteraTentacle(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(LivingEntity master, Vec3 target) {
        this.master = master;
        this.targetPos = target;
    }

    @Override
    public void tick() {
        super.tick();
        if (master == null || !master.isAlive() || age++ > LIFETIME) {
            discard();
            return;
        }
        if (level().isClientSide) return;

        // Reach toward target
        if (targetPos != null) {
            Vec3 toTarget = targetPos.subtract(position());
            if (toTarget.lengthSqr() > 0.5) {
                setDeltaMovement(toTarget.normalize().scale(0.15));
            }
        }

        // Contact damage
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.3))) {
            if (e != master && master.canAttack(e)) {
                e.hurt(damageSources().mobAttack(master), DAMAGE);
            }
        }
    }

    @Override
    public boolean isPickable() {return true;}

    @Override
    public boolean canBeCollidedWith() {return true;}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag t) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag t) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return new ClientboundAddEntityPacket(this);}

    @Override
    public EntityDimensions getDimensions(Pose p) {return EntityDimensions.scalable(0.3F, 2.0F);}

    @Override
    public boolean is(Entity e) {return this == e;}

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (master != null) master.hurt(source, amount * 0.3f);
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

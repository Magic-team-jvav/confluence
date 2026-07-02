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
 * 肉丘之眼——定位在肉丘周围，向环形范围内的玩家射击。
 */
public class HillOfFleshEye extends Entity implements GeoEntity {
    private static final float DAMAGE = 10.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private HillOfFlesh master;
    private int shootDelay;
    private int shootBurst;

    public HillOfFleshEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(HillOfFlesh master) {
        this.master = master;
        this.shootDelay = 30 + random.nextInt(40);
    }

    @Override
    public void tick() {
        super.tick();
        if (master == null || !master.isAlive()) {
            discard();
            return;
        }
        if (level().isClientSide) return;

        LivingEntity target = master.findTargetForPart(position());
        if (target != null) {
            shootDelay--;
            if (shootDelay <= 0) {
                shootBurst++;
                shootAt(target);
                if (shootBurst >= 3) {
                    shootBurst = 0;
                    shootDelay = (master.isPhase2() ? 25 : 40) + random.nextInt(20);
                } else {
                    shootDelay = 8;
                }
            }
        }
    }

    private void shootAt(LivingEntity target) {
        Vec3 origin = getEyePosition();
        Vec3 dir = target.getEyePosition().subtract(origin);
        if (dir.lengthSqr() < 0.01) return;
        dir = dir.normalize();
        Vec3 end = origin.add(dir.scale(64));
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().expandTowards(dir.scale(64)).inflate(1.0))) {
            if (e == master || !master.canAttack(e)) continue;
            if (e.getBoundingBox().clip(origin, end).isPresent()) {
                e.hurt(damageSources().mobAttack(master), DAMAGE);
                break;
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return master != null && master.hurt(source, amount * 2.0f);
    }

    @Override
    public boolean isPickable() {return true;}

    @Override
    public boolean canBeCollidedWith() {return master != null && master.isAlive();}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag t) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag t) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return new ClientboundAddEntityPacket(this);}

    @Override
    public EntityDimensions getDimensions(Pose p) {return EntityDimensions.scalable(1.5F, 1.5F);}

    @Override
    public boolean is(Entity e) {return this == e;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

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
 * 机械骷髅王手臂。4 种模式：0=锯子, 1=钳子, 2=加农炮, 3=激光。
 */
public class SkeletronPrimeArm extends Entity implements GeoEntity {
    private static final float ORBIT_RADIUS = 4.0F;
    private static final float SWIPE_SPEED = 0.8F;
    private static final float DAMAGE = 12.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private SkeletronPrime master;
    private int armType; // 0=saw, 1=vice, 2=cannon, 3=laser
    private double orbitAngle;
    private int swipeTimer;
    private int swipeCooldown;
    private boolean swiping;
    private Vec3 swipeTarget;

    public SkeletronPrimeArm(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(SkeletronPrime master, int type) {
        this.master = master;
        this.armType = type;
        this.orbitAngle = type * Math.PI / 2;
        this.swipeCooldown = 60 + master.getRandom().nextInt(60);
    }

    @Override
    public void tick() {
        super.tick();
        if (master == null || !master.isAlive()) {
            discard();
            return;
        }
        if (level().isClientSide) return;

        updateOrbit();
        updateSwipe();
    }

    private void updateOrbit() {
        if (swiping) return;
        double speed = armType == 0 ? 0.05 : 0.03; // saw arm orbits faster
        orbitAngle += speed;
        double tx = master.getX() + Math.cos(orbitAngle) * ORBIT_RADIUS;
        double ty = master.getY() - 1 + Math.sin(orbitAngle * 0.7) * 1.5;
        double tz = master.getZ() + Math.sin(orbitAngle) * ORBIT_RADIUS;
        Vec3 target = new Vec3(tx, ty, tz);
        Vec3 toTarget = target.subtract(position());
        if (toTarget.lengthSqr() > 0.1) {
            setDeltaMovement(toTarget.normalize().scale(0.3));
        }
        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
    }

    private void updateSwipe() {
        if (swiping) {
            swipeTimer--;
            Vec3 toTarget = swipeTarget.subtract(position());
            if (toTarget.lengthSqr() < 1 || swipeTimer <= 0) {
                swiping = false;
                swipeCooldown = armType == 2 ? 80 : 40 + master.getRandom().nextInt(40);
                setDeltaMovement(Vec3.ZERO);
            } else {
                setDeltaMovement(toTarget.normalize().scale(SWIPE_SPEED + armType * 0.1));
            }
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.5))) {
                if (e != master && master.canAttack(e)) {
                    e.hurt(damageSources().mobAttack(master), DAMAGE + armType * 3);
                }
            }
        } else {
            swipeCooldown--;
            if (swipeCooldown <= 0) beginSwipe();
        }
    }

    private void beginSwipe() {
        LivingEntity target = master.getTarget();
        if (target == null) return;
        swiping = true;
        swipeTimer = 20 + armType * 2;
        swipeTarget = target.position();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (master != null && master.isAlive()) master.hurt(source, amount * 0.5f);
        return true;
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
    public EntityDimensions getDimensions(Pose p) {return EntityDimensions.scalable(1.0F, 0.5F);}

    @Override
    public boolean is(Entity e) {return this == e;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

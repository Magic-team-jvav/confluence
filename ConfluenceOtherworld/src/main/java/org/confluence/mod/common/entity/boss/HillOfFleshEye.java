package org.confluence.mod.common.entity.boss;

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
public class HillOfFleshEye extends BaseBossPart<HillOfFlesh> implements GeoEntity {
    private static final float DAMAGE = 10.0F;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int shootDelay;
    private int shootBurst;

    public HillOfFleshEye(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(HillOfFlesh master) {
        bindTo(master);
        this.shootDelay = 30 + random.nextInt(40);
    }

    @Override
    protected void tickPart(HillOfFlesh master) {
        if (level().isClientSide) return;
        if (master.isInitializing()) return;

        LivingEntity target = master.findTargetForPart(position());
        if (target != null) {
            shootDelay--;
            if (shootDelay <= 0) {
                shootBurst++;
                shootAt(master, target);
                if (shootBurst >= 3) {
                    shootBurst = 0;
                    shootDelay = (master.isPhase2() ? 25 : 40) + random.nextInt(20);
                } else {
                    shootDelay = 8;
                }
            }
        }
    }

    private void shootAt(HillOfFlesh master, LivingEntity target) {
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
        return hurtOwnerAndPart(source, amount, 2.0F);
    }

    @Override
    protected Class<HillOfFlesh> getOwnerType() {
        return HillOfFlesh.class;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

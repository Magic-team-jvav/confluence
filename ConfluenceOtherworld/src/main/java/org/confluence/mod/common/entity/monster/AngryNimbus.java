package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.projectile.NimbusRain;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

public final class AngryNimbus extends BaseFlyingMonster {
    public AngryNimbus(EntityType<? extends AngryNimbus> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new ConditionalSwitchNode(() -> getTarget() != null && getTarget().isAlive(), new BTNode() {
                    private int pathTicks;
                    private int rainTicks;

                    @Override
                    public BTStatus execute() {
                        LivingEntity target = getTarget();
                        if (target == null) return BTStatus.FAILURE;
                        if (--pathTicks <= 0) {
                            pathTicks = 10;
                            getNavigation().moveTo(target.getX(), Math.min(level().getMaxBuildHeight() - getBbHeight() - 1.0, target.getY() + target.getBbHeight() + 10.0), target.getZ(), 1.0);
                        }
                        if (++rainTicks >= 6) {
                            rainTicks = 0;
                            NimbusRain rain = ModEntities.NIMBUS_RAIN.get().create(level());
                            if (rain != null) {
                                float damage = LibUtils.isMaster(level(), blockPosition()) ? 120.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 80.0F : 40.0F;
                                damage *= (float) getAttributeValue(Attributes.ATTACK_DAMAGE) / 50.0F;
                                Vec3 origin = new Vec3(getRandomX(0.7), getY() - 1.0, getRandomZ(0.7));
                                rain.configure(AngryNimbus.this, origin, new Vec3(0.0, -0.6, 0.0), damage, 100);
                                if (!level().addFreshEntity(rain)) rain.discard();
                            }
                        }
                        return BTStatus.RUNNING;
                    }

                    @Override
                    public void stop() {
                        getNavigation().stop();
                        pathTicks = 0;
                        rainTicks = 0;
                    }
                }, new FlyWanderAction(AngryNimbus.this, 0.2, 8));
            }
        };
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ANGRY_NIMBUS_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ANGRY_NIMBUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ANGRY_NIMBUS_DEATH.get();
    }

}

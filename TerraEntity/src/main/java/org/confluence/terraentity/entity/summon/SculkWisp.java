package org.confluence.terraentity.entity.summon;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.proj.BaseProj;

public class SculkWisp extends FlyRangeAttackSummonMob<BaseProj<?>> {

    public SculkWisp(EntityType<? extends SculkWisp> entityType, Level level) {
        super(entityType, level, 30,20,20,20, null);
    }

    @Override
    protected void actualRangedAttack(LivingEntity target, float v) {
        Vec3 vec31 = target.getEyePosition().subtract(getEyePosition());
        Vec3 vec32 = vec31.normalize();
        int i = Mth.floor(vec31.length()) + 7;

        for(int j = 1; j < i; ++j) {
            Vec3 vec33 = getEyePosition().add(vec32.scale(j));
            if(level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        DamageSource damageSource = summon_getDamageSource();
        if (level() instanceof ServerLevel serverLevel && target.hurt(damageSource, summon_getAttackDamage(target, serverLevel, damageSource))) {
            double d1 = 0.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double d0 = 2.5 * (1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            target.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
        }
    }

}

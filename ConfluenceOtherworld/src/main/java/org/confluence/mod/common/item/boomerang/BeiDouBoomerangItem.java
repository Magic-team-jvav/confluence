package org.confluence.mod.common.item.boomerang;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.joml.Vector4f;

/// 北斗飞镖命中时抽取一种减益，飞行过程中留下蓝白星光。
public final class BeiDouBoomerangItem extends BoomerangItem {
    public BeiDouBoomerangItem() {
        super(new Settings(95.0F, 3.0F, 3.0F, 40, 5, 4, 7, false, new Properties().stacksTo(1)));
    }

    @Override
    public void onProjectileHit(BoomerangProjectile projectile, LivingEntity owner, LivingEntity target) {
        int roll = owner.getRandom().nextInt(22);
        Holder<MobEffect> effect;
        if (roll < 6) effect = MobEffects.POISON;
        else if (roll < 11) effect = ModEffects.HELLFIRE;
        else if (roll < 16) effect = MobEffects.WITHER;
        else if (roll < 17) effect = MobEffects.HARM;
        else effect = ModEffects.FROST_BURN;
        if (effect.value().isInstantenous()) {
            effect.value().applyInstantenousEffect(projectile, owner, target, 6, 1.0);
        } else {
            target.addEffect(new MobEffectInstance(effect, 200, 3), owner);
        }
    }

    @Override
    public void emitFlightParticles(BoomerangProjectile projectile, Vec3 movement) {
        Vector4f curve = new Vector4f(0.0F, 1.0F, 1.0F, 1.0F);
        CrossDustParticleOptions particle = new CrossDustParticleOptions(false,
                0xFFE9FAFF, 0xFF788FFF, movement.scale(-0.02).toVector3f(), curve,
                0.12F + projectile.level().random.nextFloat() * 0.08F, 12, 30, curve,
                true, true, true, false);
        for (int i = 1; i <= 3; i++) {
            Vec3 position = projectile.position().add(movement.scale(i / 3.0));
            projectile.level().addParticle(particle, position.x, position.y, position.z, 0.0, 0.0, 0.0);
        }
    }
}

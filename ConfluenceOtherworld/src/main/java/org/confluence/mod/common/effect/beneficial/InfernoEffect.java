package org.confluence.mod.common.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import org.mesdag.particlestorm.api.MolangParticleMobEffect;

public class InfernoEffect extends MolangParticleMobEffect {  //狱火 点燃周围的怪物 （以玩家为中心的5×5×5范围内）
    public InfernoEffect(ResourceLocation id) {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500, id);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        for (Entity entity : livingEntity.level().getEntities(livingEntity, new AABB(livingEntity.blockPosition()).inflate(5.0))) {
            if(entity instanceof Enemy && entity instanceof LivingEntity)
                entity.igniteForTicks((amplifier + 1) * 100);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % (amplifier + 1) * 100 == 0;
    }
}

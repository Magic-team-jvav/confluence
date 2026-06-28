package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/**
 * 尖刺冰雪史莱姆 —— 尖刺 + 攻击附加缓慢效果。
 */
public class SpikedIceSlime extends SpikedSlime {

    public SpikedIceSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xB3F0EA, false, 70);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(6.0f, 8, 31.0f, 70);
    }

    @Override
    protected float spikeDamage() {
        return 6.0f;
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 0));
    }
}

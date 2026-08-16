package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/// 腐化史莱姆。1.21 侧使用普通史莱姆行为，不会在死亡时额外生成史莱姆灵。
public class CorruptSlime extends BaseSlime {

    public CorruptSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xC91717, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(28.0f, 20, 88.0f);
    }

    @Override
    protected void onAttackTarget(LivingEntity target) {
        if (target.getRandom().nextFloat() <= 0.25F) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300), this);
        }
    }
}

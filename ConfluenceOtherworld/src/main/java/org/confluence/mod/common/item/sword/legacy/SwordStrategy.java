package org.confluence.mod.common.item.sword.legacy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.util.LibUtils;

public class SwordStrategy {
    /// 雨伞 缓降
    public static final IInventoryTick UMBRELLA_TICK = (stack, level, entity, slotId, isSelected) -> {
        if (!level.isClientSide && isSelected && entity instanceof LivingEntity living && !living.swinging) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 2, 2, false, false, false));
        }
    };

    /// 猎鹰刃，异域弯刀攻击敌人会短暂提升12%伤害，最高可到达50%，伤害提升以每秒30%的速度衰减
    public static final IInventoryTick FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_A = (stack, level, entity, slotId, isSelected) -> {
        if (level.isClientSide) return;
        LibUtils.updateItemStackNbt(stack, tag -> {
            float bonus = tag.getFloat("Bonus");
            if (bonus > 0 && level.getGameTime() % 20 == 0) {
                tag.putFloat("Bonus", Math.max(bonus - 0.3F, 0));
            }
        });
    };

    public static final IPreLivingDamage FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_B = (stack, damageSource, attacker, victim, amount) -> {
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
        return amount * (1 + tag.getFloat("Bonus"));
    };

    public static final IPostHurtEnemy FALCON_BLADE_EXOTIC_SCIMITAR_BONUS_C = (stack, target, attacker) -> {
        LibUtils.updateItemStackNbt(stack, tag -> {
            float bonus = tag.getFloat("Bonus");
            if (bonus < 0.5F) {
                tag.putFloat("Bonus", Math.min(bonus + 0.12F, 0.5F));
            }
        });
    };
}

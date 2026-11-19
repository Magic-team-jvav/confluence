package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.LibDamageTypes;

/// 烈性酸液：每秒受到 24 点伤害（毒液箭附加的第二段减益）。
public class VenomEffect extends MobEffect {
    /// 每秒（每 20 刻）结算一次的伤害。
    public static final float DAMAGE_PER_SECOND = 24.0F;

    public VenomEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        LibDamageTypes.hurtWithoutKnockback(living, LibDamageTypes.of(living.level(), LibDamageTypes.ACID_VENOM), DAMAGE_PER_SECOND);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 20 == 0;
    }
}

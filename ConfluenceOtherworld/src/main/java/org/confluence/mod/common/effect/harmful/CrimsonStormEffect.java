package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

/**
 * 标记生物正在参与血肉山风暴遭遇。
 *
 * <p>具体牵引和区域伤害由施加效果的血肉山实例结算，避免在效果单例中保存某个 Boss
 * 的可变引用而污染其他世界、其他遭遇或多玩家状态。该效果只负责状态展示与同步。</p>
 */
public class CrimsonStormEffect extends PortMobEffect {
    public CrimsonStormEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        // 行为由对应的 HillOfFlesh 实例结算，这里不能保存跨实体的 Boss 状态。
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }
}

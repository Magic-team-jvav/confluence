package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.lib.util.LibUtils;
import org.mesdag.portlib.wrapper.common.PortEffectCure;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import java.util.Set;

/// 标记处于树妖结界内的敌人，并阻止其恢复生命。
public final class DryadsBaneEffect extends PortMobEffect {
    public DryadsBaneEffect() {
        super(MobEffectCategory.HARMFUL, 0x9ACD32);
    }

    /// 树妖之祸存在期间禁止自然恢复。
    @Override
    public void fillEffectCures(Set<PortEffectCure> cures, MobEffectInstance instance) {
        super.fillEffectCures(cures, instance);
        cures.add(LibUtils.DENY_HEAL);
    }
}

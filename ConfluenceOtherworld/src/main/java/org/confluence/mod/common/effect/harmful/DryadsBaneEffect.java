package org.confluence.mod.common.effect.harmful;

import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import org.confluence.lib.util.LibUtils;


/// 标记处于树妖结界内的敌人，并阻止其恢复生命。
public final class DryadsBaneEffect extends MobEffect {
    public DryadsBaneEffect() {
        super(MobEffectCategory.HARMFUL, 0x9ACD32);
    }

    /// 树妖之祸存在期间禁止自然恢复。
    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance instance) {
        super.fillEffectCures(cures, instance);
        cures.add(LibUtils.DENY_HEAL);
    }
}

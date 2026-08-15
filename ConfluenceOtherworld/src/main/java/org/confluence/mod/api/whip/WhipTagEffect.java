package org.confluence.mod.api.whip;

import net.minecraft.world.effect.MobEffectCategory;
import org.confluence.lib.common.effect.PublicMobEffect;

/**
 * 鞭子施加到目标身上的召唤标记效果。
 *
 * <p>每种鞭子应注册独立实例，使实体能够通过本体 DataMap 单独免疫某一种鞭子标记。
 * 默认实现只增加固定召唤伤害；附属模组可以继承本类并重写
 * {@link #modifyDamage(WhipTagDamageContext, float)}，实现特殊标记规则。</p>
 */
public class WhipTagEffect extends PublicMobEffect {
    private final float fixedDamage;

    public WhipTagEffect(int color, float fixedDamage) {
        super(MobEffectCategory.HARMFUL, color);
        if (!Float.isFinite(fixedDamage) || fixedDamage < 0.0F) {
            throw new IllegalArgumentException(
                    "Whip tag damage must be finite and non-negative"
            );
        }
        this.fixedDamage = fixedDamage;
    }

    /**
     * 在召唤物自身伤害计算完成后应用当前标记。
     */
    public float modifyDamage(WhipTagDamageContext context, float currentDamage) {
        return currentDamage + fixedDamage;
    }

    public float fixedDamage() {
        return fixedDamage;
    }
}

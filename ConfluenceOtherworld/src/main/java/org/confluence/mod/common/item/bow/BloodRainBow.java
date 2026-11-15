package org.confluence.mod.common.item.bow;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.BloodRainArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

/// 血雨弓：发射方式与代达罗斯风暴弓完全一致（蓄力时持续把箭从高空砸下来）。
///
/// 区别只有两点：一根箭会被转化成 1–3 束血雨，而且任何箭矢（原版箭、药箭、泰拉箭）都会被替换成血雨。
public class BloodRainBow extends DaedalusStormbow {
    /// 单根箭能转化出的血雨束数下限与上限。
    private static final int MIN_RAIN = 1;
    private static final int MAX_RAIN = 3;

    public BloodRainBow(float baseDamage, ModRarity rarity) {
        super(baseDamage, rarity);
    }

    @Override
    protected int getVolleySize(RandomSource random) {
        return MIN_RAIN + random.nextInt(MAX_RAIN - MIN_RAIN + 1);
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        /// 血雨是独立弹幕，不继承箭种本身的额外伤害，因此拾取物留空。
        return new BloodRainArrowEntity(ModEntities.BLOOD_RAIN_ARROW.get(), shooter, ItemStack.EMPTY, weapon);
    }
}

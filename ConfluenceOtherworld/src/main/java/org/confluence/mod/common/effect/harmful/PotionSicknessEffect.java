package org.confluence.mod.common.effect.harmful;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.util.TCUtils;

public class PotionSicknessEffect extends MobEffect { // 耐药性
    public PotionSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 0xAA0000);
    }

    // 应当使用这个方法来给玩家添加效果
    public static void addTo(LivingEntity living, int duration) {
        duration = (int) (duration * (1.0F - TCUtils.getAccessoriesValue(living, AccessoryItems.REDUCE$HEALING$COOLDOWN)));
        living.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS, duration));
    }
}

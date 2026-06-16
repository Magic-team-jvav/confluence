package org.confluence.mod.common.effect.beneficial;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.jetbrains.annotations.NotNull;

public class AromaticSatiationEffect extends MobEffect {
    public AromaticSatiationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x33FFCC);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player) {
            FoodData foodData = player.getFoodData();

            int currentFood = foodData.getFoodLevel();
            float currentSaturation = foodData.getSaturationLevel();
            int totalRecovery = 1 + amplifier;

            if (currentFood < 20) {
                int newFood = Math.min(currentFood + totalRecovery, 20);
                foodData.setFoodLevel(newFood);
            } else {
                float nextSaturation = Mth.clamp(currentSaturation + (float) totalRecovery, 0.0F, 20.0F);
                foodData.setSaturation(nextSaturation);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}

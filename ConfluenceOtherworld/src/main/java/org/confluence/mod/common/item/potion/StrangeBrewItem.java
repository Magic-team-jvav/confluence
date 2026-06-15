package org.confluence.mod.common.item.potion;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.mixed.ILivingEntity;
import org.mesdag.portlib.wrapper.world.item.PortItem;

public class StrangeBrewItem extends AbstractPotionItem {
    public StrangeBrewItem() {
        super(new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.BLUE));
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;

        RandomSource random = living.getRandom();
        living.heal(Mth.randomBetweenInclusive(random, 14, 24));
        int ticks = 0;
        float v = random.nextFloat();
        if (v < 0.11F) {
            ticks = 80;
        } else if (v < 0.31F) {
            ticks = 40;
        } else if (v < 0.61F) {
            ticks = 20;
        }
        ILivingEntity.of(living).confluence$setExtraInvulnerableTicks(ticks);
        living.addEffect(new MobEffectInstance(ModEffects.POTION_SICKNESS.get(), Mth.randomBetweenInclusive(random, 800, 1400)));
    }
}

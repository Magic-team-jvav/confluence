package org.confluence.mod.common.item.potion;

import com.google.common.collect.Iterables;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class ChaosPotionItem extends AbstractPotionItem {
    public ChaosPotionItem() {
        super(new Properties());
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        if (level.isClientSide) return;
        Collection<MobEffect> entries = ForgeRegistries.MOB_EFFECTS.getValues();
        RandomSource random = level.random;
        int index = random.nextInt(entries.size() - 1);
        MobEffect effect = Iterables.get(entries, index);
        living.addEffect(new MobEffectInstance(
                effect,
                random.nextInt(20, 600),
                random.nextInt(3)
        ));
    }
}

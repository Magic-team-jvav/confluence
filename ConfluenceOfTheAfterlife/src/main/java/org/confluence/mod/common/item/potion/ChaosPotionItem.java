package org.confluence.mod.common.item.potion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChaosPotionItem extends AbstractPotionItem {
    public ChaosPotionItem() {
        super(new Properties());
    }

    @Override
    protected void apply(ItemStack itemStack, Level level, LivingEntity living) {
        Set<Map.Entry<ResourceKey<MobEffect>, MobEffect>> entries = BuiltInRegistries.MOB_EFFECT.entrySet();
        RandomSource random = level.random;
        int index = random.nextInt(entries.size());
        Iterator<Map.Entry<ResourceKey<MobEffect>, MobEffect>> iterator = entries.iterator();
        for (int i = 0; i < index; i++) {
            if (iterator.hasNext()) {
                if (i == index - 1) {
                    living.addEffect(new MobEffectInstance(
                            BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(iterator.next().getKey()),
                            random.nextInt(20, 600),
                            random.nextInt(3)
                    ));
                } else {
                    iterator.next();
                }
            }
        }
    }
}

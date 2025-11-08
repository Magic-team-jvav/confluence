package org.confluence.mod.common.item.common;

import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.mod.common.item.food.ModFoodPropertiesBuilder;

import java.util.Arrays;

/**
 * 有效果的蜡烛物品
 */
public class EffectiveCandleItem extends BlockItem {
    public final HandheldEffect handheldEffect;

    public EffectiveCandleItem(Block block, Properties properties, ModFoodPropertiesBuilder.EffectData... handheldEffect) {
        this(block, properties, (level, entity, stack) -> Arrays.stream(handheldEffect)
                .map(e -> new MobEffectInstance(e.effect(), e.duration(), e.level()))
                .forEach(entity::addEffect));
    }

    public EffectiveCandleItem(Block block, Properties properties, HandheldEffect handheldEffect) {
        super(block, properties);
        this.handheldEffect = handheldEffect;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!isSelected || !(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        this.handheldEffect.handheld(level, livingEntity, stack);
    }

    public interface HandheldEffect{
        void handheld(Level level, LivingEntity entity, ItemStack stack);
    }
}

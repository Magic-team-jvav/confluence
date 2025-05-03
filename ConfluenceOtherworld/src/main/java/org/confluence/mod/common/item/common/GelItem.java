package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.ColoredItem;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

public class GelItem extends ColoredItem {
    public GelItem() {
        super(ModRarity.WHITE);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack itemStack = new ItemStack(MaterialItems.GEL.get());
        setColor(itemStack, 0xFF66CCFF);
        return itemStack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getMainHandItem();
        if (itemStack.is(this)) {
            pPlayer.getFoodData().eat(1, 0.3F);
            itemStack.shrink(1);
            if (pPlayer.getRandom().nextInt(100) == 0) pPlayer.addEffect(new MobEffectInstance(ModEffects.CHOKING, 1200));
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }
}

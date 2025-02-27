package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.effect.harmful.PotionSicknessEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;

public class MushroomItem extends BlockItem {

    private final float amount;

    public MushroomItem(Block pBlock, float amount) {
        super(pBlock, new Properties());
        this.amount = amount;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getMainHandItem();
        if (pPlayer.hasEffect(ModEffects.POTION_SICKNESS)) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (itemStack.is(MaterialItems.LIFE_MUSHROOM.get())) {
            pPlayer.startUsingItem(pUsedHand);
            pPlayer.heal(amount);
            itemStack.shrink(1);
            PotionSicknessEffect.addTo(pPlayer, 600);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }
}

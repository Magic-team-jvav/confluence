package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModFluids;

public class VoidBucketItem extends BucketItem {
    public VoidBucketItem() {
        super(ModFluids.VOID.fluid(), new Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        InteractionResultHolder<ItemStack> use = super.use(pLevel, pPlayer, pHand);
        if (use.getResult() == InteractionResult.PASS) {
            return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
        }
        return use;
    }
}

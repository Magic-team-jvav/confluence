package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.effect.harmful.PotionSicknessEffect;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.item.food.ModFoodProperties;

public class MushroomItem extends BlockItem {
    private final float amount;

    public MushroomItem(Block block, float amount) {
        super(block, new Properties());
        this.amount = amount;
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return super.canPlace(context, state);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = this.place(new BlockPlaceContext(context));
        if (!result.consumesAction() && context.getPlayer() != null) {
            InteractionResultHolder<ItemStack> useResult = this.use(context.getLevel(), context.getPlayer(), context.getHand());
            return useResult.getResult();
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.hasEffect(ModEffects.POTION_SICKNESS)) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (itemStack.is(MaterialItems.LIFE_MUSHROOM.get())) {
            player.heal(amount);
            player.getFoodData().eat(ModFoodProperties.LIFE_MUSHROOM);
            PotionSicknessEffect.addTo(player, 600); // 你代码里写的是600
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResultHolder.consume(itemStack);
        }
        return InteractionResultHolder.fail(itemStack);
    }
}

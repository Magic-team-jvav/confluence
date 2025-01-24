package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CoinItem extends BlockItem {
    public final Supplier<CoinItem> upgrade;

    public CoinItem(Block block, ModRarity rarity, @Nullable Supplier<CoinItem> upgrade) {
        super(block, new Properties().fireResistant().stacksTo(99).component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.upgrade = upgrade;
    }

    @Override
    public MutableComponent getName(ItemStack pStack) {
        return Component.translatable(getDescriptionId()).withStyle(style -> style.withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isCrouching() && upgrade != null) {
            if (!level.isClientSide) {
                ItemStack coin = upgrade.get().getDefaultInstance();
                player.getInventory().add(coin);
                if (!coin.isEmpty()) {
                    player.drop(coin, true);
                }
                player.setItemInHand(usedHand, ItemStack.EMPTY);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (upgrade != null && stack.getCount() == getMaxStackSize(stack)) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.coin").withColor(0xAAAAAA));
        }
    }
}

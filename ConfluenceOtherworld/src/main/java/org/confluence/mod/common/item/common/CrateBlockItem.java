package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.ArrayList;
import java.util.List;

public class CrateBlockItem extends BlockItem {
    private final List<Component> commonTooltips;
    public CrateBlockItem(Block block, ResourceKey<LootTable> lootTable) {
        super(block, new Properties().fireResistant().component(ModDataComponentTypes.LOOT.get(), new LootComponent(lootTable)));
        this.commonTooltips = createCommonTooltips();
    }

    private List<Component> createCommonTooltips() {
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(Component.translatable("tooltip.item.confluence.crate.common.0")
                .withStyle(ChatFormatting.GRAY));
        return tooltips;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(commonTooltips);

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (hand == InteractionHand.MAIN_HAND && !player.isCrouching()) {
                if(LootComponent.open(serverPlayer, itemStack) && !serverPlayer.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        } else {
            player.playSound(ModSoundEvents.TERRA_OPERATION.get(), 0.5F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getPlayer() == null || pContext.getPlayer().isCrouching();
    }
}

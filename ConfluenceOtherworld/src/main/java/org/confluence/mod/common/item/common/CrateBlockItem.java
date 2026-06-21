package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.ArrayList;
import java.util.List;

public class CrateBlockItem extends BlockItem {
    private final List<Component> commonTooltips;

    public CrateBlockItem(Block block, ResourceLocation lootTable) {
        super(block, new PortItem.PortProperties().fireResistant().component(ModDataComponentTypes.LOOT.get(), new LootComponent(lootTable)));
        this.commonTooltips = createCommonTooltips();
    }

    private List<Component> createCommonTooltips() {
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(Component.translatable("tooltip.item.confluence.crate.common.0")
                .withStyle(ChatFormatting.GRAY));
        return tooltips;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(commonTooltips);
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!player.isCrouching() && LootComponent.open(serverPlayer, itemStack) && !player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
        } else {
            player.playSound(ModSoundEvents.TERRA_OPERATION.get(), 0.5F, 1.0F);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isCrouching()) {
            ItemStack itemStack = context.getItemInHand();
            if (player instanceof ServerPlayer serverPlayer) {
                if (LootComponent.open(serverPlayer, itemStack) && !player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            } else {
                player.playSound(ModSoundEvents.TERRA_OPERATION.get(), 0.5F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getPlayer() == null || pContext.getPlayer().isCrouching();
    }
}

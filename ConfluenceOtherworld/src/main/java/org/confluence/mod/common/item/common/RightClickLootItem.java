package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.component.LootComponent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.List;

public class RightClickLootItem extends CustomRarityItem {
    public RightClickLootItem(ModRarity rarity, ResourceLocation lootTable) {
        super(new PortItem.PortProperties().component(ModDataComponentTypes.LOOT.get(), new LootComponent(lootTable)), rarity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            LootComponent component = stack.get(ModDataComponentTypes.LOOT);
            if (component == null) return InteractionResultHolder.fail(stack);
            LootParams lootparams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(player.getLuck())
                    .create(LootContextParamSets.GIFT);
            LootTable loottable = serverLevel.getServer().getLootData().getLootTable(component.value());
            int count = 1;
            if (player.isCrouching()) count = stack.getCount();
            for (int i = 0; i < count; i++) {
                for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                    if (!player.addItem(loot)) player.drop(loot, false, false);
                }
            }
            stack.shrink(count);
        } else {
            player.playSound(ModSoundEvents.TERRA_OPERATION.get(), 0.5F, 1.0F);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.right_click.common.0").withStyle(ChatFormatting.GRAY));
    }
}

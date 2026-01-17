package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;

import java.util.List;
import java.util.Objects;

public class GameEventItem extends TooltipItem {
    private final ResourceKey<? extends GameEvent> key;

    public GameEventItem(Properties properties, ModRarity rarity, List<Component> tooltips, ResourceKey<? extends GameEvent> key) {
        super(properties, rarity, tooltips);
        this.key = key;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            GameEvent event = GameEventSystem.INSTANCE.getEventInstance(key);
            if (Objects.requireNonNull(event).forceStart() && !player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}

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
import org.confluence.mod.Confluence;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.gameevent.LanternNightGameEvent;

import java.util.List;

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
            if (event == null) {
                Confluence.LOGGER.error("Cannot start missing game event {}", key.location());
                return InteractionResultHolder.fail(stack);
            }
            if (event.forceStart()) {
                if (!player.hasInfiniteMaterials()) {
                    stack.shrink(1);
                }
                LanternNightGameEvent.INSTANCE.forceEnd();
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}

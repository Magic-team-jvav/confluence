package org.confluence.mod.common.item.common;

import com.google.common.collect.Streams;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class BossSummoningItem extends TooltipItem {
    private final Predicate<Player> condition;
    private final Function<Level, Mob> factory;

    public BossSummoningItem(Predicate<Player> condition, Function<Level, Mob> factory, List<Component> tooltips) {
        super(new Properties(), ModRarity.BLUE, tooltips);
        this.condition = condition;
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel && condition.test(player)) {
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
            Mob mob = factory.apply(level);
            if (Streams.stream(serverLevel.getEntities().getAll()).anyMatch(mob.getClass()::isInstance)) {
                return InteractionResultHolder.fail(itemStack);
            }
            mob.setPos(
                    player.getX() + Mth.randomBetweenInclusive(level.random, -50, 50),
                    player.getY(),
                    player.getZ() + Mth.randomBetweenInclusive(level.random, -50, 50)
            );
            if (TEUtils.internalSpawnEntity(mob, serverLevel)) {
                serverLevel.addFreshEntityWithPassengers(mob);
            }
            level.addFreshEntity(mob);
        }
        return InteractionResultHolder.success(itemStack);
    }
}

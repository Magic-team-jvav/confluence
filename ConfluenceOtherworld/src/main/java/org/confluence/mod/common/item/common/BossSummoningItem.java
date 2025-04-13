package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;

import java.util.List;
import java.util.function.Predicate;

public class BossSummoningItem extends TooltipItem {
    private final Predicate<Player> condition;
    private final Factory factory;

    public BossSummoningItem(ModRarity rarity, Predicate<Player> condition, Factory factory) {
        super(new Properties(), rarity, List.of());
        this.condition = condition;
        this.factory = factory;
    }

    public BossSummoningItem(Predicate<Player> condition, Factory factory, List<Component> tooltips) {
        super(new Properties(), ModRarity.BLUE, tooltips);
        this.condition = condition;
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide && condition.test(pPlayer)) {
            itemStack.shrink(1);
            LivingEntity boss = factory.create(pLevel);
            if (!pLevel.getEntitiesOfClass(boss.getClass(), pPlayer.getBoundingBox().inflate(Short.MAX_VALUE)).isEmpty()) {
                return InteractionResultHolder.fail(itemStack);
            }
            boss.setPos(pPlayer.getX() + pLevel.random.nextInt(-50, 51), pPlayer.getY(), pPlayer.getZ() + pLevel.random.nextInt(-50, 51));
            pLevel.addFreshEntity(boss);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @FunctionalInterface
    public interface Factory {
        LivingEntity create(Level level);
    }
}

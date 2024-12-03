package org.confluence.mod.common.item.common;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BossSummingItem extends CustomRarityItem {
    private final Predicate<Player> condition;
    private final Factory factory;

    public BossSummingItem(ModRarity rarity, Predicate<Player> condition, Factory factory) {
        super(new Properties(), rarity);
        this.condition = condition;
        this.factory = factory;
    }

    public BossSummingItem(Predicate<Player> condition, Factory factory) {
        this(ModRarity.BLUE, condition, factory);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
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

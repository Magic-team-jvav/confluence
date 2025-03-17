package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BossSummingItem extends TooltipItem {
    private final Predicate<Player> condition;
    private final Factory factory;

    public BossSummingItem(ModRarity rarity, Predicate<Player> condition, Factory factory) {
        super(new Properties(), rarity, List.of());
        this.condition = condition;
        this.factory = factory;
    }

    public BossSummingItem(Predicate<Player> condition, Factory factory, List<Component> tooltips) {
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

    public static List<Component> getTooltipsFromString(String id, int lineCount, ChatFormatting color) {
        List<Component> components = new ArrayList<>();
        for (int i = 1; i <= lineCount; i++) {
            components.add(Component.translatable("item.confluence." + id + ".tooltip." + i).withStyle(color));
        }
        return components;
    }
}

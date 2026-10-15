package org.confluence.mod.common.item.soul;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulArtifactItem extends CustomRarityItem {
    private static final int MAX_ARTIFACT_STACK_SIZE = 1;

    protected float soulCost;
    protected int cooldownTicks;
    protected @Nullable BiPredicate<ServerPlayer, ItemStack> useCondition;
    protected @Nullable BiConsumer<ServerPlayer, ItemStack> useAction;

    public SoulArtifactItem(Properties properties, ModRarity rarity, float soulCost, int cooldownTicks,
                            @Nullable BiPredicate<ServerPlayer, ItemStack> useCondition, @Nullable BiConsumer<ServerPlayer, ItemStack> useAction) {
        super(properties.stacksTo(MAX_ARTIFACT_STACK_SIZE), rarity);
        this.soulCost = soulCost;
        this.cooldownTicks = cooldownTicks;
        this.useCondition = useCondition;
        this.useAction = useAction;
    }

    public float getSoulCost(ServerPlayer player, ItemStack stack) {
        return soulCost;
    }

    public int getCooldownTicks(ServerPlayer player, ItemStack stack) {
        return cooldownTicks;
    }

    public boolean canUse(ServerPlayer player, ItemStack stack) {
        return useAction != null && (useCondition == null || useCondition.test(player, stack));
    }

    public void activate(ServerPlayer player, ItemStack stack) {
        if (useAction != null) {
            useAction.accept(player, stack);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.useSoulArtifact(serverPlayer, usedHand)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}

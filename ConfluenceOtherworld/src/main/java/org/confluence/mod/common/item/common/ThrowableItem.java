package org.confluence.mod.common.item.common;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;

public class ThrowableItem<T extends ThrowableItemProjectile> extends CustomRarityItem {
    protected float throwSpeed;
    protected float inaccuracy;
    private final Factory<T> factory;

    public ThrowableItem(ModRarity rarity, float throwSpeed, float inaccuracy, Factory<T> factory) {
        super(new Properties(), rarity);
        this.throwSpeed = throwSpeed;
        this.inaccuracy = inaccuracy;
        this.factory = factory;
    }

    public ThrowableItem(float throwSpeed, Factory<T> factory) {
        this(ModRarity.BLUE, throwSpeed, 1.0F, factory);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            T projectile = factory.create(player);
            projectile.setOwner(player);
            projectile.setItem(itemStack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, throwSpeed, inaccuracy);
            level.addFreshEntity(projectile);

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @FunctionalInterface
    public interface Factory<T extends ThrowableItemProjectile> {
        T create(Player player);
    }
}

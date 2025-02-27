package org.confluence.mod.common.item.common;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ThrowableItem<T extends ThrowableItemProjectile> extends Item {
    protected float throwSpeed;
    private final Factory<T> factory;

    public ThrowableItem(float throwSpeed, Factory<T> factory) {
        super(new Properties().rarity(Rarity.COMMON));
        this.throwSpeed = throwSpeed;
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            T bomb = factory.create(player);
            bomb.setOwner(player);
            bomb.setItem(itemStack);
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, throwSpeed, 1.0F);
            level.addFreshEntity(bomb);

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
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

package org.confluence.mod.common.item.common;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.GrenadeEntity;

public class GrenadeItem extends Item {
    public GrenadeItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            GrenadeEntity grenade = new GrenadeEntity(player);
            grenade.setOwner(player);
            grenade.setItem(itemStack);
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.8F, 1.0F);
            level.addFreshEntity(grenade);

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}

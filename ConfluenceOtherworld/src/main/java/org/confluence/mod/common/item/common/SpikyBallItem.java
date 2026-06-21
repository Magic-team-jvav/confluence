package org.confluence.mod.common.item.common;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.SpikyBallProjectile;

public class SpikyBallItem extends Item {
    public SpikyBallItem() {
        super(new Properties().stacksTo(MAX_STACK_SIZE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), TESounds.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            SpikyBallProjectile projectile = new SpikyBallProjectile(player);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.625F, 0.5F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, 5);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.hasInfiniteMaterials()) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
    }
}

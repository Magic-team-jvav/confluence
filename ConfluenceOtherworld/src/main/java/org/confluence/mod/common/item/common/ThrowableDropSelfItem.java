package org.confluence.mod.common.item.common;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

public class ThrowableDropSelfItem extends Item {
    final EntityType<? extends ThrowableDropSelfProjectile> entityType;
    final boolean dropSelf;
    final float inaccuracy;
    final float power;
    final int cooldown;
    final float damage;
    final int flyTicks;

    public ThrowableDropSelfItem(EntityType<? extends ThrowableDropSelfProjectile> entityType, float damage, float power, float inaccuracy, int cooldown, int flyTicks, boolean dropSelf) {
        super(new Properties());
        this.entityType = entityType;
        this.dropSelf = dropSelf;
        this.inaccuracy = inaccuracy;
        this.power = power;
        this.cooldown = cooldown;
        this.damage = damage;
        this.flyTicks = flyTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            ThrowableDropSelfProjectile projectile = entityType.create(level);
            if (projectile != null) {
                projectile.setOwner(player);
                if (dropSelf) {
                    projectile.setItem(getDefaultInstance());
                }
                projectile.setDamage(damage);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power, inaccuracy);
                projectile.setFlyTicks(flyTicks);
                level.addFreshEntity(projectile);
                player.getCooldowns().addCooldown(this, cooldown);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.hasInfiniteMaterials()) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide);
    }
}

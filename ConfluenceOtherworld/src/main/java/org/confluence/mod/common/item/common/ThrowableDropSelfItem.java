package org.confluence.mod.common.item.common;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.function.Supplier;

public class ThrowableDropSelfItem extends Item implements IWeaponTooltip {
    protected final Supplier<EntityType<? extends ThrowableDropSelfProjectile>> typeSup;
    protected final boolean dropSelf;
    protected final float inaccuracy;
    protected final float power;
    protected final int cooldown;
    protected final float damage;
    protected final int flyTicks;

    public ThrowableDropSelfItem(Supplier<EntityType<? extends ThrowableDropSelfProjectile>> typeSup, float damage, float power, float inaccuracy, int cooldown, int flyTicks, boolean dropSelf) {
        super(new Properties());
        this.typeSup = typeSup;
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
            ThrowableDropSelfProjectile projectile = typeSup.get().create(level);
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

    @Override
    public Attribute getTooltipDamageAttribute() {
        return LibAttributes.getRangedDamage().value();
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue) {
        return damage * attributeValue;
    }

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return 20.0 / Math.max(1, cooldown);
    }
}

package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.BaseDraggingProjectile;

import java.util.function.Consumer;

public class BaseDraggingStaffItem<E extends BaseDraggingProjectile> extends ManaStaffItem<E> {
    public BaseDraggingStaffItem(Properties properties, ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity, factory, damage, manaCost, rawVelocity, cooldown);
    }

    public BaseDraggingStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown, Consumer<ItemAttributeModifiers.Builder> consumer) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, consumer);
    }

    public BaseDraggingStaffItem(ModRarity rarity, ProjectileFactory<E> factory, float damage, int manaCost, float rawVelocity, int cooldown, double critChance) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, critChance);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        super.use(level, player, usedHand);
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, E projectile) {
        Vec3 vector = player.calculateViewVector(0, player.getYRot());
        projectile.setPos(player.getX() + vector.x, player.getEyeY() - 0.1, player.getZ() + vector.z);
        projectile.setDamage(damage);
        projectile.setDefaultVelocity(velocity);
        projectile.setOwner(player);
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack stack, E projectile) {
        player.level().playSound(null, player.getX(), player.getEyeY(), player.getZ(), getShootSound(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    protected void rayTrace(ServerPlayer player, ItemStack stack, E projectile) {}
}

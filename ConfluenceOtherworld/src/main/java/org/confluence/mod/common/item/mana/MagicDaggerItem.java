package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicDaggerProjectile;
import org.confluence.terraentity.init.TESounds;

public class MagicDaggerItem extends ManaStaffItem<MagicDaggerProjectile> {
    public MagicDaggerItem() {
        super(ModRarity.LIGHT_RED, MagicDaggerProjectile::new, 23, 6, 12, 2, 0.04);
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, MagicDaggerProjectile projectile) {
        projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        projectile.setItem(itemStack);
        projectile.setFlyTicks(10);
        super.beforeShoot(player, itemStack, projectile);
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack itemStack, MagicDaggerProjectile projectile) {
        player.getCooldowns().addCooldown(this, cooldown);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), TESounds.WAVING.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}

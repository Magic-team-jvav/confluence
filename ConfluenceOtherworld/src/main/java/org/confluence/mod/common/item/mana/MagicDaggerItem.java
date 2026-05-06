package org.confluence.mod.common.item.mana;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicDaggerProjectile;
import org.confluence.terraentity.init.TESounds;

public class MagicDaggerItem extends ManaStaffItem<MagicDaggerProjectile> {
    public MagicDaggerItem() {
        super(ModRarity.LIGHT_RED, MagicDaggerProjectile::new, 20, 6, 12, 2, 0.04);
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, MagicDaggerProjectile projectile) {
        projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        projectile.setItem(stack);
        projectile.setFlyTicks(10);
        super.beforeShoot(player, stack, projectile);
    }

    @Override
    protected SoundEvent getShootSound() {
        return TESounds.WAVING.get();
    }
}

package org.confluence.mod.common.item.gun;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.entity.projectile.BeeProjectile;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public class BeeGunItem extends ManaGunItem<BeeProjectile> {
    public BeeGunItem() {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.GREEN), 1.8F, 0, 0.25F, 1.5F, 5);
    }

    @Override
    public BeeProjectile createAmmo(Level level, Player shooter, ItemStack gunStack, ItemStack ammoStack) {
        BeeProjectile beeProjectile = new BeeProjectile(level, shooter, TCUtils.hasAccessoriesType(shooter, TCItems.HIVE$PACK));
        beeProjectile.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        return beeProjectile;
    }

    @Override
    public float getRealAmmoSpeed(Player player, BeeProjectile projectile, ItemStack gunStack, ItemStack ammoStack) {
        return 0.4F;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack)).withColor(stack.get(TCDataComponentTypes.MOD_RARITY).getColor());
    }
}

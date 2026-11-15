package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;
import org.confluence.mod.common.item.gun.definition.GunDefinition;

public class StarCannonItem extends BaseGun {
    public StarCannonItem(Properties properties) {
        super(properties, GunDefinition.manual(4, 14.8F, 1.8F, 0.15F, 0.04F, -1, 0.0F, ModRarity.GREEN));
    }

    public StarCannonBulletEntity createProjectile(ServerPlayer player, ItemStack bullet) {
        return new StarCannonBulletEntity(player, 0.0F, bullet);
    }
}

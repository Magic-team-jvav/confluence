package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;
import org.confluence.terra_guns.common.definition.GunDefinition;
import org.confluence.terra_guns.common.item.gun.BaseGun;

public class StarCannonItem extends BaseGun {
    public StarCannonItem(Properties properties) {
        super(properties, GunDefinition.manual(4, 14.8f, 1.8f, 0.15f, 0.04f, -1, 0.0f, ModRarity.GREEN));
    }

    public StarCannonBulletEntity createProjectile(ServerPlayer player, ItemStack bullet) {
        return new StarCannonBulletEntity(player, 0.0F, bullet);
    }
}

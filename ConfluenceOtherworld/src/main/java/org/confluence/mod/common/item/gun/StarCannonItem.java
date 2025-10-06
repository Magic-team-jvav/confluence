package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.StarCannonBulletEntity;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.item.gun.CustomGun;

import java.util.List;

public class StarCannonItem extends CustomGun {
    public StarCannonItem(Properties properties) {
        super(properties, 4, 14.8f, 1.8f, 0.15f, 0.04f, -1, 0.0f, ModRarity.GREEN, 0f);
    }

    @Override
    protected BaseBulletEntity createBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        return new StarCannonBulletEntity(player, gravity, bullet);
    }
}

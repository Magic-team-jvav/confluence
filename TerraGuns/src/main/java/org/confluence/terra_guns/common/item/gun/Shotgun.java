package org.confluence.terra_guns.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Shotgun extends BaseGun {
    private final int minBullet;
    private final int maxBullet;

    public Shotgun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, int minBullet, int maxBullet) {
        super(properties, cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity);
        this.minBullet = minBullet;
        this.maxBullet = maxBullet;
    }

    public Shotgun(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, float inaccuracy, ModRarity rarity, int minBullet, int maxBullet) {
        super(properties, cooldown, damage, velocity, knockback, critical, inaccuracy, rarity);
        this.minBullet = minBullet;
        this.maxBullet = maxBullet;
    }

    @Override
    protected void prepareBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        int times = ThreadLocalRandom.current().nextInt(this.minBullet, this.maxBullet + 1);

        IntStream.range(0, times).forEach(i -> {
            BaseBulletEntity baseBulletEntity = new BaseBulletEntity(player, bullet);

            baseBulletEntity.setColorID(((BaseGun) gun.getItem()).getColorID());
            baseBulletEntity.damage = damage;
            baseBulletEntity.knockback = knockback;
            baseBulletEntity.penetrate = penetrate;
            baseBulletEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, velocity, inaccuracy);

            baseBulletEntities.add(baseBulletEntity);
        });
    }
}


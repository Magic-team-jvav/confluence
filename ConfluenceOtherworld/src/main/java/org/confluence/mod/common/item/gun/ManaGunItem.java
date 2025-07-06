package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_guns.api.event.GunEvent;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terra_guns.common.item.gun.BaseGun;

import java.util.List;


public class ManaGunItem extends BaseGun {
    private final int manaCost;
    private final float damage;
    private final float knockback;
    private final float critical;
    private final float velocity;
    private final int penetrate;
    private final float inaccuracy;

    public ManaGunItem(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, int manaCost) {
        super(properties, cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity);
        this.manaCost=manaCost;
        this.damage = damage;
        this.critical = critical;
        this.knockback = knockback;
        this.velocity = velocity;
        this.penetrate = penetrate;
        this.inaccuracy = inaccuracy;
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public void shoot(ServerPlayer player, ItemStack bullet, ItemStack gunStack) {
        if (PlayerUtils.extractMana(player, gunStack, () -> (float) manaCost)) {
            GunEvent.AmmoDataEvent ammoDataEvent = new GunEvent.AmmoDataEvent(player, this, gunStack, damage, critical, knockback, velocity, penetrate, inaccuracy);
            NeoForge.EVENT_BUS.post(ammoDataEvent);

            prepareBulletEntity(baseBulletEntities, player, TGItems.EMPTY_BULLET.toStack(), gunStack, ammoDataEvent.getDamage(), ammoDataEvent.getKnockback(), ammoDataEvent.getVelocity(), ammoDataEvent.getPenetrate(), ammoDataEvent.getInaccuracy());
            baseBulletEntities.forEach(player.serverLevel()::addFreshEntity);
            baseBulletEntities.clear();
        }
    }

    @Override
    protected BaseBulletEntity createBulletEntity(List<Projectile> baseBulletEntities, ServerPlayer player, ItemStack bullet, ItemStack gun, float damage, float knockback, float velocity, int penetrate, float inaccuracy) {
        return new BaseBulletEntity(player, bullet) {
            @Override
            public DamageSource getDamageSource() {
                return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
            }
        };
    }

    public float getDamage() {
        return damage;
    }

    public float getInaccuracy() {
        return inaccuracy;
    }

    public float getVelocity() {
        return velocity;
    }

    public int getPenetrate() {
        return penetrate;
    }

    public float getKnockback() {
        return knockback;
    }

    public float getCritical() {
        return critical;
    }
}

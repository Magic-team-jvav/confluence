package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.event.PortEventHandler;

public class ManaGunItem extends BaseGun {
    private final float damage;
    private final float knockback;
    private final float critical;
    private final float velocity;
    private final int penetrate;
    private final float inaccuracy;

    public ManaGunItem(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, int manaCost) {
        super(new Builder(cooldown, damage, velocity)
                .knockback(knockback)
                .critical(critical)
                .penetrate(penetrate)
                .inaccuracy(inaccuracy)
                .rarity(rarity)
                .manaCost(manaCost)
                .bulletFactory((player, bullet) -> new BaseBulletEntity(player, bullet) {
                    @Override
                    public DamageSource getDamageSource() {
                        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
                    }
                })
                .properties(properties));
        this.damage = damage;
        this.critical = critical;
        this.knockback = knockback;
        this.velocity = velocity;
        this.penetrate = penetrate;
        this.inaccuracy = inaccuracy;
    }

    @Override
    public void shoot(ServerPlayer player, ItemStack bullet, ItemStack gunStack) {
        if (PlayerUtils.extractMana(player, gunStack, this::getManaCost)) {
            GunEvent.AmmoDataEvent ammoDataEvent = new GunEvent.AmmoDataEvent(player, this, gunStack, damage, critical, knockback, velocity, penetrate, inaccuracy);
            PortEventHandler.postEvent(ammoDataEvent);

            prepareBulletEntity(baseBulletEntities, player, GunItems.EMPTY_BULLET.get().getDefaultInstance(), gunStack, ammoDataEvent.getDamage(), ammoDataEvent.getKnockback(), ammoDataEvent.getVelocity(), ammoDataEvent.getPenetrate(), ammoDataEvent.getInaccuracy());
            baseBulletEntities.forEach(player.serverLevel()::addFreshEntity);
            baseBulletEntities.clear();
        }
    }

    public float getDamage() {return damage;}

    public float getInaccuracy() {return inaccuracy;}

    public float getVelocity() {return velocity;}

    public int getPenetrate() {return penetrate;}

    public float getKnockback() {return knockback;}

    public float getCritical() {return critical;}
}

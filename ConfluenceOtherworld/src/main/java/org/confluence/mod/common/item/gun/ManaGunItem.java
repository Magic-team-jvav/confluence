package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_guns.common.definition.GunDefinition;
import org.confluence.terra_guns.common.entity.bullet.BaseBulletEntity;
import org.confluence.terra_guns.common.item.gun.BaseGun;

public class ManaGunItem extends BaseGun {
    private final int manaCost;

    public ManaGunItem(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, int manaCost) {
        super(properties, GunDefinition.manual(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity));
        this.manaCost = manaCost;
    }

    public int getManaCost() {
        return manaCost;
    }

    public boolean consumeMana(ServerPlayer player, ItemStack gunStack) {
        return PlayerUtils.extractMana(player, gunStack, () -> manaCost);
    }

    public BaseBulletEntity createProjectile(ServerPlayer player, ItemStack bullet) {
        return new BaseBulletEntity(player, bullet) {
            @Override
            public DamageSource getDamageSource() {
                return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
            }
        };
    }

    public float getDamage() {
        return getDefinition().damage();
    }

    public float getInaccuracy() {
        return getDefinition().inaccuracy();
    }

    public float getVelocity() {
        return getDefinition().velocity();
    }

    public int getPenetrate() {
        return getDefinition().penetrate();
    }

    public float getKnockback() {
        return getDefinition().knockback();
    }

    public float getCritical() {
        return getDefinition().critical();
    }
}

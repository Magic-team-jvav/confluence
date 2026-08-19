package org.confluence.mod.common.item.gun;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.gun.definition.GunDefinition;
import org.confluence.mod.util.PlayerUtils;

/// 使用魔力而不是常规弹药的 Confluence 枪械扩展。
public class ManaGunItem extends BaseGun {
    private final int manaCost;

    public ManaGunItem(Properties properties, int cooldown, float damage, float velocity, float knockback, float critical, int penetrate, float inaccuracy, ModRarity rarity, int manaCost) {
        super(properties, GunDefinition.manual(cooldown, damage, velocity, knockback, critical, penetrate, inaccuracy, rarity));
        this.manaCost = manaCost;
    }

    public boolean consumeMana(ServerPlayer player, ItemStack gunStack) {
        return PlayerUtils.extractMana(player, gunStack, () -> manaCost);
    }

    public BaseBulletEntity createProjectile(ServerPlayer player, ItemStack bullet) {
        return new BaseBulletEntity(player, bullet) {
            @Override
            public DamageSource getDamageSource() {
                return LibDamageTypes.of(level(), LibDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
            }
        };
    }

    public int getManaCost() {
        return manaCost;
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

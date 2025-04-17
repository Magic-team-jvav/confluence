package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;

public class MagicDaggerProjectile extends ThrowableDropSelfProjectile {
    public MagicDaggerProjectile(EntityType<? extends ThrowableDropSelfProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public MagicDaggerProjectile(Player player) {
        super(ModEntities.MAGIC_DAGGER_PROJECTILE.get(), player.level());
    }

    @Override
    protected DamageSource getDamageSource() {
        return ModDamageTypes.of(level(), ModDamageTypes.MAGICAL_PROJECTILE, this, getOwner());
    }
}

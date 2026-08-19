package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.PaladinHammerProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 以旋转重锤进行远程攻击的地牢圣骑士。
public final class Paladin extends RangedMonster {
    public Paladin(EntityType<? extends Paladin> type, Level level) {
        super(type, level, 50, 1.1);
    }

    @Override
    protected Projectile createProjectile(LivingEntity target) {
        PaladinHammerProjectile projectile = ModEntities.PALADIN_HAMMER_PROJECTILE.get().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(this, target, (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * shotMultiplier()));
        return projectile;
    }
}

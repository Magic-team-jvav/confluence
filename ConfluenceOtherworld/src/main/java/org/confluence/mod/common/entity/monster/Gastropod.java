package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/**
 * 发射粉色能量弹幕的腹足怪。
 */
public final class Gastropod extends RangedMonster {
    public Gastropod(EntityType<? extends Gastropod> type, Level level) {
        super(type, level, 55, 0.8);
    }

    @Override
    protected Projectile createProjectile(LivingEntity target) {
        HostileParticleProjectile projectile =
                ModEntities.GASTROPOD_PROJECTILE.get().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(
                this,
                target,
                (float) (getAttributeValue(Attributes.ATTACK_DAMAGE)
                        * shotMultiplier()));
        return projectile;
    }
}

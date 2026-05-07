package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;

import java.util.function.UnaryOperator;

public class WaterBoltProjectile extends AbstractManaProjectile {
    public WaterBoltProjectile(EntityType<WaterBoltProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("water_stream"));
    }

    public WaterBoltProjectile(LivingEntity living) {
        this(ModEntities.WATER_BOLT_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doBouncyMove(false, () -> doCollisionCheck(5), UnaryOperator.identity());
        doAgeCheck(600);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 1.5, 0.2);
            doDiscardInMaxPenetrate(10);
        }
    }
}

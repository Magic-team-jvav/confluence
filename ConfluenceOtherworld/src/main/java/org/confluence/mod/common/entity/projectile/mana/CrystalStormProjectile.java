package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;

public class CrystalStormProjectile extends AbstractManaProjectile {
    public CrystalStormProjectile(EntityType<CrystalStormProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        withParticle(Confluence.asResource("crystal_storm_projectile_trail"));
    }

    public CrystalStormProjectile(LivingEntity living) {
        this(ModEntities.CRYSTAL_STORM_PROJECTILE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        setDeltaMovement(getDeltaMovement().scale(0.96));
        doBouncyMove(false, this::doNothing, vec3 -> vec3.scale(0.96));
        doAgeCheck(60);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        doHurtAndKnockback(result.getEntity(), 0.5, 0.2);
        discard();
    }
}

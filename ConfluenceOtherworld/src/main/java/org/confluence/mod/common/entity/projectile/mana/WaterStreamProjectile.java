package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;

public class WaterStreamProjectile extends AbstractManaProjectile {
    public WaterStreamProjectile(EntityType<WaterStreamProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("water_stream"));
    }

    public WaterStreamProjectile(LivingEntity living) {
        this(ModEntities.WATER_STREAM.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        setDeltaMovement(doSimpleMove().add(0.0, -0.24, 0.0));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 3.5, 0.2);
            doDiscardInMaxPenetrate(5);
        }
    }
}

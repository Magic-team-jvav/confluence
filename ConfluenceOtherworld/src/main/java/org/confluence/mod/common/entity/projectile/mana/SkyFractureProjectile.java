package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;

public class SkyFractureProjectile extends AbstractManaProjectile {
    public SkyFractureProjectile(EntityType<? extends SkyFractureProjectile> entityType, Level level) {
        super(entityType, level);
        withParticle(Confluence.asResource("spark_projectile"));
    }

    public SkyFractureProjectile(LivingEntity living) {
        this(ModEntities.SKY_FRACTURE.get(), living.level());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        doSimpleMove();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide && doHurtAndKnockback(result.getEntity(), 0.6, 0.2)) {
            discard();
        }
    }
}

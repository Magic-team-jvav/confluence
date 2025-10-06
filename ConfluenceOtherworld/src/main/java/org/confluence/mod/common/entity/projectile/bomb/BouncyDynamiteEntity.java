package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;

public class BouncyDynamiteEntity extends BaseDynamiteEntity {
    public BouncyDynamiteEntity(EntityType<BouncyDynamiteEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.8;
    }

    public BouncyDynamiteEntity(LivingEntity pShooter) {
        super(ModEntities.BOUNCY_DYNAMITE.get(), pShooter);
        this.bounceFactor = 0.8;
    }
}

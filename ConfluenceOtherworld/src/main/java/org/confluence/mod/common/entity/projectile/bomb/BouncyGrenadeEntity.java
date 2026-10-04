package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;

public class BouncyGrenadeEntity extends BaseGrenadeEntity {
    public BouncyGrenadeEntity(EntityType<? extends BaseGrenadeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.8;
    }

    public BouncyGrenadeEntity(LivingEntity shooter) {
        super(ModEntities.BOUNCY_GRENADE.get(), shooter);
        this.bounceFactor = 0.8;
    }
}

package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;

public class BouncyBombEntity extends BaseBombEntity {
    public BouncyBombEntity(EntityType<? extends BouncyBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.bounceFactor = 0.8;
    }

    public BouncyBombEntity(LivingEntity pShooter) {
        super(ModEntities.BOUNCY_BOMB_ENTITY.get(), pShooter);
        this.bounceFactor = 0.8;
    }
}

package org.confluence.mod.common.entity.projectile.bomb;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.entity.BeeProjectile;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public class BeenadeEntity extends BaseGrenadeEntity {
    public BeenadeEntity(EntityType<? extends BaseGrenadeEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BeenadeEntity(LivingEntity pShooter) {
        super(ModEntities.BEENADE.get(), pShooter);
    }

    @Override
    protected void explodeFunction() {
        super.explodeFunction();
        if (getOwner() instanceof LivingEntity living) {
            boolean hivePack = TCUtils.hasAccessoriesType(living, TCItems.HIVE$PACK);
            int amount = Mth.randomBetweenInclusive(living.getRandom(), 15, 20);
            if (hivePack && living.getRandom().nextFloat() < 0.3333F) amount++;
            for (int i = 0; i < amount; i++) {
                BeeProjectile bee = new BeeProjectile(level(), living, hivePack && living.getRandom().nextBoolean());
                bee.setBaseDamage(2.5F);
                bee.setPos(position());
                level().addFreshEntity(bee);
            }
        }
    }
}

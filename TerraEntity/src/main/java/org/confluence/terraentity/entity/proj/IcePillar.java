package org.confluence.terraentity.entity.proj;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.init.TEEffects;
import org.joml.Vector3f;

public class IcePillar extends BaseProj<IcePillar> {

    public final Vector3f axis;

    public IcePillar(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new MobEffectInstance(TEEffects.FROST_BURN, 100));
        this.canPenetrateBlock = true;
        axis = new Vector3f(
                this.level().random.nextFloat() - 0.5f,
                2,
                this.level().random.nextFloat() - 0.5f);


    }

    @Override
    public int getLifetime() {
        return 40;
    }
}

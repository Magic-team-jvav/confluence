package org.confluence.mod.mixin.client.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class ClientLivingEntityMixin implements IClientLivingEntity {
    @Unique
    private boolean confluence$deadO;
    @Unique
    private Vec3 confluence$deathMotion;

    @Override
    public boolean confluence$deadO(boolean... dead) {
        if (dead != null && dead.length != 0) {
            this.confluence$deadO = dead[0];
        }
        return confluence$deadO;
    }

    @Override
    public Vec3 confluence$deathMotion(Vec3... motion) {
        if (motion != null && motion.length > 0) {
            confluence$deathMotion = motion[0];
        }
        return confluence$deathMotion;
    }
}

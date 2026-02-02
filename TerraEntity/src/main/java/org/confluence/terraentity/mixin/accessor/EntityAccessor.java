package org.confluence.terraentity.mixin.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setVehicle(Entity vehicle);

    @Invoker
    void callRemovePassenger(Entity pPassenger);

    @Invoker
    Vec3 callCollide(Vec3 motion);
}

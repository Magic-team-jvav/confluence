package org.confluence.terraentity.mixin.accessor;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Invoker
    void callSetPosition(double x, double y, double z);

    @Invoker
    void callSetPosition(Vec3 pos);

}

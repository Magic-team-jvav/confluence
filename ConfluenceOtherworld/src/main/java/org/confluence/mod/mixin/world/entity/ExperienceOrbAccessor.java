package org.confluence.mod.mixin.world.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.entity.ExperienceOrb.class)
public interface ExperienceOrbAccessor {
    @Invoker
    static boolean callTryMergeToExisting(ServerLevel level, Vec3 pos, int amount) {
        throw new UnsupportedOperationException();
    }
}

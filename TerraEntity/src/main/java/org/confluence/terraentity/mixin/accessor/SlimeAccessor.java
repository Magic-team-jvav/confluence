package org.confluence.terraentity.mixin.accessor;

import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slime.class)
public interface SlimeAccessor {
    @Accessor
    boolean isWasOnGround();
}

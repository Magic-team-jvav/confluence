package org.confluence.terraentity.mixin.accessor;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {

    @Accessor
    void setXpReward(int xpReward);

    @Accessor
    int getXpReward();

}

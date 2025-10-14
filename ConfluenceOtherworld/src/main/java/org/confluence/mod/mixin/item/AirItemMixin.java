package org.confluence.mod.mixin.item;

import net.minecraft.world.item.AirItem;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AirItem.class)
public class AirItemMixin implements Immunity {
    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }
}

package org.confluence.mod.mixin.neoforge.common;

import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.confluence.mod.mixed.IDamageContainer;
import org.confluence.mod.util.SoulcererUtils.SoulDamageState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = DamageContainer.class, remap = false)
public abstract class DamageContainerMixin implements IDamageContainer {
    @Unique
    private final SoulDamageState confluence$soulDamageState = new SoulDamageState();

    @Override
    public SoulDamageState confluence$getSoulDamageState() {
        return confluence$soulDamageState;
    }
}

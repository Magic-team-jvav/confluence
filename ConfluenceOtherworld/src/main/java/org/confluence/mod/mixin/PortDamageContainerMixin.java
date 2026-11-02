package org.confluence.mod.mixin;

import org.confluence.mod.mixed.IDamageContainer;
import org.confluence.mod.util.SoulcererUtils.SoulDamageState;
import org.mesdag.portlib.wrapper.common.damagesource.PortDamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PortDamageContainer.class, remap = false)
public abstract class PortDamageContainerMixin implements IDamageContainer {
    @Unique
    private final SoulDamageState confluence$soulDamageState = new SoulDamageState();

    @Override
    public SoulDamageState confluence$getSoulDamageState() {
        return confluence$soulDamageState;
    }
}

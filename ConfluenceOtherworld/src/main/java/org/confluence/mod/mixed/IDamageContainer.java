package org.confluence.mod.mixed;

import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.confluence.mod.util.SoulcererUtils.SoulDamageState;

public interface IDamageContainer {
    SoulDamageState confluence$getSoulDamageState();

    static IDamageContainer of(DamageContainer container) {
        return (IDamageContainer) container;
    }
}

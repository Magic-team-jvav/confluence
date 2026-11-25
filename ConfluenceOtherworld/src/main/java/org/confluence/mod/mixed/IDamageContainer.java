package org.confluence.mod.mixed;

import org.confluence.mod.util.SoulcererUtils.SoulDamageState;
import org.mesdag.portlib.wrapper.common.damagesource.PortDamageContainer;

public interface IDamageContainer {
    SoulDamageState confluence$getSoulDamageState();

    static IDamageContainer of(PortDamageContainer container) {
        return (IDamageContainer) container;
    }
}

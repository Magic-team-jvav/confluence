package org.confluence.mod.mixed;

import org.mesdag.portlib.wrapper.common.damagesource.PortDamageContainer;
import org.confluence.mod.util.SoulcererUtils.SoulDamageState;

public interface IDamageContainer {
    SoulDamageState confluence$getSoulDamageState();

    static IDamageContainer of(PortDamageContainer container) {
        return (IDamageContainer) container;
    }
}

package org.confluence.mod.api.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.Event;

public class EffectSwitchableCheckEvent extends Event {
    protected final MobEffectInstance effectInstance;
    private final boolean originalSwitchable;
    private boolean switchable;

    public EffectSwitchableCheckEvent(MobEffectInstance effectInstance, boolean switchable) {
        this.effectInstance = effectInstance;
        this.originalSwitchable = switchable;
        this.switchable = switchable;
    }

    public MobEffectInstance getEffectInstance() {
        return effectInstance;
    }

    public boolean isOriginalSwitchable() {
        return originalSwitchable;
    }

    public void setSwitchable(boolean switchable) {
        this.switchable = switchable;
    }

    public boolean isSwitchable() {
        return switchable;
    }
}

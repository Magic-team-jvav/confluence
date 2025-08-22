package org.confluence.mod.mixed;

import net.minecraft.world.effect.MobEffectInstance;

public interface IMobEffectInstance {
    void confluence$setEnabled(boolean enabled);

    boolean confluence$isEnabled();

    static IMobEffectInstance of(MobEffectInstance instance) {
        return (IMobEffectInstance) instance;
    }
}

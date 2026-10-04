package org.confluence.mod.common.summoner.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;

public final class SummonerSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Confluence.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> USE_MINION_WEAPON = SOUNDS.register(
            "use_minion_weapon",
            () -> SoundEvent.createVariableRangeEvent(Confluence.asResource("use_minion_weapon"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> USE_TERRAPRISM = SOUNDS.register(
            "use_terraprism",
            () -> SoundEvent.createVariableRangeEvent(Confluence.asResource("use_terraprism"))
    );

    private SummonerSoundEvents() {
    }

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}

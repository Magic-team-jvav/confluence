package org.confluence.mod.integration.heaven_destiny_moment;

import net.neoforged.bus.api.IEventBus;
import org.confluence.mod.integration.heaven_destiny_moment.init.ModMapCodecRegisters;
import org.confluence.mod.integration.heaven_destiny_moment.init.ModMomentProbabilityFunction;

public final class HDMEvents {
    public static void register(IEventBus eventBus) {
        ModMapCodecRegisters.CONDITION_CODEC.register(eventBus);
        ModMomentProbabilityFunction.MOMENT_PROBABILITY_FUNCTION.register(eventBus);
    }
}
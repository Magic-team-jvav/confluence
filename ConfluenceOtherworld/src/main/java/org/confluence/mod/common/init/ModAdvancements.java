package org.confluence.mod.common.init;

import net.minecraft.advancements.CriteriaTriggers;
import org.confluence.mod.common.advancement.ShimmerTransmutationTrigger;

public final class ModAdvancements {
    public static void init() {}

    public static final ShimmerTransmutationTrigger SHIMMER_TRANSMUTATION = CriteriaTriggers.register(new ShimmerTransmutationTrigger());
}

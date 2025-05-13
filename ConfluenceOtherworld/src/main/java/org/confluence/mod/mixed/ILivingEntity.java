package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public interface ILivingEntity {
    void confluence$setBreakEasyCrashBlock(boolean breaking);

    boolean confluence$isBreakEasyCrashBlock();

    Object2IntMap<Immunity> confluence$getImmunityTicks();

    boolean confluence$deadO(boolean... dead);

    void confluence$setExtraInvulnerableTicks(int ticks);

    int confluence$getExtraInvulnerableTicks();
}

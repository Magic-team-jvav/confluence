package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.mixed.SelfGetter;

public interface ILivingEntity extends SelfGetter<LivingEntity> {
    void confluence$setBreakEasyCrashBlock(boolean breaking);

    boolean confluence$isBreakEasyCrashBlock();

    Object2IntMap<Immunity> confluence$getImmunityTicks();

    void confluence$setExtraInvulnerableTicks(int ticks);

    int confluence$getExtraInvulnerableTicks();

    static ILivingEntity of(LivingEntity living) {
        return (ILivingEntity) living;
    }
}

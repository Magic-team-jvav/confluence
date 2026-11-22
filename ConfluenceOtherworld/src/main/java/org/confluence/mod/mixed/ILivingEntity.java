package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.mixed.SelfGetter;

public interface ILivingEntity extends SelfGetter<LivingEntity> {
    void confluence$setBreakEasyCrashBlock(boolean breaking);

    boolean confluence$isBreakEasyCrashBlock();

    /// 键按对象身份比较，值为允许再次命中的世界刻（含该刻）。
    Reference2LongMap<Object> confluence$getImmunityDeadlines();

    void confluence$setExtraInvulnerableTicks(int ticks);

    int confluence$getExtraInvulnerableTicks();

    static ILivingEntity of(LivingEntity living) {
        return (ILivingEntity) living;
    }
}

package org.confluence.mod.integration.heaven_destiny_moment.context.condition;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.heaven_destiny_moment.common.automation.AutomationContext;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.ICondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.jetbrains.annotations.ApiStatus;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public record EvilBrokenCountCondition(int value) implements ICondition {
    public static final MapCodec<EvilBrokenCountCondition> CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("evil_broken_count")
            .xmap(EvilBrokenCountCondition::new, EvilBrokenCountCondition::value);

    @Override
    public boolean matches(AutomationContext context) {
        return ConfluenceData.get((ServerLevel) context.getLevel()).getEvilBrokenCount() >= value;
    }

    @Override
    public MapCodec<EvilBrokenCountCondition> codec() {
        return CODEC;
    }
}

package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record AlwaysTrueCondition() implements TradeCondition {
    public static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();
    public static final MapCodec<AlwaysTrueCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return true;
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.ALWAYS_TRUE.get();
    }
}

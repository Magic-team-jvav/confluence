package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record AnyBossDefeatedCondition() implements TradeCondition {
    public static final AnyBossDefeatedCondition INSTANCE = new AnyBossDefeatedCondition();
    public static final MapCodec<AnyBossDefeatedCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        return !KillBoard.INSTANCE.getDefeatedBosses().isEmpty();
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.ANY_BOSS_DEFEATED.get(); }
}

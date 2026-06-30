package org.confluence.mod.common.entity.npc.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.conditions.AndCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.NotCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.OrCondition;
import org.confluence.mod.common.init.ModCustomRegistries;

public interface TradeCondition {

    Codec<TradeCondition> CODEC = ModCustomRegistries.TRADE_CONDITIONS.byNameCodec().dispatch(TradeCondition::codec, MapCodec::codec);

    boolean test(ServerLevel level, BaseNPC npc);

    MapCodec<? extends TradeCondition> codec();

    default TradeCondition and(TradeCondition other) {
        return new AndCondition(this, other);
    }

    default TradeCondition or(TradeCondition other) {
        return new OrCondition(this, other);
    }

    default TradeCondition not() {
        return new NotCondition(this);
    }
}

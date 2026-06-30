package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record BestiaryCondition(int count) implements TradeCondition {
    public static final MapCodec<BestiaryCondition> CODEC = Codec.INT.fieldOf("count").xmap(BestiaryCondition::new, BestiaryCondition::count);

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        return Bestiary.INSTANCE.getUnlockedCount() >= count;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.BESTIARY.get(); }
}

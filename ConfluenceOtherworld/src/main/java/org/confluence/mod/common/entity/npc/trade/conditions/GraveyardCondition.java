package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record GraveyardCondition() implements TradeCondition {
    public static final GraveyardCondition INSTANCE = new GraveyardCondition();
    public static final MapCodec<GraveyardCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        EnvironmentLevelAccess access = new EnvironmentLevelAccess(npc.level(), npc.blockPosition());
        access.initializeIfNeeded(player);
        return EnvironmentLevelAccess.matcher(null, null, true).matches(access);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.GRAVEYARD.get();
    }
}

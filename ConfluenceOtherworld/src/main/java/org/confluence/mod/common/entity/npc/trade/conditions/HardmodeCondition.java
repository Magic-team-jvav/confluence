package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;
import org.confluence.mod.mixed.IMinecraftServer;

public record HardmodeCondition() implements TradeCondition {
    public static final HardmodeCondition INSTANCE = new HardmodeCondition();
    public static final MapCodec<HardmodeCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override public boolean test(ServerPlayer player, BaseNPC npc) {
        return IMinecraftServer.isHardmode(player.server);
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.HARDMODE.get(); }
}

package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;
import org.confluence.mod.mixed.IMinecraftServer;

public record WorldFlagCondition(long flag) implements TradeCondition {
    public static final MapCodec<WorldFlagCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.LONG.fieldOf("flag").forGetter(WorldFlagCondition::flag)
    ).apply(instance, WorldFlagCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return IMinecraftServer.matchesSecretFlag(player.server, flag);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.WORLD_FLAG.get();
    }
}

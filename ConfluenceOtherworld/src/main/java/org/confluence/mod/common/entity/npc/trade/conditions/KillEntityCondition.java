package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record KillEntityCondition(EntityType<?> entityType) implements TradeCondition {
    public static final MapCodec<KillEntityCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                            .fieldOf("entity").forGetter(KillEntityCondition::entityType)
            ).apply(b, KillEntityCondition::new));

    @Override public boolean test(ServerPlayer player, BaseNPC npc) {
        return player.getStats().getValue(Stats.ENTITY_KILLED.get(entityType)) > 0;
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.KILL_ENTITY.get(); }
}

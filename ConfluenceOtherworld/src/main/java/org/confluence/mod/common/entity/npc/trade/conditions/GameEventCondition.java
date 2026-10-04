package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.ModTradeConditions;

public record GameEventCondition(ResourceKey<? extends GameEvent> event) implements TradeCondition {
    public static final MapCodec<GameEventCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GameEvent.KEY_CODEC.fieldOf("event").forGetter(GameEventCondition::event)
    ).apply(instance, GameEventCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return GameEventSystem.INSTANCE.isEventStarted(event);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.GAME_EVENT.get();
    }
}

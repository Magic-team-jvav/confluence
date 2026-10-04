package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record NPCNearbyCondition(EntityType<?> npcType) implements TradeCondition {
    public static final MapCodec<NPCNearbyCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                            .fieldOf("npc").forGetter(NPCNearbyCondition::npcType)
            ).apply(b, NPCNearbyCondition::new));

    @Override public boolean test(ServerPlayer player, BaseNPC npc) {
        return !player.serverLevel().getEntitiesOfClass(BaseNPC.class,
                npc.getBoundingBox().inflate(32),
                n -> n != npc && n.getType() == npcType).isEmpty();
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.NPC_NEARBY.get(); }
}

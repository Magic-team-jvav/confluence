package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record BossDefeatedCondition(EntityType<?> bossType) implements TradeCondition {
    public static final MapCodec<BossDefeatedCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec()
                            .fieldOf("boss").forGetter(BossDefeatedCondition::bossType)
            ).apply(b, BossDefeatedCondition::new));

    @Override public boolean test(ServerLevel level, BaseNPC npc) {
        return KillBoard.INSTANCE.getDefeatedBosses().getBoolean(bossType);
    }
    @Override public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.BOSS_DEFEATED.get(); }
}

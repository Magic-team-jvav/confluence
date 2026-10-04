package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record DimensionCondition(ResourceKey<Level> dimension) implements TradeCondition {
    public static final MapCodec<DimensionCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimensionCondition::dimension)
    ).apply(instance, DimensionCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return npc.level().dimension().equals(dimension);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.DIMENSION.get();
    }
}

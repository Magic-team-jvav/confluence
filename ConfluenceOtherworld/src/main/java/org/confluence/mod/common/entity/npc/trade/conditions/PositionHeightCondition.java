package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record PositionHeightCondition(int minY, int maxY, boolean exclude) implements TradeCondition {
    public static final MapCodec<PositionHeightCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            Codec.INT.fieldOf("min_y").forGetter(PositionHeightCondition::minY),
            Codec.INT.fieldOf("max_y").forGetter(PositionHeightCondition::maxY),
            Codec.BOOL.optionalFieldOf("exclude", false).forGetter(PositionHeightCondition::exclude)
    ).apply(b, PositionHeightCondition::new));

    public PositionHeightCondition(int minY, int maxY) {
        this(minY, maxY, false);
    }

    public static PositionHeightCondition pos(int minY, int maxY) {
        return new PositionHeightCondition(minY, maxY);
    }

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        int y = npc.blockPosition().getY();
        boolean inRange = y >= minY && y <= maxY;
        return inRange != exclude;
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.POSITION_HEIGHT.get(); }
}

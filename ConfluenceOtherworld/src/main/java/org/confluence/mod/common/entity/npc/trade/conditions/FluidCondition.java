package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record FluidCondition(TagKey<Fluid> fluid) implements TradeCondition {
    public static final MapCodec<FluidCondition> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            TagKey.codec(Registries.FLUID).fieldOf("fluid").forGetter(FluidCondition::fluid)
    ).apply(b, FluidCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return player.isEyeInFluidType(fluid);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() { return ModTradeConditions.FLUID.get(); }
}

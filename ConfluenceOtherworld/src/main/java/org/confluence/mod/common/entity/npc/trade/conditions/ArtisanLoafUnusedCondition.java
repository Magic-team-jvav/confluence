package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

public record ArtisanLoafUnusedCondition() implements TradeCondition {
    public static final ArtisanLoafUnusedCondition INSTANCE = new ArtisanLoafUnusedCondition();
    public static final MapCodec<ArtisanLoafUnusedCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return !EverBeneficial.of(player).isArtisanLoafUsed();
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.ARTISAN_LOAF_UNUSED.get();
    }
}

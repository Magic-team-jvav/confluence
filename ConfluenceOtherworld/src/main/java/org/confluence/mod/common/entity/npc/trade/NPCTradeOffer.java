package org.confluence.mod.common.entity.npc.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.mesdag.portlib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;

import java.util.Optional;

/**
 * NPC 出售的商品——物品 + 条件。价格从 {@link org.confluence.mod.common.component.ValueComponent} 读取。
 */
public record NPCTradeOffer(ItemStack item, TradeCondition condition) {
    public static final Codec<NPCTradeOffer> CODEC = RecordCodecBuilder.create(b -> b.group(
            PortItemStackExtension.itemNonAirCodec().fieldOf("item").forGetter(NPCTradeOffer::item),
            TradeCondition.CODEC.optionalFieldOf("condition")
                    .forGetter(o -> o.condition instanceof AlwaysCondition ? Optional.empty() : Optional.of(o.condition))
    ).apply(b, (item, cond) -> new NPCTradeOffer(item, cond.orElse(AlwaysCondition.INSTANCE))));

    public boolean isAvailable(ServerLevel level, BaseNPC npc) {
        return condition.test(level, npc);
    }
}

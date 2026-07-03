package org.confluence.mod.common.entity.npc.trade;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;

/// NPC 出售的商品——物品 + 条件。价格从 [org.confluence.mod.common.component.ValueComponent] 读取。
public record NPCTradeOffer(ItemStack stack, TradeCondition condition) {
    public static final Codec<NPCTradeOffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortItemStackExtension.codec().fieldOf("item").forGetter(NPCTradeOffer::stack),
            PortCodecExtension.lenientOptionalFieldOf(TradeCondition.CODEC, "condition", TradeCondition.alwaysTrue()).forGetter(NPCTradeOffer::condition)
    ).apply(instance, NPCTradeOffer::new));

    public boolean isAvailable(ServerPlayer player, BaseNPC npc) {
        return condition.test(player, npc);
    }
}

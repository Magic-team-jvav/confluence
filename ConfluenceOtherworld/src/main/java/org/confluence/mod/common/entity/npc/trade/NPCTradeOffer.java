package org.confluence.mod.common.entity.npc.trade;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.Objects;

/// 数据包声明的 NPC 商品。
///
/// 商品价格不写入报价，而是在成交时由物品的 ValueComponent 和 NPC 心情计算。
public record NPCTradeOffer(ResourceLocation id, ItemStack stack, TradeCondition condition) {
    public static final Codec<NPCTradeOffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NPCTradeOffer::id),
            PortItemStackExtension.codec().fieldOf("item").forGetter(NPCTradeOffer::stack),
            PortCodecExtension.lenientOptionalFieldOf(TradeCondition.CODEC, "condition", TradeCondition.alwaysTrue()).forGetter(NPCTradeOffer::condition)
    ).apply(instance, NPCTradeOffer::new));

    public NPCTradeOffer {
        Objects.requireNonNull(id, "NPC trade offer id must not be null");
        Objects.requireNonNull(stack, "NPC trade result must not be null");
        Objects.requireNonNull(
                condition, "NPC trade condition must not be null");
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("NPC trade result cannot be empty");
        }
        stack = stack.copy();
    }

    /// 返回商品结果的独立副本。
    ///
    /// 外部调用方不能通过修改返回值污染已经加载的商店表。
    @Override
    public ItemStack stack() {
        return stack.copy();
    }

    public boolean isAvailable(ServerPlayer player, BaseNPC npc) {
        return condition.test(player, npc);
    }
}

package org.confluence.mod.common.entity.npc.trade;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.List;
import java.util.Objects;

/// 数据包声明的 NPC 商品。
///
/// 未声明材料花费时，价格在成交时由物品的 ValueComponent 和 NPC 心情计算。
public record NPCTradeOffer(ItemStack stack, List<ItemStack> costs, TradeCondition condition) {
    public static final Codec<NPCTradeOffer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(NPCTradeOffer::stack),
            PortCodecExtension.lenientOptionalFieldOf(ItemStack.CODEC.listOf(), "costs", List.of()).forGetter(NPCTradeOffer::costs),
            PortCodecExtension.lenientOptionalFieldOf(TradeCondition.CODEC, "condition", TradeCondition.alwaysTrue()).forGetter(NPCTradeOffer::condition)
    ).apply(instance, NPCTradeOffer::new));

    public NPCTradeOffer(ItemStack stack, TradeCondition condition) {
        this(stack, List.of(), condition);
    }

    public NPCTradeOffer {
        Objects.requireNonNull(stack, "NPC trade item must not be null");
        Objects.requireNonNull(costs, "NPC trade costs must not be null");
        Objects.requireNonNull(condition, "NPC trade condition must not be null");
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("NPC trade result cannot be empty");
        }
        if (costs.stream().anyMatch(ItemStack::isEmpty)) {
            throw new IllegalArgumentException("NPC trade costs cannot contain empty stacks");
        }
        stack = stack.copy();
        costs = List.copyOf(costs.stream().map(ItemStack::copy).toList());
    }

    /// 返回商品结果的独立副本。
    ///
    /// 外部调用方不能通过修改返回值污染已经加载的商店表。
    @Override
    public ItemStack stack() {
        return stack.copy();
    }

    @Override
    public List<ItemStack> costs() {
        return costs.stream().map(ItemStack::copy).toList();
    }

    public boolean isAvailable(ServerPlayer player, BaseNPC npc) {
        return condition.test(player, npc);
    }
}

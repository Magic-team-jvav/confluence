package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

/// NPC 主手持有指定物品时成立。
public record NPCItemInHandCondition(Item item) implements TradeCondition {
    public static final MapCodec<NPCItemInHandCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(NPCItemInHandCondition::item)).apply(instance, NPCItemInHandCondition::new));

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return npc.getMainHandItem().is(item);
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.NPC_ITEM_IN_HAND.get();
    }
}

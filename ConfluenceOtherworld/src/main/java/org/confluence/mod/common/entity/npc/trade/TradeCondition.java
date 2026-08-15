package org.confluence.mod.common.entity.npc.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.conditions.AlwaysTrueCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.AndCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.NotCondition;
import org.confluence.mod.common.entity.npc.trade.conditions.OrCondition;
import org.confluence.mod.common.init.ModCustomRegistries;

/**
 * NPC 报价的纯服务端可用性条件。
 *
 * <p>实现只能读取玩家、NPC 和世界状态并返回判断结果，不得执行扣款、发货、渲染或网络
 * 同步。附属模组可以向 {@link org.confluence.mod.common.init.ModCustomRegistries.Keys#TRADE_CONDITIONS}
 * 对应的 Forge 注册表注册自己的 {@link MapCodec}；数据包和 KubeJS 数据目录随后可以直接
 * 在报价 JSON 中引用该类型。</p>
 */
public interface TradeCondition {
    Codec<TradeCondition> CODEC = ModCustomRegistries.TRADE_CONDITIONS.byNameCodec().dispatch(TradeCondition::codec, MapCodec::codec);

    boolean test(ServerPlayer player, BaseNPC npc);

    MapCodec<? extends TradeCondition> codec();

    default TradeCondition and(TradeCondition other) {
        return new AndCondition(this, other);
    }

    default TradeCondition or(TradeCondition other) {
        return new OrCondition(this, other);
    }

    default TradeCondition not() {
        return new NotCondition(this);
    }

    static TradeCondition alwaysTrue() {
        return AlwaysTrueCondition.INSTANCE;
    }
}

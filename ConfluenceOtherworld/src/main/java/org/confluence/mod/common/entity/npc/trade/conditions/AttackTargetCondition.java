package org.confluence.mod.common.entity.npc.trade.conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.confluence.mod.common.init.ModTradeConditions;

/// NPC 当前拥有存活攻击目标时成立。
public enum AttackTargetCondition implements TradeCondition {
    INSTANCE;

    public static final MapCodec<AttackTargetCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ServerPlayer player, BaseNPC npc) {
        return npc.getTarget() != null && npc.getTarget().isAlive();
    }

    @Override
    public MapCodec<? extends TradeCondition> codec() {
        return ModTradeConditions.ATTACK_TARGET.get();
    }
}

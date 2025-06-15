package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade_lock.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.Arrays;
import java.util.List;

public record ConditionsLock(ITradeLock subLock, List<ICondition> conditions) implements ITradeLock {
    public static final MapCodec<ConditionsLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ITradeLock.TYPED_CODEC.fieldOf("sub_lock").forGetter(ConditionsLock::subLock),
            ICondition.LIST_CODEC.fieldOf("conditions").forGetter(ConditionsLock::conditions)
    ).apply(instance, ConditionsLock::new));

    public ConditionsLock(ITradeLock subLock, ICondition... conditions) {
        this(subLock, Arrays.stream(conditions).toList());
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return conditions.stream().allMatch(condition -> condition.test(ICondition.IContext.TAGS_INVALID)) && subLock.canTrade(player, npc, index);
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.CONDITIONS_LOCK.get();
    }
}

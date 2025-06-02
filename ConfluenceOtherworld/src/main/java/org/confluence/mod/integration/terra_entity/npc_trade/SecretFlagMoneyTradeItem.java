package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.api.SecretFlagMatcher;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

public class SecretFlagMoneyTradeItem extends MoneyTradeItem implements SecretFlagMatcher {
    public static final MapCodec<SecretFlagMoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MoneyTradeItem.CODEC.forGetter(t -> t),
            Impl.CODEC.forGetter(SecretFlagMatcher::asImpl)
    ).apply(instance, (moneyTradeItem, impl) -> new SecretFlagMoneyTradeItem(moneyTradeItem, impl.secretFlag(), impl.flipMatch())));
    private final long secretFlag;
    private final boolean flipMatch;

    public SecretFlagMoneyTradeItem(ItemStack result, @Nullable TradeProperties properties, long secretFlag, boolean flipMatch) {
        super(result, properties);
        this.secretFlag = secretFlag;
        this.flipMatch = flipMatch;
    }

    public SecretFlagMoneyTradeItem(MoneyTradeItem moneyTradeItem, long secretFlag, boolean flipMatch) {
        this(moneyTradeItem.result(), moneyTradeItem.properties(), secretFlag, flipMatch);
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        return matchesSecretFlag() && super.canTrade(player, npc, index);
    }

    @Override
    public long secretFlag() {
        return secretFlag;
    }

    @Override
    public boolean flipMatch() {
        return flipMatch;
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.SECRET_FLAG_MONEY_TRADE_ITEM.get();
    }
}

package org.confluence.mod.integration.terra_entity.npc_trade;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.terra_entity.init.ModTradeProviders;
import org.confluence.terraentity.entity.npc.trade.ITradeHolder;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class MoonPhaseMoneyTradeItem extends MoneyTradeItem {
    public static final MapCodec<MoonPhaseMoneyTradeItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MoneyTradeItem.CODEC.forGetter(moonPhaseMoneyTradeItem -> moonPhaseMoneyTradeItem),
            Codec.either(ExtraCodecs.nonEmptyList(MoonPhase.CODEC.listOf()), MoonPhase.CODEC).xmap(
                    either -> either.map(Function.identity(), List::of),
                    moonPhases -> moonPhases.size() == 1 ? Either.right(moonPhases.getFirst()) : Either.left(moonPhases)
            ).fieldOf("moon_phases").forGetter(MoonPhaseMoneyTradeItem::moonPhases)
    ).apply(instance, MoonPhaseMoneyTradeItem::new));
    private final List<MoonPhase> moonPhases;

    public MoonPhaseMoneyTradeItem(ItemStack result, @Nullable TradeProperties properties, List<MoonPhase> moonPhases) {
        super(result, properties);
        this.moonPhases = moonPhases;
    }

    public MoonPhaseMoneyTradeItem(MoneyTradeItem moneyTradeItem, List<MoonPhase> moonPhases) {
        this(moneyTradeItem.result(), moneyTradeItem.properties(), moonPhases);
    }

    public List<MoonPhase> moonPhases() {
        return moonPhases;
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        int phase = player.level().getMoonPhase();
        return moonPhases.stream().anyMatch(moonPhase -> moonPhase.match(phase)) && super.canTrade(player, npc, index);
    }

    @Override
    public TradeProvider getCodec() {
        return ModTradeProviders.MOON_PHASE_MONEY_TRADE_ITEM.get();
    }

    public enum MoonPhase implements StringRepresentable {
        FULL_MOON,
        WANING_GIBBOUS,
        THIRD_QUARTER,
        WANING_CRESCENT,
        NEW_MOON,
        WAXING_CRESCENT,
        FIRST_QUARTER,
        WAXING_GIBBOUS;

        public static final Codec<MoonPhase> CODEC = StringRepresentable.fromEnum(MoonPhase::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean match(Level level) {
            return match(level.getMoonPhase());
        }

        public boolean match(int moonPhase) {
            return moonPhase == ordinal();
        }
    }
}

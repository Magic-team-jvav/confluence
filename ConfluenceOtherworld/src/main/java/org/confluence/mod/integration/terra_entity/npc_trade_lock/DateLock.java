package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.nlf.calendar.Lunar;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.data.saved.DateStamp;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.mod.util.DateUtils;
import org.confluence.terraentity.api.trade.ITradeHolder;
import org.confluence.terraentity.api.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.Calendar;

public record DateLock(boolean isLunar, DateStamp fromInclusive, DateStamp toInclusive) implements ITradeLock {
    public static final DateLock HALLOWEENS = new DateLock(false, new DateStamp(Calendar.OCTOBER, 10), new DateStamp(Calendar.NOVEMBER, 1));
    public static final DateLock CHRISTMAS = new DateLock(false, new DateStamp(Calendar.DECEMBER, 15), new DateStamp(Calendar.DECEMBER, 31));
    public static final MapCodec<DateLock> CODEC = Codec.mapEither(Codec.STRING.xmap(string -> {
        if ("halloweens".equals(string)) return HALLOWEENS;
        if ("christmas".equals(string)) return CHRISTMAS;
        throw new IllegalArgumentException("Unknown date " + string);
    }, condition -> {
        if (condition == HALLOWEENS) return "halloweens";
        if (condition == CHRISTMAS) return "christmas";
        throw new IllegalArgumentException("Unknown date " + condition);
    }).fieldOf("preset"), RecordCodecBuilder.<DateLock>mapCodec(instance -> instance.group(
            Codec.BOOL.lenientOptionalFieldOf("is_lunar", false).forGetter(DateLock::isLunar),
            DateStamp.CODEC.fieldOf("from_inclusive").forGetter(DateLock::fromInclusive),
            DateStamp.CODEC.fieldOf("to_inclusive").forGetter(DateLock::toInclusive)
    ).apply(instance, DateLock::new))).xmap(Either::unwrap, Either::right);

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        int month, day;
        if (isLunar) {
            Lunar lunar = DateUtils.getLunar();
            month = Math.abs(lunar.getMonth());
            day = lunar.getDay();
        } else {
            Calendar calendar = DateUtils.getCalendar();
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
        }
        return month >= fromInclusive.month() && month <= toInclusive.month() &&
                day >= fromInclusive.day() && day <= toInclusive.day();
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.DATE_LOCK.get();
    }

    @Override
    public String toString() {
        return "DateLock{" +
                "isLunar=" + isLunar +
                ", fromInclusive=" + fromInclusive +
                ", toInclusive=" + toInclusive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof DateLock(boolean isLunar1, DateStamp fromInclusive1, DateStamp toInclusive1) &&
                isLunar1 == isLunar && fromInclusive1.equals(fromInclusive) && toInclusive1.equals(toInclusive);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 + (Boolean.hashCode(isLunar))) + fromInclusive.hashCode()) + toInclusive.hashCode();
    }
}

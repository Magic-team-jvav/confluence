package org.confluence.mod.common.loot;

import PortLib.extensions.com.mojang.datafixers.util.Either.PortEitherExtension;
import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.mod.api.lunar.Lunar;
import org.confluence.mod.common.data.saved.DateStamp;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.util.DateUtils;

import java.util.Calendar;

public record DateLootItemCondition(
        boolean isLunar,
        DateStamp fromInclusive,
        DateStamp toInclusive
) implements LootItemCondition {
    public static final DateLootItemCondition HALLOWEENS = new DateLootItemCondition(false, new DateStamp(Calendar.OCTOBER, 10), new DateStamp(Calendar.NOVEMBER, 1));
    public static final DateLootItemCondition CHRISTMAS = new DateLootItemCondition(false, new DateStamp(Calendar.DECEMBER, 15), new DateStamp(Calendar.DECEMBER, 31));
    public static final MapCodec<DateLootItemCondition> CODEC = Codec.mapEither(Codec.STRING.xmap(string -> {
        if ("halloweens".equals(string)) return HALLOWEENS;
        if ("christmas".equals(string)) return CHRISTMAS;
        throw new IllegalArgumentException("Unknown date " + string);
    }, condition -> {
        if (condition == HALLOWEENS) return "halloweens";
        if (condition == CHRISTMAS) return "christmas";
        throw new IllegalArgumentException("Unknown date " + condition);
    }).fieldOf("preset"), RecordCodecBuilder.<DateLootItemCondition>mapCodec(instance -> instance.group(
            PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "is_lunar", false).forGetter(DateLootItemCondition::isLunar),
            DateStamp.CODEC.fieldOf("from_inclusive").forGetter(DateLootItemCondition::fromInclusive),
            DateStamp.CODEC.fieldOf("to_inclusive").forGetter(DateLootItemCondition::toInclusive)
    ).apply(instance, DateLootItemCondition::new))).xmap(PortEitherExtension::unwrap, Either::right);

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.DATE.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
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
    public String toString() {
        return "DateLootItemCondition{" +
                "isLunar=" + isLunar +
                ", fromInclusive=" + fromInclusive +
                ", toInclusive=" + toInclusive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof DateLootItemCondition c &&
                c.isLunar == isLunar && c.fromInclusive.equals(fromInclusive) && c.toInclusive.equals(toInclusive);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 + (Boolean.hashCode(isLunar))) + fromInclusive.hashCode()) + toInclusive.hashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {
        private boolean isLunar;
        private DateStamp fromInclusive;
        private DateStamp toInclusive;

        public Builder lunar() {
            this.isLunar = true;
            return this;
        }

        public Builder from(int month, int day) {
            this.fromInclusive = new DateStamp(month, day);
            return this;
        }

        public Builder to(int month, int day) {
            this.toInclusive = new DateStamp(month, day);
            return this;
        }

        @Override
        public DateLootItemCondition build() {
            return new DateLootItemCondition(isLunar, fromInclusive, toInclusive);
        }
    }
}

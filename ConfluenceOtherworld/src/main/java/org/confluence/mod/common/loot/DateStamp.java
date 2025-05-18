package org.confluence.mod.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DateStamp(byte month, byte day) {
    public static final Codec<DateStamp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BYTE.fieldOf("month").forGetter(DateStamp::month),
            Codec.BYTE.fieldOf("day").forGetter(DateStamp::day)
    ).apply(instance, DateStamp::new));

    public DateStamp(int month, int day) {
        this((byte) month, (byte) day);
    }

    @Override
    public String toString() {
        return "DateStamp{" +
                "month=" + month +
                ", day=" + day +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        return o instanceof DateStamp(byte month1, byte day1) && day == day1 && month == month1;
    }

    @Override
    public int hashCode() {
        return 31 * (31 + month) + day;
    }
}

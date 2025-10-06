package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;

import java.util.Locale;

public enum MoonPhase implements StringRepresentable {
    /**
     * 满月
     */
    FULL_MOON,
    /**
     * 亏凸月
     */
    WANING_GIBBOUS,
    /**
     * 下弦月
     */
    THIRD_QUARTER,
    /**
     * 残月
     */
    WANING_CRESCENT,
    /**
     * 新月
     */
    NEW_MOON,
    /**
     * 峨嵋月
     */
    WAXING_CRESCENT,
    /**
     * 上弦月
     */
    FIRST_QUARTER,
    /**
     * 盈凸月
     */
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

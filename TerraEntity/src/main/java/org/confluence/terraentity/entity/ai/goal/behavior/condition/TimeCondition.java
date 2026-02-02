package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibDateUtils;

public class TimeCondition extends AbstractConditionLeaf {

    final int from;
    final int to;
    final Level level;

    public TimeCondition(int from, int to, Level level) {
        this.from = from;
        this.to = to;
        this.level = level;
    }

    @Override
    public boolean check() {
        int dayTime = (int) (level.dayTime() % 24000);
        if (from > to) {
            return (dayTime >= from || dayTime <= to);
        }
        return (dayTime >= from && dayTime <= to);
    }

    public static TimeCondition isDay(Level level) {
        return new TimeCondition(LibDateUtils._04$30, LibDateUtils._19$30, level);
    }

    public static TimeCondition isNight(Level level) {
        return new TimeCondition(LibDateUtils._19$30, LibDateUtils._04$30, level);
    }
}

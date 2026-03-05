package org.confluence.mod.mixed;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.confluence.lib.mixed.IExtraSyncedData;

public interface IAbstractArrow extends IExtraSyncedData<AbstractArrow> {
    @Deprecated
    default boolean confluence$isShootFromShortBow() {
        return confluence$isDamageNotAffectedBySpeedBonus();
    }

    @Deprecated
    default void confluence$setShootFromShortBow(boolean is) {
        confluence$setDamageNotAffectedBySpeedBonus(is);
    }

    /// 伤害不受速度影响
    boolean confluence$isDamageNotAffectedBySpeedBonus();

    void confluence$setDamageNotAffectedBySpeedBonus(boolean value);

    /// 落地是否立即消失
    boolean confluence$isDisappearingOnGround();

    void confluence$setDisappearingOnGround(boolean value);

    static IAbstractArrow of(AbstractArrow arrow) {
        return (IAbstractArrow) arrow;
    }

    String DNABSB_KEY = "confluence:damage_not_affected_by_speed_bonus";
    String DOG_KEY = "confluence:disappearing_on_ground";
}

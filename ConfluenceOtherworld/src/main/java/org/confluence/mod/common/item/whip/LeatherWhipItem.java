package org.confluence.mod.common.item.whip;

import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

import java.util.function.Supplier;

public final class LeatherWhipItem extends BaseWhipItem {
    public static final float TAG_DAMAGE = 1.0F;

    public LeatherWhipItem(Supplier<? extends SummonMarkType> summonMarkType) {
        super("leather_whip", 7F, 0.5F, 0.9F, 15, summonMarkType);
    }

    @Override
    public float damageFalloff() {return 0.5F;}

    @Override
    public float minimumDamageMultiplier() {return 0.0F;}
}

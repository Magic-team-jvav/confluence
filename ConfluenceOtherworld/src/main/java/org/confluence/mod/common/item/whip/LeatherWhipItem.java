package org.confluence.mod.common.item.whip;

import org.confluence.mod.api.whip.WhipTagEffect;

import java.util.function.Supplier;

public final class LeatherWhipItem extends BaseWhipItem {
    public static final float TAG_DAMAGE = 1.0F;

    public LeatherWhipItem(Supplier<? extends WhipTagEffect> tagEffect) {
        super("leather_whip", 7F, 0.5F, 0.9F, 15, tagEffect);
    }

    @Override
    public float damageFalloff() {return 0.5F;}

    @Override
    public float minimumDamageMultiplier() {return 0.0F;}
}

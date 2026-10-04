package org.confluence.mod.common.item.whip;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.confluence.mod.api.whip.WhipDirectHitContext;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;

import java.util.function.Supplier;

public final class SwampWhipItem extends BaseWhipItem {
    public static final float TAG_DAMAGE = 2.0F;

    public SwampWhipItem(Supplier<? extends SummonMarkType> summonMarkType) {
        super("swamp_whip", 13F, 0.6F, 1.6F, 15, summonMarkType);
    }

    @Override
    public void onDirectHit(WhipDirectHitContext context) {
        context.target().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40), context.owner());
    }
}

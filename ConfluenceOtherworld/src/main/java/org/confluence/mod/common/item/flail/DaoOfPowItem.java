package org.confluence.mod.common.item.flail;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.terra_curio.common.init.TCEffects;

/**
 * 太极连枷：命中时有较高概率使目标短暂困惑。
 */
public class DaoOfPowItem extends BaseFlailItem {
    private static final int CONFUSE_TICKS = 40;
    private final float confuseChance;

    public DaoOfPowItem(FlailComponent component, ModRarity rarity) {
        this(component, rarity, 0.8F);
    }

    DaoOfPowItem(
            FlailComponent component,
            ModRarity rarity,
            float confuseChance
    ) {
        super(component, rarity);
        if (confuseChance < 0.0F || confuseChance > 1.0F) {
            throw new IllegalArgumentException(
                    "Flail confuse chance must be between zero and one");
        }
        this.confuseChance = confuseChance;
    }

    @Override
    public void onFlailHit(
            Player owner,
            LivingEntity target,
            BaseFlailEntity flail
    ) {
        if (target.getRandom().nextFloat() < confuseChance) {
            target.addEffect(new MobEffectInstance(
                    TCEffects.CONFUSED.get(),
                    CONFUSE_TICKS));
        }
    }
}

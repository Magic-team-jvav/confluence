package org.confluence.mod.common.item.sword.stagedy;


import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.item.sword.BaseSwordItem.ModifierBuilder;
import org.confluence.mod.common.item.sword.stagedy.projectile.IProjContainer;
import org.confluence.terraentity.hit_effect.EffectStrategy;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.stagedy.InventoryTickStrategy.UMBRELLA_TICK;

/**
 * 这个类用于组合各种剑的特殊技能 strategy，以便于快速注册
 * @author coffee
 */
public class SwordPrefabs {

    /**普通短剑*/
    public static final Supplier<ModifierBuilder> SHORT_SWORD = ()->new ModifierBuilder()
            .setCanNotPerformSweep();

    /**普通宽剑*/
    public static final Function<Float,ModifierBuilder>  BOARD_SWORD = (range)->new ModifierBuilder()
            .setSweepRange(range);

    /**一般宽剑*/
    public static final Supplier<ModifierBuilder>  NORMAL_SWORD = ()->BOARD_SWORD.apply(1.5f);

    /** 弹幕剑*/
    public static final Function<Supplier<? extends IProjContainer> ,ModifierBuilder> PROJ_SWORD = (strategy)->new ModifierBuilder()
            .setProj(strategy);

    /** 效果剑*/
    public static final Function<DeferredHolder<EffectStrategy,EffectStrategy> , ModifierBuilder>  EFFECT_SWORD = (effect)->new ModifierBuilder()
            .addOnHitEffect(effect);


    /**特殊类*/
        // 雨伞
    public static final Supplier<ModifierBuilder> UMBRELLA_SWORD = ()->SHORT_SWORD.get()
            .setInventoryTick(UMBRELLA_TICK);

}

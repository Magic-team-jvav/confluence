package org.confluence.mod.common.item.sword.legacy;


import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.item.sword.BaseSwordItem.ModifierBuilder;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.item.sword.legacy.SwordStrategy.UMBRELLA_TICK;

/// 这个类用于组合各种剑的特殊技能 strategy，以便于快速注册
///
/// @author coffee
public class SwordPrefabs {
    /// 普通短剑
    public static final Supplier<ModifierBuilder> SHORT_SWORD = () -> new ModifierBuilder().setCanNotPerformSweep();

    /// 普通宽剑
    public static final Function<Float, ModifierBuilder> BOARD_SWORD = ratio -> withSpecialSweep(ratio, new ModifierBuilder());

    /// 一般宽剑
    public static final Supplier<ModifierBuilder> NORMAL_SWORD = ModifierBuilder::new;

    /// 弹幕剑
    public static final Function<Supplier<SwordProjectileComponent>, ModifierBuilder> PROJ_SWORD = (strategy) -> new ModifierBuilder().setProj(strategy);

    /// 效果剑
    public static final Function<EffectStrategyComponent, ModifierBuilder> EFFECT_SWORD = (effect) -> new ModifierBuilder().setOnHitEffect(effect);

    /// 特殊类
    // 雨伞
    public static final Supplier<ModifierBuilder> UMBRELLA_SWORD = () -> SHORT_SWORD.get().setInventoryTick(UMBRELLA_TICK);

    public static ModifierBuilder withSpecialSweep(float ratio, ModifierBuilder builder) {
        builder.setSpecialSweep();
        if (ratio > 0.0F) {
            return builder.addAttributeModifier(PortAttributesExtension.sweepingDamageRatio(), ratio, PortAttributeModifier.PortOperation.ADD_VALUE);
        }
        return builder;
    }
}

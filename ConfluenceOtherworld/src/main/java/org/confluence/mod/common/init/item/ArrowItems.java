package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.item.bow.BaseArrowItem;
import org.confluence.terraentity.init.TEEffectStrategies;

public class ArrowItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    // 以木箭MC4伤，TR5伤为起点，TR伤害每+1，MC伤害+0.2
    public static final DeferredItem<BaseArrowItem> FLAMING_ARROW = ITEMS.register("flaming_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/flaming_arrow.png", () -> new BaseArrowEntity.Builder().setParticleId(Confluence.asResource("flaming_arrow_flame"))
                    .setDamage(4.4f).setCauseFire(10 * 20).setLuminance(12))
    ));
    public static final DeferredItem<BaseArrowItem> UNHOLY_ARROW = ITEMS.register("unholy_arrow", () -> new BaseArrowItem(ModRarity.BLUE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/unholy_arrow.png", () -> new BaseArrowEntity.Builder()
                    .setDamage(5.4f).setPenetration(5).setKnockBack(1.5f))
    ));
    public static final DeferredItem<BaseArrowItem> STAR_ARROW = ITEMS.register("star_arrow", () -> new BaseArrowItem(ModRarity.BLUE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/star_arrow.png", () -> new BaseArrowEntity.Builder().setParticleId(Confluence.asResource("falling_star"))
                    .setDamage(5f).setPenetration(99).setKnockBack(2).setSpeedFactor(0.8f).setAutoDiscard(50).setGravity(0).setLuminance(8))
    ));
    public static final DeferredItem<BaseArrowItem> HELLFIRE_ARROW = ITEMS.register("hellfire_arrow", () -> new BaseArrowItem(ModRarity.GREEN,
            BaseArrowEntity.Factory.create("textures/entity/arrow/hellfire_arrow.png", () -> new BaseArrowEntity.Builder().setParticleId(Confluence.asResource("ball_of_fire_trail"))
                    .setDamage(5.6f).setCauseFire(5 * 20).addOnHitEffect(TEEffectStrategies.Components.HELL_FIRE_EFFECT.get()).setLuminance(8))
    ));
    public static final DeferredItem<BaseArrowItem> FROSTBURN_ARROW = ITEMS.register("frostburn_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/frostburn_arrow.png", () -> new BaseArrowEntity.Builder().setParticleId(Confluence.asResource("frost_projectile"))
                    .setDamage(4.4f).addOnHitEffect(TEEffectStrategies.Components.FROST_BURN_EFFECT.get()).setLuminance(5))
    ));
    public static final DeferredItem<BaseArrowItem> BONE_ARROW = ITEMS.register("bone_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/bone_arrow.png", () -> new BaseArrowEntity.Builder()
                    .setDamage(4.6f).setKnockBack(1.75F))
    ));
    public static final DeferredItem<BaseArrowItem> SHIMMER_ARROW = ITEMS.register("shimmer_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/shimmer_arrow.png", () -> new BaseArrowEntity.Builder()
                    .setDamage(5.4f).setKnockBack(1.5F).setGravity(-0.05F).setAutoDiscard(1200))
    ));
    public static final DeferredItem<BaseArrowItem> FOSSIL_ARROW = ITEMS.register("fossil_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/fossil_arrow.png", () -> new BaseArrowEntity.Builder()
                    .setDamage(4.4f).setPenetration(2))
    ));
    public static final DeferredItem<BaseArrowItem> FLY_FISH_ARROW = ITEMS.register("fly_fish_arrow", () -> new BaseArrowItem(ModRarity.WHITE,
            BaseArrowEntity.Factory.create("textures/entity/arrow/fly_fish_arrow.png", () -> new BaseArrowEntity.Builder()
                    .setDamage(4.2f).setDamageInRain(3).setSpeedUpInRain(1.5f).setSpeedInertiaInWater(0.8f))
    ));
}

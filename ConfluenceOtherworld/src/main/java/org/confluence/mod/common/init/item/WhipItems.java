package org.confluence.mod.common.init.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.whip.*;
import org.confluence.mod.api.whip.curve.WhipCurves;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.whip.BaseWhipItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

/// 鞭子物品注册。
///
/// <p>每种普通鞭子只在这里声明数值和效果差异。发射、轨迹、碰撞、耐久和按键配置由
/// {@link BaseWhipItem} 统一处理；对应的独立鞭痕效果也在同一次声明中注册。</p>
public final class WhipItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    /// 当前鞭节模型沿纵轴占四个像素，因此使用四像素间距首尾衔接。
    private static final int DEFAULT_SEGMENT_SPACING_PIXELS = 4;
    private static final List<RegistryObject<WhipTagEffect>> TAG_EFFECTS = new ArrayList<>();

    public static final PortDeferredItem<BaseWhipItem> LEATHER_WHIP =
            register(
                    "leather_whip",
                    10.0F,
                    1.0F,
                    0.5F,
                    0.9F,
                    15,
                    200,
                    List.of(),
                    List.of(context -> context.summon().addEffect(
                            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100),
                            context.owner()
                    ))
            );
    public static final PortDeferredItem<BaseWhipItem> SLUB_WHIP = register("slub_whip", 8.0F, 1.0F, 0.2F, 0.5F, 15, 300);
    public static final PortDeferredItem<BaseWhipItem> RUBY_WHIP = register("ruby_whip", 12.7F, 1.0F, 0.5F, 0.8F, 15, 760);
    public static final PortDeferredItem<BaseWhipItem> AMBER_WHIP = register("amber_whip", 12.7F, 1.0F, 0.5F, 0.8F, 15, 740);
    public static final PortDeferredItem<BaseWhipItem> TOPAZ_WHIP = register("topaz_whip", 12.5F, 1.0F, 0.5F, 0.8F, 15, 700);
    public static final PortDeferredItem<BaseWhipItem> JADE_WHIP = register("jade_whip", 12.6F, 1.0F, 0.5F, 0.8F, 15, 900);
    public static final PortDeferredItem<BaseWhipItem> DIAMOND_WHIP = register("diamond_whip", 12.8F, 1.0F, 0.5F, 0.8F, 15, 1000);
    public static final PortDeferredItem<BaseWhipItem> SAPPHIRE_WHIP = register("sapphire_whip", 12.6F, 1.0F, 0.5F, 0.8F, 15, 720);
    public static final PortDeferredItem<BaseWhipItem> AMETHYST_WHIP = register("amethyst_whip", 12.5F, 1.0F, 0.5F, 0.8F, 15, 700);
    public static final PortDeferredItem<BaseWhipItem> SWAMP_WHIP = register(
            "swamp_whip",
            16.0F,
            2.0F,
            0.6F,
            1.6F,
            15,
            1200,
            context -> context.target().addEffect(
                    new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40),
                    context.owner()
            )
    );
    public static final PortDeferredItem<BaseWhipItem> SNAPTHORN = register(
            "snapthorn",
            18.0F,
            3.0F,
            0.7F,
            1.85F,
            15,
            3600,
            context -> context.target().addEffect(
                    new MobEffectInstance(MobEffects.POISON, 60, 1),
                    context.owner()
            )
    );
    public static final PortDeferredItem<BaseWhipItem> SPINAL_TAP = register("spinal_tap", 29.0F, 4.0F, 0.8F, 1.6F, 13, 3600);
    public static final PortDeferredItem<BaseWhipItem> FIRECRACKER = register(
            "firecracker",
            37.0F,
            0.0F,
            0.5F,
            1.85F,
            15,
            3600,
            context -> context.target().addEffect(
                    new MobEffectInstance(ModEffects.HELLFIRE.get(), 40),
                    context.owner()
            )
    );

    private WhipItems() {}

    public static void init() {}

    /// 返回本体注册的全部鞭痕效果，供免疫 DataMap 和覆盖检查复用。
    public static List<RegistryObject<WhipTagEffect>> whipTagEffects() {
        return List.copyOf(TAG_EFFECTS);
    }

    private static PortDeferredItem<BaseWhipItem> register(String name, float damage, float tagDamage, float attackSpeedModifier, float range, int hitCooldownTicks, int durability, WhipDirectHitEffect... directEffects) {
        return register(name, damage, tagDamage, attackSpeedModifier, range, hitCooldownTicks, durability, List.of(directEffects), List.of());
    }

    private static PortDeferredItem<BaseWhipItem> register(
            String name,
            float damage,
            float tagDamage,
            float attackSpeedModifier,
            float range,
            int hitCooldownTicks,
            int durability,
            List<WhipDirectHitEffect> directEffects,
            List<WhipFriendlyHitEffect> friendlyEffects
    ) {
        int baseDurationTicks = Math.max(1, (int) (80.0 / (4.0 * (1.0 + attackSpeedModifier))));
        RegistryObject<WhipTagEffect> tagEffect = ModEffects.registerWhipTag(name, tagDamage);
        TAG_EFFECTS.add(tagEffect);
        WhipDefinition definition = new WhipDefinition(baseDurationTicks, hitCooldownTicks, damage, range, 0.8F, 0.2F, false, WhipCurves.DEFAULT, directEffects, friendlyEffects, tagEffect);
        PortItemAttributeModifiers attributes = PortItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new PortAttributeModifier(Confluence.asResource("whip_attack_speed_modifier"), attackSpeedModifier, PortAttributeModifier.Operation.ADD_MULTIPLIED_BASE), PortEquipmentSlotGroup.MAINHAND)
                .add(ConfluenceMagicLib.WHIP_RANGE, new PortAttributeModifier(Confluence.asResource("whip_range_modifier"), range, PortAttributeModifier.Operation.ADD_MULTIPLIED_BASE), PortEquipmentSlotGroup.MAINHAND)
                .build();
        return ITEMS.register(name, () -> new BaseWhipItem(
                new Item.Properties()
                        .stacksTo(1)
                        .durability(durability)
                        .attributes(attributes),
                definition,
                WhipAppearance.segments(WhipSegment.fixedSpacing(
                        Confluence.asResource("item/whip_segments/" + name),
                        DEFAULT_SEGMENT_SPACING_PIXELS
                ))
        ));
    }
}

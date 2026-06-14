package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.ArrowInBowRenderer;
import org.confluence.mod.common.entity.projectile.range.arrow.BeeArrow;
import org.confluence.mod.common.entity.projectile.range.arrow.DriveAwayArrow;
import org.confluence.mod.common.entity.projectile.range.arrow.HellBatArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.bow.DaedalusStormbow;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Function;

/// 弓箭位置修正参考[ArrowInBowRenderer]
public class BowItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    // 短弓
    public static final PortDeferredItem<ShortBowItem> WOODEN_SHORT_BOW = ITEMS.register("wooden_short_bow", () -> new ShortBowItem(4.0F, 384));
    public static final PortDeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = ITEMS.register("ebonwood_short_bow", () -> new ShortBowItem(4.3F, 404));
    public static final PortDeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = ITEMS.register("shadewood_short_bow", () -> new ShortBowItem(4.4F, 424));
    public static final PortDeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = ITEMS.register("ash_wood_short_bow", () -> new ShortBowItem(4.5F, 444));
    public static final PortDeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = ITEMS.register("pearlwood_short_bow", () -> new ShortBowItem(5.0F, 1000));
    public static final PortDeferredItem<ShortBowItem> COPPER_SHORT_BOW = ITEMS.register("copper_short_bow", () -> new ShortBowItem(4.5F, 640));
    public static final PortDeferredItem<ShortBowItem> TIN_SHORT_BOW = ITEMS.register("tin_short_bow", () -> new ShortBowItem(4.5F, 768));
    public static final PortDeferredItem<ShortBowItem> IRON_SHORT_BOW = ITEMS.register("iron_short_bow", () -> new ShortBowItem(5.0F, 896));
    public static final PortDeferredItem<ShortBowItem> LEAD_SHORT_BOW = ITEMS.register("lead_short_bow", () -> new ShortBowItem(5.0F, 1024));
    public static final PortDeferredItem<ShortBowItem> SILVER_SHORT_BOW = ITEMS.register("silver_short_bow", () -> new ShortBowItem(5.5F, 1152));
    public static final PortDeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = ITEMS.register("tungsten_short_bow", () -> new ShortBowItem(5.5F, 1280));
    public static final PortDeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = ITEMS.register("golden_short_bow", () -> new ShortBowItem(6.0F, 1408));
    public static final PortDeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = ITEMS.register("platinum_short_bow", () -> new ShortBowItem(6.0F, 1536));


    // 无效果蓄力弓
    public static final PortDeferredItem<BaseTerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", 3.0F, 404);
    public static final PortDeferredItem<BaseTerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", 3.1F, 424);
    public static final PortDeferredItem<BaseTerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", 3.2F, 444);
    public static final PortDeferredItem<BaseTerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", 3.5F, 1000);
    public static final PortDeferredItem<BaseTerraBowItem> COPPER_BOW = register("copper_bow", 3.0F, 640);
    public static final PortDeferredItem<BaseTerraBowItem> TIN_BOW = register("tin_bow", 3.0F, 768);
    public static final PortDeferredItem<BaseTerraBowItem> IRON_BOW = register("iron_bow", 3.5F, 896);
    public static final PortDeferredItem<BaseTerraBowItem> LEAD_BOW = register("lead_bow", 3.5F, 1024);
    public static final PortDeferredItem<BaseTerraBowItem> SILVER_BOW = register("silver_bow", 4.0F, 1152);
    public static final PortDeferredItem<BaseTerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", 4.0F, 1280);
    public static final PortDeferredItem<BaseTerraBowItem> GOLDEN_BOW = register("golden_bow", 4.5F, 1408);
    public static final PortDeferredItem<BaseTerraBowItem> PLATINUM_BOW = register("platinum_bow", 4.5F, 1536);

    // DIY蓄力弓
    /**
     * 如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}
     */

    public static final PortDeferredItem<BaseTerraBowItem> FOSSIL_BOW = register("fossil_bow", 4.6F, m -> m
            .setRarity(ModRarity.BLUE)
            .setArrowTransform(ArrowItems.FOSSIL_ARROW.get())
    );
    public static final PortDeferredItem<BaseTerraBowItem> HUNTING_BOW = register("hunting_bow", 3.5F, m -> m
            .setRarity(ModRarity.BLUE)
            .setOnHitEffect(TEEffectStrategies.Components.HUNTING_RIFLE_EFFECT.get())
    );
    public static final PortDeferredItem<BaseTerraBowItem> DEMON_BOW = register("demon_bow", 4.9F, m -> m
            .setRarity(ModRarity.BLUE)
            .setFullPullHitEffect(ModEffectStrategies.Components.LIGHTS_BANE_EFFECT.get())
    );
    public static final PortDeferredItem<BaseTerraBowItem> TENDON_BOW = register("tendon_bow", 5.2F, m -> m
            .setRarity(ModRarity.BLUE)
            .setFullPullHitEffect(ModEffectStrategies.Components.BLOOD_BUTCHERED_EFFECT.get())
    );
    public static final PortDeferredItem<BaseTerraBowItem> MOLTEN_FURY = register("molten_fury", 5.8F, m -> m
            .setRarity(ModRarity.ORANGE)
            .setArrowTransform(ArrowItems.HELLFIRE_ARROW.get())
    );
    public static final PortDeferredItem<BaseTerraBowItem> THE_BEES_KNEES = register("the_bees_knees", 6.7F, m -> m
            .setRarity(ModRarity.YELLOW)
            .setMultiShoot(3, (shootingIndex, shootingTotality) -> new Vec3(-shootingIndex * 0.25f, 0, 0))
            .setCanMultiShoot(ammo -> !(ammo.getItem() instanceof BaseTerraArrowItem))
            .setEntityTransform(BaseTerraArrowItem.EntityTransform.create(ModEntities.BEE_ARROW.get(), BeeArrow::new))
    );
    public static final PortDeferredItem<BaseTerraBowItem> HELLWING_BOW = register("hellwing_bow", 7.5f, m -> m
            .setRarity(ModRarity.RED)
            .setInaccuracy(1f)
            .setEntityTransform(BaseTerraArrowItem.EntityTransform.create(ModEntities.HELL_BAT_ARROW.get(), HellBatArrowEntity::new))
    );

    // 稻草人弓 - 驱离鸟妖，对飞行单位造成1.5倍伤害
    public static final PortDeferredItem<BaseTerraBowItem> SCAREBOW = register("scarebow", 3.5F, m -> m
            .setRarity(ModRarity.BLUE)
            .setEntityTransform(BaseTerraArrowItem.EntityTransform.create(ModEntities.DRIVE_AWAY_ARROW.get(), DriveAwayArrow::create))
    );

    // 代达罗斯风暴弓
    public static final PortDeferredItem<BaseTerraBowItem> DAEDALUS_STORM_BOW = register("daedalus_storm_bow", () -> new DaedalusStormbow(12f, ModRarity.PURPLE));


    public static final PortDeferredItem<BaseTerraBowItem> DEVELOPER_BOW = register("developer_bow", 9999F,
            m -> m.setRarity(ModRarity.MASTER).addModifyArrowBuilder(
                    modifier -> modifier.setCauseFire(200)
                            .setDamage(9999)
                            .setSpeedFactor(2)
                            .setPenetration(9999)
                            .setGravity(0)
            ));


    public static PortDeferredItem<BaseTerraBowItem> register(String name, Supplier<BaseTerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /// 注册效果修饰的有耐久弓
    public static PortDeferredItem<BaseTerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new BaseTerraBowItem(damage, new BaseTerraArrowItem.ModifyArrowBuilder().setDuration(durability)));
    }

    /// 注册带有效果修饰的无耐久弓
    public static PortDeferredItem<BaseTerraBowItem> register(String name, float damage, Function<BaseTerraArrowItem.ModifyArrowBuilder, BaseTerraArrowItem.ModifyArrowBuilder> modifier) {
        return register(name, () -> new BaseTerraBowItem(damage, modifier.apply(new BaseTerraArrowItem.ModifyArrowBuilder().setUnBreakable())));
    }
}

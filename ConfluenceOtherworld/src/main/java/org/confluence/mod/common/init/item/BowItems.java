package org.confluence.mod.common.init.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.common.entity.projectile.range.arrow.BeeArrow;
import org.confluence.mod.common.entity.projectile.range.arrow.HellBatArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.bow.BaseArrowItem;
import org.confluence.mod.common.item.bow.DaedalusStormbow;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.confluence.mod.integration.terra_entity.init.ModEffectStrategies;
import org.confluence.terraentity.init.TEEffectStrategies;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 弓箭位置修正参考{@link ArrowInBowHud}
 *
 */
public class BowItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 短弓
    public static final DeferredItem<ShortBowItem> WOODEN_SHORT_BOW = ITEMS.register("wooden_short_bow",() -> new ShortBowItem( 4.0F, 384));
    public static final DeferredItem<ShortBowItem> EBONWOOD_SHORT_BOW = ITEMS.register("ebonwood_short_bow",() -> new ShortBowItem( 4.3F, 404));
    public static final DeferredItem<ShortBowItem> SHADEWOOD_SHORT_BOW = ITEMS.register("shadewood_short_bow",() -> new ShortBowItem( 4.4F, 424));
    public static final DeferredItem<ShortBowItem> ASH_WOOD_SHORT_BOW = ITEMS.register("ash_wood_short_bow",() -> new ShortBowItem( 4.5F, 444));
    public static final DeferredItem<ShortBowItem> PEARLWOOD_SHORT_BOW = ITEMS.register("pearlwood_short_bow",() -> new ShortBowItem( 5.0F, 1000));
    public static final DeferredItem<ShortBowItem> COPPER_SHORT_BOW = ITEMS.register("copper_short_bow", () -> new ShortBowItem(4.5F, 640));
    public static final DeferredItem<ShortBowItem> TIN_SHORT_BOW = ITEMS.register("tin_short_bow", () -> new ShortBowItem(4.5F, 768));
    public static final DeferredItem<ShortBowItem> IRON_SHORT_BOW = ITEMS.register("iron_short_bow", () -> new ShortBowItem(5.0F, 896));
    public static final DeferredItem<ShortBowItem> LEAD_SHORT_BOW = ITEMS.register("lead_short_bow", () -> new ShortBowItem(5.0F, 1024));
    public static final DeferredItem<ShortBowItem> SILVER_SHORT_BOW = ITEMS.register("silver_short_bow", () -> new ShortBowItem(5.5F, 1152));
    public static final DeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = ITEMS.register("tungsten_short_bow", () -> new ShortBowItem(5.5F, 1280));
    public static final DeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = ITEMS.register("golden_short_bow", () -> new ShortBowItem(6.0F, 1408));
    public static final DeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = ITEMS.register("platinum_short_bow", () -> new ShortBowItem(6.0F, 1536));


    // 无效果蓄力弓
    public static final DeferredItem<TerraBowItem> EBONWOOD_BOW = register("ebonwood_bow", 3.0F, 404);
    public static final DeferredItem<TerraBowItem> SHADEWOOD_BOW = register("shadewood_bow", 3.1F, 424);
    public static final DeferredItem<TerraBowItem> ASH_WOOD_BOW = register("ash_wood_bow", 3.2F, 444);
    public static final DeferredItem<TerraBowItem> PEARLWOOD_BOW = register("pearlwood_bow", 3.5F, 1000);
    public static final DeferredItem<TerraBowItem> COPPER_BOW = register("copper_bow", 3.0F, 640);
    public static final DeferredItem<TerraBowItem> TIN_BOW = register("tin_bow", 3.0F, 768);
    public static final DeferredItem<TerraBowItem> IRON_BOW = register("iron_bow", 3.5F, 896);
    public static final DeferredItem<TerraBowItem> LEAD_BOW = register("lead_bow", 3.5F, 1024);
    public static final DeferredItem<TerraBowItem> SILVER_BOW = register("silver_bow", 4.0F, 1152);
    public static final DeferredItem<TerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", 4.0F, 1280);
    public static final DeferredItem<TerraBowItem> GOLDEN_BOW = register("golden_bow", 4.5F, 1408);
    public static final DeferredItem<TerraBowItem> PLATINUM_BOW = register("platinum_bow", 4.5F, 1536);

    // DIY蓄力弓
    /**如果需要速射，加上tag {@link org.confluence.mod.common.init.ModTags.Items#FAST_BOW}*/

    public static final DeferredItem<TerraBowItem> FOSSIL_BOW = register("fossil_bow", 4.6F, m->m
            .setRarity(ModRarity.BLUE)
            .setArrowTransform(ArrowItems.FOSSIL_ARROW.get())
    );
    public static final DeferredItem<TerraBowItem> HUNTING_BOW = register("hunting_bow", 3.5F, m->m
            .setRarity(ModRarity.BLUE)
            .setOnHitEffect(TEEffectStrategies.Components.HUNTING_RIFLE_EFFECT.get())
    );
    public static final DeferredItem<TerraBowItem> DEMON_BOW = register("demon_bow", 4.7F, m->m
            .setRarity(ModRarity.BLUE)
            .setFullPullHitEffect(ModEffectStrategies.Components.LIGHTS_BANE_EFFECT.get())
    );
    public static final DeferredItem<TerraBowItem> TENDON_BOW = register("tendon_bow",  4.8F, m->m
            .setRarity(ModRarity.BLUE)
            .setFullPullHitEffect(ModEffectStrategies.Components.BLOOD_BUTCHERED_EFFECT.get())
    );
    public static final DeferredItem<TerraBowItem> MOLTEN_FURY = register("molten_fury",  5.3F, m->m
            .setRarity(ModRarity.ORANGE)
            .setArrowTransform(ArrowItems.HELLFIRE_ARROW.get())
    );
    public static final DeferredItem<TerraBowItem> THE_BEES_KNEES = register("the_bees_knees",  6.0F, m->m
            .setRarity(ModRarity.YELLOW)
            .setMultiShoot(3, (i, c)->new Vec3(-i*0.25f,0,0))
            .setCanMultiShoot(ammo->!(ammo.getItem() instanceof BaseArrowItem))
            .setEntityTransform(TerraBowItem.EntityTransform.create(ModEntities.BEE_ARROW.get(), BeeArrow::new))
    );
    public static final DeferredItem<TerraBowItem> HELLWING_BOW = register("hellwing_bow",  6.3f, m->m
            .setRarity(ModRarity.RED)
            .setEntityTransform(TerraBowItem.EntityTransform.create(ModEntities.HELL_BAT_ARROW.get(), HellBatArrowEntity::new))
    );

    // 代达罗斯风暴弓
    public static final DeferredItem<TerraBowItem> DAEDALUS_STORM_BOW = register("daedalus_storm_bow",()->new DaedalusStormbow(10f, ModRarity.PURPLE));


    public static final DeferredItem<TerraBowItem> DEVELOPER_BOW = register("developer_bow", 9999F,
            m->m.setRarity(ModRarity.MASTER).addModifyArrowBuilder(
                    modifier->modifier.setCauseFire(200)
                            .setDamage(9999)
                            .setSpeedFactor(2)
                            .setPenetration(9999)
                            .setGravity(0)
            ));


    public static DeferredItem<TerraBowItem> register(String name, Supplier<TerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    /**
     * 注册效果修饰的有耐久弓
     */
    public static DeferredItem<TerraBowItem> register(String name, float damage, int durability) {
        return register(name, () -> new TerraBowItem(damage, new TerraBowItem.Builder().setDuration(durability)));
    }

    /**
     * 注册带有效果修饰的无耐久弓
     */
    public static DeferredItem<TerraBowItem> register(String name, float damage, Function<TerraBowItem.Builder, TerraBowItem.Builder> modifier) {
        return register(name, () -> new TerraBowItem(damage,  modifier.apply(new TerraBowItem.Builder().setUnBreakable())));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerProperties() {
        ResourceLocation pull = ResourceLocation.withDefaultNamespace("pull");
        ClampedItemPropertyFunction shortBowPull = (itemStack, clientLevel, living, speed) -> living != null && living.getUseItem() == itemStack ? (float) (itemStack.getUseDuration(living) - living.getUseItemRemainingTicks()) / ShortBowItem.MAX_DRAW_DURATION : 0.0F;
        ClampedItemPropertyFunction bowPull = (itemStack, clientLevel, living, speed) -> living != null && living.getUseItem() == itemStack ? (float) (itemStack.getUseDuration(living) - living.getUseItemRemainingTicks()) / BowItem.MAX_DRAW_DURATION : 0.0F;
        ResourceLocation pulling = ResourceLocation.withDefaultNamespace("pulling");
        ClampedItemPropertyFunction bowPulling = (itemStack, clientLevel, living, speed) -> living != null && living.isUsingItem() && living.getUseItem() == itemStack ? 1.0F : 0.0F;

        ITEMS.getEntries().forEach(item -> {
            if(item.get() instanceof ShortBowItem) ItemProperties.register(item.get(), pull, shortBowPull);
            else ItemProperties.register(item.get(), pull, bowPull);
            ItemProperties.register(item.get(), pulling, bowPulling);
        });
    }
}

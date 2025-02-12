package org.confluence.mod.common.init.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.bow.DaedalusStormbow;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.confluence.mod.common.item.bow.TerraBowItem;
import org.confluence.mod.common.item.sword.stagedy.EffectStrategy;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terraentity.init.TEEffects;

import java.util.function.Supplier;

/**
 * 弓箭位置修正参考{@link ArrowInBowHud}
 *
 */
public class BowItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    // 短弓
    public static final DeferredItem<ShortBowItem> WOODEN_SHORT_BOW = ITEMS.register("wooden_short_bow",() -> new ShortBowItem( 4.0F, 384));
    public static final DeferredItem<ShortBowItem> COPPER_SHORT_BOW = ITEMS.register("copper_short_bow", () -> new ShortBowItem(4.5F, 640));
    public static final DeferredItem<ShortBowItem> TIN_SHORT_BOW = ITEMS.register("tin_short_bow", () -> new ShortBowItem(4.5F, 768));
    public static final DeferredItem<ShortBowItem> IRON_SHORT_BOW = ITEMS.register("iron_short_bow", () -> new ShortBowItem(5.0F, 896));
    public static final DeferredItem<ShortBowItem> LEAD_SHORT_BOW = ITEMS.register("lead_short_bow", () -> new ShortBowItem(5.0F, 1024));
    public static final DeferredItem<ShortBowItem> SILVER_SHORT_BOW = ITEMS.register("silver_short_bow", () -> new ShortBowItem(5.5F, 1152));
    public static final DeferredItem<ShortBowItem> TUNGSTEN_SHORT_BOW = ITEMS.register("tungsten_short_bow", () -> new ShortBowItem(5.5F, 1280));
    public static final DeferredItem<ShortBowItem> GOLDEN_SHORT_BOW = ITEMS.register("golden_short_bow", () -> new ShortBowItem(6.0F, 1408));
    public static final DeferredItem<ShortBowItem> PLATINUM_SHORT_BOW = ITEMS.register("platinum_short_bow", () -> new ShortBowItem(6.0F, 1536));


    // 无效果蓄力弓
    public static final DeferredItem<TerraBowItem> COPPER_BOW = register("copper_bow", 3.0F, 640);
    public static final DeferredItem<TerraBowItem> TIN_BOW = register("tin_bow", 3.0F, 768);
    public static final DeferredItem<TerraBowItem> IRON_BOW = register("iron_bow", 3.5F, 896);
    public static final DeferredItem<TerraBowItem> LEAD_BOW = register("lead_bow", 3.5F, 1024);
    public static final DeferredItem<TerraBowItem> SILVER_BOW = register("silver_bow", 4.0F, 1152);
    public static final DeferredItem<TerraBowItem> TUNGSTEN_BOW = register("tungsten_bow", 4.0F, 1280);
    public static final DeferredItem<TerraBowItem> GOLDEN_BOW = register("golden_bow", 4.5F, 1408);
    public static final DeferredItem<TerraBowItem> PLATINUM_BOW = register("platinum_bow", 4.5F, 1536);

    // DIY蓄力弓
    public static final DeferredItem<TerraBowItem> HUNTING_BOW = register("hunting_bow", () -> new TerraBowItem(3.5F, ModRarity.BLUE, // 猎弓
            modifier->modifier.addOnHitEffect(EffectStrategy.TIME_EFFECT.apply(TEEffects.SUMMON_FOCUS, 80))
    ));
    public static final DeferredItem<TerraBowItem> FOSSIL_BOW = register("fossil_bow", () -> new TerraBowItem(5.5F, ModRarity.BLUE,
            modifier->modifier.setTransformArrow(ArrowItems.FOSSIL_ARROW.get())
    ));
    public static final DeferredItem<TerraBowItem> DEMON_BOW = register("demon_bow", () -> new TerraBowItem(5.5F, ModRarity.BLUE,
            modifier->modifier.addFullPullHitEffect(EffectStrategy.LIGHTS_BANE_EFFECT)
    ));
    public static final DeferredItem<TerraBowItem> TENDON_BOW = register("tendon_bow",  () -> new TerraBowItem(6F, ModRarity.BLUE,
            modifier->modifier.addFullPullHitEffect(EffectStrategy.BLOOD_BUTCHERED_EFFECT)
    ));
    public static final DeferredItem<TerraBowItem> MOLTEN_FURY = register("molten_fury",  () -> new TerraBowItem(7F, ModRarity.BLUE,
            modifier->modifier.setTransformArrow(ArrowItems.HELLFIRE_ARROW.get())
    ));


    // 代达罗斯风暴弓
    public static final DeferredItem<TerraBowItem> DAEDALUS_STORM_BOW = register("daedalus_storm_bow", ()->new DaedalusStormbow(10F, ModRarity.PURPLE));


    public static final DeferredItem<TerraBowItem> DEVELOPER_BOW = register("developer_bow", () -> new TerraBowItem(1F, ModRarity.MASTER,
            modifier->modifier.setCauseFire(200)
                    .setDamage(10)
                    .setSpeedFactor(2)
                    .setPenetration(2)
            ));


    public static DeferredItem<TerraBowItem> register(String name, Supplier<TerraBowItem> supplier) {
        return ITEMS.register(name, supplier);
    }
    public static DeferredItem<TerraBowItem> register(String name, float damage, int durability, ModRarity rarity) {
        return register(name, () -> new TerraBowItem(damage, durability, rarity));
    }
    public static DeferredItem<TerraBowItem> register(String name, float damage, int durability) {
        return register(name, damage, durability, ModRarity.WHITE);
    }

    public static void acceptTag(IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> tag) {
        ITEMS.getEntries().forEach(entry -> tag.add(entry.get()));
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

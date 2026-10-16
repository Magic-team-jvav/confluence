package org.confluence.mod.common.init.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHammerItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.common.PortSimpleTier;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HammerItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseHammerItem> WOODEN_HAMMER = registerItem("wooden_hammer", "木锤", () -> new BaseHammerItem(Tiers.WOOD, 2, 0.8F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> EBONWOOD_HAMMER = registerItem("ebonwood_hammer", "乌木锤", () -> new BaseHammerItem(Tiers.WOOD, 5, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> SHADEWOOD_HAMMER = registerItem("shadewood_hammer", "暗影木锤", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> ASH_WOOD_HAMMER = registerItem("ash_wood_hammer", "灰烬木锤", () -> new BaseHammerItem(Tiers.WOOD, 6, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> PEARLWOOD_HAMMER = registerItem("pearlwood_hammer", "珍珠木锤", () -> new BaseHammerItem(Tiers.WOOD, 10, 0.8F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));

    public static final PortDeferredItem<BaseHammerItem> COPPER_HAMMER = registerItem("copper_hammer", "铜锤", () -> new BaseHammerItem(ModTiers.COPPER, 4, 0.9F, new Item.Properties(), attributes(-1, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> TIN_HAMMER = registerItem("tin_hammer", "锡锤", () -> new BaseHammerItem(ModTiers.TIN, 6, 0.9F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> IRON_HAMMER = registerItem("iron_hammer", "铁锤", () -> new BaseHammerItem(ModTiers.IRON, 7, 1.0F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> LEAD_HAMMER = registerItem("lead_hammer", "铅锤", () -> new BaseHammerItem(ModTiers.LEAD, 8, 1.1F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> SILVER_HAMMER = registerItem("silver_hammer", "银锤", () -> new BaseHammerItem(ModTiers.SILVER, 9, 1.2F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> TUNGSTEN_HAMMER = registerItem("tungsten_hammer", "钨锤", () -> new BaseHammerItem(ModTiers.TUNGSTEN, 9, 1.3F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> GOLDEN_HAMMER = registerItem("golden_hammer", "金锤", () -> new BaseHammerItem(ModTiers.GOLD, 9, 1.5F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> PLATINUM_HAMMER = registerItem("platinum_hammer", "铂金锤", () -> new BaseHammerItem(ModTiers.PLATINUM, 10, 1.6F, new Item.Properties(), attributes(0, 0.55), ModRarity.WHITE));
    public static final PortDeferredItem<BaseHammerItem> THE_BREAKER = registerItem("the_breaker", "魔锤", () -> new BaseHammerItem(ModTiers.DEMONITE, 11, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final PortDeferredItem<BaseHammerItem> FLESH_GRINDER = registerItem("flesh_grinder", "血肉锤", () -> new BaseHammerItem(ModTiers.CRIMTANE, 12, 1.6F, unbreakable(), attributes(0, 0.6), ModRarity.BLUE).hasImage());
    public static final PortDeferredItem<BaseHammerItem> ROCKFISH = registerItem("rockfish", "岩鱼锤", () -> new BaseHammerItem(new PortSimpleTier(ModTags.Blocks.NEEDS_4_LEVEL, 10000, 9, 3, 10, () -> Ingredient.of(ItemTags.FISHES)), 13, 1.2F, unbreakable(), attributes(0, 0.6), ModRarity.ORANGE).hasImage());

    public static final PortDeferredItem<BaseHammerItem> PWNHAMMER = registerItem("pwnhammer", "神锤", () -> new BaseHammerItem(ModTiers.HALLOWED, 14, 2.0F, unbreakable(), attributes(0, 0.75), ModRarity.LIGHT_RED));
    public static final PortDeferredItem<BaseHammerItem> HAMMUSH = registerItem("hammush", "蘑菇锤", () -> new BaseHammerItem(ModTiers.SHROOMITE, 16, 2.2F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final PortDeferredItem<BaseHammerItem> CHLOROPHYTE_WARHAMMER = registerItem("chlorophyte_warhammer", "叶绿战锤", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 20, 2.4F, unbreakable(), attributes(0, 0.75), ModRarity.LIME));
    public static final PortDeferredItem<BaseHammerItem> CHLOROPHYTE_JACKHAMMER = registerItem("chlorophyte_jackhammer", "叶绿手提钻", () -> new BaseHammerItem(ModTiers.CHLOROPHYTE, 24, 4, unbreakable(), attributes(0, 0.52), ModRarity.LIME));

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}

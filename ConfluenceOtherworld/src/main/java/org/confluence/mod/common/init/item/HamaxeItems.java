package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.BaseHamaxeItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HamaxeItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseHamaxeItem> METEOR_HAMAXE = registerItem("meteor_hamaxe", "流星锤斧", () -> new BaseHamaxeItem(ModTiers.METEOR, 20, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE).hasImage());
    public static final DeferredItem<BaseHamaxeItem> MOLTEN_HAMAXE = registerItem("molten_hamaxe", "熔岩锤斧", () -> new BaseHamaxeItem(ModTiers.HELLSTONE, 23, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE).hasImage());
    public static final DeferredItem<BaseHamaxeItem> HAEMORRHAXE = registerItem("haemorrhaxe", "血锤斧", () -> new BaseHamaxeItem(ModTiers.CRIMTANE, 30, 0.9F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<BaseHamaxeItem> SPECTRE_HAMAXE = registerItem("spectre_hamaxe", "幽灵锤斧", () -> new BaseHamaxeItem(ModTiers.SPECTRE, 45, 1.0F, unbreakable(), attributes(3, 0.7), ModRarity.YELLOW));
    public static final DeferredItem<BaseHamaxeItem> SOLAR_FLARE_HAMAXE = registerItem("solar_flare_hamaxe", "耀斑锤斧", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.2F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<BaseHamaxeItem> VORTEX_HAMAXE = registerItem("vortex_hamaxe", "星旋锤斧", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.3F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<BaseHamaxeItem> NEBULA_HAMAXE = registerItem("nebula_hamaxe", "星云锤斧", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.4F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<BaseHamaxeItem> STARDUST_HAMAXE = registerItem("stardust_hamaxe", "星尘锤斧", () -> new BaseHamaxeItem(ModTiers.LUMINITE, 60, 1.6F, unbreakable(), attributes(4, 0.7), ModRarity.RED));
    public static final DeferredItem<BaseHamaxeItem> THE_AXE = registerItem("the_axe", "吉他斧", () -> new BaseHamaxeItem(ModTiers.CHLOROPHYTE, 72, 1.8F, unbreakable(), attributes(1, 0.725), ModRarity.YELLOW));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}

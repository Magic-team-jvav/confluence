package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.HoeShovelItem;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;
import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeShovelItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HoeShovelItem> METEOR_HOE_SHOVEL = registerItem("meteor_hoe_shovel", "流星锄锹", () -> new HoeShovelItem(ModTiers.METEOR, 8, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE));
    public static final DeferredItem<HoeShovelItem> MOLTEN_HOE_SHOVEL = registerItem("molten_hoe_shovel", "熔火锄锹", () -> new HoeShovelItem(ModTiers.HELLSTONE, 9, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE));
    public static final DeferredItem<HoeShovelItem> COBALT_HOE_SHOVEL = registerItem("cobalt_hoe_shovel", "钴锄锹", () -> new HoeShovelItem(ModTiers.COBALT, 10, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> PALLADIUM_HOE_SHOVEL = registerItem("palladium_hoe_shovel", "钯金锄锹", () -> new HoeShovelItem(ModTiers.PALLADIUM, 11, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> MYTHRIL_HOE_SHOVEL = registerItem("mythril_hoe_shovel", "秘银锄锹", () -> new HoeShovelItem(ModTiers.MYTHRIL, 12, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> ORICHALCUM_HOE_SHOVEL = registerItem("orichalcum_hoe_shovel", "山铜锄锹", () -> new HoeShovelItem(ModTiers.ORICHALCUM, 13, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> ADAMANTITE_HOE_SHOVEL = registerItem("adamantite_hoe_shovel", "精金锄锹", () -> new HoeShovelItem(ModTiers.ADAMANTITE, 14, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> TITANIUM_HOE_SHOVEL = registerItem("titanium_hoe_shovel", "钛金锄锹", () -> new HoeShovelItem(ModTiers.TITANIUM, 15, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> HALLOWED_HOE_SHOVEL = registerItem("hallowed_hoe_shovel", "神圣锄锹", () -> new HoeShovelItem(ModTiers.HALLOWED, 16, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIME));
    public static final DeferredItem<HoeShovelItem> CHLOROPHYTE_HOE_SHOVEL = registerItem("chlorophyte_hoe_shovel", "叶绿锄锹", () -> new HoeShovelItem(ModTiers.CHLOROPHYTE, 17, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIME));

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}

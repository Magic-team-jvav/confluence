package org.confluence.mod.common.init.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.common.HoeShovelItem;

import static org.confluence.mod.common.init.item.ModItems.attributes;
import static org.confluence.mod.common.init.item.ModItems.unbreakable;

public class HoeShovelItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<HoeShovelItem> METEOR_HOE_SHOVEL = ITEMS.register("meteor_hoe_shovel", () -> new HoeShovelItem(ModTiers.METEOR, 8, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.BLUE));
    public static final DeferredItem<HoeShovelItem> MOLTEN_HOE_SHOVEL = ITEMS.register("molten_hoe_shovel", () -> new HoeShovelItem(ModTiers.HELLSTONE, 9, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.ORANGE));
    public static final DeferredItem<HoeShovelItem> COBALT_HOE_SHOVEL = ITEMS.register("cobalt_hoe_shovel", () -> new HoeShovelItem(ModTiers.COBALT, 10, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> PALLADIUM_HOE_SHOVEL = ITEMS.register("palladium_hoe_shovel", () -> new HoeShovelItem(ModTiers.PALLADIUM, 11, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> MYTHRIL_HOE_SHOVEL = ITEMS.register("mythril_hoe_shovel", () -> new HoeShovelItem(ModTiers.MYTHRIL, 12, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> ORICHALCUM_HOE_SHOVEL = ITEMS.register("orichalcum_hoe_shovel", () -> new HoeShovelItem(ModTiers.ORICHALCUM, 13, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> ADAMANTITE_HOE_SHOVEL = ITEMS.register("adamantite_hoe_shovel", () -> new HoeShovelItem(ModTiers.ADAMANTITE, 14, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> TITANIUM_HOE_SHOVEL = ITEMS.register("titanium_hoe_shovel", () -> new HoeShovelItem(ModTiers.TITANIUM, 15, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIGHT_RED));
    public static final DeferredItem<HoeShovelItem> HALLOWED_HOE_SHOVEL = ITEMS.register("hallowed_hoe_shovel", () -> new HoeShovelItem(ModTiers.HALLOWED, 16, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIME));
    public static final DeferredItem<HoeShovelItem> CHLOROPHYTE_HOE_SHOVEL = ITEMS.register("chlorophyte_hoe_shovel", () -> new HoeShovelItem(ModTiers.CHLOROPHYTE, 17, 0.8F, unbreakable(), attributes(0, 0.7), ModRarity.LIME));
}

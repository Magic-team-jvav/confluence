package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class SpawnEggItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<Item> RETINAZER_SPAWN_EGG = ITEMS.registerSimpleItem("retinazer_spawn_egg", new Item.Properties());
    public static final PortDeferredItem<Item> SPAZMATISM_SPAWN_EGG = ITEMS.registerSimpleItem("spazmatism_spawn_egg", new Item.Properties());
    public static final PortDeferredItem<Item> THE_DESTROYER_SPAWN_EGG = ITEMS.registerSimpleItem("the_destroyer_spawn_egg", new Item.Properties());
    public static final PortDeferredItem<Item> THE_TWINS_SPAWN_EGG = ITEMS.registerSimpleItem("the_twins_spawn_egg", new Item.Properties());
    public static final PortDeferredItem<Item> SKELETRON_PRIME_SPAWN_EGG = ITEMS.registerSimpleItem("skeletron_prime_spawn_egg", new Item.Properties());
    public static final PortDeferredItem<Item> PLANTERA_SPAWN_EGG = ITEMS.registerSimpleItem("plantera_spawn_egg", new Item.Properties());
}

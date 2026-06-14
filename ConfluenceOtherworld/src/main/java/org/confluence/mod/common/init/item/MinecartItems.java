package org.confluence.mod.common.init.item;

import com.google.common.base.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.minecart.*;
import org.confluence.mod.common.entity.minecart.GenericMinecartEntity.Variant;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.common.BaseMinecartItem;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import static org.confluence.mod.common.entity.minecart.BaseMinecartEntity.*;

@SuppressWarnings("unchecked")
public class MinecartItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<BaseMinecartItem> MECHANICAL_CART = ITEMS.register("mechanical_cart", () -> new BaseMinecartItem(new Item.Properties().fireResistant(), ModRarity.EXPERT, Types.MECHANICAL, MechanicalCartEntity::new));
    public static final PortDeferredItem<BaseMinecartItem> DESERT_MINECART = registerGeneric("desert_minecart", Types.DESERT, Variant.DESERT);
    public static final PortDeferredItem<BaseMinecartItem> MINECARP = ITEMS.register("minecarp", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.BLUE, Types.MINECARP, MinecarpEntity::new));
    public static final PortDeferredItem<BaseMinecartItem> BEE_MINECART = registerGeneric("bee_minecart", Types.BEE, Variant.BEE);
    public static final PortDeferredItem<BaseMinecartItem> LADYBUG_MINECART = registerGeneric("ladybug_minecart", Types.LADYBUG, Variant.LADYBUG);
    public static final PortDeferredItem<BaseMinecartItem> PIGRON_MINECART = registerGeneric("pigron_minecart", Types.PIGRON, Variant.PIGRON);
    public static final PortDeferredItem<BaseMinecartItem> SUNFLOWER_MINECART = registerGeneric("sunflower_minecart", Types.SUNFLOWER, Variant.SUNFLOWER);
    public static final PortDeferredItem<BaseMinecartItem> DEMONIC_HELLCART = ITEMS.register("demonic_hellcart", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.BLUE, Types.DEMONIC, DemonicHellcartEntity::new));
    public static final PortDeferredItem<BaseMinecartItem> SHROOM_MINECART = registerGeneric("shroom_minecart", Types.SHROOM, Variant.SHROOM);
    public static final PortDeferredItem<BaseMinecartItem> AMETHYST_MINECART = registerGeneric("amethyst_minecart", Types.AMETHYST, Variant.AMETHYST);
    public static final PortDeferredItem<BaseMinecartItem> TOPAZ_MINECART = registerGeneric("topaz_minecart", Types.TOPAZ, Variant.TOPAZ);
    public static final PortDeferredItem<BaseMinecartItem> SAPPHIRE_MINECART = registerGeneric("sapphire_minecart", Types.SAPPHIRE, Variant.SAPPHIRE);
    public static final PortDeferredItem<BaseMinecartItem> JADE_MINECART = registerGeneric("jade_minecart", Types.JADE, Variant.JADE);
    public static final PortDeferredItem<BaseMinecartItem> RUBY_MINECART = registerGeneric("ruby_minecart", Types.RUBY, Variant.RUBY);
    public static final PortDeferredItem<BaseMinecartItem> DIAMOND_MINECART = registerGeneric("diamond_minecart", Types.DIAMOND, Variant.DIAMOND);
    public static final PortDeferredItem<BaseMinecartItem> AMBER_MINECART = registerGeneric("amber_minecart", Types.AMBER, Variant.AMBER);
    public static final PortDeferredItem<BaseMinecartItem> BEETLE_MINECART = registerGeneric("beetle_minecart", Types.BEETLE, Variant.BEETLE);
    public static final PortDeferredItem<BaseMinecartItem> MEOWMERE_MINECART = ITEMS.register("meowmere", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.RED, Types.MEOWMERE, MeowmereMinecartEntity::new));
    public static final PortDeferredItem<BaseMinecartItem> PARTY_WAGON = registerGeneric("party_wagon", Types.PARTY, Variant.PARTY);
    public static final PortDeferredItem<BaseMinecartItem> THE_DUTCHMAN = registerGeneric("the_dutchman", Types.DUTCHMAN, Variant.DUTCHMAN);
    public static final PortDeferredItem<BaseMinecartItem> STEAMPUNK_MINECART = registerGeneric("steampunk_minecart", Types.STEAMPUNK, Variant.STEAMPUNK);
    public static final PortDeferredItem<BaseMinecartItem> COFFIN_MINECART = registerGeneric("coffin_minecart", Types.COFFIN, Variant.COFFIN);
    public static final PortDeferredItem<BaseMinecartItem> DIGGING_MOLECART = ITEMS.register("digging_molecart", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.BLUE, Types.MOLECART, DiggingMolecartEntity::new));
    public static final PortDeferredItem<BaseMinecartItem> FART_KART = ITEMS.register("fart_kart", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.GREEN, Types.FART, (level, x, y, z, abilities) -> new GenericMinecartEntity(level, x, y, z, abilities, Variant.FART)));
    public static final PortDeferredItem<BaseMinecartItem> TERRA_FART_KART = ITEMS.register("terra_fart_kart", () -> new BaseMinecartItem(new Item.Properties(), ModRarity.YELLOW, Types.TERRA_FART, (level, x, y, z, abilities) -> new GenericMinecartEntity(level, x, y, z, abilities, Variant.TERRA_FART)));

    private static PortDeferredItem<BaseMinecartItem> registerGeneric(String name, Abilities<GenericMinecartEntity> abilities, Variant variant) {
        return ITEMS.register(name, () -> new BaseMinecartItem(new Item.Properties(), ModRarity.BLUE, abilities, (level, x, y, z, abilities1) -> new GenericMinecartEntity(level, x, y, z, abilities1, variant)));
    }

    public static class Types {
        public static final float DEFAULT_MAX_SPEED = 0.4F;
        public static final double DEFAULT_ACCELERATION = 0.75;

        public static final Abilities<BaseMinecartEntity> VANILLA = register(ModEntities.VANILLA_MINECART, Confluence.asResource("vanilla"), DEFAULT_MAX_SPEED, DEFAULT_ACCELERATION, IAbstractMinecartExtension.DEFAULT_AIR_DRAG);
        public static final Abilities<BaseMinecartEntity> WOODEN = register(ModEntities.WOODEN_MINECART, ResourceLocation.withDefaultNamespace("air"), 0.308F, 0.16, 0.94);
        public static final Abilities<MechanicalCartEntity> MECHANICAL = register(ModEntities.MECHANICAL_CART, Confluence.asResource("mechanical_cart"), (float) MECHANICAL_CART_MAX_SPEED, MECHANICAL_CART_ACCELERATION, MECHANICAL_CART_DRAG_AIR);
        public static final Abilities<GenericMinecartEntity> DESERT = registerGeneric(Confluence.asResource("desert_minecart"));
        public static final Abilities<MinecarpEntity> MINECARP = registerGeneric(ModEntities.MINECARP, Confluence.asResource("minecarp"));
        public static final Abilities<GenericMinecartEntity> BEE = registerGeneric(Confluence.asResource("bee_minecart"));
        public static final Abilities<GenericMinecartEntity> LADYBUG = registerGeneric(Confluence.asResource("ladybug_minecart"));
        public static final Abilities<GenericMinecartEntity> PIGRON = registerGeneric(Confluence.asResource("pigron_minecart"));
        public static final Abilities<GenericMinecartEntity> SUNFLOWER = registerGeneric(Confluence.asResource("sunflower_minecart"));
        public static final Abilities<DemonicHellcartEntity> DEMONIC = registerGeneric(ModEntities.DEMONIC_HELLCART, Confluence.asResource("demonic_hellcart"));
        public static final Abilities<GenericMinecartEntity> SHROOM = registerGeneric(Confluence.asResource("shroom_minecart"));
        public static final Abilities<GenericMinecartEntity> AMETHYST = registerGeneric(Confluence.asResource("amethyst_minecart"));
        public static final Abilities<GenericMinecartEntity> TOPAZ = registerGeneric(Confluence.asResource("topaz_minecart"));
        public static final Abilities<GenericMinecartEntity> SAPPHIRE = registerGeneric(Confluence.asResource("sapphire_minecart"));
        public static final Abilities<GenericMinecartEntity> JADE = registerGeneric(Confluence.asResource("jade_minecart"));
        public static final Abilities<GenericMinecartEntity> RUBY = registerGeneric(Confluence.asResource("ruby_minecart"));
        public static final Abilities<GenericMinecartEntity> DIAMOND = registerGeneric(Confluence.asResource("diamond_minecart"));
        public static final Abilities<GenericMinecartEntity> AMBER = registerGeneric(Confluence.asResource("amber_minecart"));
        public static final Abilities<GenericMinecartEntity> BEETLE = registerGeneric(Confluence.asResource("beetle_minecart"));
        public static final Abilities<MeowmereMinecartEntity> MEOWMERE = registerGeneric(ModEntities.MEOWMERE_MINECART, Confluence.asResource("meowmere"));
        public static final Abilities<GenericMinecartEntity> PARTY = registerGeneric(Confluence.asResource("party_wagon"));
        public static final Abilities<GenericMinecartEntity> DUTCHMAN = registerGeneric(Confluence.asResource("the_dutchman"));
        public static final Abilities<GenericMinecartEntity> STEAMPUNK = registerGeneric(Confluence.asResource("steampunk_minecart"));
        public static final Abilities<GenericMinecartEntity> COFFIN = registerGeneric(Confluence.asResource("coffin_minecart"));
        public static final Abilities<DiggingMolecartEntity> MOLECART = register(ModEntities.DIGGING_MOLECART, Confluence.asResource("digging_molecart"), 0.185F, 0.15, 0.93);
        public static final Abilities<GenericMinecartEntity> FART = registerGeneric(Confluence.asResource("fart_kart"));
        public static final Abilities<GenericMinecartEntity> TERRA_FART = registerGeneric(Confluence.asResource("terra_fart_kart"));

        private static <E extends BaseMinecartEntity> Abilities<E> register(Supplier<EntityType<E>> entityType, ResourceLocation item, float maxSpeed, double acceleration, double dragAir) {
            return new Abilities<>(entityType, item, maxSpeed, acceleration, dragAir);
        }

        private static <E extends BaseMinecartEntity> Abilities<E> registerGeneric(Supplier<EntityType<E>> entityType, ResourceLocation item) {
            return new Abilities<>(entityType, item, DEFAULT_MAX_SPEED, DEFAULT_ACCELERATION, IAbstractMinecartExtension.DEFAULT_AIR_DRAG);
        }

        private static Abilities<GenericMinecartEntity> registerGeneric(ResourceLocation item) {
            return new Abilities<>(ModEntities.GENERIC_MINECART, item, DEFAULT_MAX_SPEED, DEFAULT_ACCELERATION, IAbstractMinecartExtension.DEFAULT_AIR_DRAG);
        }
    }
}

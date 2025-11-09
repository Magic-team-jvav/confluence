package org.confluence.mod.common.init.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.extensions.IAbstractMinecartExtension;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.minecart.*;
import org.confluence.mod.common.entity.minecart.GenericMinecartEntity.Variant;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.common.BaseMinecartItem;

import java.util.function.Supplier;

import static org.confluence.mod.common.entity.minecart.BaseMinecartEntity.*;

@SuppressWarnings("unchecked")
public class MinecartItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final DeferredItem<BaseMinecartItem> MECHANICAL_CART = ITEMS.registerItem("mechanical_cart", properties -> new BaseMinecartItem(properties.fireResistant(), ModRarity.EXPERT, Types.MECHANICAL, MechanicalCartEntity::new));
    public static final DeferredItem<BaseMinecartItem> DESERT_MINECART = registerGeneric("desert_minecart", Types.DESERT, Variant.DESERT);
    public static final DeferredItem<BaseMinecartItem> MINECARP = ITEMS.registerItem("minecarp", properties -> new BaseMinecartItem(properties, ModRarity.BLUE, Types.MINECARP, MinecarpEntity::new));
    public static final DeferredItem<BaseMinecartItem> BEE_MINECART = registerGeneric("bee_minecart", Types.BEE, Variant.BEE);
    public static final DeferredItem<BaseMinecartItem> LADYBUG_MINECART = registerGeneric("ladybug_minecart", Types.LADYBUG, Variant.LADYBUG);
    public static final DeferredItem<BaseMinecartItem> PIGRON_MINECART = registerGeneric("pigron_minecart", Types.PIGRON, Variant.PIGRON);
    public static final DeferredItem<BaseMinecartItem> SUNFLOWER_MINECART = registerGeneric("sunflower_minecart", Types.SUNFLOWER, Variant.SUNFLOWER);
    public static final DeferredItem<BaseMinecartItem> DEMONIC_HELLCART = ITEMS.registerItem("demonic_hellcart", properties -> new BaseMinecartItem(properties, ModRarity.BLUE, Types.DEMONIC, DemonicHellcartEntity::new));
    public static final DeferredItem<BaseMinecartItem> SHROOM_MINECART = registerGeneric("shroom_minecart", Types.SHROOM, Variant.SHROOM);
    public static final DeferredItem<BaseMinecartItem> AMETHYST_MINECART = registerGeneric("amethyst_minecart", Types.AMETHYST, Variant.AMETHYST);
    public static final DeferredItem<BaseMinecartItem> TOPAZ_MINECART = registerGeneric("topaz_minecart", Types.TOPAZ, Variant.TOPAZ);
    public static final DeferredItem<BaseMinecartItem> SAPPHIRE_MINECART = registerGeneric("sapphire_minecart", Types.SAPPHIRE, Variant.SAPPHIRE);
    public static final DeferredItem<BaseMinecartItem> JADE_MINECART = registerGeneric("jade_minecart", Types.JADE, Variant.JADE);
    public static final DeferredItem<BaseMinecartItem> RUBY_MINECART = registerGeneric("ruby_minecart", Types.RUBY, Variant.RUBY);
    public static final DeferredItem<BaseMinecartItem> DIAMOND_MINECART = registerGeneric("diamond_minecart", Types.DIAMOND, Variant.DIAMOND);
    public static final DeferredItem<BaseMinecartItem> AMBER_MINECART = registerGeneric("amber_minecart", Types.AMBER, Variant.AMBER);
    public static final DeferredItem<BaseMinecartItem> BEETLE_MINECART = registerGeneric("beetle_minecart", Types.BEETLE, Variant.BEETLE);
    public static final DeferredItem<BaseMinecartItem> MEOWMERE_MINECART = ITEMS.registerItem("meowmere", properties -> new BaseMinecartItem(properties, ModRarity.RED, Types.MEOWMERE, MeowmereMinecartEntity::new));
    public static final DeferredItem<BaseMinecartItem> PARTY_WAGON = registerGeneric("party_wagon", Types.PARTY, Variant.PARTY);
    public static final DeferredItem<BaseMinecartItem> THE_DUTCHMAN = registerGeneric("the_dutchman", Types.DUTCHMAN, Variant.DUTCHMAN);
    public static final DeferredItem<BaseMinecartItem> STEAMPUNK_MINECART = registerGeneric("steampunk_minecart", Types.STEAMPUNK, Variant.STEAMPUNK);
    public static final DeferredItem<BaseMinecartItem> COFFIN_MINECART = registerGeneric("coffin_minecart", Types.COFFIN, Variant.COFFIN);
    public static final DeferredItem<BaseMinecartItem> DIGGING_MOLECART = ITEMS.registerItem("digging_molecart", properties -> new BaseMinecartItem(properties, ModRarity.BLUE, Types.MOLECART, DiggingMolecartEntity::new));
    public static final DeferredItem<BaseMinecartItem> FART_KART = ITEMS.registerItem("fart_kart", properties -> new BaseMinecartItem(properties, ModRarity.GREEN, Types.FART, (level, x, y, z, abilities) -> new GenericMinecartEntity(level, x, y, z, abilities, Variant.FART)));
    public static final DeferredItem<BaseMinecartItem> TERRA_FART_KART = ITEMS.registerItem("terra_fart_kart", properties -> new BaseMinecartItem(properties, ModRarity.YELLOW, Types.TERRA_FART, (level, x, y, z, abilities) -> new GenericMinecartEntity(level, x, y, z, abilities, Variant.TERRA_FART)));

    private static DeferredItem<BaseMinecartItem> registerGeneric(String name, Abilities<GenericMinecartEntity> abilities, Variant variant) {
        return ITEMS.registerItem(name, properties -> new BaseMinecartItem(properties, ModRarity.BLUE, abilities, (level, x, y, z, abilities1) -> new GenericMinecartEntity(level, x, y, z, abilities1, variant)));
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

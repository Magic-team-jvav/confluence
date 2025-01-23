package org.confluence.mod.common.init.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModJukeboxSongs;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.item.common.GrassSeedItem;
import org.confluence.terra_curio.common.component.ModRarity;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Unbreakable UNBREAKABLE = new Unbreakable(false);

    public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = Confluence.asResource("base_attack_knockback");
    public static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    public static final ResourceLocation BASE_BLOCK_INTERACTION_RANGE_ID = Confluence.asResource("base_block_interaction_range");

    public static final Supplier<Item> ALPHA = ITEMS.register("alpha", () -> new CustomRarityItem(new Item.Properties().stacksTo(1).fireResistant().jukeboxPlayable(ModJukeboxSongs.ALPHA), ModRarity.EXPERT));

    public static final Supplier<Item> STAR = ITEMS.register("star", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> SOUL_CAKE = ITEMS.register("soul_cake", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> SUGAR_PLUM = ITEMS.register("sugar_plum", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> HEART = ITEMS.register("heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> CANDY_APPLE = ITEMS.register("candy_apple", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> CANDY_CANE = ITEMS.register("candy_cane", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));

    public static final Supplier<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN_PILE.get(), ModRarity.WHITE, ModItems.SILVER_COIN));
    public static final Supplier<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN_PILE.get(), ModRarity.ORANGE, ModItems.GOLDEN_COIN));
    public static final Supplier<CoinItem> GOLDEN_COIN = ITEMS.register("golden_coin", () -> new CoinItem(ModBlocks.GOLDEN_COIN_PILE.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN));
    public static final Supplier<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN_PILE.get(), ModRarity.CYAN, ModItems.EMERALD_COIN));
    public static final Supplier<CoinItem> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new CoinItem(ModBlocks.EMERALD_COIN_PILE.get(), ModRarity.PURPLE, null));

    public static final Supplier<Item> DEAD_MANS_SWEATER = ITEMS.registerItem("dead_mans_seater", properties -> new CustomRarityItem(properties.stacksTo(1), ModRarity.GREEN)); // todo 模型
    public static final Supplier<Item> WHOOPIE_CUSHION = ITEMS.registerSimpleItem("whoopie_cushion", new Item.Properties().stacksTo(1));

    public static final Supplier<GrassSeedItem> GRASS_SEED = ITEMS.register("grass_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, Blocks.GRASS_BLOCK)));
    public static final Supplier<GrassSeedItem> JUNGLE_GRASS_SEED =  ITEMS.register("jungle_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> MUSHROOM_GRASS_SEED =  ITEMS.register("mushroom_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> CORRUPT_SEED = ITEMS.register("corrupt_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> TR_CRIMSON_SEED = ITEMS.register("tr_crimson_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get(), Blocks.MUD, NatureBlocks.TR_CRIMSON_JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> HALLOWED_SEED =  ITEMS.register("hallowed_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> ASH_GRASS_SEED =  ITEMS.register("ash_grass_seed", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
        AccessoryItems.ITEMS.register(eventBus);
        ArmorItems.register(eventBus);
        ArrowItems.ITEMS.register(eventBus);
        AxeItems.ITEMS.register(eventBus);
        BaitItems.ITEMS.register(eventBus);
        BoomerangItems.ITEMS.register(eventBus);
        BowItems.ITEMS.register(eventBus);
        ConsumableItems.ITEMS.register(eventBus);
        VanityArmorItems.ITEMS.register(eventBus);
        DrillItems.ITEMS.register(eventBus);
        FishingPoleItems.ITEMS.register(eventBus);
        FoodItems.ITEMS.register(eventBus);
        HammerItems.ITEMS.register(eventBus);
        HookItems.ITEMS.register(eventBus);
        IconItems.ITEMS.register(eventBus);
        ManaStaffItems.ITEMS.register(eventBus);
        MaterialItems.ITEMS.register(eventBus);
        MinecartItems.ITEMS.register(eventBus);
        PaintItems.ITEMS.register(eventBus);
        PickaxeAxeItems.ITEMS.register(eventBus);
        PickaxeItems.ITEMS.register(eventBus);
        PotionItems.ITEMS.register(eventBus);
        QuestedFishes.ITEMS.register(eventBus);
        SwordItems.ITEMS.register(eventBus);
        ToolItems.ITEMS.register(eventBus);
    }
}

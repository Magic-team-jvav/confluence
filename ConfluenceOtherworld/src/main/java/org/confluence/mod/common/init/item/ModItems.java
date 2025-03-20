package org.confluence.mod.common.init.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
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
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sponsor.*;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

import java.util.Map;
import java.util.function.Supplier;

import static org.confluence.mod.util.ModUtils.MAX_STACK_SIZE;

@SuppressWarnings("unused")
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items HIDDEN = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Unbreakable UNBREAKABLE = new Unbreakable(false);

    public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = Confluence.asResource("base_attack_knockback");
    public static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    public static final ResourceLocation BASE_BLOCK_INTERACTION_RANGE_ID = Confluence.asResource("base_block_interaction_range");

    public static final Supplier<Item> ALPHA = HIDDEN.register("alpha", () -> new CustomRarityItem(new Item.Properties().stacksTo(1).fireResistant().jukeboxPlayable(ModJukeboxSongs.ALPHA), ModRarity.EXPERT));
    public static final Supplier<Item> STAR = HIDDEN.register("star", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> SOUL_CAKE = HIDDEN.register("soul_cake", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> SUGAR_PLUM = HIDDEN.register("sugar_plum", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> HEART = HIDDEN.register("heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> CANDY_APPLE = HIDDEN.register("candy_apple", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<Item> CANDY_CANE = HIDDEN.register("candy_cane", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));
    public static final Supplier<EntityDisplayItem> ENTITY_DISPLAY = HIDDEN.register("entity_display", EntityDisplayItem::new);
    // 赞助物品
    public static final Supplier<BoredomsPactFallingResolve> BOREDOMS_PACT_FALLING_RESOLVE = HIDDEN.register(BoredomsPactFallingResolve.ID.getPath(), BoredomsPactFallingResolve::new);
    public static final Supplier<ParadoxInteractiveMedal> PARADOX_INTERACTIVE_MEDAL = HIDDEN.register("paradox_interactive_medal", ParadoxInteractiveMedal::new);
    public static final Supplier<TooltipItem> TOKYO_TEDDY_BEAR = HIDDEN.register("tokyo_teddy_bear", () -> new TooltipItem(new Item.Properties(), ModRarity.MASTER, TooltipItem.getTooltipsFromString("tokyo_teddy_bear", 6)));
    public static final Supplier<IceTofuBrickItem> ICE_TOFU_BRICK = HIDDEN.register("ice_tofu_brick", IceTofuBrickItem::new);
    public static final Supplier<FailedSkullItem> FAILED_SKULL = HIDDEN.register("failed_skull", FailedSkullItem::new);
    public static final Supplier<KindMitaRingItem> KIND_MITA_RING = HIDDEN.register("kind_mita_ring", KindMitaRingItem::new);

    public static final Supplier<TooltipItem> MYSTERIOUS_NOTE = HIDDEN.register("mysterious_note", () -> new TooltipItem(new Item.Properties(), ModRarity.MASTER, TooltipItem.getTooltipsFromString("mysterious_note", 1)));

    public static final Supplier<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN_PILE.get(), ModRarity.WHITE, ModItems.SILVER_COIN, 100));
    public static final Supplier<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN_PILE.get(), ModRarity.ORANGE, ModItems.GOLDEN_COIN, 100));
    public static final Supplier<CoinItem> GOLDEN_COIN = ITEMS.register("golden_coin", () -> new CoinItem(ModBlocks.GOLDEN_COIN_PILE.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN, 100));
    public static final Supplier<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN_PILE.get(), ModRarity.CYAN, null, MAX_STACK_SIZE));
    public static final Supplier<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new BlockItem(ModBlocks.EMERALD_COIN_PILE.get(), new Item.Properties().component(TCDataComponentTypes.MOD_RARITY, ModRarity.PURPLE).stacksTo(MAX_STACK_SIZE)));

    public static final Supplier<Item> DEAD_MANS_SWEATER = ITEMS.registerItem("dead_mans_seater", properties -> new CustomRarityItem(properties.stacksTo(1), ModRarity.GREEN));
    public static final Supplier<Item> WHOOPIE_CUSHION = ITEMS.registerSimpleItem("whoopie_cushion", new Item.Properties().stacksTo(1));

    public static final Supplier<GrassSeedItem> GRASS_SEED = ITEMS.register("grass_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, Blocks.GRASS_BLOCK)));
    public static final Supplier<GrassSeedItem> JUNGLE_GRASS_SEED = ITEMS.register("jungle_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> MUSHROOM_GRASS_SEED = ITEMS.register("mushroom_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> CORRUPT_SEED = ITEMS.register("corrupt_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> TR_CRIMSON_SEED = ITEMS.register("tr_crimson_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.TR_CRIMSON_GRASS_BLOCK.get(), Blocks.MUD, NatureBlocks.TR_CRIMSON_JUNGLE_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> HALLOWED_SEED = ITEMS.register("hallowed_seed", () -> new GrassSeedItem(Map.of(Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get())));
    public static final Supplier<GrassSeedItem> ASH_GRASS_SEED = ITEMS.register("ash_grass_seed", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static final Supplier<BlockItem> CATTAILS = BLOCK_ITEMS.register("cattails", () -> new BlockItem(NatureBlocks.CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final Supplier<BlockItem> JUNGLE_CATTAILS = BLOCK_ITEMS.register("jungle_cattails", () -> new BlockItem(NatureBlocks.JUNGLE_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final Supplier<BlockItem> GLOWING_MUSHROOM_CATTAILS = BLOCK_ITEMS.register("glowing_mushroom_cattails", () -> new BlockItem(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final Supplier<BlockItem> HALLOW_CATTAILS = BLOCK_ITEMS.register("hallow_cattails", () -> new BlockItem(NatureBlocks.HALLOW_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final Supplier<BlockItem> EBONY_CATTAILS = BLOCK_ITEMS.register("ebony_cattails", () -> new BlockItem(NatureBlocks.EBONY_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final Supplier<BlockItem> TR_CRIMSON_CATTAILS = BLOCK_ITEMS.register("tr_crimson_cattails", () -> new BlockItem(NatureBlocks.TR_CRIMSON_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));

    public static final Supplier<BlockPlacingWandItem> LIVING_WOOD_WAND = ITEMS.register("living_wood_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_LOG_BLOCKS.getLog().get()));
    public static final Supplier<BlockPlacingWandItem> LEAVES_WAND = ITEMS.register("leaves_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().get()));
    public static final Supplier<BlockPlacingWandItem> LIVING_MAHOGANY_WAND= ITEMS.register("living_mahogany_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLog().get()));
    public static final Supplier<BlockPlacingWandItem> RICH_MAHOGANY_LEAF_WAND= ITEMS.register("rich_mahogany_leaf_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLeaves().get()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        HIDDEN.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
        AccessoryItems.ITEMS.register(eventBus);
        ArmorItems.register(eventBus);
        ArrowItems.ITEMS.register(eventBus);
        AxeItems.ITEMS.register(eventBus);
        BaitItems.ITEMS.register(eventBus);
        BoomerangItems.ITEMS.register(eventBus);
        BowItems.ITEMS.register(eventBus);
        ConsumableItems.ITEMS.register(eventBus);
        DrillItems.ITEMS.register(eventBus);
        FishingPoleItems.ITEMS.register(eventBus);
        FoodItems.ITEMS.register(eventBus);
        HammerItems.ITEMS.register(eventBus);
        HookItems.ITEMS.register(eventBus);
        IconItems.ITEMS.register(eventBus);
        LightPetItems.ITEMS.register(eventBus);
        ManaWeaponItems.ITEMS.register(eventBus);
        MaterialItems.ITEMS.register(eventBus);
        MinecartItems.ITEMS.register(eventBus);
        PaintItems.ITEMS.register(eventBus);
        PickaxeAxeItems.ITEMS.register(eventBus);
        PickaxeItems.ITEMS.register(eventBus);
        HoeItems.ITEMS.register(eventBus);
        ShovelItems.ITEMS.register(eventBus);
        PotionItems.ITEMS.register(eventBus);
        QuestedFishes.ITEMS.register(eventBus);
        SwordItems.ITEMS.register(eventBus);
        ToolItems.ITEMS.register(eventBus);
        TreasureBagItems.ITEMS.register(eventBus);
        VanityArmorItems.ITEMS.register(eventBus);
    }
}

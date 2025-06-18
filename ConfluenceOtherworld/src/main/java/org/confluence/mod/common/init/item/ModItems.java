package org.confluence.mod.common.init.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.JungleHiveBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sponsor.*;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;
import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;

@SuppressWarnings("unused")
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items HIDDEN = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Unbreakable UNBREAKABLE = new Unbreakable(true);

    public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = Confluence.asResource("base_attack_knockback");
    public static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    public static final ResourceLocation BASE_BLOCK_INTERACTION_RANGE_ID = Confluence.asResource("base_block_interaction_range");

    public static final DeferredItem<Item> STAR = HIDDEN.register("star", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> SOUL_CAKE = HIDDEN.register("soul_cake", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> SUGAR_PLUM = HIDDEN.register("sugar_plum", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> HEART = HIDDEN.register("heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> CANDY_APPLE = HIDDEN.register("candy_apple", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> CANDY_CANE = HIDDEN.register("candy_cane", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<EntityDisplayItem> ENTITY_DISPLAY = HIDDEN.register("entity_display", EntityDisplayItem::new);
    public static final DeferredItem<HardmodeConvertorItem> HARDMODE_CONVERTOR = HIDDEN.register("hardmode_convertor", HardmodeConvertorItem::new);
    // 赞助物品
    public static final DeferredItem<BoredomsPactFallingResolve> BOREDOMS_PACT_FALLING_RESOLVE = HIDDEN.register(BoredomsPactFallingResolve.ID.getPath(), BoredomsPactFallingResolve::new);
    public static final DeferredItem<ParadoxInteractiveMedal> PARADOX_INTERACTIVE_MEDAL = HIDDEN.register("paradox_interactive_medal", ParadoxInteractiveMedal::new);
    public static final DeferredItem<TooltipItem> TOKYO_TEDDY_BEAR = HIDDEN.register("tokyo_teddy_bear", () -> new TooltipItem(new Item.Properties(), ModRarity.MASTER, TooltipItem.getTooltipsFromString("tokyo_teddy_bear", 6, ChatFormatting.GRAY)));
    public static final DeferredItem<IceTofuBrickItem> ICE_TOFU_BRICK = HIDDEN.register("ice_tofu_brick", IceTofuBrickItem::new);
    public static final DeferredItem<FailedSkullItem> FAILED_SKULL = HIDDEN.register("failed_skull", FailedSkullItem::new);
    public static final DeferredItem<KindMisideRingItem> KIND_MISIDE_RING = HIDDEN.register("kind_miside_ring", KindMisideRingItem::new);

    public static final DeferredItem<KindMisideRingItem> FERTILE_SINGULARITY = HIDDEN.register("fertile_singularity", KindMisideRingItem::new); // 占位符 丰饶奇点
    public static final DeferredItem<KindMisideRingItem> PERPLEXED_CAT_MEDAL = HIDDEN.register("perplexed_cat_medal", KindMisideRingItem::new); // 占位符 疑惑猫猫勋章
    public static final DeferredItem<KindMisideRingItem> PULSAR = HIDDEN.register("pulsar", KindMisideRingItem::new); // 占位符 脉冲星

    public static final DeferredItem<Item> MYSTERIOUS_NOTE = HIDDEN.register("mysterious_note", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_SLATE = HIDDEN.register("mysterious_slate", () -> new Item(new Item.Properties()));

    public static final DeferredItem<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN.get(), ModRarity.WHITE, ModItems.SILVER_COIN, 100));
    public static final DeferredItem<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN.get(), ModRarity.ORANGE, ModItems.GOLDEN_COIN, 100));
    public static final DeferredItem<CoinItem> GOLDEN_COIN = ITEMS.register("golden_coin", () -> new CoinItem(ModBlocks.GOLDEN_COIN.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN, 100));
    public static final DeferredItem<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN.get(), ModRarity.CYAN, null, MAX_STACK_SIZE));
    public static final DeferredItem<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new BlockItem(ModBlocks.EMERALD_COIN.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE).stacksTo(MAX_STACK_SIZE)));

    public static final DeferredItem<Item> WHOOPIE_CUSHION = ITEMS.registerSimpleItem("whoopie_cushion", new Item.Properties().stacksTo(1));

    public static final DeferredItem<GrassSeedItem> GRASS_SEED = ITEMS.register("grass_seed", () -> new GrassSeedItem(
            Map.of(
                    Blocks.DIRT, Blocks.GRASS_BLOCK,
                    NatureBlocks.CRIMSON_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
                    NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
                    NatureBlocks.HALLOW_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK)));
    public static final DeferredItem<GrassSeedItem> JUNGLE_GRASS_SEED = ITEMS.register("jungle_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> MUSHROOM_GRASS_SEED = ITEMS.register("mushroom_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> CORRUPT_SEED = ITEMS.register("corrupt_seed", () -> new GrassSeedItem(
            Map.of(
                    Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get(),
                    Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
                    Blocks.GRASS_BLOCK, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
                    NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
                    NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> CRIMSON_SEED = ITEMS.register("crimson_seed", () -> new GrassSeedItem(
            Map.of(
                    Blocks.MUD, NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get(),
                    Blocks.DIRT, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
                    Blocks.GRASS_BLOCK, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
                    NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
                    NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> HALLOWED_SEED = ITEMS.register("hallowed_seed", () -> new GrassSeedItem(
            Map.of(
                    Blocks.MUD, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
                    Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
                    Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
                    NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get(),
                    NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> ASH_GRASS_SEED = ITEMS.register("ash_grass_seed", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static final DeferredItem<BlockItem> CATTAILS = BLOCK_ITEMS.register("cattails", () -> new BlockItem(NatureBlocks.CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> JUNGLE_CATTAILS = BLOCK_ITEMS.register("jungle_cattails", () -> new BlockItem(NatureBlocks.JUNGLE_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> GLOWING_MUSHROOM_CATTAILS = BLOCK_ITEMS.register("glowing_mushroom_cattails", () -> new BlockItem(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> HALLOW_CATTAILS = BLOCK_ITEMS.register("hallow_cattails", () -> new BlockItem(NatureBlocks.HALLOW_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> EBONY_CATTAILS = BLOCK_ITEMS.register("ebony_cattails", () -> new BlockItem(NatureBlocks.EBONY_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> CRIMSON_CATTAILS = BLOCK_ITEMS.register("crimson_cattails", () -> new BlockItem(NatureBlocks.CRIMSON_CATTAILS_HEAD.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredItem<BlockPlacingWandItem> LIVING_WOOD_WAND = ITEMS.register("living_wood_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_LOG_BLOCKS.getLog().get()));
    public static final DeferredItem<BlockPlacingWandItem> LEAF_WAND = ITEMS.register("leaf_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().get()));
    public static final DeferredItem<BlockPlacingWandItem> LIVING_MAHOGANY_WAND = ITEMS.register("living_mahogany_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLog().get()));
    public static final DeferredItem<BlockPlacingWandItem> RICH_MAHOGANY_LEAF_WAND = ITEMS.register("rich_mahogany_leaf_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLeaves().get()));
    public static final DeferredItem<BlockPlacingWandItem> HIVE_WAND = ITEMS.register("hive_wand", () -> new BlockPlacingWandItem(null, NatureBlocks.JUNGLE_HIVE_BLOCK.get(), (context, state) -> state.setValue(JungleHiveBlock.NATURAL, true)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        HIDDEN.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
        AccessoryItems.ITEMS.register(eventBus);
        ArmorItems.register(eventBus);
        ArrowItems.ITEMS.register(eventBus);
        AxeItems.ITEMS.register(eventBus);
        BaitItems.ITEMS.register(eventBus);
        BowItems.ITEMS.register(eventBus);
        ConsumableItems.ITEMS.register(eventBus);
        DrillItems.ITEMS.register(eventBus);
        FishingPoleItems.ITEMS.register(eventBus);
        FoodItems.ITEMS.register(eventBus);
        HamaxeItems.ITEMS.register(eventBus);
        HoeShovelItems.ITEMS.register(eventBus);
        HammerItems.ITEMS.register(eventBus);
        HookItems.ITEMS.register(eventBus);
        IconItems.ITEMS.register(eventBus);
        LanceItems.ITEMS.register(eventBus);
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
        GunItems.ITEMS.register(eventBus);
    }

    public static Item.@NotNull Properties unbreakable() {
        return new Item.Properties().component(DataComponents.UNBREAKABLE, UNBREAKABLE);
    }

    public static Consumer<ItemAttributeModifiers.Builder> attributes(double blockInteractionRange, double attackKnockback) {
        return builder -> {
            if (blockInteractionRange != 0)
                builder.add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(BASE_BLOCK_INTERACTION_RANGE_ID, blockInteractionRange, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            if (attackKnockback != 0)
                builder.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(BASE_ATTACK_KNOCKBACK_ID, attackKnockback, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
            builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(BASE_CRITICAL_CHANCE_ID, 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        };
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed, Consumer<ItemAttributeModifiers.Builder> consumer) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        consumer.accept(builder);
        return builder.add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).build();
    }
}

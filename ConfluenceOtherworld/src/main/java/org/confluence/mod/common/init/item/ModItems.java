package org.confluence.mod.common.init.item;

import com.google.common.collect.Iterables;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.block.natural.JungleHiveBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sponsor.*;
import org.confluence.mod.integration.sodium.iris.IrisHelper;
import org.confluence.mod.util.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Item.BASE_ATTACK_DAMAGE_ID;
import static net.minecraft.world.item.Item.BASE_ATTACK_SPEED_ID;
import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

@SuppressWarnings("unused")
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items HIDDEN = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Confluence.MODID);

    public static final Unbreakable UNBREAKABLE = new Unbreakable(true);

    public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = Confluence.asResource("base_attack_knockback");
    public static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    public static final ResourceLocation BASE_BLOCK_INTERACTION_RANGE_ID = Confluence.asResource("base_block_interaction_range");
    public static final ResourceLocation BASE_ENTITY_INTERACTION_RANGE_ID = Confluence.asResource("base_entity_interaction_range");

    public static final DeferredItem<Item> STAR = registerHidden("star", "魔力星", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> SOUL_CAKE = registerHidden("soul_cake", "灵魂蛋糕", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> SUGAR_PLUM = registerHidden("sugar_plum", "蜜糖李", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> HEART = registerHidden("heart", "心", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> CANDY_APPLE = registerHidden("candy_apple", "焦糖苹果", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<Item> CANDY_CANE = registerHidden("candy_cane", "糖棒", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final DeferredItem<EntityDisplayItem> ENTITY_DISPLAY = registerHidden("entity_display", "生物显示", EntityDisplayItem::new);
    public static final DeferredItem<HardmodeConvertorItem> HARDMODE_CONVERTOR = registerHidden("hardmode_convertor", "困难模式转换器", HardmodeConvertorItem::new);
    // 赞助物品
    public static final DeferredItem<BoredomsPactFallingResolve> BOREDOMS_PACT_FALLING_RESOLVE = registerHiddenWithEnglishName(BoredomsPactFallingResolve.ID.getPath(), "「无聊之咒·陨志」", "Boredom's Pact - Falling Resolve", BoredomsPactFallingResolve::new);
    public static final DeferredItem<ParadoxInteractiveMedal> PARADOX_INTERACTIVE_MEDAL = registerHidden("paradox_interactive_medal", "帕拉多克斯·英特拉克缇福勋章", ParadoxInteractiveMedal::new);
    public static final DeferredItem<CustomRarityItem> TOKYO_TEDDY_BEAR = registerHidden("tokyo_teddy_bear", "东京泰迪熊", () -> new CustomRarityItem(new Item.Properties(), ModRarity.MASTER));
    public static final DeferredItem<IceTofuBrickItem> ICE_TOFU_BRICK = registerHidden("ice_tofu_brick", "冰豆腐砖", IceTofuBrickItem::new);
    public static final DeferredItem<FailedSkullItem> FAILED_SKULL = HIDDEN.register("failed_skull", FailedSkullItem::new);
    public static final DeferredItem<KindMisideRingItem> KIND_MISIDE_RING = registerHidden("kind_miside_ring", "善良米塔的戒指", KindMisideRingItem::new);

    public static final DeferredItem<KindMisideRingItem> FERTILE_SINGULARITY = registerHidden("fertile_singularity", "丰饶奇点", KindMisideRingItem::new); // 占位符 丰饶奇点
    public static final DeferredItem<KindMisideRingItem> PERPLEXED_CAT_MEDAL = registerHidden("perplexed_cat_medal", "疑惑猫猫勋章", KindMisideRingItem::new); // 占位符 疑惑猫猫勋章
    public static final DeferredItem<KindMisideRingItem> PULSAR = registerHidden("pulsar", "脉冲星", KindMisideRingItem::new); // 占位符 脉冲星

    public static final DeferredItem<Item> MYSTERIOUS_NOTE = registerHidden("mysterious_note", "神秘纸条", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MYSTERIOUS_SLATE = registerHidden("mysterious_slate", "神秘石板", () -> new Item(new Item.Properties()));

    public static final DeferredItem<BestiaryItem> BESTIARY = registerHidden("bestiary", "怪物图鉴", BestiaryItem::new);
    public static final DeferredItem<Item> BACKGROUND_IMAGE_MAKER = registerHidden("background_image_maker", "背景图生成器", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));

    public static final DeferredItem<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN.get(), ModRarity.WHITE, ModItems.SILVER_COIN, 100));
    public static final DeferredItem<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN.get(), ModRarity.ORANGE, ModItems.GOLD_COIN, 100));
    public static final DeferredItem<CoinItem> GOLD_COIN = ITEMS.register("gold_coin", () -> new CoinItem(ModBlocks.GOLD_COIN.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN, 100));
    public static final DeferredItem<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN.get(), ModRarity.CYAN, null, MAX_STACK_SIZE));
    public static final DeferredItem<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new BlockItem(ModBlocks.EMERALD_COIN.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE).stacksTo(MAX_STACK_SIZE)));

    public static final DeferredItem<Item> WHOOPIE_CUSHION = registerItemSimple("whoopie_cushion", "整蛊坐垫", new Item.Properties().stacksTo(1));

    public static final DeferredItem<GrassSeedItem> GRASS_SEED = registerItem("grass_seed", "草种子", () -> new GrassSeedItem(Map.of(
            Blocks.DIRT, Blocks.GRASS_BLOCK,
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK
    )));
    public static final DeferredItem<GrassSeedItem> JUNGLE_GRASS_SEED = registerItem("jungle_grass_seed", "丛林草种子", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> MUSHROOM_GRASS_SEED = registerItem("mushroom_grass_seed", "蘑菇草种子", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final DeferredItem<GrassSeedItem> CORRUPT_SEED = registerItem("corrupt_seed", "腐化种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get()
    )));
    public static final DeferredItem<GrassSeedItem> CRIMSON_SEED = registerItem("crimson_seed", "猩红种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get()
    )));
    public static final DeferredItem<GrassSeedItem> HALLOWED_SEED = registerItem("hallowed_seed", "神圣种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get()
    )));
    public static final DeferredItem<GrassSeedItem> ASH_GRASS_SEED = registerItem("ash_grass_seed", "灰烬草种子", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static final DeferredItem<BlockItem> CATTAIL = BLOCK_ITEMS.register("cattail", () -> new BlockItem(NatureBlocks.CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> JUNGLE_CATTAIL = BLOCK_ITEMS.register("jungle_cattail", () -> new BlockItem(NatureBlocks.JUNGLE_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> GLOWING_MUSHROOM_CATTAIL = BLOCK_ITEMS.register("glowing_mushroom_cattail", () -> new BlockItem(NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> HALLOW_CATTAIL = BLOCK_ITEMS.register("hallow_cattail", () -> new BlockItem(NatureBlocks.HALLOW_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> EBONY_CATTAIL = BLOCK_ITEMS.register("ebony_cattail", () -> new BlockItem(NatureBlocks.EBONY_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final DeferredItem<BlockItem> CRIMSON_CATTAIL = BLOCK_ITEMS.register("crimson_cattail", () -> new BlockItem(NatureBlocks.CRIMSON_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredItem<BlockPlacingWandItem> LIVING_WOOD_WAND = registerItem("living_wood_wand", "生命木魔棒", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_LOG_BLOCKS.LOG.get()));
    public static final DeferredItem<BlockPlacingWandItem> LEAF_WAND = registerItem("leaf_wand", "树叶魔棒", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_LOG_BLOCKS.LEAVES.get()));
    public static final DeferredItem<BlockPlacingWandItem> LIVING_MAHOGANY_WAND = registerItem("living_mahogany_wand", "生命红木魔棒", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LOG.get()));
    public static final DeferredItem<BlockPlacingWandItem> RICH_MAHOGANY_LEAF_WAND = registerItem("rich_mahogany_leaf_wand", "红木树叶魔棒", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LEAVES.get()));
    public static final DeferredItem<BlockPlacingWandItem> HIVE_WAND = registerItem("hive_wand", "蜂巢魔棒", () -> new BlockPlacingWandItem(null, NatureBlocks.JUNGLE_HIVE_BLOCK.get(), (context, state) -> state.setValue(JungleHiveBlock.NATURAL, true)));

    public static final DeferredItem<ScryingOrb> SCRYING_ORB = registerItem("scrying_orb", "占卜球", ScryingOrb::new);

    public static final DeferredItem<Item> HUANG_LI = registerHidden("huang_li", "黄历", () -> new Item(new Item.Properties()) {
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.literal("今日宜：" + DateUtils.getYi()).withStyle(ChatFormatting.GOLD), false);
                player.displayClientMessage(Component.literal("今日忌：" + DateUtils.getJi()).withStyle(ChatFormatting.GRAY), false);
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
    });

    public static final DeferredItem<AbstractEnemyBannerBlock.BItem> ENEMY_BANNER = ITEMS.register("enemy_banner", AbstractEnemyBannerBlock.BItem::new);

    public static final DeferredItem<DeferredSpawnEggItem> RAINBOW_SHEEP_SPAWN_EGG = registerItem("rainbow_sheep_spawn_egg", "彩虹羊刷怪蛋", () -> new DeferredSpawnEggItem(ModEntities.RAINBOW_SHEEP, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        HIDDEN.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
        AccessoryItems.ITEMS.register(eventBus);
        ArmorItems.register(eventBus);
        ArrowItems.ITEMS.register(eventBus);
        AxeItems.ITEMS.register(eventBus);
        BaitItems.ITEMS.register(eventBus);
        BoatItems.register(eventBus);
        BoomerangItems.ITEMS.register(eventBus);
        BowItems.ITEMS.register(eventBus);
        ChainsawItems.ITEMS.register(eventBus);
        ConsumableItems.ITEMS.register(eventBus);
        CrossbowItems.ITEMS.register(eventBus);
        DrillItems.ITEMS.register(eventBus);
        FishingPoleItems.ITEMS.register(eventBus);
        FlailItems.ITEMS.register(eventBus);
        FoodItems.ITEMS.register(eventBus);
        GardenShearsItems.ITEMS.register(eventBus);
        GunItems.ITEMS.register(eventBus);
        HamaxeItems.ITEMS.register(eventBus);
        HammerItems.ITEMS.register(eventBus);
        HoeItems.ITEMS.register(eventBus);
        HoeShovelItems.ITEMS.register(eventBus);
        HookItems.ITEMS.register(eventBus);
        IconItems.ITEMS.register(eventBus);
        LanceItems.ITEMS.register(eventBus);
        LightPetItems.ITEMS.register(eventBus);
        ManaWeaponItems.ITEMS.register(eventBus);
        MaterialItems.ITEMS.register(eventBus);
        MinecartItems.ITEMS.register(eventBus);
        MountItems.ITEMS.register(eventBus);
        PetItems.ITEMS.register(eventBus);
        PaintItems.ITEMS.register(eventBus);
        PickaxeAxeItems.ITEMS.register(eventBus);
        PickaxeItems.ITEMS.register(eventBus);
        PotionItems.ITEMS.register(eventBus);
        QuestedFishes.ITEMS.register(eventBus);
        ShovelItems.ITEMS.register(eventBus);
        SpawnEggItems.ITEMS.register(eventBus);
        SpearItems.ITEMS.register(eventBus);
        SummonItems.ITEMS.register(eventBus);
        SwordItems.ITEMS.register(eventBus);
        ToolItems.ITEMS.register(eventBus);
        TreasureBagItems.ITEMS.register(eventBus);
        VanityArmorItems.ITEMS.register(eventBus);
        WhipItems.ITEMS.register(eventBus);
        YoyoItems.ITEMS.register(eventBus);

        if (LibUtils.isModLoaded("iris")) {
            IrisHelper.register(HIDDEN);
        }

        Iterables.concat(
                BoomerangItems.ITEMS.getEntries(),
                GunItems.ITEMS.getEntries(),
                MountItems.ITEMS.getEntries(),
                PetItems.ITEMS.getEntries(),
                SpawnEggItems.ITEMS.getEntries(),
                SummonItems.ITEMS.getEntries(),
                WhipItems.ITEMS.getEntries(),
                YoyoItems.ITEMS.getEntries()
        ).forEach(holder -> BuiltInRegistries.ITEM.addAlias(
                ResourceLocation.fromNamespaceAndPath("terra_entity", holder.getId().getPath()),
                holder.getId()
        ));
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
            builder.add(LibAttributes.getCriticalChance(), new AttributeModifier(BASE_CRITICAL_CHANCE_ID, 0.04, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        };
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed, Consumer<ItemAttributeModifiers.Builder> consumer) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        consumer.accept(builder);
        return builder.add(
                LibAttributes.getAttackDamage(),
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).build();
    }

    private static <T extends Item> DeferredItem<T> registerHidden(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = HIDDEN.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        DeferredItem<T> entry = ITEMS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static DeferredItem<Item> registerItemSimple(String registryId, String chineseName, Item.Properties properties) {
        DeferredItem<Item> entry = ITEMS.registerSimpleItem(registryId, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> DeferredItem<T> registerHiddenWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        DeferredItem<T> entry = HIDDEN.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}

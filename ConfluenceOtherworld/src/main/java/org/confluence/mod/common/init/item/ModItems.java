package org.confluence.mod.common.init.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.block.natural.JungleHiveBlock;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sponsor.*;
import org.confluence.mod.util.DateUtils;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

@SuppressWarnings("unused")
public final class ModItems {
    public static void init() {
        AccessoryItems.init();
        ArmorItems.init();
        ArrowItems.init();
        AxeItems.init();
        BaitItems.init();
        BoatItems.init();
        BoomerangItems.init();
        BowItems.init();
        ChainsawItems.init();
        ConsumableItems.init();
        CrossbowItems.init();
        DrillItems.init();
        FishingPoleItems.init();
        FlailItems.init();
        FoodItems.init();
        GardenShearsItems.init();
        GunItems.init();
        HamaxeItems.init();
        HammerItems.init();
        HoeItems.init();
        HoeShovelItems.init();
        HookItems.init();
        IconItems.init();
        LanceItems.init();
        LightPetItems.init();
        ManaWeaponItems.init();
        MaterialItems.init();
        MinecartItems.init();
        MountItems.init();
        PaintItems.init();
        PetItems.init();
        PickaxeAxeItems.init();
        PickaxeItems.init();
        PotionItems.init();
        QuestedFishes.init();
        ShovelItems.init();
        SpawnEggItems.init();
        SpearItems.init();
        SummonItems.init();
        SwordItems.init();
        ToolItems.init();
        TreasureBagItems.init();
        VanityArmorItems.init();
        WhipItems.init();
        YoyoItems.init();
    }

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final PortItemRegistration HIDDEN = PortRegisterHandler.item(Confluence.MODID);
    public static final PortItemRegistration BLOCK_ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
    public static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");
    public static final ResourceLocation BASE_ATTACK_KNOCKBACK_ID = Confluence.asResource("base_attack_knockback");
    public static final ResourceLocation BASE_CRITICAL_CHANCE_ID = Confluence.asResource("base_critical_chance");
    public static final ResourceLocation BASE_BLOCK_INTERACTION_RANGE_ID = Confluence.asResource("base_block_interaction_range");
    public static final ResourceLocation BASE_ENTITY_INTERACTION_RANGE_ID = Confluence.asResource("base_entity_interaction_range");

    public static final PortDeferredItem<Item> STAR = registerHidden("star", "魔力星", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> SOUL_CAKE = registerHidden("soul_cake", "灵魂蛋糕", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> SUGAR_PLUM = registerHidden("sugar_plum", "蜜糖李", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> HEART = registerHidden("heart", "心", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> CANDY_APPLE = registerHidden("candy_apple", "焦糖苹果", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> CANDY_CANE = registerHidden("candy_cane", "糖棒", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<EntityDisplayItem> ENTITY_DISPLAY = registerHidden("entity_display", "生物显示", EntityDisplayItem::new);
    public static final PortDeferredItem<HardmodeConvertorItem> HARDMODE_CONVERTOR = registerHidden("hardmode_convertor", "困难模式转换器", HardmodeConvertorItem::new);
    // 赞助物品
    public static final PortDeferredItem<BoredomsPactFallingResolve> BOREDOMS_PACT_FALLING_RESOLVE = registerHiddenWithEnglishName(BoredomsPactFallingResolve.ID.getPath(), "「无聊之咒·陨志」", "Boredom's Pact - Falling Resolve", BoredomsPactFallingResolve::new);
    public static final PortDeferredItem<ParadoxInteractiveMedal> PARADOX_INTERACTIVE_MEDAL = registerHidden("paradox_interactive_medal", "帕拉多克斯·英特拉克缇福勋章", ParadoxInteractiveMedal::new);
    public static final PortDeferredItem<CustomRarityItem> TOKYO_TEDDY_BEAR = registerHidden("tokyo_teddy_bear", "东京泰迪熊", () -> new CustomRarityItem(new Item.Properties(), ModRarity.MASTER));
    public static final PortDeferredItem<IceTofuBrickItem> ICE_TOFU_BRICK = registerHidden("ice_tofu_brick", "冰豆腐砖", IceTofuBrickItem::new);
    public static final PortDeferredItem<FailedSkullItem> FAILED_SKULL = HIDDEN.register("failed_skull", FailedSkullItem::new);
    public static final PortDeferredItem<KindMisideRingItem> KIND_MISIDE_RING = registerHidden("kind_miside_ring", "善良米塔的戒指", KindMisideRingItem::new);

    public static final PortDeferredItem<KindMisideRingItem> FERTILE_SINGULARITY = registerHidden("fertile_singularity", "丰饶奇点", KindMisideRingItem::new); // 占位符 丰饶奇点
    public static final PortDeferredItem<KindMisideRingItem> PERPLEXED_CAT_MEDAL = registerHidden("perplexed_cat_medal", "疑惑猫猫勋章", KindMisideRingItem::new); // 占位符 疑惑猫猫勋章
    public static final PortDeferredItem<KindMisideRingItem> PULSAR = registerHidden("pulsar", "脉冲星", KindMisideRingItem::new); // 占位符 脉冲星

    public static final PortDeferredItem<Item> MYSTERIOUS_NOTE = registerHidden("mysterious_note", "神秘纸条", () -> new Item(new Item.Properties()));
    public static final PortDeferredItem<Item> MYSTERIOUS_SLATE = registerHidden("mysterious_slate", "神秘石板", () -> new Item(new Item.Properties()));
    /// 临时开放魂师界面的开发者物品。
    ///
    /// 该物品不进入正常获取流程，仅用于隔离尚未完成的魂师界面。
    /// 魂师体系正式接入玩家进度后，应移除界面门禁，而不是把它变成正式解锁条件。
    public static final PortDeferredItem<Item> TEST_SOUL_GUI = registerHiddenSimple("test_soul_gui", "魂师GUI测试", new Item.Properties().stacksTo(1));

    public static final PortDeferredItem<BestiaryItem> BESTIARY = registerHidden("bestiary", "怪物图鉴", BestiaryItem::new);

    public static final PortDeferredItem<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN.get(), ModRarity.WHITE, ModItems.SILVER_COIN, 100));
    public static final PortDeferredItem<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN.get(), ModRarity.ORANGE, ModItems.GOLD_COIN, 100));
    public static final PortDeferredItem<CoinItem> GOLD_COIN = ITEMS.register("gold_coin", () -> new CoinItem(ModBlocks.GOLD_COIN.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN, 100));
    public static final PortDeferredItem<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN.get(), ModRarity.CYAN, null, MAX_STACK_SIZE));
    public static final PortDeferredItem<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new BlockItem(ModBlocks.EMERALD_COIN.get(), new Item.Properties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE).stacksTo(MAX_STACK_SIZE)));

    public static final PortDeferredItem<Item> WHOOPIE_CUSHION = registerItemSimple("whoopie_cushion", "整蛊坐垫", new Item.Properties().stacksTo(1));

    public static final PortDeferredItem<GrassSeedItem> GRASS_SEED = registerItem("grass_seed", "草种子", () -> new GrassSeedItem(Map.of(
            Blocks.DIRT, Blocks.GRASS_BLOCK,
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK
    )));
    public static final PortDeferredItem<GrassSeedItem> JUNGLE_GRASS_SEED = registerItem("jungle_grass_seed", "丛林草种子", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final PortDeferredItem<GrassSeedItem> MUSHROOM_GRASS_SEED = registerItem("mushroom_grass_seed", "蘑菇草种子", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final PortDeferredItem<GrassSeedItem> CORRUPT_SEED = registerItem("corrupt_seed", "腐化种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> CRIMSON_SEED = registerItem("crimson_seed", "猩红种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> HALLOWED_SEED = registerItem("hallowed_seed", "神圣种子", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> ASH_GRASS_SEED = registerItem("ash_grass_seed", "灰烬草种子", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static final PortDeferredItem<BlockItem> CATTAIL = BLOCK_ITEMS.register("cattail", () -> new BlockItem(NatureBlocks.CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> JUNGLE_CATTAIL = BLOCK_ITEMS.register("jungle_cattail", () -> new BlockItem(NatureBlocks.JUNGLE_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> GLOWING_MUSHROOM_CATTAIL = BLOCK_ITEMS.register("glowing_mushroom_cattail", () -> new BlockItem(NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> HALLOW_CATTAIL = BLOCK_ITEMS.register("hallow_cattail", () -> new BlockItem(NatureBlocks.HALLOW_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> EBONY_CATTAIL = BLOCK_ITEMS.register("ebony_cattail", () -> new BlockItem(NatureBlocks.EBONY_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> CRIMSON_CATTAIL = BLOCK_ITEMS.register("crimson_cattail", () -> new BlockItem(NatureBlocks.CRIMSON_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));

    public static final PortDeferredItem<BlockPlacingWandItem> LIVING_WOOD_WAND = registerItem("living_wood_wand", "生命木魔棒", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_LOG_BLOCKS.LOG.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> LEAF_WAND = registerItem("leaf_wand", "树叶魔棒", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_LOG_BLOCKS.LEAVES.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> LIVING_MAHOGANY_WAND = registerItem("living_mahogany_wand", "生命红木魔棒", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LOG.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> RICH_MAHOGANY_LEAF_WAND = registerItem("rich_mahogany_leaf_wand", "红木树叶魔棒", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LEAVES.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> HIVE_WAND = registerItem("hive_wand", "蜂巢魔棒", () -> new BlockPlacingWandItem(null, NatureBlocks.JUNGLE_HIVE_BLOCK.get(), (context, state) -> state.setValue(JungleHiveBlock.NATURAL, true)));

    public static final PortDeferredItem<ScryingOrb> SCRYING_ORB = registerItem("scrying_orb", "占卜球", ScryingOrb::new);

    public static final PortDeferredItem<Item> HUANG_LI = registerHidden("huang_li", "黄历", () -> new Item(new Item.Properties()) {
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.literal("今日宜：" + DateUtils.getYi()).withStyle(ChatFormatting.GOLD), false);
                player.displayClientMessage(Component.literal("今日忌：" + DateUtils.getJi()).withStyle(ChatFormatting.GRAY), false);
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
    });

    public static final PortDeferredItem<AbstractEnemyBannerBlock.BItem> ENEMY_BANNER = ITEMS.register("enemy_banner", AbstractEnemyBannerBlock.BItem::new);

    public static final PortDeferredItem<ForgeSpawnEggItem> RAINBOW_SHEEP_SPAWN_EGG = registerItem("rainbow_sheep_spawn_egg", "彩虹羊刷怪蛋", () -> new ForgeSpawnEggItem(ModEntities.RAINBOW_SHEEP, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static Item.Properties unbreakable() {
        return new Item.Properties().unbreakable();
    }

    public static Consumer<PortItemAttributeModifiers.Builder> attributes(double blockInteractionRange, double attackKnockback) {
        return builder -> {
            if (blockInteractionRange != 0)
                builder.add(Attributes.BLOCK_INTERACTION_RANGE, new PortAttributeModifier(BASE_BLOCK_INTERACTION_RANGE_ID, blockInteractionRange, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            if (attackKnockback != 0)
                builder.add(Attributes.ATTACK_KNOCKBACK, new PortAttributeModifier(BASE_ATTACK_KNOCKBACK_ID, attackKnockback, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            builder.add(LibAttributes.getCriticalChance(), new PortAttributeModifier(BASE_CRITICAL_CHANCE_ID, 0.04, PortAttributeModifier.Operation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
        };
    }

    /// 生成武器的属性组件：伤害=攻击力+阶级加成（玩家基础 1 点另计），攻速为原始修正值，
    /// 额外属性由 `consumer` 追加。与 1.21.1 的同名方法保持一致，便于双版本对齐。
    public static PortItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed, Consumer<PortItemAttributeModifiers.Builder> consumer) {
        PortItemAttributeModifiers.Builder builder = PortItemAttributeModifiers.builder();
        consumer.accept(builder);
        return builder.add(
                LibAttributes.getAttackDamage(),
                new PortAttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), PortAttributeModifier.Operation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND
        ).add(
                Attributes.ATTACK_SPEED,
                new PortAttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, PortAttributeModifier.Operation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND
        ).build();
    }

    public static float getAttackSpeed(float rawSpeed) {
        return rawSpeed - 4;
    }

    public static float getAttackDamage(Tier tier, float rawDamage) {
        return rawDamage - tier.getAttackDamageBonus() - 1;
    }

    public static Multimap<Attribute, AttributeModifier> mergeModifiers(Multimap<Attribute, AttributeModifier> original, Consumer<PortItemAttributeModifiers.Builder> consumer) {
        PortItemAttributeModifiers.Builder builder = PortItemAttributeModifiers.builder();
        consumer.accept(builder);
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .putAll(original)
                .putAll(builder.build().getAttributeModifiers(EquipmentSlot.MAINHAND))
                .build();
    }

    private static <T extends Item> PortDeferredItem<T> registerHidden(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = HIDDEN.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<Item> registerHiddenSimple(String registryId, String chineseName, Item.Properties properties) {
        PortDeferredItem<Item> entry = HIDDEN.registerSimpleItem(registryId, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static PortDeferredItem<Item> registerItemSimple(String registryId, String chineseName, Item.Properties properties) {
        PortDeferredItem<Item> entry = ITEMS.registerSimpleItem(registryId, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerHiddenWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = HIDDEN.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}

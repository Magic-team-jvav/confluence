package org.confluence.mod.common.init.item;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
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
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.block.natural.JungleHiveBlock;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.common.*;
import org.confluence.mod.common.item.sponsor.*;
import org.confluence.mod.util.DateUtils;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.PortItem;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Map;
import java.util.function.Consumer;

import static org.confluence.lib.util.LibUtils.MAX_STACK_SIZE;

@SuppressWarnings("unused")
public final class ModItems {
    public static void init() {
        AccessoryItems.init();
        ArmorItems.init();
        ArrowItems.init();
        AxeItems.init();
        BaitItems.init();
        BoatItems.init();
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
        PaintItems.init();
        PickaxeAxeItems.init();
        PickaxeItems.init();
        PotionItems.init();
        QuestedFishes.init();
        ShovelItems.init();
        SpearItems.init();
        SwordItems.init();
        ToolItems.init();
        TreasureBagItems.init();
        VanityArmorItems.init();
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

    public static final PortDeferredItem<Item> STAR = HIDDEN.register("star", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> SOUL_CAKE = HIDDEN.register("soul_cake", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> SUGAR_PLUM = HIDDEN.register("sugar_plum", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> HEART = HIDDEN.register("heart", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> CANDY_APPLE = HIDDEN.register("candy_apple", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<Item> CANDY_CANE = HIDDEN.register("candy_cane", () -> new CustomRarityItem(new Item.Properties().stacksTo(MAX_STACK_SIZE), ModRarity.MASTER));
    public static final PortDeferredItem<EntityDisplayItem> ENTITY_DISPLAY = HIDDEN.register("entity_display", EntityDisplayItem::new);
    public static final PortDeferredItem<HardmodeConvertorItem> HARDMODE_CONVERTOR = HIDDEN.register("hardmode_convertor", HardmodeConvertorItem::new);
    // 赞助物品
    public static final PortDeferredItem<BoredomsPactFallingResolve> BOREDOMS_PACT_FALLING_RESOLVE = HIDDEN.register(BoredomsPactFallingResolve.ID.getPath(), BoredomsPactFallingResolve::new);
    public static final PortDeferredItem<ParadoxInteractiveMedal> PARADOX_INTERACTIVE_MEDAL = HIDDEN.register("paradox_interactive_medal", ParadoxInteractiveMedal::new);
    public static final PortDeferredItem<TooltipItem> TOKYO_TEDDY_BEAR = HIDDEN.register("tokyo_teddy_bear", () -> new TooltipItem(new Item.Properties(), ModRarity.MASTER, TooltipItem.getTooltipsFromString("tokyo_teddy_bear", 6, ChatFormatting.GRAY)));
    public static final PortDeferredItem<IceTofuBrickItem> ICE_TOFU_BRICK = HIDDEN.register("ice_tofu_brick", IceTofuBrickItem::new);
    public static final PortDeferredItem<FailedSkullItem> FAILED_SKULL = HIDDEN.register("failed_skull", FailedSkullItem::new);
    public static final PortDeferredItem<KindMisideRingItem> KIND_MISIDE_RING = HIDDEN.register("kind_miside_ring", KindMisideRingItem::new);

    public static final PortDeferredItem<KindMisideRingItem> FERTILE_SINGULARITY = HIDDEN.register("fertile_singularity", KindMisideRingItem::new); // 占位符 丰饶奇点
    public static final PortDeferredItem<KindMisideRingItem> PERPLEXED_CAT_MEDAL = HIDDEN.register("perplexed_cat_medal", KindMisideRingItem::new); // 占位符 疑惑猫猫勋章
    public static final PortDeferredItem<KindMisideRingItem> PULSAR = HIDDEN.register("pulsar", KindMisideRingItem::new); // 占位符 脉冲星

    public static final PortDeferredItem<Item> MYSTERIOUS_NOTE = HIDDEN.register("mysterious_note", () -> new Item(new Item.Properties()));
    public static final PortDeferredItem<Item> MYSTERIOUS_SLATE = HIDDEN.register("mysterious_slate", () -> new Item(new Item.Properties()));

    public static final PortDeferredItem<BestiaryItem> BESTIARY = HIDDEN.register("bestiary", BestiaryItem::new);
    public static final PortDeferredItem<Item> BACKGROUND_IMAGE_MAKER = HIDDEN.register("background_image_maker", () -> new CustomRarityItem(new Item.Properties().stacksTo(1), ModRarity.MASTER));

    public static final PortDeferredItem<CoinItem> COPPER_COIN = ITEMS.register("copper_coin", () -> new CoinItem(ModBlocks.COPPER_COIN.get(), ModRarity.WHITE, ModItems.SILVER_COIN, 100));
    public static final PortDeferredItem<CoinItem> SILVER_COIN = ITEMS.register("silver_coin", () -> new CoinItem(ModBlocks.SILVER_COIN.get(), ModRarity.ORANGE, ModItems.GOLD_COIN, 100));
    public static final PortDeferredItem<CoinItem> GOLD_COIN = ITEMS.register("gold_coin", () -> new CoinItem(ModBlocks.GOLD_COIN.get(), ModRarity.LIGHT_PURPLE, ModItems.PLATINUM_COIN, 100));
    public static final PortDeferredItem<CoinItem> PLATINUM_COIN = ITEMS.register("platinum_coin", () -> new CoinItem(ModBlocks.PLATINUM_COIN.get(), ModRarity.CYAN, null, MAX_STACK_SIZE));
    public static final PortDeferredItem<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new BlockItem(ModBlocks.EMERALD_COIN.get(), new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, ModRarity.PURPLE).stacksTo(MAX_STACK_SIZE)));

    public static final PortDeferredItem<Item> WHOOPIE_CUSHION = ITEMS.registerSimpleItem("whoopie_cushion", new Item.Properties().stacksTo(1));

    public static final PortDeferredItem<GrassSeedItem> GRASS_SEED = ITEMS.register("grass_seed", () -> new GrassSeedItem(Map.of(
            Blocks.DIRT, Blocks.GRASS_BLOCK,
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK,
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), Blocks.GRASS_BLOCK
    )));
    public static final PortDeferredItem<GrassSeedItem> JUNGLE_GRASS_SEED = ITEMS.register("jungle_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.JUNGLE_GRASS_BLOCK.get())));
    public static final PortDeferredItem<GrassSeedItem> MUSHROOM_GRASS_SEED = ITEMS.register("mushroom_grass_seed", () -> new GrassSeedItem(Map.of(Blocks.MUD, NatureBlocks.MUSHROOM_GRASS_BLOCK.get())));
    public static final PortDeferredItem<GrassSeedItem> CORRUPT_SEED = ITEMS.register("corrupt_seed", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CORRUPT_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CORRUPT_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> CRIMSON_SEED = ITEMS.register("crimson_seed", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.CRIMSON_JUNGLE_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get(),
            NatureBlocks.HALLOW_GRASS_BLOCK.get(), NatureBlocks.CRIMSON_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> HALLOWED_SEED = ITEMS.register("hallowed_seed", () -> new GrassSeedItem(Map.of(
            Blocks.MUD, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.DIRT, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            Blocks.GRASS_BLOCK, NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CRIMSON_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get(),
            NatureBlocks.CORRUPT_GRASS_BLOCK.get(), NatureBlocks.HALLOW_GRASS_BLOCK.get()
    )));
    public static final PortDeferredItem<GrassSeedItem> ASH_GRASS_SEED = ITEMS.register("ash_grass_seed", () -> new GrassSeedItem(Map.of(NatureBlocks.ASH_BLOCK.get(), NatureBlocks.ASH_GRASS_BLOCK.get())));

    public static final PortDeferredItem<BlockItem> CATTAIL = BLOCK_ITEMS.register("cattail", () -> new BlockItem(NatureBlocks.CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> JUNGLE_CATTAIL = BLOCK_ITEMS.register("jungle_cattail", () -> new BlockItem(NatureBlocks.JUNGLE_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> GLOWING_MUSHROOM_CATTAIL = BLOCK_ITEMS.register("glowing_mushroom_cattail", () -> new BlockItem(NatureBlocks.GLOWING_MUSHROOM_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> HALLOW_CATTAIL = BLOCK_ITEMS.register("hallow_cattail", () -> new BlockItem(NatureBlocks.HALLOW_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> EBONY_CATTAIL = BLOCK_ITEMS.register("ebony_cattail", () -> new BlockItem(NatureBlocks.EBONY_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));
    public static final PortDeferredItem<BlockItem> CRIMSON_CATTAIL = BLOCK_ITEMS.register("crimson_cattail", () -> new BlockItem(NatureBlocks.CRIMSON_CATTAIL_BLOCK.get(), new Item.Properties().stacksTo(64)));

    public static final PortDeferredItem<BlockPlacingWandItem> LIVING_WOOD_WAND = ITEMS.register("living_wood_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_LOG_BLOCKS.LOG.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> LEAF_WAND = ITEMS.register("leaf_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_LOG_BLOCKS.LEAVES.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> LIVING_MAHOGANY_WAND = ITEMS.register("living_mahogany_wand", () -> new BlockPlacingWandItem(BlockTags.LOGS, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LOG.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> RICH_MAHOGANY_LEAF_WAND = ITEMS.register("rich_mahogany_leaf_wand", () -> new BlockPlacingWandItem(BlockTags.LEAVES, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.LEAVES.get()));
    public static final PortDeferredItem<BlockPlacingWandItem> HIVE_WAND = ITEMS.register("hive_wand", () -> new BlockPlacingWandItem(null, NatureBlocks.JUNGLE_HIVE_BLOCK.get(), (context, state) -> state.setValue(JungleHiveBlock.NATURAL, true)));

    public static final PortDeferredItem<ScryingOrb> SCRYING_ORB = ITEMS.register("scrying_orb", ScryingOrb::new);

    public static final PortDeferredItem<Item> HUANG_LI = HIDDEN.register("huang_li", () -> new Item(new Item.Properties()) {
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

    public static final PortDeferredItem<ForgeSpawnEggItem> RAINBOW_SHEEP_SPAWN_EGG = ITEMS.register("rainbow_sheep_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.RAINBOW_SHEEP, 0xFFFFFF, 0xFFFFFF, new Item.Properties()));

    public static PortItem.PortProperties unbreakable() {
        return new PortItem.PortProperties().unbreakable();
    }

    public static Consumer<PortItemAttributeModifiers.PortBuilder> attributes(double blockInteractionRange, double attackKnockback) {
        return builder -> {
            if (blockInteractionRange != 0)
                builder.add(PortAttributesExtension.blockInteractionRange(), new PortAttributeModifier(BASE_BLOCK_INTERACTION_RANGE_ID, blockInteractionRange, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            if (attackKnockback != 0)
                builder.add(Attributes.ATTACK_KNOCKBACK, new PortAttributeModifier(BASE_ATTACK_KNOCKBACK_ID, attackKnockback, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
            builder.add(LibAttributes.getCriticalChance(), new PortAttributeModifier(BASE_CRITICAL_CHANCE_ID, 0.04, PortAttributeModifier.PortOperation.ADD_VALUE), PortEquipmentSlotGroup.MAINHAND);
        };
    }

//    public static PortItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed, Consumer<PortItemAttributeModifiers.PortBuilder> consumer) {
//        PortItemAttributeModifiers.PortBuilder builder = PortItemAttributeModifiers.builder();
//        consumer.accept(builder);
//        return builder.add(
//                LibAttributes.getAttackDamage(),
//                new PortAttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), PortAttributeModifier.PortOperation.ADD_VALUE),
//                PortEquipmentSlotGroup.MAINHAND
//        ).add(
//                Attributes.ATTACK_SPEED,
//                new PortAttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, PortAttributeModifier.PortOperation.ADD_VALUE),
//                PortEquipmentSlotGroup.MAINHAND
//        ).build();
//    }

    public static float getAttackSpeed(float rawSpeed) {
        return rawSpeed - 4;
    }

    public static float getAttackDamage(Tier tier, float rawDamage) {
        return rawDamage - tier.getAttackDamageBonus() - 1;
    }

    public static Multimap<Attribute, AttributeModifier> mergeModifiers(Multimap<Attribute, AttributeModifier> original, Consumer<PortItemAttributeModifiers.PortBuilder> consumer) {
        PortItemAttributeModifiers.PortBuilder builder = PortItemAttributeModifiers.builder();
        consumer.accept(builder);
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .putAll(original)
                .putAll(builder.build().getAttributeModifiers(EquipmentSlot.MAINHAND))
                .build();
    }
}

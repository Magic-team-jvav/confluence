package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.block.EmptyPickupLiquidBlock;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.*;
import org.confluence.mod.common.block.food.BoulderBreadBlock;
import org.confluence.mod.common.block.food.GreenDumplingBlock;
import org.confluence.mod.common.block.functional.enemybanner.AbstractEnemyBannerBlock;
import org.confluence.mod.common.block.functional.enemybanner.EnemyBannerBlock;
import org.confluence.mod.common.block.functional.enemybanner.WallEnemyBannerBlock;
import org.confluence.mod.common.block.natural.CoinPileBlock;
import org.confluence.mod.common.block.natural.CursedFlameBlock;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.STONE;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    public static final Object2BooleanMap<DeferredBlock<TombstoneBlock>> TOMBSTONES = new Object2BooleanOpenHashMap<>();

    public static final DeferredBlock<Block> ANDESITE_CASING = registerWithItem("andesite_casing", "安山机壳", () -> new Block(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<CoinPileBlock> COPPER_COIN = registerWithoutItem("copper_coin", "铜币", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> SILVER_COIN = registerWithoutItem("silver_coin", "银币", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> GOLD_COIN = registerWithoutItem("gold_coin", "金币", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> PLATINUM_COIN = registerWithoutItem("platinum_coin", "铂金币", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> EMERALD_COIN = registerWithoutItem("emerald_coin", "绿宝石币", CoinPileBlock::new);

    // 流体
    public static final DeferredBlock<LiquidBlock> HONEY = registerWithoutItem("honey", "蜂蜜", () -> new LiquidBlock(ModFluids.HONEY.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<VoidBlock> VOID = registerWithoutItem("void", "虚空", () -> new VoidBlock(ModFluids.VOID.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<LiquidBlock> SHIMMER = registerWithoutItem("shimmer", "微光", () -> new EmptyPickupLiquidBlock(ModFluids.SHIMMER.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<AetheriumCauldronBlock> AETHERIUM_CAULDRON = registerWithItem("aetherium_cauldron", "装有微光的炼药锅", () -> new AetheriumCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));
    public static final DeferredBlock<HoneyCauldronBlock> HONEY_CAULDRON = registerWithItem("honey_cauldron", "装有蜂蜜的炼药锅", () -> new HoneyCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));
    public static final Supplier<BlockEntityType<VoidBlock.VoidBlockEntity>> VOID_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("void_block_entity", () ->
                    BlockEntityType.Builder.of(VoidBlock.VoidBlockEntity::new, VOID.get()).build(DSL.remainderType())
            );
    // 草药

    public static final DeferredBlock<BaseRopeBlock> ROPE = registerWithItem("rope", "绳子", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> VINE_ROPE = registerWithItem("vine_rope", "藤蔓绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> SILK_ROPE = registerWithItem("silk_rope", "丝绸绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> WEB_ROPE = registerWithItem("web_rope", "蛛丝绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> PINE_NEEDLE_HANDMADE_ROPE_SET = registerWithItem("pine_needle_handmade_rope_set", "松针手工绳套", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);

    public static final DeferredBlock<Block> FAILED_SKULL_BLOCK = registerWithoutItemWithEnglishName("failed_skull_block", "失败品的头颅", "Failed Skull", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FAILED_SKULL_WALL_BLOCK = registerWithoutItem("failed_skull_wall_block", "残缺头骨墙", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<TombstoneBlock> TOMBSTONE = registerTombstone("tombstone", "墓石", false);
    public static final DeferredBlock<TombstoneBlock> GRAVE_MARKER = registerTombstone("grave_marker", "墓石碑", false);
    public static final DeferredBlock<TombstoneBlock> CROSS_GRAVE_MARKER = registerTombstone("cross_grave_marker", "十字墓石碑", false);
    public static final DeferredBlock<TombstoneBlock> HEADSTONE = registerTombstone("headstone", "碑石", false);
    public static final DeferredBlock<TombstoneBlock> GRAVESTONE = registerTombstone("gravestone", "墓碑", false);
    public static final DeferredBlock<TombstoneBlock> OBELISK = registerTombstone("obelisk", "方尖碑", false);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_TOMBSTONE = registerTombstone("golden_tombstone", "金墓石", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_GRAVE_MARKER = registerTombstone("golden_grave_marker", "金墓石碑", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_CROSS_GRAVE_MARKER = registerTombstone("golden_cross_grave_marker", "金十字墓石碑", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_HEADSTONE = registerTombstone("golden_headstone", "金碑石", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_GRAVESTONE = registerTombstone("golden_gravestone", "金墓碑", true);
    public static final Supplier<BlockEntityType<TombstoneBlock.BEntity>> TOMBSTONE_ENTITY = BLOCK_ENTITIES.register("tombstone_entity", () -> BlockEntityType.Builder.of(TombstoneBlock.BEntity::new, TOMBSTONES.keySet().stream().map(DeferredHolder::get).toArray(TombstoneBlock[]::new)).build(DSL.remainderType()));

    public static final DeferredBlock<GreenDumplingBlock> GREEN_DUMPLING_BLOCK = registerWithoutItemWithEnglishName("green_dumpling_block", "青团", "Green Dumpling", GreenDumplingBlock::new);
    public static final DeferredBlock<BoulderBreadBlock> BOULDER_BREAD_BLOCK = registerWithoutItemWithEnglishName("boulder_bread_block", "巨石面包", "Boulder Bread", BoulderBreadBlock::new);

    public static final DeferredBlock<CursedFlameBlock> CURSED_FLAME_BLOCK = registerWithoutItemWithEnglishName("cursed_flame_block", "诅咒焰", "Cursed Flame", () -> new CursedFlameBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollission().instabreak().lightLevel(l -> 7).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));

    // test block 要测试直接复制下面这一行改名
    public static final DeferredBlock<Block> TEST_BLOCK = registerWithItem("test_block", "测试方块", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<EnemyBannerBlock> ENEMY_BANNER = registerBlock("enemy_banner", "敌怪旗", EnemyBannerBlock::new);
    public static final DeferredBlock<WallEnemyBannerBlock> WALL_ENEMY_BANNER = registerBlock("wall_enemy_banner", "敌怪旗", WallEnemyBannerBlock::new);
    public static final Supplier<BlockEntityType<AbstractEnemyBannerBlock.BEntity>> ENEMY_BANNER_ENTITY = BLOCK_ENTITIES.register("enemy_banner_entity", () -> BlockEntityType.Builder.of(AbstractEnemyBannerBlock.BEntity::new, ENEMY_BANNER.get(), WALL_ENEMY_BANNER.get()).build(DSL.remainderType()));

    private static DeferredBlock<TombstoneBlock> registerTombstone(String id, boolean isGolden) {
        DeferredBlock<TombstoneBlock> tombstone = registerWithItem(id, TombstoneBlock::new);
        TOMBSTONES.put(tombstone, isGolden);
        return tombstone;
    }

    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        return registerWithItem(id, block, new Item.Properties());
    }

    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> item) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> item.apply(object.get()));
        return object;
    }

    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Item.Properties properties) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object, properties);
        return object;
    }

    public static <B extends Block> DeferredBlock<B> registerWithoutItem(String id, Supplier<B> block) {
        return BLOCKS.register(id, block);
    }

    /// 基于黑曜石的爆炸抗性，汇流来世的方块设置爆炸抗性时，应当使用这个方法
    ///
    /// 对于泰拉爆炸，小于黑曜石爆炸抗性的方块都会被炸掉
    ///
    /// @param delta 偏差值
    /// @return 爆炸抗性
    @SuppressWarnings("deprecation")
    public static float getObsidianBasedExplosionResistance(float delta) {
        return Blocks.OBSIDIAN.getExplosionResistance() + delta;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        ChestBlocks.BLOCKS.register(eventBus);
        CrateBlocks.BLOCKS.register(eventBus);
        DecorativeBlocks.BLOCKS.register(eventBus);
        FunctionalBlocks.BLOCKS.register(eventBus);
        NatureBlocks.BLOCKS.register(eventBus);
        NatureBlocks.BLOCK_ENTITIES.register(eventBus);
        OreBlocks.BLOCKS.register(eventBus);
        PotBlocks.BLOCKS.register(eventBus);
        PylonBlocks.BLOCKS.register(eventBus);
        StatueBlocks.BLOCKS.register(eventBus);
        TorchBlocks.BLOCKS.register(eventBus);
    }

    private static DeferredBlock<TombstoneBlock> registerTombstone(String registryId, String chineseName, boolean isGolden) {
        DeferredBlock<TombstoneBlock> entry = registerTombstone(registryId, isGolden);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block) {
        DeferredBlock<B> entry = registerWithItem(registryId, block, new Item.Properties());
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Function<B, BlockItem> item) {
        DeferredBlock<B> entry = registerWithItem(registryId, block, item);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Item.Properties properties) {
        DeferredBlock<B> entry = registerWithItem(registryId, block, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithoutItem(String registryId, String chineseName, Supplier<B> block) {
        DeferredBlock<B> entry = BLOCKS.register(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String registryId, String chineseName, Supplier<T> factory) {
        DeferredBlock<T> entry = BLOCKS.register(registryId, factory);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> DeferredBlock<B> registerWithoutItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<B> block) {
        DeferredBlock<B> entry = BLOCKS.register(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}

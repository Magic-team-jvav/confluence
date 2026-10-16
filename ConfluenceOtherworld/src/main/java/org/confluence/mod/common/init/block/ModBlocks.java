package org.confluence.mod.common.init.block;

import com.google.common.base.Supplier;
import com.mojang.datafixers.DSL;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.registries.Registries;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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
import org.confluence.mod.common.item.common.CursedFlameItem;
import org.mesdag.portlib.diff.Diff;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Function;

import static net.minecraft.world.level.block.Blocks.STONE;
import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public final class ModBlocks {
    public static void init(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        NatureBlocks.BLOCK_ENTITIES.register(eventBus);
        ChestBlocks.init();
        CrateBlocks.init();
        DecorativeBlocks.init();
        FunctionalBlocks.init();
        NatureBlocks.init();
        OreBlocks.init();
        PotBlocks.init();
        StatueBlocks.init();
        TorchBlocks.init();
    }

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    public static final Object2BooleanMap<PortDeferredBlock<TombstoneBlock>> TOMBSTONES = new Object2BooleanOpenHashMap<>();

    public static final PortDeferredBlock<Block> ANDESITE_CASING = registerWithItem("andesite_casing", "安山机壳", () -> new Block(BlockBehaviour.Properties.of()));

    public static final PortDeferredBlock<CoinPileBlock> COPPER_COIN = registerWithoutItem("copper_coin", "铜币", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> SILVER_COIN = registerWithoutItem("silver_coin", "银币", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> GOLD_COIN = registerWithoutItem("gold_coin", "金币", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> PLATINUM_COIN = registerWithoutItem("platinum_coin", "铂金币", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> EMERALD_COIN = registerWithoutItem("emerald_coin", "绿宝石币", CoinPileBlock::new);

    // 流体
    public static final PortDeferredBlock<LiquidBlock> HONEY = registerWithoutItem("honey", "蜂蜜", () -> new LiquidBlock(ModFluids.HONEY.fluid()::get, BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_YELLOW)));
    public static final PortDeferredBlock<VoidBlock> VOID = registerWithoutItem("void", "虚空", () -> new VoidBlock(ModFluids.VOID.fluid()::get, BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_BLACK)));
    public static final RegistryObject<BlockEntityType<VoidBlock.VoidBlockEntity>> VOID_ENTITY = BLOCK_ENTITIES.register("void_entity", () -> BlockEntityType.Builder.of(VoidBlock.VoidBlockEntity::new, VOID.get()).build(DSL.remainderType()));
    public static final PortDeferredBlock<LiquidBlock> SHIMMER = registerWithoutItem("shimmer", "微光", () -> new EmptyPickupLiquidBlock(ModFluids.SHIMMER.fluid()::get, BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final PortDeferredBlock<AetheriumCauldronBlock> AETHERIUM_CAULDRON = registerWithItem("aetherium_cauldron", "装有微光的炼药锅", () -> new AetheriumCauldronBlock(BlockBehaviour.Properties.copy(Blocks.WATER_CAULDRON)));
    public static final PortDeferredBlock<HoneyCauldronBlock> HONEY_CAULDRON = registerWithItem("honey_cauldron", "装有蜂蜜的炼药锅", () -> new HoneyCauldronBlock(BlockBehaviour.Properties.copy(Blocks.WATER_CAULDRON)));

    public static final PortDeferredBlock<BaseRopeBlock> ROPE = registerWithItem("rope", "绳子", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> VINE_ROPE = registerWithItem("vine_rope", "藤蔓绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> SILK_ROPE = registerWithItem("silk_rope", "丝绸绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> WEB_ROPE = registerWithItem("web_rope", "蛛丝绳", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> PINE_NEEDLE_HANDMADE_ROPE_SET = registerWithItem("pine_needle_handmade_rope_set", "松针手工绳套", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);

    public static final PortDeferredBlock<Block> FAILED_SKULL = registerWithoutItem("failed_skull", "失败品的头颅", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final PortDeferredBlock<Block> FAILED_SKULL_WALL = registerWithoutItem("failed_skull_wall", "失败品头颅墙", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final PortDeferredBlock<TombstoneBlock> TOMBSTONE = registerTombstone("tombstone", "墓石", false);
    public static final PortDeferredBlock<TombstoneBlock> GRAVE_MARKER = registerTombstone("grave_marker", "墓石碑", false);
    public static final PortDeferredBlock<TombstoneBlock> CROSS_GRAVE_MARKER = registerTombstone("cross_grave_marker", "十字墓石碑", false);
    public static final PortDeferredBlock<TombstoneBlock> HEADSTONE = registerTombstone("headstone", "碑石", false);
    public static final PortDeferredBlock<TombstoneBlock> GRAVESTONE = registerTombstone("gravestone", "墓碑", false);
    public static final PortDeferredBlock<TombstoneBlock> OBELISK = registerTombstone("obelisk", "方尖碑", false);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_TOMBSTONE = registerTombstone("golden_tombstone", "金墓石", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_GRAVE_MARKER = registerTombstone("golden_grave_marker", "金墓石碑", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_CROSS_GRAVE_MARKER = registerTombstone("golden_cross_grave_marker", "金十字墓石碑", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_HEADSTONE = registerTombstone("golden_headstone", "金碑石", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_GRAVESTONE = registerTombstone("golden_gravestone", "金墓碑", true);
    public static final RegistryObject<BlockEntityType<TombstoneBlock.BEntity>> TOMBSTONE_ENTITY = BLOCK_ENTITIES.register("tombstone_entity", () -> BlockEntityType.Builder.of(TombstoneBlock.BEntity::new, TOMBSTONES.keySet().stream().map(PortDeferredBlock::get).toArray(TombstoneBlock[]::new)).build(DSL.remainderType()));

    public static final PortDeferredBlock<GreenDumplingBlock> GREEN_DUMPLING = registerWithoutItem("green_dumpling", "青团", GreenDumplingBlock::new);
    public static final PortDeferredBlock<BoulderBreadBlock> BOULDER_BREAD = registerWithoutItem("boulder_bread", "巨石面包", BoulderBreadBlock::new);

    public static final PortDeferredBlock<CursedFlameBlock> CURSED_FLAME = registerWithItem("cursed_flame", "诅咒焰", () -> new CursedFlameBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollission().instabreak().lightLevel(l -> 7).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)), CursedFlameItem::new);

    // test block 要测试直接复制下面这一行改名
    public static final PortDeferredBlock<Block> TEST = registerWithItem("test", "测试方块", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.COLOR_BLUE)));
    public static final PortDeferredBlock<EnemyBannerBlock> ENEMY_BANNER = registerBlock("enemy_banner", "敌怪旗", EnemyBannerBlock::new);
    public static final PortDeferredBlock<WallEnemyBannerBlock> WALL_ENEMY_BANNER = registerBlock("wall_enemy_banner", "敌怪旗", WallEnemyBannerBlock::new);
    public static final RegistryObject<BlockEntityType<AbstractEnemyBannerBlock.BEntity>> ENEMY_BANNER_ENTITY = BLOCK_ENTITIES.register("enemy_banner_entity", () -> BlockEntityType.Builder.of(AbstractEnemyBannerBlock.BEntity::new, ENEMY_BANNER.get(), WALL_ENEMY_BANNER.get()).build(DSL.remainderType()));

    private static PortDeferredBlock<TombstoneBlock> registerTombstone(String id, boolean isGolden) {
        PortDeferredBlock<TombstoneBlock> tombstone = registerWithItem(id, TombstoneBlock::new);
        TOMBSTONES.put(tombstone, isGolden);
        return tombstone;
    }

    public static <B extends Block> PortDeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        return registerWithItem(id, block, new Item.Properties());
    }

    public static <B extends Block> PortDeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> item) {
        PortDeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> item.apply(object.get()));
        return object;
    }

    public static <B extends Block> PortDeferredBlock<B> registerWithItem(String id, Supplier<B> block, Item.Properties properties) {
        PortDeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object, properties);
        return object;
    }

    public static <B extends Block> PortDeferredBlock<B> registerWithoutItem(String id, Supplier<B> block) {
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

    @Diff
    public static BlockBehaviour.Properties tuffProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.TUFF)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    @Diff
    public static BlockBehaviour.Properties tuffBricksProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.TUFF_BRICKS)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    private static PortDeferredBlock<TombstoneBlock> registerTombstone(String registryId, String chineseName, boolean isGolden) {
        PortDeferredBlock<TombstoneBlock> entry = registerTombstone(registryId, isGolden);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block) {
        PortDeferredBlock<B> entry = registerWithItem(registryId, block, new Item.Properties());
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Function<B, BlockItem> item) {
        PortDeferredBlock<B> entry = registerWithItem(registryId, block, item);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithItem(String registryId, String chineseName, Supplier<B> block, Item.Properties properties) {
        PortDeferredBlock<B> entry = registerWithItem(registryId, block, properties);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <B extends Block> PortDeferredBlock<B> registerWithoutItem(String registryId, String chineseName, Supplier<B> block) {
        PortDeferredBlock<B> entry = BLOCKS.register(registryId, block);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Block> PortDeferredBlock<T> registerBlock(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredBlock<T> entry = BLOCKS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }
}

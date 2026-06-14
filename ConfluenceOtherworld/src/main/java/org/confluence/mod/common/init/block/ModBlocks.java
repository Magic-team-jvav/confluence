package org.confluence.mod.common.init.block;

import PortLib.extensions.net.minecraft.world.level.block.SoundType.PortSoundTypeExtension;
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
import org.confluence.mod.common.block.natural.herbs.*;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.registries.PortBlockRegistration;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.function.Function;

import static net.minecraft.world.level.block.Blocks.STONE;

public final class ModBlocks {
    public static void init(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        ChestBlocks.init();
        CrateBlocks.init();
        DecorativeBlocks.init();
        FunctionalBlocks.init();
        NatureBlocks.init();
        OreBlocks.init();
        PotBlocks.init();
        StatueBlocks.init();
    }

    public static final PortBlockRegistration BLOCKS = PortRegisterHandler.block(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    public static final Object2BooleanMap<PortDeferredBlock<TombstoneBlock>> TOMBSTONES = new Object2BooleanOpenHashMap<>();

    public static final PortDeferredBlock<Block> ANDESITE_CASING = registerWithItem("andesite_casing", () -> new Block(BlockBehaviour.Properties.of()));

    public static final PortDeferredBlock<CoinPileBlock> COPPER_COIN = registerWithoutItem("copper_coin", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> SILVER_COIN = registerWithoutItem("silver_coin", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> GOLD_COIN = registerWithoutItem("gold_coin", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> PLATINUM_COIN = registerWithoutItem("platinum_coin", CoinPileBlock::new);
    public static final PortDeferredBlock<CoinPileBlock> EMERALD_COIN = registerWithoutItem("emerald_coin", CoinPileBlock::new);

    // 流体
    public static final PortDeferredBlock<LiquidBlock> HONEY = registerWithoutItem("honey", () -> new LiquidBlock(ModFluids.HONEY.fluid().get(), BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_YELLOW)));
    public static final PortDeferredBlock<VoidBlock> VOID = registerWithoutItem("void", () -> new VoidBlock(ModFluids.VOID.fluid().get(), BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_BLACK)));
    public static final PortDeferredBlock<LiquidBlock> SHIMMER = registerWithoutItem("shimmer", () -> new EmptyPickupLiquidBlock(ModFluids.SHIMMER.fluid()::get, BlockBehaviour.Properties.copy(Blocks.WATER).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final PortDeferredBlock<AetheriumCauldronBlock> AETHERIUM_CAULDRON = registerWithItem("aetherium_cauldron", () -> new AetheriumCauldronBlock(BlockBehaviour.Properties.copy(Blocks.WATER_CAULDRON)));
    public static final PortDeferredBlock<HoneyCauldronBlock> HONEY_CAULDRON = registerWithItem("honey_cauldron", () -> new HoneyCauldronBlock(BlockBehaviour.Properties.copy(Blocks.WATER_CAULDRON)));
    public static final RegistryObject<BlockEntityType<VoidBlock.VoidBlockEntity>> VOID_BLOCK_ENTITY = BLOCK_ENTITIES.register("void_block_entity", () -> BlockEntityType.Builder.of(VoidBlock.VoidBlockEntity::new, VOID.get()).build(DSL.remainderType()));
    // 草药
    public static final PortDeferredBlock<BaseHerbBlock> WATERLEAF = registerWithoutItem("waterleaf", Waterleaf::new); // 幌菊
    public static final PortDeferredBlock<Fireblossom> FIREBLOSSOM = registerWithoutItem("fireblossom", Fireblossom::new); // 火焰花
    public static final PortDeferredBlock<Moonglow> MOONGLOW = registerWithoutItem("moonglow", Moonglow::new); // 月光草
    public static final PortDeferredBlock<BaseHerbBlock> BLINKROOT = registerWithoutItem("blinkroot", Blinkroot::new); // 闪耀根
    public static final PortDeferredBlock<BaseHerbBlock> SHIVERTHORN = registerWithoutItem("shiverthorn", ShiveringThorn::new); // 寒颤棘
    public static final PortDeferredBlock<BaseHerbBlock> DAYBLOOM = registerWithoutItem("daybloom", Daybloom::new); // 太阳花
    public static final PortDeferredBlock<DeathWeed> DEATHWEED = registerWithoutItem("deathweed", DeathWeed::new); // 死亡草
    public static final RegistryObject<BlockEntityType<BaseHerbBlock.BEntity>> HERBS_ENTITY = BLOCK_ENTITIES.register("herbs_entity", () -> BlockEntityType.Builder.of(BaseHerbBlock.BEntity::new,
            WATERLEAF.get(), FIREBLOSSOM.get(), MOONGLOW.get(), BLINKROOT.get(), SHIVERTHORN.get(), DAYBLOOM.get(), DEATHWEED.get()).build(DSL.remainderType()));

    public static final PortDeferredBlock<PooBlock> POO = registerWithItem("poo", () -> new PooBlock(BlockBehaviour.Properties.copy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    public static final PortDeferredBlock<BaseRopeBlock> ROPE = registerWithItem("rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> VINE_ROPE = registerWithItem("vine_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> SILK_ROPE = registerWithItem("silk_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> WEB_ROPE = registerWithItem("web_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final PortDeferredBlock<BaseRopeBlock> PINE_NEEDLE_HANDMADE_ROPE_SET = registerWithItem("pine_needle_handmade_rope_set", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);

    public static final PortDeferredBlock<Block> FAILED_SKULL_BLOCK = registerWithoutItem("failed_skull_block", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final PortDeferredBlock<Block> FAILED_SKULL_WALL_BLOCK = registerWithoutItem("failed_skull_wall_block", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final PortDeferredBlock<TombstoneBlock> TOMBSTONE = registerTombstone("tombstone", false);
    public static final PortDeferredBlock<TombstoneBlock> GRAVE_MARKER = registerTombstone("grave_marker", false);
    public static final PortDeferredBlock<TombstoneBlock> CROSS_GRAVE_MARKER = registerTombstone("cross_grave_marker", false);
    public static final PortDeferredBlock<TombstoneBlock> HEADSTONE = registerTombstone("headstone", false);
    public static final PortDeferredBlock<TombstoneBlock> GRAVESTONE = registerTombstone("gravestone", false);
    public static final PortDeferredBlock<TombstoneBlock> OBELISK = registerTombstone("obelisk", false);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_TOMBSTONE = registerTombstone("golden_tombstone", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_GRAVE_MARKER = registerTombstone("golden_grave_marker", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_CROSS_GRAVE_MARKER = registerTombstone("golden_cross_grave_marker", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_HEADSTONE = registerTombstone("golden_headstone", true);
    public static final PortDeferredBlock<TombstoneBlock> GOLDEN_GRAVESTONE = registerTombstone("golden_gravestone", true);
    public static final RegistryObject<BlockEntityType<TombstoneBlock.BEntity>> TOMBSTONE_ENTITY = BLOCK_ENTITIES.register("tombstone_entity", () -> BlockEntityType.Builder.of(TombstoneBlock.BEntity::new, TOMBSTONES.keySet().stream().map(PortDeferredBlock::get).toArray(TombstoneBlock[]::new)).build(DSL.remainderType()));

    public static final PortDeferredBlock<GreenDumplingBlock> GREEN_DUMPLING_BLOCK = registerWithoutItem("green_dumpling_block", GreenDumplingBlock::new);
    public static final PortDeferredBlock<BoulderBreadBlock> BOULDER_BREAD_BLOCK = registerWithoutItem("boulder_bread_block", BoulderBreadBlock::new);

    public static final PortDeferredBlock<CursedFlameBlock> CURSED_FLAME_BLOCK = registerWithoutItem("cursed_flame_block", () -> new CursedFlameBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollission().instabreak().lightLevel(l -> 7).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));

    // test block 要测试直接复制下面这一行改名
    public static final PortDeferredBlock<Block> TEST_BLOCK = registerWithItem("test_block", () -> new Block(BlockBehaviour.Properties.copy(STONE).mapColor(MapColor.COLOR_BLUE)));
    public static final PortDeferredBlock<EnemyBannerBlock> ENEMY_BANNER = BLOCKS.register("enemy_banner", EnemyBannerBlock::new);
    public static final PortDeferredBlock<WallEnemyBannerBlock> WALL_ENEMY_BANNER = BLOCKS.register("wall_enemy_banner", WallEnemyBannerBlock::new);
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

    public static BlockBehaviour.Properties tuffProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.TUFF)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }

    public static BlockBehaviour.Properties tuffBricksProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.TERRACOTTA_GRAY)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(PortSoundTypeExtension.tuffBricks())
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F);
    }
}

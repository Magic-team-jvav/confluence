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
import org.confluence.mod.common.block.natural.herbs.*;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.Blocks.STONE;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);
    public static final Object2BooleanMap<DeferredBlock<TombstoneBlock>> TOMBSTONES = new Object2BooleanOpenHashMap<>();

    public static final DeferredBlock<Block> ANDESITE_CASING = registerWithItem("andesite_casing", () -> new Block(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<CoinPileBlock> COPPER_COIN = registerWithoutItem("copper_coin", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> SILVER_COIN = registerWithoutItem("silver_coin", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> GOLD_COIN = registerWithoutItem("gold_coin", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> PLATINUM_COIN = registerWithoutItem("platinum_coin", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> EMERALD_COIN = registerWithoutItem("emerald_coin", CoinPileBlock::new);

    // 流体
    public static final DeferredBlock<LiquidBlock> HONEY = registerWithoutItem("honey", () -> new LiquidBlock(ModFluids.HONEY.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<VoidBlock> VOID = registerWithoutItem("void", () -> new VoidBlock(ModFluids.VOID.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_BLACK)));
    public static final DeferredBlock<LiquidBlock> SHIMMER = registerWithoutItem("shimmer", () -> new EmptyPickupLiquidBlock(ModFluids.SHIMMER.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final DeferredBlock<AetheriumCauldronBlock> AETHERIUM_CAULDRON = registerWithItem("aetherium_cauldron", () -> new AetheriumCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));
    public static final DeferredBlock<HoneyCauldronBlock> HONEY_CAULDRON = registerWithItem("honey_cauldron", () -> new HoneyCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));
    public static final Supplier<BlockEntityType<VoidBlock.VoidBlockEntity>> VOID_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("void_block_entity", () ->
                    BlockEntityType.Builder.of(VoidBlock.VoidBlockEntity::new, VOID.get()).build(DSL.remainderType())
            );
    // 草药
    public static final DeferredBlock<BaseHerbBlock> WATERLEAF = registerWithoutItem("waterleaf", Waterleaf::new); // 幌菊
    public static final DeferredBlock<Fireblossom> FIREBLOSSOM = registerWithoutItem("fireblossom", Fireblossom::new); // 火焰花
    public static final DeferredBlock<Moonglow> MOONGLOW = registerWithoutItem("moonglow", Moonglow::new); // 月光草
    public static final DeferredBlock<BaseHerbBlock> BLINKROOT = registerWithoutItem("blinkroot", Blinkroot::new); // 闪耀根
    public static final DeferredBlock<BaseHerbBlock> SHIVERTHORN = registerWithoutItem("shiverthorn", ShiveringThorn::new); // 寒颤棘
    public static final DeferredBlock<BaseHerbBlock> DAYBLOOM = registerWithoutItem("daybloom", Daybloom::new); // 太阳花
    public static final DeferredBlock<DeathWeed> DEATHWEED = registerWithoutItem("deathweed", DeathWeed::new); // 死亡草
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaseHerbBlock.BEntity>> HERBS_ENTITY = BLOCK_ENTITIES.register("herbs_entity", () -> BlockEntityType.Builder.of(BaseHerbBlock.BEntity::new,
            WATERLEAF.get(), FIREBLOSSOM.get(), MOONGLOW.get(), BLINKROOT.get(), SHIVERTHORN.get(), DAYBLOOM.get(), DEATHWEED.get()).build(DSL.remainderType()));

    public static final DeferredBlock<PooBlock> POO = registerWithItem("poo", () -> new PooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD).mapColor(MapColor.COLOR_BROWN)));

    public static final DeferredBlock<BaseRopeBlock> ROPE = registerWithItem("rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BROWN).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> VINE_ROPE = registerWithItem("vine_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.GREEN).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> SILK_ROPE = registerWithItem("silk_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> WEB_ROPE = registerWithItem("web_rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.WOOL).noCollission().instabreak()), BaseRopeBlock.BItem::new);
    public static final DeferredBlock<BaseRopeBlock> PINE_NEEDLE_HANDMADE_ROPE_SET = registerWithItem("pine_needle_handmade_rope_set", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.BLUE).sound(SoundType.GRASS).noCollission().instabreak()), BaseRopeBlock.BItem::new);

    public static final DeferredBlock<Block> FAILED_SKULL_BLOCK = registerWithoutItem("failed_skull_block", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FAILED_SKULL_WALL_BLOCK = registerWithoutItem("failed_skull_wall_block", () -> new BaseSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.CREEPER).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<TombstoneBlock> TOMBSTONE = registerTombstone("tombstone", false);
    public static final DeferredBlock<TombstoneBlock> GRAVE_MARKER = registerTombstone("grave_marker", false);
    public static final DeferredBlock<TombstoneBlock> CROSS_GRAVE_MARKER = registerTombstone("cross_grave_marker", false);
    public static final DeferredBlock<TombstoneBlock> HEADSTONE = registerTombstone("headstone", false);
    public static final DeferredBlock<TombstoneBlock> GRAVESTONE = registerTombstone("gravestone", false);
    public static final DeferredBlock<TombstoneBlock> OBELISK = registerTombstone("obelisk", false);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_TOMBSTONE = registerTombstone("golden_tombstone", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_GRAVE_MARKER = registerTombstone("golden_grave_marker", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_CROSS_GRAVE_MARKER = registerTombstone("golden_cross_grave_marker", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_HEADSTONE = registerTombstone("golden_headstone", true);
    public static final DeferredBlock<TombstoneBlock> GOLDEN_GRAVESTONE = registerTombstone("golden_gravestone", true);
    public static final Supplier<BlockEntityType<TombstoneBlock.BEntity>> TOMBSTONE_ENTITY = BLOCK_ENTITIES.register("tombstone_entity", () -> BlockEntityType.Builder.of(TombstoneBlock.BEntity::new, TOMBSTONES.keySet().stream().map(DeferredHolder::get).toArray(TombstoneBlock[]::new)).build(DSL.remainderType()));

    public static final DeferredBlock<GreenDumplingBlock> GREEN_DUMPLING_BLOCK = registerWithoutItem("green_dumpling_block", GreenDumplingBlock::new);
    public static final DeferredBlock<BoulderBreadBlock> BOULDER_BREAD_BLOCK = registerWithoutItem("boulder_bread_block", BoulderBreadBlock::new);

    public static final DeferredBlock<CursedFlameBlock> CURSED_FLAME_BLOCK = registerWithoutItem("cursed_flame_block", () -> new CursedFlameBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).replaceable().noCollission().instabreak().lightLevel(l -> 7).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));

    // test block 要测试直接复制下面这一行改名
    public static final DeferredBlock<Block> TEST_BLOCK = registerWithItem("test_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(STONE).mapColor(MapColor.COLOR_BLUE)));
    public static final DeferredBlock<EnemyBannerBlock> ENEMY_BANNER = BLOCKS.register("enemy_banner", EnemyBannerBlock::new);
    public static final DeferredBlock<WallEnemyBannerBlock> WALL_ENEMY_BANNER = BLOCKS.register("wall_enemy_banner", WallEnemyBannerBlock::new);
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
        OreBlocks.BLOCKS.register(eventBus);
        PotBlocks.BLOCKS.register(eventBus);
        StatueBlocks.BLOCKS.register(eventBus);
    }
}

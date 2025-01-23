package org.confluence.mod.common.init.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.AetheriumCauldronBlock;
import org.confluence.mod.common.block.common.BaseRopeBlock;
import org.confluence.mod.common.block.common.HoneyCauldronBlock;
import org.confluence.mod.common.block.common.PooBlock;
import org.confluence.mod.common.block.natural.*;
import org.confluence.mod.common.block.natural.herbs.*;
import org.confluence.mod.common.fluid.EmptyPickupLiquidBlock;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Confluence.MODID);

    public static final DeferredBlock<CoinPileBlock> COPPER_COIN_PILE = registerWithoutItem("copper_coin_pile", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> SILVER_COIN_PILE = registerWithoutItem("silver_coin_pile", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> GOLDEN_COIN_PILE = registerWithoutItem("golden_coin_pile", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> PLATINUM_COIN_PILE = registerWithoutItem("platinum_coin_pile", CoinPileBlock::new);
    public static final DeferredBlock<CoinPileBlock> EMERALD_COIN_PILE = registerWithoutItem("emerald_coin_pile", CoinPileBlock::new);

    public static final Supplier<SwordInStoneBlock> SWORD_IN_STONE = registerWithItem("sword_in_stone", SwordInStoneBlock::new);
    public static final Supplier<CrackedBrickBlock> CRACKED_BLUE_BRICK = registerWithItem("cracked_blue_block", CrackedBrickBlock::new);
    public static final Supplier<CrackedBrickBlock> CRACKED_GREEN_BRICK = registerWithItem("cracked_green_block", CrackedBrickBlock::new);
    public static final Supplier<CrackedBrickBlock> CRACKED_PINK_BRICK = registerWithItem("cracked_pink_block", CrackedBrickBlock::new);
    public static final Supplier<CrispyHoneyBlock> CRISPY_HONEY_BLOCK = registerWithItem("crispy_honey_block", CrispyHoneyBlock::new);

    // 流体
    public static final Supplier<LiquidBlock> HONEY = registerWithoutItem("honey", () -> new LiquidBlock(ModFluids.HONEY.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_YELLOW)));
    public static final Supplier<LiquidBlock> SHIMMER = registerWithoutItem("shimmer", () -> new EmptyPickupLiquidBlock(ModFluids.SHIMMER.fluid().get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).mapColor(MapColor.COLOR_PINK).lightLevel(blockState -> 10)));
    public static final Supplier<AetheriumCauldronBlock> AETHERIUM_CAULDRON = registerWithItem("aetherium_cauldron", () -> new AetheriumCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));
    public static final Supplier<HoneyCauldronBlock> HONEY_CAULDRON = registerWithItem("honey_cauldron", () -> new HoneyCauldronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON)));

    // 草药
    public static final DeferredBlock<BaseHerbBlock> WATERLEAF = registerWithoutItem("waterleaf", Waterleaf::new); // 幌菊
    public static final DeferredBlock<Fireblossom> FIREBLOSSOM = registerWithoutItem("fireblossom", Fireblossom::new); // 火焰花
    public static final DeferredBlock<Moonglow> MOONGLOW = registerWithoutItem("moonglow", Moonglow::new); // 月光草
    public static final DeferredBlock<BaseHerbBlock> BLINKROOT = registerWithoutItem("blinkroot", Blinkroot::new); // 闪耀根
    public static final DeferredBlock<BaseHerbBlock> SHIVERTHORN = registerWithoutItem("shiverthorn", ShiveringThorn::new); // 寒颤棘
    public static final DeferredBlock<BaseHerbBlock> DAYBLOOM = registerWithoutItem("daybloom", Daybloom::new); // 太阳花
    public static final DeferredBlock<DeathWeed> DEATHWEED = registerWithoutItem("deathweed", DeathWeed::new); // 死亡草
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaseHerbBlock.Entity>> HERBS_ENTITY = BLOCK_ENTITIES.register("herbs_entity", () -> BlockEntityType.Builder.of(BaseHerbBlock.Entity::new,
            WATERLEAF.get(), FIREBLOSSOM.get(), MOONGLOW.get(), BLINKROOT.get(), SHIVERTHORN.get(), DAYBLOOM.get(), DEATHWEED.get()).build(null));

    public static final Supplier<PooBlock> POO = registerWithItem("poo", () -> new PooBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)));

    public static final Supplier<BaseRopeBlock> ROPE = registerWithItem("rope", () -> new BaseRopeBlock(BlockBehaviour.Properties.of().noCollission().instabreak()), BaseRopeBlock.Item::new);

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

    /**
     * 基于黑曜石的爆炸抗性，汇流来世的方块设置爆炸抗性时，应当使用这个方法
     *
     * @param delta 偏差值
     * @return 爆炸抗性
     */
    public static float getObsidianBasedExplosionResistance(float delta) {
        return Blocks.OBSIDIAN.getExplosionResistance() + delta;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CrateBlocks.BLOCKS.register(eventBus);
        DecorativeBlocks.BLOCKS.register(eventBus);
        FunctionalBlocks.register(eventBus);
        MusicBoxBlocks.BLOCKS.register(eventBus);
        NatureBlocks.BLOCKS.register(eventBus);
        OreBlocks.BLOCKS.register(eventBus);
        PotBlocks.BLOCKS.register(eventBus);
        StatueBlocks.BLOCKS.register(eventBus);
    }
}

package org.confluence.terra_furniture.common.init;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.block.crafting.GlassKilnBlock;
import org.confluence.terra_furniture.common.block.crafting.IceMachineBlock;
import org.confluence.terra_furniture.common.block.crafting.LivingLoomBlock;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.block.light.BlockShapeType;
import org.confluence.terra_furniture.common.block.light.CandelabraBlock;
import org.confluence.terra_furniture.common.block.light.LargeChandelierBlock;
import org.confluence.terra_furniture.common.block.light.SwitchableLightBlock;
import org.confluence.terra_furniture.common.block.misc.*;
import org.confluence.terra_furniture.common.block.sittable.ChairBlock;
import org.confluence.terra_furniture.common.block.sittable.PlasticChairBlock;
import org.confluence.terra_furniture.common.block.sittable.SofaBlock;
import org.confluence.terra_furniture.common.block.sittable.ToiletBlock;
import org.confluence.terra_furniture.common.item.FishBowlItem;
import org.confluence.terra_furniture.common.item.SimpleGeoRenderedItem;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static org.confluence.terra_furniture.common.init.TFBlockSetTypes.*;

@SuppressWarnings("unused")
public final class TFBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TerraFurniture.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TerraFurniture.MODID);
    private static List<DeferredBlock<?>> chairBlocks = new LinkedList<>();
    private static List<DeferredBlock<?>> toiletBlocks = new LinkedList<>();
    private static List<DeferredBlock<ClockBlock>> clockBlocks = new LinkedList<>();
    private static List<DeferredBlock<LargeChandelierBlock>> largeChandelierBlocks = new LinkedList<>();

    public static final DeferredBlock<PlasticChairBlock> PLASTIC_CHAIR = registerWithItem("plastic_chair", () -> new PlasticChairBlock(property -> property.lightLevel(BlockState -> 1).explosionResistance(3600000.8F)), PlasticChairBlock.Item::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlasticChairBlock.PlasticChairBE>> PLASTIC_CHAIR_ENTITY = BLOCK_ENTITIES.register("plastic_chair_entity", () -> BlockEntityType.Builder.of(PlasticChairBlock.PlasticChairBE::new, PLASTIC_CHAIR.get()).build(DSL.remainderType()));

    public static final DeferredBlock<GlassKilnBlock> GLASS_KILN = registerWithItem("glass_kiln", () -> new GlassKilnBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));
    public static final Supplier<BlockEntityType<GlassKilnBlock.Entity>> GLASS_KILN_ENTITY = BLOCK_ENTITIES.register("glass_kiln_entity", () -> BlockEntityType.Builder.of(GlassKilnBlock.Entity::new, GLASS_KILN.get()).build(DSL.remainderType()));
    public static final DeferredBlock<LivingLoomBlock> LIVING_LOOM = registerWithItem("living_loom", () -> new LivingLoomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LOOM)));
    public static final DeferredBlock<IceMachineBlock> ICE_MACHINE = registerWithItem("ice_machine", () -> new IceMachineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRINDSTONE)));


    public static final DeferredBlock<ChairBlock> GLASS_CHAIR = registerChairDiscardItem("glass_chair", GLASS, Blocks.GLASS, properties -> {}, 0.5f);
    public static final DeferredBlock<SofaBlock> GLASS_SOFA = registerSofaDiscardItem("glass_sofa", GLASS, Blocks.GLASS, properties -> {});
    public static final DeferredBlock<ToiletBlock> GLASS_TOILET = registerToiletDiscardItem("glass_toilet", GLASS, Blocks.GLASS, properties -> {});
    public static final DeferredBlock<SinkBlock> GLASS_SINK = registerSinkDiscardItem("glass_sink", GLASS, Blocks.GLASS, properties -> {});

    public static final DeferredBlock<Block> FISH_BOWL = registerWithItem("fish_bowl", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), block -> new FishBowlItem(block, new Item.Properties()));
    public static final DeferredBlock<Block> GOLD_FISH_BOWL = registerWithItem("gold_fish_bowl", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), block -> new FishBowlItem(block, new Item.Properties()));
    public static final DeferredBlock<Block> PUPFISH_BOWL = registerWithItem("pupfish_bowl", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), block -> new FishBowlItem(block, new Item.Properties()));
    public static final DeferredBlock<Block> LAVA_SERPENT_BOWL = registerWithItem("lava_serpent_bowl", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), block -> new FishBowlItem(block, new Item.Properties()));
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN = registerWithItem("trash_can", () -> new TrashCanBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final Supplier<BlockEntityType<TrashCanBlock.Entity>> TRASH_CAN_ENTITY = BLOCK_ENTITIES.register("trash_can_entity", () -> BlockEntityType.Builder.of(TrashCanBlock.Entity::new, TRASH_CAN.get()).build(DSL.remainderType()));

    // 玻璃
    public static final DeferredBlock<DoorBlock> GLASS_DOOR = registerWithItem("glass_door", () -> new TFDoorBlock(GLASS, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredBlock<TableBlock> GLASS_TABLE = registerWithItem("glass_table", () -> new TableBlock(GLASS, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredBlock<SwitchableLightBlock> GLASS_CANDLE = registerWithItem("glass_candle", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(litBlockEmission(14)), BlockShapeType.CANDLE));
    public static final DeferredBlock<SwitchableLightBlock> GLASS_CHANDELIER = registerWithItem("glass_chandelier", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(litBlockEmission(14)), BlockShapeType.CHANDELIER));
    public static final DeferredBlock<SwitchableLightBlock> GLASS_LANTERN = registerWithItem("glass_lantern", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(litBlockEmission(15)), BlockShapeType.LANTERN));
    public static final DeferredBlock<SwitchableLightBlock> GLASS_LAMP = registerWithItem("glass_lamp", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(litBlockEmission(15)), BlockShapeType.LAMP));
    public static final DeferredBlock<CandelabraBlock> GLASS_CANDELABRAS = registerWithItem("glass_candelabras", () -> new CandelabraBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(litBlockEmission(15))));
    public static final DeferredBlock<ClockBlock> GLASS_CLOCK = registerClock("glass_clock", () -> new ClockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredBlock<BathtubBlock> GLASS_BATHTUB = registerWithItem("glass_bathtub", () -> new BathtubBlock(GLASS, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));

    // 蓝地牢
    public static final DeferredBlock<ChairBlock> BLUE_DUNGEON_CHAIR = registerChairDiscardItem("blue_dungeon_chair", BLUE_DUNGEON, Blocks.STONE, properties -> {}, 0.5f);
    public static final DeferredBlock<SofaBlock> BLUE_DUNGEON_SOFA = registerSofaDiscardItem("blue_dungeon_sofa", BLUE_DUNGEON, Blocks.STONE, properties -> {});
    public static final DeferredBlock<ToiletBlock> BLUE_DUNGEON_TOILET = registerToiletDiscardItem("blue_dungeon_toilet", BLUE_DUNGEON, Blocks.STONE, properties -> {});
    public static final DeferredBlock<SinkBlock> BLUE_DUNGEON_SINK = registerSinkDiscardItem("blue_dungeon_sink", BLUE_DUNGEON, Blocks.STONE, properties -> {});
    public static final DeferredBlock<DoorBlock> BLUE_DUNGEON_DOOR = registerWithItem("blue_dungeon_door", () -> new TFDoorBlock(BLUE_DUNGEON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<TableBlock> BLUE_DUNGEON_TABLE = registerWithItem("blue_dungeon_table", () -> new TableBlock(BLUE_DUNGEON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<SwitchableLightBlock> BLUE_DUNGEON_CANDLE = registerWithItem("blue_dungeon_candle", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(litBlockEmission(15)), BlockShapeType.CANDLE));
    public static final DeferredBlock<SwitchableLightBlock> BLUE_DUNGEON_LANTERN = registerWithItem("blue_dungeon_lantern", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(litBlockEmission(15)), BlockShapeType.LANTERN));
    public static final DeferredBlock<SwitchableLightBlock> BLUE_DUNGEON_LAMP = registerWithItem("blue_dungeon_lamp", () -> new SwitchableLightBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(litBlockEmission(15)), BlockShapeType.LAMP));
    public static final DeferredBlock<CandelabraBlock> BLUE_DUNGEON_CANDELABRAS = registerWithItem("blue_dungeon_candelabras", () -> new CandelabraBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(litBlockEmission(15))));
    public static final DeferredBlock<ClockBlock> BLUE_DUNGEON_CLOCK = registerClock("blue_dungeon_clock", () -> new ClockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<BathtubBlock> BLUE_DUNGEON_BATHTUB = registerWithItem("blue_dungeon_bathtub", () -> new BathtubBlock(BLUE_DUNGEON, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredBlock<LargeChandelierBlock> BLUE_DUNGEON_CHANDELIER = registerLargeChandelier("blue_dungeon_chandelier", () -> new LargeChandelierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noCollission().lightLevel(litBlockEmission(15))));

    // 木质
    public static final DeferredBlock<ChairBlock> WOODEN_CHAIR = registerChairDiscardItem("wooden_chair", OAK, Blocks.OAK_PLANKS, properties -> {}, 0.5f);
    public static final DeferredBlock<TableBlock> WOODEN_TABLE = registerWithItem("wooden_table", () -> new TableBlock(OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<TableBlock> BONE_TABLE = registerWithItem("bone_table", () -> new TableBlock(BONE, BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)));
    public static final DeferredBlock<TableBlock> BAMBOO_TABLE = registerWithItem("bamboo_table", () -> new TableBlock(BAMBOO, BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS)));
    public static final DeferredBlock<TableBlock> CACTUS_TABLE = registerWithItem("cactus_table", () -> new TableBlock(CACTUS, BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS)));

    public static final DeferredBlock<PinWheel> PIN_WHEEL = registerWithoutItem("pin_wheel", () -> new PinWheel(BlockBehaviour.Properties.ofFullCopy(Blocks.BAMBOO).noCollission()));
    public static final DeferredItem<SimpleGeoRenderedItem> PIN_WHEEL_ITEM = TFItems.BLOCK_ITEMS.register("pin_wheel", () -> new SimpleGeoRenderedItem(PIN_WHEEL.get(), new Item.Properties(), false));

    public static final DeferredBlock<HangingPotBlock> HANGING_POT = registerWithoutItem("hanging_pot", () -> new HangingPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).noCollission().lightLevel(litBlockEmission(15))));
    public static final DeferredItem<HangingPotBlock.BItem> HANGING_POT_ITEM = TFItems.BLOCK_ITEMS.register("hanging_pot", () -> new HangingPotBlock.BItem(HANGING_POT.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HangingPotBlock.BEntity>> HANGING_POT_ENTITY = BLOCK_ENTITIES.register(
            "hanging_pot_entity",
            () -> BlockEntityType.Builder.of(HangingPotBlock.BEntity::new, HANGING_POT.get()).build(DSL.remainderType())
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PinWheel.BEntity>> PIN_WHEEL_ENTITY = BLOCK_ENTITIES.register(
            "pin_wheel_entity",
            () -> BlockEntityType.Builder.of(PinWheel.BEntity::new, PIN_WHEEL.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LargeChandelierBlock.BEntity>> LARGE_CHANDELIER_ENTITY = BLOCK_ENTITIES.register("large_chandelier_entity", () -> {
        BlockEntityType<LargeChandelierBlock.BEntity> entityType = BlockEntityType.Builder.of(LargeChandelierBlock.BEntity::new, largeChandelierBlocks.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType());
        largeChandelierBlocks = null;
        return entityType;
    });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChairBlock.ChairBE>> CHAIR_ENTITY = BLOCK_ENTITIES.register("chair_entity", () -> {
        BlockEntityType<ChairBlock.ChairBE> entityType = BlockEntityType.Builder.of(ChairBlock.ChairBE::new, chairBlocks.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType());
        chairBlocks = null;
        return entityType;
    });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ToiletBlock.ToiletBE>> TOILET_ENTITY = BLOCK_ENTITIES.register("toilet_entity", () -> {
        BlockEntityType<ToiletBlock.ToiletBE> entityType = BlockEntityType.Builder.of(ToiletBlock.ToiletBE::new, toiletBlocks.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType());
        toiletBlocks = null;
        return entityType;
    });
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClockBlock.Entity>> CLOCK_ENTITY = BLOCK_ENTITIES.register("clock_entity", () -> {
        BlockEntityType<ClockBlock.Entity> entityType = BlockEntityType.Builder.of(ClockBlock.Entity::new, clockBlocks.stream().map(DeferredBlock::get).toArray(Block[]::new)).build(DSL.remainderType());
        clockBlocks = null;
        return entityType;
    });



    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        return registerWithItem(id, block, new Item.Properties());
    }

    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> item) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        TFItems.BLOCK_ITEMS.register(id, () -> item.apply(object.get()));
        return object;
    }

    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Item.Properties properties) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        TFItems.BLOCK_ITEMS.registerSimpleBlockItem(object, properties);
        return object;
    }

    public static <B extends Block> DeferredBlock<B> registerWithoutItem(String id, Supplier<B> block) {
        return BLOCKS.register(id, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

    public static @NotNull DeferredBlock<ChairBlock> registerChair(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp, float sitYOff) {
        DeferredBlock<ChairBlock> block = BLOCKS.register(id, () -> new ChairBlock(type, asBlock.defaultBlockState(), extraProp, sitYOff));
        chairBlocks.add(block);
        return block;
    }

    public static @NotNull DeferredBlock<ChairBlock> registerChairDiscardItem(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp, float sitYOff) {
        DeferredBlock<ChairBlock> block = registerChair(id, type, asBlock, extraProp, sitYOff);
        TFItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static @NotNull DeferredBlock<SofaBlock> registerSofa(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        DeferredBlock<SofaBlock> block = BLOCKS.register(id, () -> new SofaBlock(type, asBlock.defaultBlockState(), extraProp, 0.55f));
        chairBlocks.add(block);
        return block;
    }

    public static @NotNull DeferredBlock<SofaBlock> registerSofaDiscardItem(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        DeferredBlock<SofaBlock> block = registerSofa(id, type, asBlock, extraProp);
        TFItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static @NotNull DeferredBlock<ToiletBlock> registerToilet(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        DeferredBlock<ToiletBlock> block = BLOCKS.register(id, () -> new ToiletBlock(type, asBlock.defaultBlockState(), extraProp, 11.0f/16));
        toiletBlocks.add(block);
        return block;
    }

    public static @NotNull DeferredBlock<ToiletBlock> registerToiletDiscardItem(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        DeferredBlock<ToiletBlock> block = registerToilet(id, type, asBlock, extraProp);
        TFItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static @NotNull DeferredBlock<SinkBlock> registerSink(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        return BLOCKS.register(id, () -> new SinkBlock(type, asBlock.defaultBlockState(), extraProp));
    }

    public static @NotNull DeferredBlock<SinkBlock> registerSinkDiscardItem(String id, TFBlockSetType type, Block asBlock, @NotNull Consumer<BlockBehaviour.Properties> extraProp) {
        DeferredBlock<SinkBlock> block = registerSink(id, type, asBlock, extraProp);
        TFItems.BLOCK_ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    public static DeferredBlock<ClockBlock> registerClock(String id, Supplier<ClockBlock> block) {
        DeferredBlock<ClockBlock> deferredBlock = BLOCKS.register(id, block);
        clockBlocks.add(deferredBlock);
        TFItems.BLOCK_ITEMS.register(id, () -> new ClockBlock.Item(deferredBlock.get(), new Item.Properties()));
        return deferredBlock;
    }

    public static DeferredBlock<LargeChandelierBlock> registerLargeChandelier(String id, Supplier<LargeChandelierBlock> block) {
        DeferredBlock<LargeChandelierBlock> deferredBlock = BLOCKS.register(id, block);
        largeChandelierBlocks.add(deferredBlock);
        TFItems.BLOCK_ITEMS.register(id, () -> new LargeChandelierBlock.BItem(deferredBlock.get(), new Item.Properties()));
        return deferredBlock;
    }


    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return blockState -> blockState.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }
}

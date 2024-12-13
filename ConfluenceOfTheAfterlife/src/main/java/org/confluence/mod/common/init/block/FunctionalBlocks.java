package org.confluence.mod.common.init.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.*;
import org.confluence.mod.common.block.functional.crafting.AltarBlock;
import org.confluence.mod.common.block.functional.crafting.ExtractinatorBlock;
import org.confluence.mod.common.block.functional.crafting.SkyMillBlock;
import org.confluence.mod.common.block.functional.network.INetworkBlock;
import org.confluence.mod.common.init.item.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class FunctionalBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    public static final DeferredRegister.Blocks HIDDEN = DeferredRegister.createBlocks(Confluence.MODID);
    private static List<Supplier<? extends Block>> MECHANICAL_BLOCKS = new ArrayList<>();

    public static final Supplier<Block> ANDESITE_CASING = registerWithItemButHidden("andesite_casing", () -> new Block(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<ExtractinatorBlock> EXTRACTINATOR = registerWithItem("extractinator", () -> new ExtractinatorBlock(BlockBehaviour.Properties.of().strength(2.2F, 5.0F)), ExtractinatorBlock.Item::new);
    public static final Supplier<BlockEntityType<ExtractinatorBlock.Entity>> EXTRACTINATOR_ENTITY = BLOCK_ENTITIES.register("extractinator_entity", () -> BlockEntityType.Builder.of(ExtractinatorBlock.Entity::new, EXTRACTINATOR.get()).build(null));
    public static final DeferredBlock<AltarBlock> DEMON_ALTAR = registerWithItem("demon_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F), AltarBlock.Variant.DEMON), AltarBlock.Item::new);
    public static final DeferredBlock<AltarBlock> CRIMSON_ALTAR = registerWithItem("crimson_altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(3.0F, 18000.0F), AltarBlock.Variant.CRIMSON), AltarBlock.Item::new);
    public static final Supplier<BlockEntityType<AltarBlock.Entity>> ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("altar_block_entity", () -> BlockEntityType.Builder.of(AltarBlock.Entity::new, DEMON_ALTAR.get(), CRIMSON_ALTAR.get()).build(null));
    public static final DeferredBlock<SkyMillBlock> SKY_MILL = registerWithItem("sky_mill", () -> new SkyMillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRINDSTONE)), SkyMillBlock.Item::new);
    public static final Supplier<BlockEntityType<SkyMillBlock.Entity>> SKY_MILL_ENTITY = BLOCK_ENTITIES.register("sky_mill_entity", () -> BlockEntityType.Builder.of(SkyMillBlock.Entity::new, SKY_MILL.get()).build(null));
    public static final Supplier<WeatherVaneBlock> WEATHER_VANE = registerWithItem("weather_vane", () -> new WeatherVaneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final Supplier<BlockEntityType<WeatherVaneBlock.Entity>> WEATHER_VANE_ENTITY = BLOCK_ENTITIES.register("weather_vane_entity", () -> BlockEntityType.Builder.of(WeatherVaneBlock.Entity::new, WEATHER_VANE.get()).build(null));
    /**
     * @see org.confluence.mod.mixin.block.AnvilBlockMixin
     */
    public static final Supplier<AnvilBlock> LEAD_ANVIL = registerWithItem("lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANVIL)));
    public static final Supplier<AnvilBlock> CHIPPED_LEAD_ANVIL = registerWithItem("chipped_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHIPPED_ANVIL)));
    public static final Supplier<AnvilBlock> DAMAGED_LEAD_ANVIL = registerWithItem("damaged_lead_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DAMAGED_ANVIL)));

    public static final Supplier<EchoBlock> ECHO_BLOCK = registerWithItem("echo_block", EchoBlock::new);
    public static final Supplier<BaseChestBlock> BASE_CHEST_BLOCK = registerWithItemButHidden("base_chest_block", BaseChestBlock::new);
    public static final Supplier<BlockEntityType<BaseChestBlock.Entity>> BASE_CHEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("base_chest_block_entity", () -> BlockEntityType.Builder.of(BaseChestBlock.Entity::new, BASE_CHEST_BLOCK.get()).build(null));
    public static final Supplier<DeathChestBlock> DEATH_CHEST_BLOCK = registerWithItemButHidden("death_chest_block", DeathChestBlock::new);
    public static final Supplier<BlockEntityType<DeathChestBlock.Entity>> DEATH_CHEST_BLOCK_ENTITY = BLOCK_ENTITIES.register("death_chest_block_entity", () -> BlockEntityType.Builder.of(DeathChestBlock.Entity::new, DEATH_CHEST_BLOCK.get()).build(null));
    public static final Supplier<EverPoweredRailBlock> EVER_POWERED_RAIL = registerWithItem("ever_powered_rail", () -> new EverPoweredRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACTIVATOR_RAIL)));
    public static final Supplier<StepOnTrapBlock> SHIMMER_TRAP = registerWithItem("shimmer_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.SHIMMER));
    public static final Supplier<StepOnTrapBlock> GRAVITATION_TRAP = registerWithItem("gravitation_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.GRAVITATION));
    public static final Supplier<StepOnTrapBlock> PNEUMATIC_TRAP = registerWithItem("pneumatic_trap", () -> new StepOnTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DISPENSER), StepOnTrapBlock.PNEUMATIC));
    public static final Supplier<SpikeBlock> SPIKE = registerWithItem("spike", () -> new SpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS)));
    public static final Supplier<FragileBlock> FRAGILE_SANDSTONE = registerWithItem("fragile_sandstone", () -> new FragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));

    public static final Supplier<BehaviourPressurePlateBlock> PLAYER_PRESSURE_PLATE = registerWithEntity("player_pressure_plate", () -> new BehaviourPressurePlateBlock(BehaviourPressurePlateBlock.PLAYER, BlockBehaviour.Properties.ofFullCopy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), BlockSetType.IRON));
    public static final Supplier<SignalPressurePlateBlock> STONE_PRESSURE_PLATE = registerWithEntity("stone_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));
    public static final Supplier<SignalPressurePlateBlock> DEEPSLATE_PRESSURE_PLATE = registerWithEntity("deepslate_pressure_plate", () -> new SignalPressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE).mapColor(MapColor.DEEPSLATE).strength(0.1F)));
    public static final Supplier<InstantExplosionBlock> INSTANTANEOUS_EXPLOSION_TNT = registerWithEntity("instantaneous_explosion_tnt", InstantExplosionBlock::new);
    public static final Supplier<SwitchBlock> SWITCH = registerWithEntity("switch", SwitchBlock::new);
    public static final Supplier<SignalAdapterBlock> SIGNAL_ADAPTER = registerWithEntity("signal_adapter", SignalAdapterBlock::new);
    public static final Supplier<DartTrapBlock> DART_TRAP = registerWithEntity("dart_trap", DartTrapBlock::new);
    public static final Supplier<TimersBlock> TIMERS_BLOCK_1_1 = registerWithEntity("timers_1_1", () -> new TimersBlock(20)); // 1s
    public static final Supplier<TimersBlock> TIMERS_BLOCK_3_1 = registerWithEntity("timers_3_1", () -> new TimersBlock(60)); // 3s
    public static final Supplier<TimersBlock> TIMERS_BLOCK_5_1 = registerWithEntity("timers_5_1", () -> new TimersBlock(100)); // 5s
    public static final Supplier<TimersBlock> TIMERS_BLOCK_1_2 = registerWithEntity("timers_1_2", () -> new TimersBlock(10)); // 1/2s
    public static final Supplier<TimersBlock> TIMERS_BLOCK_1_4 = registerWithEntity("timers_1_4", () -> new TimersBlock(5)); // 1/4s
    public static final Supplier<GeyserBlock> GEYSER_BLOCK = registerWithEntity("geyser_block", GeyserBlock::new);
    public static final Supplier<BoulderBlock> NORMAL_BOULDER = registerWithEntity("normal_boulder", BoulderBlock::new);
    public static final Supplier<DetonatorBlock> DETONATOR = registerWithEntity("detonator", () -> new DetonatorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_TRAPDOOR)));
    public static final Supplier<MechanicalFragileBlock> MECHANICAL_FRAGILE_SANDSTONE = registerWithEntity("mechanical_fragile_sandstone", () -> new MechanicalFragileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE), Blocks.SANDSTONE::defaultBlockState));

    public static final Supplier<BlockEntityType<AbstractMechanicalBlock.Entity>> MECHANICAL_BLOCK_ENTITY = BLOCK_ENTITIES.register("mechanical_block_entity", () -> {
        Block[] validBlocks = MECHANICAL_BLOCKS.stream().map(Supplier::get).toArray(Block[]::new);
        MECHANICAL_BLOCKS = null;
        return BlockEntityType.Builder.of(AbstractMechanicalBlock.Entity::new, validBlocks).build(null);
    });

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        return object;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block, Function<B, BlockItem> function) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ModItems.BLOCK_ITEMS.register(id, () -> function.apply(object.get()));
        return object;
    }

    private static <B extends Block & EntityBlock & INetworkBlock> Supplier<B> registerWithEntity(String id, Supplier<B> supplier) {
        DeferredBlock<B> holder = registerWithItem(id, supplier);
        MECHANICAL_BLOCKS.add(holder);
        return holder;
    }

    private static <B extends Block> DeferredBlock<B> registerWithItemButHidden(String id, Supplier<B> block) {
        DeferredBlock<B> object = HIDDEN.register(id, block);
        ModItems.BLOCK_ITEMS.registerSimpleBlockItem(object);
        return object;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        HIDDEN.register(eventBus);
    }
}

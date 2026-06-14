package org.confluence.mod.common.block.natural;

import com.google.common.collect.Streams;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.lib.common.item.GroupItem;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.confluence.mod.common.init.block.ModBlocks.registerWithItem;
import static org.confluence.mod.common.init.block.ModBlocks.registerWithoutItem;

public class LogBlockSet {
    public static final Map<Block, Block> WRAPPED_STRIP_TABLE = new Hashtable<>();
    public static final List<LogBlockSet> LOG_BLOCK_SETS = new ArrayList<>();
    private static Block[] SIGN_BLOCKS;
    private static Block[] HANGING_SIGN_BLOCKS;

    public final String id;
    public final boolean ignitedByLava;

    public final PortDeferredBlock<Block> PLANKS;
    public final PortDeferredBlock<RotatedPillarBlock> LOG;
    public final PortDeferredBlock<RotatedPillarBlock> STRIPPED_LOG;
    public final PortDeferredBlock<LeavesBlock> LEAVES;
    public final PortDeferredBlock<RotatedPillarBlock> WOOD;
    public final PortDeferredBlock<RotatedPillarBlock> STRIPPED_WOOD;
    public final PortDeferredBlock<ButtonBlock> BUTTON;
    public final PortDeferredBlock<FenceBlock> FENCE;
    public final PortDeferredBlock<FenceGateBlock> FENCE_GATE;
    public final PortDeferredBlock<PressurePlateBlock> PRESSURE_PLATE;
    public final PortDeferredBlock<SlabBlock> SLAB;
    public final PortDeferredBlock<StairBlock> STAIRS;
    public final PortDeferredBlock<StandingSignBlock> SIGN;
    public final PortDeferredBlock<WallSignBlock> WALL_SIGN;
    public final PortDeferredItem<SignItem> SIGN_ITEM;
    public final PortDeferredBlock<TrapDoorBlock> TRAPDOOR;
    public final PortDeferredBlock<DoorBlock> DOOR;
    public final PortDeferredBlock<CeilingHangingSignBlock> HANGING_SIGN;
    public final PortDeferredBlock<WallHangingSignBlock> WALL_HANGING_SIGN;
    public final PortDeferredItem<HangingSignItem> HANGING_SIGN_ITEM;
    // 番外个体
    public final PortDeferredBlock<Block> CHISELED_PLANKS;
    public final PortDeferredBlock<SaplingBlock> SAPLING;

    LogBlockSet(Builder builder) {
        if ((builder.log == null && builder.strippedLog != null) || (builder.wood == null && builder.strippedWood != null))
            throw new NullPointerException();
        this.id = builder.id;
        this.ignitedByLava = builder.ignitedByLava;
        this.PLANKS = registerWithItem(id + "_planks", () -> builder.planks.apply(ignitedByLava(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD))));
        this.LOG = register(builder.log, id + "_log", true, ignitedByLava(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD)));
        this.STRIPPED_LOG = register(builder.strippedLog, "stripped_" + id + "_log", true, () -> ignitedByLava(BlockBehaviour.Properties.copy(LOG.get())));
        this.LEAVES = register(builder.leaves, id + "_leaves", true, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(LogBlockSet::valid).isSuffocating(LogBlockSet::never).isViewBlocking(LogBlockSet::never).pushReaction(PushReaction.DESTROY).isRedstoneConductor(LogBlockSet::never));
        this.WOOD = register(builder.wood, id + "_wood", true, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD));
        this.STRIPPED_WOOD = register(builder.strippedWood, "stripped_" + id + "_wood", true, () -> BlockBehaviour.Properties.copy(WOOD.get()));
        this.BUTTON = register(builder.button, id + "_button", true, BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
        this.FENCE = register(builder.fence, id + "_fence", true, () -> ignitedByLava(BlockBehaviour.Properties.of().forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).mapColor(PLANKS.get().defaultMapColor())));
        this.FENCE_GATE = register(builder.fenceGate, id + "_fence_gate", true, () -> ignitedByLava(BlockBehaviour.Properties.of().forceSolidOn().instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).mapColor(PLANKS.get().defaultMapColor())));
        this.PRESSURE_PLATE = register(builder.pressurePlate, id + "_pressure_plate", true, () -> ignitedByLava(BlockBehaviour.Properties.of().forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY).mapColor(PLANKS.get().defaultMapColor())));
        this.SLAB = register(builder.slab, id + "_slab", true, ignitedByLava(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
        this.STAIRS = builder.stairs == null ? PortDeferredBlock.createBlock(Confluence.asResource(id + "_stairs")) : registerWithItem(id + "_stairs", () -> builder.stairs.apply(PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(PLANKS.get())));
        this.SIGN = register(builder.staindingSign, id + "_sign", false, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F));
        this.WALL_SIGN = register(builder.wallSign, id + "_wall_sign", false, () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).lootFrom(SIGN));
        this.SIGN_ITEM = builder.signItem == null ? PortDeferredItem.createItem(Confluence.asResource(id + "_sign")) : ModItems.BLOCK_ITEMS.register(id + "_sign", () -> builder.signItem.apply(new Item.Properties().stacksTo(16), SIGN.get(), WALL_SIGN.get()));
        this.TRAPDOOR = register(builder.trapdoor, id + "_trapdoor", true, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().isValidSpawn(LogBlockSet::never).ignitedByLava());
        this.DOOR = register(builder.door, id + "_door", true, () -> BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASS).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY).ignitedByLava().mapColor(PLANKS.get().defaultMapColor()));
        this.HANGING_SIGN = register(builder.ceilingHangingSign, id + "_hanging_sign", false, BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava());
        this.WALL_HANGING_SIGN = register(builder.wallHangingSign, id + "_wall_hanging_sign", false, () -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollission().strength(1.0F).ignitedByLava().lootFrom(HANGING_SIGN));
        this.HANGING_SIGN_ITEM = builder.hangingSignItem == null ? PortDeferredItem.createItem(Confluence.asResource(id + "_hanging_sign")) : ModItems.BLOCK_ITEMS.register(id + "_hanging_sign", () -> builder.hangingSignItem.apply(HANGING_SIGN.get(), WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

        this.CHISELED_PLANKS = register(builder.chiseledPlanks, "chiseled_" + id + "_planks", true, () -> ignitedByLava(BlockBehaviour.Properties.copy(PLANKS.get())));
        this.SAPLING = builder.sapling == null ? PortDeferredBlock.createBlock(Confluence.asResource(id + "_sapling")) : registerWithItem(id + "_sapling", () -> builder.sapling.apply(BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)));
    }

    public Stream<ItemLike> getAllItems() {
        return Streams.concat(
                Stream.of(PLANKS, LOG, STRIPPED_LOG, LEAVES, WOOD, STRIPPED_WOOD, BUTTON, FENCE, FENCE_GATE, PRESSURE_PLATE, SLAB, STAIRS, TRAPDOOR, DOOR, SAPLING).filter(PortDeferredBlock::isBound),
                Stream.of(SIGN_ITEM, HANGING_SIGN_ITEM).filter(PortDeferredItem::isBound)
        );
    }

    private static <B extends Block> PortDeferredBlock<B> register(@Nullable Function<BlockBehaviour.Properties, ? extends B> function, String name, boolean withItem, BlockBehaviour.Properties properties) {
        return register(function, name, withItem, () -> properties);
    }

    private static <B extends Block> PortDeferredBlock<B> register(@Nullable Function<BlockBehaviour.Properties, ? extends B> function, String name, boolean withItem, Supplier<BlockBehaviour.Properties> properties) {
        if (function == null) {
            return PortDeferredBlock.createBlock(Confluence.asResource(name));
        }
        Supplier<B> supplier = () -> function.apply(properties.get());
        return withItem ? registerWithItem(name, supplier) : registerWithoutItem(name, supplier);
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    private static boolean valid(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
        return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> entityType) {
        return false;
    }

    private BlockBehaviour.Properties ignitedByLava(BlockBehaviour.Properties properties) {
        return ignitedByLava ? properties.ignitedByLava() : properties;
    }

    public static Builder builder(String id, boolean ignitedByLava, WoodSetType woodSetType) {
        return new Builder(id, ignitedByLava, woodSetType);
    }

    public static void acceptTags(Function<TagKey<Block>, IntrinsicHolderTagsProvider.IntrinsicTagAppender<Block>> provider) {
        var completes = provider.apply(BlockTags.COMPLETES_FIND_TREE_TUTORIAL);
        var burn = provider.apply(BlockTags.LOGS_THAT_BURN);
        var logs = provider.apply(BlockTags.LOGS);
        var planks = provider.apply(BlockTags.PLANKS);
        var buttons = provider.apply(BlockTags.BUTTONS);
        var woodenButtons = provider.apply(BlockTags.WOODEN_BUTTONS);
        var stairs = provider.apply(BlockTags.STAIRS);
        var woodenStairs = provider.apply(BlockTags.WOODEN_STAIRS);
        var slabs = provider.apply(BlockTags.SLABS);
        var woodenSlabs = provider.apply(BlockTags.WOODEN_SLABS);
        var fences = provider.apply(BlockTags.FENCES);
        var woodenFences = provider.apply(BlockTags.WOODEN_FENCES);
        var cFences = provider.apply(PortTags.Blocks.FENCES);
        var cFencesWooden = provider.apply(PortTags.Blocks.FENCES_WOODEN);
        var fenceGates = provider.apply(BlockTags.FENCE_GATES);
        var cFenceGates = provider.apply(PortTags.Blocks.FENCE_GATES);
        var cFenceGatesWooden = provider.apply(PortTags.Blocks.FENCE_GATES_WOODEN);
        var trapdoors = provider.apply(BlockTags.TRAPDOORS);
        var woodenTrapdoors = provider.apply(BlockTags.WOODEN_TRAPDOORS);
        var woodenPressurePlates = provider.apply(BlockTags.WOODEN_PRESSURE_PLATES);
        var doors = provider.apply(BlockTags.DOORS);
        var woodenDoors = provider.apply(BlockTags.WOODEN_DOORS);
        var leaves = provider.apply(BlockTags.LEAVES);
        var standingSigns = provider.apply(BlockTags.STANDING_SIGNS);
        var wallSigns = provider.apply(BlockTags.WALL_SIGNS);
        var signs = provider.apply(BlockTags.SIGNS);
        var cStrippedLogs = provider.apply(PortTags.Blocks.STRIPPED_LOGS);
        var ceilingHangingSigns = provider.apply(BlockTags.CEILING_HANGING_SIGNS);
        var wallHangingSigns = provider.apply(BlockTags.WALL_HANGING_SIGNS);
        var allHangingSigns = provider.apply(BlockTags.ALL_HANGING_SIGNS);
        var saplings = provider.apply(BlockTags.SAPLINGS);

        for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
            boolean ignitedByLava = blockSet.ignitedByLava;
            {
                Block value = blockSet.PLANKS.get();
                planks.add(value);
            }
            if (blockSet.LOG.isBound()) {
                Block value = blockSet.LOG.get();
                completes.add(value);
                if (ignitedByLava) burn.add(value);
                logs.add(value);
            }
            if (blockSet.STRIPPED_LOG.isBound()) {
                Block value = blockSet.STRIPPED_LOG.get();
                completes.add(value);
                if (ignitedByLava) burn.add(value);
                logs.add(value);
                cStrippedLogs.add(value);
            }
            if (blockSet.WOOD.isBound()) {
                Block value = blockSet.WOOD.get();
                completes.add(value);
                if (ignitedByLava) burn.add(value);
                logs.add(value);
            }
            if (blockSet.STRIPPED_WOOD.isBound()) {
                Block value = blockSet.STRIPPED_WOOD.get();
                completes.add(value);
                if (ignitedByLava) burn.add(value);
                logs.add(value);
            }
            if (blockSet.LEAVES.isBound()) {
                Block value = blockSet.LEAVES.get();
                completes.add(value);
                leaves.add(value);
            }
            if (blockSet.BUTTON.isBound()) {
                Block value = blockSet.BUTTON.get();
                buttons.add(value);
                woodenButtons.add(value);
            }
            if (blockSet.FENCE.isBound()) {
                Block value = blockSet.FENCE.get();
                fences.add(value);
                woodenFences.add(value);
                cFences.add(value);
                cFencesWooden.add(value);
            }
            if (blockSet.FENCE_GATE.isBound()) {
                Block value = blockSet.FENCE_GATE.get();
                fenceGates.add(value);
                cFenceGates.add(value);
                cFenceGatesWooden.add(value);
            }
            if (blockSet.PRESSURE_PLATE.isBound()) {
                Block value = blockSet.PRESSURE_PLATE.get();
                woodenPressurePlates.add(value);
            }
            if (blockSet.SLAB.isBound()) {
                Block value = blockSet.SLAB.get();
                slabs.add(value);
                woodenSlabs.add(value);
            }
            if (blockSet.STAIRS.isBound()) {
                Block value = blockSet.STAIRS.get();
                stairs.add(value);
                woodenStairs.add(value);
            }
            if (blockSet.SIGN.isBound()) {
                Block value = blockSet.SIGN.get();
                standingSigns.add(value);
                signs.add(value);
            }
            if (blockSet.WALL_SIGN.isBound()) {
                Block value = blockSet.WALL_SIGN.get();
                wallSigns.add(value);
                signs.add(value);
            }
            if (blockSet.TRAPDOOR.isBound()) {
                Block value = blockSet.TRAPDOOR.get();
                trapdoors.add(value);
                woodenTrapdoors.add(value);
            }
            if (blockSet.DOOR.isBound()) {
                Block value = blockSet.DOOR.get();
                doors.add(value);
                woodenDoors.add(value);
            }
            if (blockSet.HANGING_SIGN.isBound()) {
                Block value = blockSet.HANGING_SIGN.get();
                ceilingHangingSigns.add(value);
                allHangingSigns.add(value);
            }
            if (blockSet.WALL_HANGING_SIGN.isBound()) {
                Block value = blockSet.WALL_HANGING_SIGN.get();
                wallHangingSigns.add(value);
                allHangingSigns.add(value);
            }
            if (blockSet.CHISELED_PLANKS.isBound()) {
                Block value = blockSet.CHISELED_PLANKS.get();
                planks.add(value);
            }
            if (blockSet.SAPLING.isBound()) {
                Block value = blockSet.SAPLING.get();
                saplings.add(value);
            }
        }
    }

    public static void acceptBuilding(CreativeModeTab.Output output) {
        for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
            CreativeModeTab.Output o = GroupItem.belongsTo(blockSet.id, output);

            o.accept(blockSet.PLANKS.get());
            if (blockSet.STRIPPED_LOG.isBound()) o.accept(blockSet.STRIPPED_LOG.get());
            if (blockSet.WOOD.isBound()) o.accept(blockSet.WOOD.get());
            if (blockSet.STRIPPED_WOOD.isBound()) o.accept(blockSet.STRIPPED_WOOD.get());
            if (blockSet.BUTTON.isBound()) o.accept(blockSet.BUTTON.get());
            if (blockSet.FENCE.isBound()) o.accept(blockSet.FENCE.get());
            if (blockSet.FENCE_GATE.isBound()) o.accept(blockSet.FENCE_GATE.get());
            if (blockSet.PRESSURE_PLATE.isBound()) o.accept(blockSet.PRESSURE_PLATE.get());
            if (blockSet.SLAB.isBound()) o.accept(blockSet.SLAB.get());
            if (blockSet.STAIRS.isBound()) o.accept(blockSet.STAIRS.get());
            if (blockSet.SIGN_ITEM.isBound()) o.accept(blockSet.SIGN_ITEM.get());
            if (blockSet.TRAPDOOR.isBound()) o.accept(blockSet.TRAPDOOR.get());
            if (blockSet.DOOR.isBound()) o.accept(blockSet.DOOR.get());
            if (blockSet.HANGING_SIGN_ITEM.isBound()) o.accept(blockSet.HANGING_SIGN_ITEM.get());
            if (blockSet.CHISELED_PLANKS.isBound()) o.accept(blockSet.CHISELED_PLANKS.get());
        }
    }

    public static void acceptNature(CreativeModeTab.Output output) {
        for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
            CreativeModeTab.Output o = GroupItem.belongsTo(blockSet.id, output);

            if (blockSet.LOG.isBound()) o.accept(blockSet.LOG.get());
            if (blockSet.LEAVES.isBound()) o.accept(blockSet.LEAVES.get());
            if (blockSet.SAPLING.isBound()) o.accept(blockSet.SAPLING.get());
        }
    }

    public static void wrapStrip() {
        for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
            if (blockSet.LOG.isBound() && blockSet.STRIPPED_LOG.isBound()) {
                WRAPPED_STRIP_TABLE.put(blockSet.LOG.get(), blockSet.STRIPPED_LOG.get());
            }
            if (blockSet.WOOD.isBound() && blockSet.STRIPPED_WOOD.isBound()) {
                WRAPPED_STRIP_TABLE.put(blockSet.WOOD.get(), blockSet.STRIPPED_WOOD.get());
            }
        }
    }

    public static Block[] getSignBlocks() {
        if (SIGN_BLOCKS == null) {
            List<Block> list = new ArrayList<>();
            for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
                if (blockSet.SIGN.isBound()) list.add(blockSet.SIGN.get());
                if (blockSet.WALL_SIGN.isBound()) list.add(blockSet.WALL_SIGN.get());
            }
            SIGN_BLOCKS = list.toArray(new Block[0]);
        }
        return SIGN_BLOCKS;
    }

    public static Block[] getHangingSignBlocks() {
        if (HANGING_SIGN_BLOCKS == null) {
            List<Block> list = new ArrayList<>();
            for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
                if (blockSet.HANGING_SIGN.isBound()) list.add(blockSet.HANGING_SIGN.get());
                if (blockSet.WALL_HANGING_SIGN.isBound())
                    list.add(blockSet.WALL_HANGING_SIGN.get());
            }
            HANGING_SIGN_BLOCKS = list.toArray(new Block[0]);
        }
        return HANGING_SIGN_BLOCKS;
    }

    public static void setFlammable() {
        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (LogBlockSet blockSet : LOG_BLOCK_SETS) {
            boolean ignitedByLava = blockSet.ignitedByLava;
            if (ignitedByLava) {
                fireblock.setFlammable(blockSet.PLANKS.get(), 5, 20);
            }
            if (blockSet.LOG.isBound()) {
                Block value = blockSet.LOG.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 5);
                }
            }
            if (blockSet.STRIPPED_LOG.isBound()) {
                Block value = blockSet.STRIPPED_LOG.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 5);
                }
            }
            if (blockSet.WOOD.isBound()) {
                Block value = blockSet.WOOD.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 5);
                }
            }
            if (blockSet.STRIPPED_WOOD.isBound()) {
                Block value = blockSet.STRIPPED_WOOD.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 5);
                }
            }
            if (blockSet.LEAVES.isBound()) {
                Block value = blockSet.LEAVES.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 30, 60);
                }
            }
            if (blockSet.FENCE.isBound()) {
                Block value = blockSet.FENCE.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 20);
                }
            }
            if (blockSet.FENCE_GATE.isBound()) {
                Block value = blockSet.FENCE_GATE.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 20);
                }
            }
            if (blockSet.SLAB.isBound()) {
                Block value = blockSet.SLAB.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 20);
                }
            }
            if (blockSet.STAIRS.isBound()) {
                Block value = blockSet.STAIRS.get();
                if (ignitedByLava) {
                    fireblock.setFlammable(value, 5, 20);
                }
            }
        }
    }

    public static class Builder {
        private final String id;
        private final boolean ignitedByLava;
        private Function<BlockBehaviour.Properties, ? extends Block> planks = Block::new;
        private Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> log = RotatedPillarBlock::new;
        private Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> strippedLog = RotatedPillarBlock::new;
        private Function<BlockBehaviour.Properties, ? extends LeavesBlock> leaves = LeavesBlock::new;
        private Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> wood = RotatedPillarBlock::new;
        private Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> strippedWood = RotatedPillarBlock::new;
        private Function<BlockBehaviour.Properties, ? extends ButtonBlock> button;
        private Function<BlockBehaviour.Properties, ? extends FenceBlock> fence = FenceBlock::new;
        private Function<BlockBehaviour.Properties, ? extends FenceGateBlock> fenceGate;
        private Function<BlockBehaviour.Properties, ? extends PressurePlateBlock> pressurePlate;
        private Function<BlockBehaviour.Properties, ? extends SlabBlock> slab = SlabBlock::new;
        private BiFunction<BlockState, BlockBehaviour.Properties, ? extends StairBlock> stairs = StairBlock::new;
        private Function<BlockBehaviour.Properties, ? extends StandingSignBlock> staindingSign;
        private Function<BlockBehaviour.Properties, ? extends WallSignBlock> wallSign;
        private TriFunction<Item.Properties, StandingSignBlock, WallSignBlock, ? extends SignItem> signItem;
        private Function<BlockBehaviour.Properties, ? extends TrapDoorBlock> trapdoor;
        private Function<BlockBehaviour.Properties, ? extends DoorBlock> door;
        private Function<BlockBehaviour.Properties, ? extends CeilingHangingSignBlock> ceilingHangingSign;
        private Function<BlockBehaviour.Properties, ? extends WallHangingSignBlock> wallHangingSign;
        private TriFunction<CeilingHangingSignBlock, WallHangingSignBlock, Item.Properties, ? extends HangingSignItem> hangingSignItem;
        // 最后之作
        private Function<BlockBehaviour.Properties, ? extends Block> chiseledPlanks = Block::new;
        private Function<BlockBehaviour.Properties, ? extends SaplingBlock> sapling;

        Builder(String id, boolean ignitedByLava, WoodSetType woodSetType) {
            this.id = id;
            this.ignitedByLava = ignitedByLava;
            this.button = properties -> new ButtonBlock(properties, woodSetType.SET, 30, true);
            this.fenceGate = properties -> new FenceGateBlock(properties, woodSetType.TYPE);
            this.pressurePlate = properties -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, properties, woodSetType.SET);
            this.staindingSign = properties -> new StandingSignBlock(properties, woodSetType.TYPE);
            this.wallSign = properties -> new WallSignBlock(properties, woodSetType.TYPE);
            this.signItem = SignItem::new;
            this.trapdoor = properties -> new TrapDoorBlock(properties, woodSetType.SET);
            this.door = properties -> new DoorBlock(properties, woodSetType.SET);
            this.ceilingHangingSign = properties -> new CeilingHangingSignBlock(properties, woodSetType.TYPE);
            this.wallHangingSign = properties -> new WallHangingSignBlock(properties, woodSetType.TYPE);
            this.hangingSignItem = HangingSignItem::new;
        }

        public Builder planks(Function<BlockBehaviour.Properties, ? extends Block> function) {
            this.planks = function;
            return this;
        }

        public Builder log(@Nullable Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> function) {
            this.log = function;
            return this;
        }

        public Builder strippedLog(@Nullable Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> function) {
            this.strippedLog = function;
            return this;
        }

        public Builder leaves(@Nullable Function<BlockBehaviour.Properties, ? extends LeavesBlock> function) {
            this.leaves = function;
            return this;
        }

        public Builder wood(@Nullable Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> function) {
            this.wood = function;
            return this;
        }

        public Builder strippedWood(@Nullable Function<BlockBehaviour.Properties, ? extends RotatedPillarBlock> function) {
            this.strippedWood = function;
            return this;
        }

        public Builder button(@Nullable Function<BlockBehaviour.Properties, ? extends ButtonBlock> function) {
            this.button = function;
            return this;
        }

        public Builder fence(@Nullable Function<BlockBehaviour.Properties, ? extends FenceBlock> function) {
            this.fence = function;
            return this;
        }

        public Builder fenceGate(@Nullable Function<BlockBehaviour.Properties, ? extends FenceGateBlock> function) {
            this.fenceGate = function;
            return this;
        }

        public Builder pressurePlate(@Nullable Function<BlockBehaviour.Properties, ? extends PressurePlateBlock> function) {
            this.pressurePlate = function;
            return this;
        }

        public Builder slab(@Nullable Function<BlockBehaviour.Properties, ? extends SlabBlock> function) {
            this.slab = function;
            return this;
        }

        public Builder stair(@Nullable BiFunction<BlockState, BlockBehaviour.Properties, ? extends StairBlock> function) {
            this.stairs = function;
            return this;
        }

        public Builder sign(
                @Nullable Function<BlockBehaviour.Properties, ? extends StandingSignBlock> standingSign,
                @Nullable Function<BlockBehaviour.Properties, ? extends WallSignBlock> wallSign,
                @Nullable TriFunction<Item.Properties, StandingSignBlock, WallSignBlock, ? extends SignItem> signItem
        ) {
            if ((standingSign == null && wallSign == null && signItem == null) || (standingSign != null && wallSign != null && signItem != null)) {
                this.staindingSign = standingSign;
                this.wallSign = wallSign;
                this.signItem = signItem;
            } else {
                throw new IllegalArgumentException("sign must be bound to a standing sign, wall sign, and sign item");
            }
            return this;
        }

        public Builder trapdoor(@Nullable Function<BlockBehaviour.Properties, ? extends TrapDoorBlock> function) {
            this.trapdoor = function;
            return this;
        }

        public Builder door(@Nullable Function<BlockBehaviour.Properties, ? extends DoorBlock> function) {
            this.door = function;
            return this;
        }

        public Builder hangingSign(
                @Nullable Function<BlockBehaviour.Properties, ? extends CeilingHangingSignBlock> ceilingHangingSign,
                @Nullable Function<BlockBehaviour.Properties, ? extends WallHangingSignBlock> wallHangingSign,
                @Nullable TriFunction<CeilingHangingSignBlock, WallHangingSignBlock, Item.Properties, ? extends HangingSignItem> hangingSignItem
        ) {
            if ((ceilingHangingSign == null && wallHangingSign == null && hangingSignItem == null) || (ceilingHangingSign != null && wallHangingSign != null && hangingSignItem != null)) {
                this.ceilingHangingSign = ceilingHangingSign;
                this.wallHangingSign = wallHangingSign;
                this.hangingSignItem = hangingSignItem;
            } else {
                throw new IllegalArgumentException("hanging sign must be bound to a ceiling hanging sign, wall hanging sign, and hanging sign item");
            }
            return this;
        }

        public Builder chiseledPlanks(@Nullable Function<BlockBehaviour.Properties, ? extends Block> function) {
            this.chiseledPlanks = function;
            return this;
        }

        public Builder sapling(Function<BlockBehaviour.Properties, ? extends SaplingBlock> function) {
            this.sapling = function;
            return this;
        }

        public LogBlockSet build() {
            LogBlockSet blockSet = new LogBlockSet(this);
            LOG_BLOCK_SETS.add(blockSet);
            return blockSet;
        }
    }

    public static class WoodSetType {
        public static final WoodSetType EBONY = new WoodSetType("ebony");
        public static final WoodSetType PEARL = new WoodSetType("pearl");
        public static final WoodSetType SHADOW = new WoodSetType("shadow");
        public static final WoodSetType PALM = new WoodSetType("palm");
        public static final WoodSetType BAOBAB = new WoodSetType("baobab");
        public static final WoodSetType GLOWING_MUSHROOM = new WoodSetType("glowing_mushroom");
        public static final WoodSetType YELLOW_WILLOW = new WoodSetType("yellow_willow");
        public static final WoodSetType LIVING = new WoodSetType("living");
        public static final WoodSetType LIVING_MAHOGANY = new WoodSetType("living_mahogany");
        public static final WoodSetType ASH = new WoodSetType("ash");
        public static final WoodSetType DYNASTY = new WoodSetType("dynasty");
        public static final WoodSetType PINE = new WoodSetType("pine");
        public static final WoodSetType GAZE = new WoodSetType("gaze");
        public static final WoodSetType VOID = new WoodSetType("void");
        public static final WoodSetType SPOOKY = new WoodSetType("spooky");
        public static final WoodSetType MOONGLOW_WILLOW = new WoodSetType("moonglow_willow");
        public static final WoodSetType FEY = new WoodSetType("fey");

        public final BlockSetType SET;
        public final WoodType TYPE;

        WoodSetType(String id) {
            id = Confluence.asPlainId(id);
            this.SET = BlockSetType.register(new BlockSetType(id));
            this.TYPE = WoodType.register(new WoodType(id, SET));
        }
    }
}

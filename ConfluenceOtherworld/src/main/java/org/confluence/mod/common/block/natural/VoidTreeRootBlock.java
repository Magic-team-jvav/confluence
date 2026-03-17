package org.confluence.mod.common.block.natural;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoidTreeRootBlock extends Block implements EntityBlock {

    private static final float RADIUS = 0.25F;

    private static final Map<BlockState, VoxelShape> SHAPE_MAP = new ConcurrentHashMap<>();

    public static final Map<Direction, EnumProperty<ConnectType>> CONNECTION_PROPERTIES =
            Util.make(new EnumMap<>(Direction.class), map -> {
                for (Direction direction : LibUtils.DIRECTIONS) {
                    map.put(direction, EnumProperty.create(direction.getName(), ConnectType.class));
                }
            });

    public VoidTreeRootBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava());

        BlockState defaultState = this.stateDefinition.any();
        for (EnumProperty<ConnectType> prop : CONNECTION_PROPERTIES.values()) {
            defaultState = defaultState.setValue(prop, ConnectType.DIS_CONNECT);
        }
        this.registerDefaultState(defaultState);
    }

    @Override
    public net.minecraft.world.level.material.PushReaction getPistonPushReaction(BlockState state) {
        return net.minecraft.world.level.material.PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE_MAP.computeIfAbsent(state, s -> {
            double min = 0.5 - RADIUS;
            double max = 0.5 + RADIUS;
            VoxelShape shape = Block.box(min * 16, min * 16, min * 16, max * 16, max * 16, max * 16);

            for (Direction direction : LibUtils.DIRECTIONS) {
                ConnectType type = s.getValue(CONNECTION_PROPERTIES.get(direction));

                if (type != ConnectType.DIS_CONNECT) {
                    shape = Shapes.or(shape, getArmShape(direction));
                }
            }
            return shape;
        });
    }

    private VoxelShape getArmShape(Direction direction) {
        double min = 0.5 - RADIUS;
        double max = 0.5 + RADIUS;

        return switch (direction) {
            case DOWN -> Block.box(min * 16, 0, min * 16, max * 16, max * 16, max * 16);
            case UP -> Block.box(min * 16, min * 16, min * 16, max * 16, 16, max * 16);
            case NORTH -> Block.box(min * 16, min * 16, 0, max * 16, max * 16, max * 16);
            case SOUTH -> Block.box(min * 16, min * 16, min * 16, max * 16, max * 16, 16);
            case WEST -> Block.box(0, min * 16, min * 16, max * 16, max * 16, max * 16);
            case EAST -> Block.box(min * 16, min * 16, min * 16, 16, max * 16, max * 16);
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockState neighborState = level.getBlockState(pos.relative(direction));
            if (neighborState.is(this)
                    || neighborState.is(NatureBlocks.VOID_LOG_BLOCKS.LOG.get())
                    || neighborState.is(NatureBlocks.VOID_LOG_BLOCKS.WOOD.get())
                    || neighborState.is(NatureBlocks.VOID_LOG_BLOCKS.STRIPPED_LOG.get())
                    || neighborState.is(NatureBlocks.VOID_LOG_BLOCKS.STRIPPED_WOOD.get())
            ) {
                state = state.setValue(CONNECTION_PROPERTIES.get(direction), ConnectType.CONNECT);
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        EnumProperty<ConnectType> prop = CONNECTION_PROPERTIES.get(direction);
        ConnectType currentType = state.getValue(prop);

        if (currentType == ConnectType.CONNECT_BY_PORTAL) return state;

        ConnectType newType = neighborState.is(this) ? ConnectType.CONNECT : ConnectType.DIS_CONNECT;
        return state.setValue(prop, newType);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        CONNECTION_PROPERTIES.values().forEach(builder::add);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level instanceof ServerLevel serverLevel) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BEntity rootEntity) {
                    BlockPos linkedPos = rootEntity.getLinkedPos();

                    boolean hasPortalSide = CONNECTION_PROPERTIES.values().stream().anyMatch(prop -> state.getValue(prop) == ConnectType.CONNECT_BY_PORTAL);

                    if (hasPortalSide && linkedPos != null) {
                        serverLevel.getChunkAt(linkedPos);

                        BlockState linkedState = level.getBlockState(linkedPos);
                        if (linkedState.is(this)) {
                            level.destroyBlock(linkedPos, true);
                        }
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    public static class BEntity extends BlockEntity {
        private BlockPos linkedPos;

        public BEntity(BlockPos pos, BlockState state) {
            super(NatureBlocks.VOID_TREE_ROOT_BLOCK_ENTITY.get(), pos, state);
        }

        public void setLinkedPos(BlockPos pos) {
            this.linkedPos = pos;
            this.setChanged();
        }

        public @Nullable BlockPos getLinkedPos() {
            return linkedPos;
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            if (linkedPos != null) {
                tag.putLong("LinkedPortalPos", linkedPos.asLong());
            }
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            if (tag.contains("LinkedPortalPos")) {
                this.linkedPos = BlockPos.of(tag.getLong("LinkedPortalPos"));
            }
        }
    }

    public enum ConnectType implements StringRepresentable {
        CONNECT("connect"),
        DIS_CONNECT("dis_connect"),
        CONNECT_BY_PORTAL("connect_by_portal");

        private final String name;

        ConnectType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}

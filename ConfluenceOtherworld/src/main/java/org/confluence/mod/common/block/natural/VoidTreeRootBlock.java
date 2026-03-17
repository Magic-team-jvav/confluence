package org.confluence.mod.common.block.natural;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class VoidTreeRootBlock extends Block implements EntityBlock {

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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Direction direction : LibUtils.DIRECTIONS) {
            BlockState neighborState = level.getBlockState(pos.relative(direction));
            if (neighborState.is(this)) {
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BEntity rootEntity) {
                BlockPos linkedPos = rootEntity.getLinkedPos();
                boolean hasPortalSide = CONNECTION_PROPERTIES.values().stream().anyMatch(prop -> state.getValue(prop) == ConnectType.CONNECT_BY_PORTAL);

                if (hasPortalSide && linkedPos != null) {
                    BlockState linkedState = level.getBlockState(linkedPos);
                    if (linkedState.is(this)) {
                        level.destroyBlock(linkedPos, true);
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

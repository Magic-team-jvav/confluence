package org.confluence.mod.common.block.natural;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoidTreeRootBlock extends Block implements EntityBlock {
    private static final float RADIUS = 0.25F;
    private static final TagKey<Block> VOID_TREE_ROOT_CAN_CONNECT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "void_tree_root_can_connect"));
    private static final Map<BlockState, VoxelShape> SHAPE_MAP = new ConcurrentHashMap<>();
    public static final Map<Direction, EnumProperty<ConnectType>> CONNECTION_PROPERTIES =
            Util.make(new EnumMap<>(Direction.class), map -> {
                for (Direction direction : LibUtils.DIRECTIONS) {
                    map.put(direction, EnumProperty.create(direction.getName(), ConnectType.class));
                }
            });

    public VoidTreeRootBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F).sound(SoundType.WOOD));
        BlockState defaultState = this.stateDefinition.any();
        for (EnumProperty<ConnectType> prop : CONNECTION_PROPERTIES.values()) {
            defaultState = defaultState.setValue(prop, ConnectType.DIS_CONNECT);
        }
        this.registerDefaultState(defaultState);
    }

    @Override
    public boolean isSignalSource(BlockState state) {return true;}

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof BEntity be) return be.getMaxRemotePower();
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        Direction face = direction.getOpposite();
        if (state.getValue(CONNECTION_PROPERTIES.get(face)) == ConnectType.CONNECT_BY_PORTAL) {
            return getSignal(state, level, pos, direction);
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (!level.isClientSide) level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof BEntity be) be.evaluateAndSync();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level instanceof ServerLevel sl && level.getBlockEntity(pos) instanceof BEntity be) {
                be.linkedPositions.values().forEach(target -> {
                    if (sl.isLoaded(target) && sl.getBlockState(target).is(this))
                        sl.destroyBlock(target, true);
                });
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE_MAP.computeIfAbsent(state, s -> {
            double min = 0.5 - RADIUS, max = 0.5 + RADIUS;
            VoxelShape shape = Block.box(min * 16, min * 16, min * 16, max * 16, max * 16, max * 16);
            for (Direction d : LibUtils.DIRECTIONS) {
                if (s.getValue(CONNECTION_PROPERTIES.get(d)) != ConnectType.DIS_CONNECT)
                    shape = Shapes.or(shape, getArmShape(d));
            }
            return shape;
        });
    }

    private VoxelShape getArmShape(Direction d) {
        double min = 0.5 - RADIUS, max = 0.5 + RADIUS;
        return switch (d) {
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
        for (Direction d : LibUtils.DIRECTIONS) {
            BlockState n = context.getLevel().getBlockState(context.getClickedPos().relative(d));
            if (n.is(this) || n.is(VOID_TREE_ROOT_CAN_CONNECT))
                state = state.setValue(CONNECTION_PROPERTIES.get(d), ConnectType.CONNECT);
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction d, BlockState ns, LevelAccessor level, BlockPos pos, BlockPos np) {
        if (state.getValue(CONNECTION_PROPERTIES.get(d)) == ConnectType.CONNECT_BY_PORTAL)
            return state;
        return state.setValue(CONNECTION_PROPERTIES.get(d), (ns.is(this) || ns.is(VOID_TREE_ROOT_CAN_CONNECT)) ? ConnectType.CONNECT : ConnectType.DIS_CONNECT);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {CONNECTION_PROPERTIES.values().forEach(builder::add);}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new BEntity(pos, state);}

    public static class BEntity extends BlockEntity {
        public final Map<Direction, BlockPos> linkedPositions = new EnumMap<>(Direction.class);
        private final Map<BlockPos, Integer> remotePowerSources = new HashMap<>();
        private int localInputPower = 0;
        private int lastMaxRemotePower = 0;

        public BEntity(BlockPos pos, BlockState state) {
            super(NatureBlocks.VOID_TREE_ROOT_BLOCK_ENTITY.get(), pos, state);
        }

        public int getMaxRemotePower() {
            if (remotePowerSources.isEmpty()) return 0;
            return Collections.max(remotePowerSources.values());
        }

        public void evaluateAndSync() {
            if (level == null || level.isClientSide) return;
            int maxIn = 0;
            int currentRemote = getMaxRemotePower();
            for (Direction dir : LibUtils.DIRECTIONS) {
                int s = level.getSignal(worldPosition.relative(dir), dir);
                if (getBlockState().getValue(CONNECTION_PROPERTIES.get(dir)) == ConnectType.CONNECT_BY_PORTAL) {
                    if (s <= currentRemote) s = 0;
                }
                if (s > maxIn) maxIn = s;
            }

            if (this.localInputPower != maxIn) {
                this.localInputPower = maxIn;
                this.setChanged();
                int toSend = Math.max(0, this.localInputPower - 1);
                if (level instanceof ServerLevel sl) {
                    sl.getServer().execute(() -> {
                        if (!this.isRemoved()) {
                            linkedPositions.values().forEach(target -> {
                                if (sl.isLoaded(target) && sl.getBlockEntity(target) instanceof BEntity remoteBE) {
                                    remoteBE.receivePower(worldPosition, toSend);
                                }
                            });
                        }
                    });
                }
            }
        }

        public void receivePower(BlockPos source, int p) {
            if (p <= 0) remotePowerSources.remove(source);
            else remotePowerSources.put(source, p);

            int currentMax = getMaxRemotePower();
            if (this.lastMaxRemotePower != currentMax) {
                this.lastMaxRemotePower = currentMax;
                this.setChanged();
                if (level != null) {
                    level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                    level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
                }
            }
        }

        public void addLink(Direction d, BlockPos p) {
            this.linkedPositions.put(d, p);
            this.setChanged();
            if (level != null && !level.isClientSide)
                level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            ListTag links = new ListTag();
            linkedPositions.forEach((d, p) -> {
                CompoundTag e = new CompoundTag();
                e.putInt("d", d.ordinal());
                e.putLong("p", p.asLong());
                links.add(e);
            });
            tag.put("Links", links);

            ListTag powers = new ListTag();
            remotePowerSources.forEach((pos, p) -> {
                CompoundTag e = new CompoundTag();
                e.putLong("pos", pos.asLong());
                e.putInt("p", p);
                powers.add(e);
            });
            tag.put("RemotePowers", powers);
            tag.putInt("LP", localInputPower);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            linkedPositions.clear();
            if (tag.contains("Links")) {
                ListTag list = tag.getList("Links", Tag.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag e = list.getCompound(i);
                    linkedPositions.put(Direction.values()[e.getInt("d")], BlockPos.of(e.getLong("p")));
                }
            }
            remotePowerSources.clear();
            if (tag.contains("RemotePowers")) {
                ListTag list = tag.getList("RemotePowers", Tag.TAG_COMPOUND);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag e = list.getCompound(i);
                    remotePowerSources.put(BlockPos.of(e.getLong("pos")), e.getInt("p"));
                }
            }
            localInputPower = tag.getInt("LP");
            lastMaxRemotePower = getMaxRemotePower();
        }
    }

    public enum ConnectType implements StringRepresentable {
        CONNECT("connect"), DIS_CONNECT("dis_connect"), CONNECT_BY_PORTAL("connect_by_portal");
        private final String n;

        ConnectType(String n) {this.n = n;}

        @Override
        public String getSerializedName() {return n;}
    }
}

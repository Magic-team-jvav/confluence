package org.confluence.mod.common.block.functional.terraform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.mod.client.renderer.block.EnvironmentalMachineRenderer;
import org.confluence.mod.common.data.saved.EnvironmentalMachines;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

// 手持溶液右键存入，潜行空手右键取出；漏斗使用同一个三槽库存，不允许混装。
public class EnvironmentalMachineBlock extends HorizontalDirectionalWithVerticalTwoPartBlock implements EntityBlock {
    private static final VoxelShape BASE_SHAPE = box(0, 0, 0, 16, 16, 16);
    // 三个插入口固定在机身顶部，装填前后使用同一组交互范围。
    private static final double[][][] TANK_BOUNDS = {
            {{-9.5, 13, -2, -5.5, 16, 2}, {5.5, 13, -2, 9.5, 16, 2}, {-2, 13, 5.5, 2, 16, 9.5}},
            {{-9.5, 13, -2, -5.5, 16, 2}, {5.5, 13, -2, 9.5, 16, 2}, {-2, 13, 5.5, 2, 16, 9.5}}
    };
    private static final VoxelShape[][][] MACHINE_SHAPES = new VoxelShape[4][8][2];

    static {
        // 交互形状只覆盖机身与三个插入口，动画中的悬空罐体不参与碰撞。
        for (Direction front : Direction.Plane.HORIZONTAL) {
            for (int mask = 0; mask < 8; mask++) {
                for (int part = 0; part < 2; part++) {
                    VoxelShape shape = part == 0 ? BASE_SHAPE : Shapes.empty();
                    for (int slot = 0; slot < 3; slot++) {
                        AABB tank = tankBounds(front, slot, (mask & 1 << slot) != 0);
                        double minY = Math.max(tank.minY, part * 16);
                        double maxY = Math.min(tank.maxY, (part + 1) * 16);
                        if (minY < maxY)
                            shape = Shapes.or(shape, box(tank.minX, minY - part * 16, tank.minZ,
                                    tank.maxX, maxY - part * 16, tank.maxZ));
                    }
                    MACHINE_SHAPES[front.get2DDataValue()][mask][part] = shape;
                }
            }
        }
    }

    private static AABB tankBounds(Direction front, int slot, boolean inserted) {
        double[] bounds = TANK_BOUNDS[inserted ? 1 : 0][slot];
        Direction right = front.getClockWise();
        double minX = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (int x : new int[]{0, 3}) {
            for (int z : new int[]{2, 5}) {
                // Geo 模型导出时翻转了 X 轴，交互区域必须使用相同的变换。
                double px = 8 - bounds[x] * right.getStepX() - bounds[z] * front.getStepX();
                double pz = 8 - bounds[x] * right.getStepZ() - bounds[z] * front.getStepZ();
                minX = Math.min(minX, px);
                maxX = Math.max(maxX, px);
                minZ = Math.min(minZ, pz);
                maxZ = Math.max(maxZ, pz);
            }
        }
        return new AABB(minX, bounds[1], minZ, maxX, bounds[4], maxZ);
    }

    private static int filledSlots(BlockGetter level, BlockPos pos) {
        int mask = 0;
        if (level.getBlockEntity(pos) instanceof BEntity entity) {
            for (int slot = 0; slot < 3; slot++)
                if (entity.hasSolutionInSlot(slot)) mask |= 1 << slot;
        }
        return mask;
    }

    public final boolean world;

    public EnvironmentalMachineBlock(boolean world) {
        super(Properties.of().strength(3.5F).dynamicShape().noOcclusion().pushReaction(PushReaction.BLOCK));
        this.world = world;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int part = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE ? 0 : 1;
        return MACHINE_SHAPES[state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue()][filledSlots(level, pos)][part];
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new BEntity(pos, state);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        interact(level, pos, player, stack, slot(state, pos, hit, filledSlots(level, pos)));
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        interact(level, pos, player, ItemStack.EMPTY, slot(state, pos, hit, filledSlots(level, pos)));
        return InteractionResult.SUCCESS;
    }

    // 三个插入口位置固定，上下两半都以底座坐标计算。
    private static int slot(BlockState state, BlockPos pos, BlockHitResult hit, int mask) {
        BlockPos base = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE ? pos : pos.below();
        double x = (hit.getLocation().x - base.getX()) * 16;
        double y = (hit.getLocation().y - base.getY()) * 16;
        double z = (hit.getLocation().z - base.getZ()) * 16;
        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (int slot = 0; slot < 3; slot++) {
            AABB bounds = tankBounds(front, slot, (mask & 1 << slot) != 0).inflate(0.001);
            if (bounds.contains(x, y, z)) return slot;
        }
        return -1;
    }

    // 玩家与自动化都由服务端校验种类；预付工作时间未用完时也不能换成另一种溶液。
    private void interact(Level level, BlockPos pos, Player player, ItemStack stack, int slot) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof BEntity entity))
            return;
        if (slot < 0) {
            if (entity.isBase && stack.isEmpty() && !player.isShiftKeyDown()) {
                var state = entity.data();
                // 先结算当前时间，再切换开关，停机不会丢失已付费时长。
                if (!state.enabled) {
                    state.power(level.getDayTime());
                    // 空罐不能开启；已付费且尚未消耗完的溶液仍允许继续使用。
                    if (state.solution == null || (state.stored() == 0 && state.paidUntil <= level.getDayTime()))
                        return;
                } else state.lastTime = level.getDayTime();
                state.enabled = !state.enabled;
                entity.setChanged();
                player.displayClientMessage(Component.translatable(state.enabled
                        ? "message.confluence.environmental_machine.enabled" : "message.confluence.environmental_machine.disabled"), true);
            }
            return;
        }
        var data = entity.data();
        if (data.solution != null && data.stored() == 0 && level.getDayTime() >= data.paidUntil)
            data.solution = null;
        if (stack.isEmpty() && player.isShiftKeyDown()) {
            ItemStack taken = entity.removeItem(slot, data.slots[slot]);
            if (!player.addItem(taken)) player.drop(taken, false);
        } else if (!stack.isEmpty()) {
            Solution solution = Solution.of(stack);
            if (solution == null || (solution.advanced && !world) || (data.solution != null && data.solution != solution)) {
                player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.incompatible"), true);
                return;
            }
            int amount = Math.min(stack.getCount(), EnvironmentalMachines.CAPACITY - data.slots[slot]);
            if (amount > 0) {
                data.solution = solution;
                data.slots[slot] += amount;
                if (!player.getAbilities().instabuild) stack.shrink(amount);
                entity.setChanged();
            }
        }
        player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.stock",
                data.solution == null ? Component.translatable("message.confluence.environmental_machine.empty") : data.solution.item().getDescription(), data.slots[slot]), true);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (state.is(replacement.getBlock())) return;
        if (level instanceof ServerLevel server) {
            BlockPos basePos = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE ? pos : pos.below();
            var data = EnvironmentalMachines.INSTANCE.remove(server, basePos);
            if (data != null && data.solution != null) {
                int remaining = data.stored();
                while (remaining > 0) {
                    ItemStack stack = new ItemStack(data.solution.item());
                    int amount = Math.min(remaining, stack.getMaxStackSize());
                    stack.setCount(amount);
                    popResource(level, pos, stack);
                    remaining -= amount;
                }
            }
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity, WorldlyContainer {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;
        public Solution solution;
        public final int[] stored = new int[3];
        public int activeSlot = -1;
        public long cycle;
        public boolean enabled;

        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.ENVIRONMENTAL_MACHINE_ENTITY.get(), pos, state);
            isBase = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE;
        }

        public BlockPos basePos() {return isBase ? worldPosition : worldPosition.below();}

        public EnvironmentalMachines.State data() {
            return EnvironmentalMachines.INSTANCE.get((ServerLevel) level, basePos(), ((EnvironmentalMachineBlock) getBlockState().getBlock()).world);
        }

        public void refresh() {
            if (level instanceof ServerLevel) {
                var data = data();
                solution = data.solution;
                System.arraycopy(data.slots, 0, stored, 0, 3);
                activeSlot = data.activeSlot;
                cycle = data.cycle;
                enabled = data.enabled;
            }
        }

        @Override
        public void onLoad() {
            super.onLoad();
            refresh();
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (level instanceof ServerLevel server) EnvironmentalMachines.sync(server, basePos());
        }

        // 库存和当前已付费的溶液均属于对应槽位，动画与存放数量分开控制。
        public boolean hasSolutionInSlot(int slot) {
            return solution != null && (stored[slot] > 0 || activeSlot == slot);
        }

        public boolean isWorking() {
            return enabled && solution != null && (stored[0] > 0 || stored[1] > 0 || stored[2] > 0 || activeSlot >= 0);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            if (!isBase) return;
            String[] sides = {"left", "right", "back"};
            for (int slot = 0; slot < sides.length; slot++) {
                int index = slot;
                String side = sides[slot];
                RawAnimation idle = RawAnimation.begin().thenPlayAndHold("slot_" + side + "_empty");
                // 每实际消耗一个溶液才播放一轮，库存数量不参与动画进度。
                RawAnimation consume = RawAnimation.begin().thenPlay(side + "_inserting")
                        .thenPlay(side + "_insert").thenPlay(side + "_opening")
                        .thenPlay(side + "_open").thenPlay(side + "_closing")
                        .thenPlayAndHold(side + "_close");
                long[] playedCycle = {0};
                controllers.add(new AnimationController<>(this, "slot_" + side, 0, state -> {
                    var controller = state.getController();
                    controller.setAnimationSpeed(enabled ? 1 : 0);
                    if (enabled && activeSlot == index && cycle > playedCycle[0]) {
                        playedCycle[0] = cycle;
                        controller.forceAnimationReset();
                        return state.setAndContinue(consume);
                    }
                    return state.setAndContinue(playedCycle[0] > 0 ? consume : idle);
                }));
            }
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

        // 更新包只传渲染所需的信息；库存的唯一持久化来源仍是 IGlobalData。
        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
            refresh();
            CompoundTag tag = new CompoundTag();
            if (solution != null) tag.putString("Solution", solution.id());
            tag.putBoolean("Enabled", enabled);
            tag.putLong("Cycle", cycle);
            tag.putIntArray("Stored", stored);
            tag.putInt("ActiveSlot", activeSlot);
            return tag;
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            enabled = tag.getBoolean("Enabled");
            solution = null;
            try {
                if (tag.contains("Solution"))
                    solution = Solution.valueOf(tag.getString("Solution").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {}
            int[] values = tag.getIntArray("Stored");
            Arrays.fill(stored, 0);
            System.arraycopy(values, 0, stored, 0, Math.min(3, values.length));
            cycle = tag.getLong("Cycle");
            activeSlot = tag.contains("ActiveSlot") ? tag.getInt("ActiveSlot") : -1;
        }

        @Override
        public int getContainerSize() {return 3;}

        @Override
        public int getMaxStackSize() {return EnvironmentalMachines.CAPACITY;}

        @Override
        public boolean isEmpty() {return getItem(0).isEmpty() && getItem(1).isEmpty() && getItem(2).isEmpty();}

        @Override
        public ItemStack getItem(int slot) {
            if (slot < 0 || slot >= 3 || !(level instanceof ServerLevel)) return ItemStack.EMPTY;
            var data = data();
            return data.solution == null || data.slots[slot] == 0 ? ItemStack.EMPTY : new ItemStack(data.solution.item(), data.slots[slot]);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = getItem(slot);
            if (amount <= 0 || stack.isEmpty()) return ItemStack.EMPTY;
            stack.setCount(Math.min(amount, stack.getCount()));
            data().slots[slot] -= stack.getCount();
            setChanged();
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {return removeItem(slot, EnvironmentalMachines.CAPACITY);}

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!(level instanceof ServerLevel) || slot < 0 || slot >= 3 || (!stack.isEmpty() && !canPlaceItem(slot, stack)))
                return;
            var data = data();
            if (!stack.isEmpty()) data.solution = Solution.of(stack);
            data.slots[slot] = Math.min(EnvironmentalMachines.CAPACITY, stack.getCount());
            setChanged();
        }

        @Override
        public boolean stillValid(Player player) {return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64;}

        @Override
        public void clearContent() {
            if (level instanceof ServerLevel) {
                Arrays.fill(data().slots, 0);
                setChanged();
            }
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            if (slot < 0 || slot >= 3 || !(level instanceof ServerLevel)) return false;
            Solution incoming = Solution.of(stack);
            var data = data();
            boolean empty = data.stored() == 0 && level.getDayTime() >= data.paidUntil;
            return incoming != null && (!incoming.advanced || ((EnvironmentalMachineBlock) getBlockState().getBlock()).world)
                    && (data.solution == null || empty || data.solution == incoming);
        }

        @Override
        public int[] getSlotsForFace(Direction direction) {return new int[]{0, 1, 2};}

        @Override
        public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {return canPlaceItem(slot, stack);}

        @Override
        public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {return slot >= 0 && slot < 3;}
    }

    public static class BItem extends TooltipBlockItem implements GeoItem {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        public BItem(EnvironmentalMachineBlock block) {super(block, new Properties(), ModRarity.LIME, List.of(Component.translatable(block.world ? "tooltip.confluence.terraformer" : "tooltip.confluence.clentaminator")));}

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(EnvironmentalMachineRenderer.itemRenderer(((EnvironmentalMachineBlock) getBlock()).world));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
    }
}

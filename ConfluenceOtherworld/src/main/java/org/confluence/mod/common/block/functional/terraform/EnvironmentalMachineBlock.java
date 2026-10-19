package org.confluence.mod.common.block.functional.terraform;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipBlockItem;
import org.confluence.mod.client.renderer.block.EnvironmentalMachineRenderer;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.data.saved.EnvironmentalMachines;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Consumer;

// 玩家和漏斗共用逐槽库存；允许跨槽混装，混装会停机并在尝试启动时引发事故。
public class EnvironmentalMachineBlock extends HorizontalDirectionalWithVerticalTwoPartBlock implements EntityBlock {
    private static final int SLOT_COUNT = EnvironmentalMachines.SLOT_COUNT;
    private static final float MODEL_UNITS = 16;
    private static final float BLOCK_STRENGTH = 3.5F;
    private static final String[] SLOT_NAMES = {"left", "right", "back"};
    private static final double MODEL_CENTER = MODEL_UNITS / 2;
    private static final double HIT_TOLERANCE = 0.001;
    private static final double INTERACTION_DISTANCE_SQUARED = 64;
    private static final float SWITCH_VOLUME = 0.5F;
    private static final float SWITCH_ON_PITCH = 0.6F;
    private static final float SWITCH_OFF_PITCH = 0.5F;
    // 爆炸强度与末地水晶一致，污染范围和转换概率可独立调整。
    public static final int FAILURE_RADIUS = 8;
    public static final float FAILURE_CONVERSION_CHANCE = 0.80F;
    public static final float FAILURE_EXPLOSION_POWER = 6.0F;
    public static final int FAILURE_MIN_SOLUTION_COST = 5;
    public static final int FAILURE_MAX_SOLUTION_COST = 20;
    private static final VoxelShape BASE_SHAPE = box(0, 0, 0, MODEL_UNITS, MODEL_UNITS, MODEL_UNITS);
    // 三个插入口固定在机身顶部，装填前后使用同一组交互范围。
    private static final double[][] TANK_BOUNDS = {
            {-9.5, 13, -2, -5.5, 16, 2}, {5.5, 13, -2, 9.5, 16, 2}, {-2, 13, 5.5, 2, 16, 9.5}
    };
    private static final VoxelShape[] MACHINE_SHAPES = new VoxelShape[4];

    static {
        // 悬空罐体只用于动画；三个插入口保持固定交互区域。
        for (Direction front : Direction.Plane.HORIZONTAL) {
            VoxelShape shape = BASE_SHAPE;
            for (int slot = 0; slot < SLOT_COUNT; slot++) {
                AABB tank = tankBounds(front, slot);
                shape = Shapes.or(shape, box(tank.minX, tank.minY, tank.minZ, tank.maxX, tank.maxY, tank.maxZ));
            }
            MACHINE_SHAPES[front.get2DDataValue()] = shape;
        }
    }

    private static AABB tankBounds(Direction front, int slot) {
        double[] bounds = TANK_BOUNDS[slot];
        Direction right = front.getClockWise();
        double minX = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (int x : new int[]{0, 3}) {
            for (int z : new int[]{2, 5}) {
                // Geo 加载时会还原模型的 X 轴；交互区域使用还原后的同名槽位坐标。
                double px = MODEL_CENTER + bounds[x] * right.getStepX() - bounds[z] * front.getStepX();
                double pz = MODEL_CENTER + bounds[x] * right.getStepZ() - bounds[z] * front.getStepZ();
                minX = Math.min(minX, px);
                maxX = Math.max(maxX, px);
                minZ = Math.min(minZ, pz);
                maxZ = Math.max(maxZ, pz);
            }
        }
        return new AABB(minX, bounds[1], minZ, maxX, bounds[4], maxZ);
    }

    public final boolean world;

    public EnvironmentalMachineBlock(boolean world) {
        super(Properties.of().strength(BLOCK_STRENGTH).dynamicShape().noOcclusion().pushReaction(PushReaction.BLOCK));
        this.world = world;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == StateProperties.VerticalTwoPart.BASE
                ? MACHINE_SHAPES[state.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue()] : Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {return new BEntity(pos, state);}

    @Override
    public RenderShape getRenderShape(BlockState state) {return RenderShape.ENTITYBLOCK_ANIMATED;}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND)
            interact(level, pos, player, player.getItemInHand(hand), slot(state, pos, hit));
        return InteractionResult.SUCCESS;
    }

    // 三个插入口位置固定，上下两半都以底座坐标计算。
    private static int slot(BlockState state, BlockPos pos, BlockHitResult hit) {
        BlockPos base = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE ? pos : pos.below();
        double x = (hit.getLocation().x - base.getX()) * MODEL_UNITS;
        double y = (hit.getLocation().y - base.getY()) * MODEL_UNITS;
        double z = (hit.getLocation().z - base.getZ()) * MODEL_UNITS;
        Direction front = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            AABB bounds = tankBounds(front, slot).inflate(HIT_TOLERANCE);
            if (bounds.contains(x, y, z)) return slot;
        }
        return -1;
    }

    // 每个槽只存放一种溶液；跨槽种类不同则停机，由启动操作处理混合反应。
    private void interact(Level level, BlockPos pos, Player player, ItemStack stack, int slot) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof BEntity entity))
            return;
        // 上半部的命中也统一使用底座库存，避免存入和取出操作落在不同方块实体上。
        if (!entity.isBase) {
            if (!(level.getBlockEntity(entity.basePos()) instanceof BEntity base)) return;
            entity = base;
        }
        if (!entity.stillValid(player) || !player.mayBuild()) return;
        if (slot < 0) {
            if (stack.isEmpty() && !player.isShiftKeyDown()) {
                var data = entity.data();
                if (!data.enabled && data.hasMixedSolutions()) {
                    failMixedSolutions(server, entity.basePos(), player);
                    return;
                }
                if (data.setEnabled(!data.enabled, level.getDayTime())) {
                    entity.setChanged();
                    level.playSound(null, entity.basePos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS,
                            SWITCH_VOLUME, data.enabled ? SWITCH_ON_PITCH : SWITCH_OFF_PITCH);
                    player.displayClientMessage(Component.translatable(data.enabled
                            ? "message.confluence.environmental_machine.enabled" : "message.confluence.environmental_machine.disabled"), true);
                }
            }
            return;
        }
        var data = entity.data();
        if (player.isShiftKeyDown()) {
            ItemStack taken = entity.removeItem(slot, data.slots[slot]);
            if (taken.isEmpty()) return;
            if (!player.addItem(taken)) player.drop(taken, false);
        } else if (!stack.isEmpty()) {
            Solution solution = Solution.of(stack);
            if (solution != null && solution.advanced && !world) {
                player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.requires_terraformer"), true);
                return;
            }
            if (solution == null || (data.slots[slot] > 0 && data.slotSolutions[slot] != solution)) {
                player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.incompatible"), true);
                return;
            }
            int amount = Math.min(stack.getCount(), EnvironmentalMachines.CAPACITY - data.slots[slot]);
            if (amount > 0) {
                data.slotSolutions[slot] = solution;
                data.slots[slot] += amount;
                if (!player.getAbilities().instabuild) stack.shrink(amount);
                entity.setChanged();
            }
        }
        player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.stock",
                data.slotSolutions[slot] == null ? Component.translatable("message.confluence.environmental_machine.empty")
                        : data.slotSolutions[slot].item().getDescription(), data.slots[slot]), true);
    }

    // 混装事故消耗参与反应的溶液并污染地形，爆炸保留机器的上下两部分。
    private static void failMixedSolutions(ServerLevel level, BlockPos origin, Player player) {
        if (!CommonConfigs.ENVIRONMENTAL_MACHINE_MIXED_EXPLOSION.get()) {
            player.displayClientMessage(Component.translatable("message.confluence.environmental_machine.mixed"), true);
            return;
        }
        if (!(level.getBlockEntity(origin) instanceof BEntity entity)) return;
        var data = entity.data();
        EnumSet<Solution> solutions = EnumSet.noneOf(Solution.class);
        // 已付费但未耗尽的溶液同样参与反应，保持与混装判定一致。
        if (data.paidUntil > data.lastTime && data.solution != null) solutions.add(data.solution);
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (data.slots[slot] > 0 && data.slotSolutions[slot] != null)
                solutions.add(data.slotSolutions[slot]);
        }
        if (solutions.size() < 2) return;
        List<BlockState> results = solutions.stream().map(Solution::failureBlock).distinct().toList();
        data.setEnabled(false, level.getDayTime());
        // 每种溶液独立抽取消耗量，同种溶液跨槽扣除，不足时全部耗尽。
        for (Solution solution : solutions) {
            int remaining = level.random.nextInt(FAILURE_MAX_SOLUTION_COST - FAILURE_MIN_SOLUTION_COST + 1)
                    + FAILURE_MIN_SOLUTION_COST;
            for (int slot = 0; slot < SLOT_COUNT && remaining > 0; slot++) {
                if (data.slotSolutions[slot] != solution) continue;
                int consumed = Math.min(remaining, data.slots[slot]);
                data.slots[slot] -= consumed;
                remaining -= consumed;
            }
        }
        // 已注入的剩余液体随反应失效，避免事故后仍保留已付费的工作时长。
        data.paidUntil = Long.MIN_VALUE;
        data.activeSlot = -1;
        data.solution = null;
        data.inventoryChanged(level.getDayTime());
        entity.setChanged();
        level.explode(player, null, new ExplosionDamageCalculator() {
                    // 爆炸从机身内发出；机器不能吸收爆炸射线，否则会把周围的爆炸范围一起挡住。
                    @Override
                    public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, FluidState fluid) {
                        return state.getBlock() instanceof EnvironmentalMachineBlock ? Optional.of(0.0F)
                                : super.getBlockExplosionResistance(explosion, world, pos, state, fluid);
                    }

                    @Override
                    public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
                        return !(state.getBlock() instanceof EnvironmentalMachineBlock)
                                && super.shouldBlockExplode(explosion, world, pos, state, power);
                    }
                }, origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5,
                FAILURE_EXPLOSION_POWER, false, Level.ExplosionInteraction.BLOCK);
        for (BlockPos target : BlockPos.betweenClosed(origin.offset(-FAILURE_RADIUS, -FAILURE_RADIUS, -FAILURE_RADIUS),
                origin.offset(FAILURE_RADIUS, FAILURE_RADIUS, FAILURE_RADIUS))) {
            if (origin.distSqr(target) > FAILURE_RADIUS * FAILURE_RADIUS || !level.hasChunkAt(target)
                    || !level.getWorldBorder().isWithinBounds(target) || level.random.nextFloat() >= FAILURE_CONVERSION_CHANCE)
                continue;
            BlockState source = level.getBlockState(target);
            if (source.isAir() || source.hasBlockEntity() || source.getBlock() instanceof EnvironmentalMachineBlock
                    || !source.getFluidState().isEmpty() || source.getDestroySpeed(level, target) < 0)
                continue;
            level.setBlock(target, results.get(level.random.nextInt(results.size())), 2);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (state.is(replacement.getBlock())) return;
        if (level instanceof ServerLevel server) {
            BlockPos basePos = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE ? pos : pos.below();
            var data = EnvironmentalMachines.INSTANCE.remove(server, basePos);
            if (data != null) {
                for (int slot = 0; slot < SLOT_COUNT; slot++) {
                    if (data.slotSolutions[slot] != null && data.slots[slot] > 0)
                        popResource(level, basePos, new ItemStack(data.slotSolutions[slot].item(), data.slots[slot]));
                }
            }
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    public static class BEntity extends BlockEntity implements GeoBlockEntity, WorldlyContainer {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
        public final boolean isBase;
        public Solution solution;
        public final Solution[] slotSolutions = new Solution[SLOT_COUNT];
        public boolean mixedSolutions;
        public boolean mixedExplosionEnabled = true;
        public final int[] stored = new int[SLOT_COUNT];
        public int activeSlot = -1;
        public boolean enabled;
        public int fuelProgress;
        public int rangeChunks;

        public BEntity(BlockPos pos, BlockState state) {
            super(FunctionalBlocks.ENVIRONMENTAL_MACHINE_ENTITY.get(), pos, state);
            isBase = state.getValue(PART) == StateProperties.VerticalTwoPart.BASE;
            rangeChunks = ((EnvironmentalMachineBlock) state.getBlock()).world
                    ? EnvironmentalMachines.DEFAULT_TERRAFORMER_RANGE_CHUNKS : EnvironmentalMachines.DEFAULT_CLENTAMINATOR_RANGE_CHUNKS;
        }

        public BlockPos basePos() {return isBase ? worldPosition : worldPosition.below();}

        public EnvironmentalMachines.State data() {
            return EnvironmentalMachines.INSTANCE.get((ServerLevel) level, basePos(), ((EnvironmentalMachineBlock) getBlockState().getBlock()).world);
        }

        public void refresh() {
            if (level instanceof ServerLevel) {
                var data = data();
                solution = data.solution;
                System.arraycopy(data.slotSolutions, 0, slotSolutions, 0, SLOT_COUNT);
                mixedSolutions = data.hasMixedSolutions();
                mixedExplosionEnabled = CommonConfigs.ENVIRONMENTAL_MACHINE_MIXED_EXPLOSION.get();
                System.arraycopy(data.slots, 0, stored, 0, SLOT_COUNT);
                activeSlot = data.activeSlot;
                enabled = data.enabled;
                long duration = EnvironmentalMachines.DAY_TICKS / (data.world
                        ? EnvironmentalMachines.TERRAFORMER_DAILY_COST : EnvironmentalMachines.CLENTAMINATOR_DAILY_COST);
                rangeChunks = EnvironmentalMachines.rangeChunks(data.world);
                long remaining = data.solution == null || data.paidUntil <= data.lastTime ? 0 : data.paidUntil - data.lastTime;
                fuelProgress = (int) Math.min(EnvironmentalMachines.FUEL_PROGRESS_MAX,
                        remaining * EnvironmentalMachines.FUEL_PROGRESS_MAX / duration);
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
            if (level instanceof ServerLevel server) {
                data().inventoryChanged(level.getDayTime());
                EnvironmentalMachines.sync(server, basePos());
            }
        }

        // 罐体仅反映该槽的实际库存；已经注入机器的工作余量不占用储液槽。
        public boolean hasSolutionInSlot(int slot) {
            return slotSolutions[slot] != null && stored[slot] > 0;
        }

        public boolean isWorking() {
            return enabled && !mixedSolutions && solution != null && (stored[0] > 0 || stored[1] > 0 || stored[2] > 0 || activeSlot >= 0);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
            if (!isBase) return;
            for (int slot = 0; slot < SLOT_COUNT; slot++) {
                int index = slot;
                String side = SLOT_NAMES[slot];
                RawAnimation idle = RawAnimation.begin().thenPlayAndHold(side + "_idle");
                RawAnimation inserted = RawAnimation.begin().thenPlay(side + "_inserting")
                        .thenPlayAndHold(side + "_insert");
                // 装入即播放该槽插入动画，随后保持；补充库存、启停和扣量均不重播。
                controllers.add(new AnimationController<>(this, "slot_" + side, 0,
                        state -> state.setAndContinue(hasSolutionInSlot(index) ? inserted : idle)));
            }
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

        // 更新包只传渲染所需的信息；库存的唯一持久化来源仍是 IGlobalData。
        @Override
        public CompoundTag getUpdateTag() {
            refresh();
            CompoundTag tag = new CompoundTag();
            if (solution != null) tag.putString("Solution", solution.id());
            tag.putBoolean("Enabled", enabled);
            tag.putBoolean("MixedSolutions", mixedSolutions);
            tag.putBoolean("MixedExplosionEnabled", mixedExplosionEnabled);
            ListTag solutions = new ListTag();
            for (Solution solution : slotSolutions)
                solutions.add(StringTag.valueOf(solution == null ? "" : solution.id()));
            tag.put("SlotSolutions", solutions);
            tag.putInt("FuelProgress", fuelProgress);
            tag.putInt("RangeChunks", rangeChunks);
            tag.putIntArray("Stored", stored);
            tag.putInt("ActiveSlot", activeSlot);
            return tag;
        }

        @Override
        public ClientboundBlockEntityDataPacket getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            enabled = tag.getBoolean("Enabled");
            mixedSolutions = tag.getBoolean("MixedSolutions");
            mixedExplosionEnabled = !tag.contains("MixedExplosionEnabled") || tag.getBoolean("MixedExplosionEnabled");
            Arrays.fill(slotSolutions, null);
            ListTag solutions = tag.getList("SlotSolutions", Tag.TAG_STRING);
            for (int slot = 0; slot < Math.min(SLOT_COUNT, solutions.size()); slot++) {
                try {
                    slotSolutions[slot] = Solution.valueOf(solutions.getString(slot).toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {}
            }
            fuelProgress = Mth.clamp(tag.getInt("FuelProgress"), 0, EnvironmentalMachines.FUEL_PROGRESS_MAX);
            if (tag.contains("RangeChunks")) rangeChunks = Mth.clamp(tag.getInt("RangeChunks"),
                    EnvironmentalMachines.MIN_RANGE_CHUNKS, EnvironmentalMachines.MAX_RANGE_CHUNKS);
            solution = null;
            try {
                if (tag.contains("Solution"))
                    solution = Solution.valueOf(tag.getString("Solution").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {}
            int[] values = tag.getIntArray("Stored");
            Arrays.fill(stored, 0);
            System.arraycopy(values, 0, stored, 0, Math.min(SLOT_COUNT, values.length));
            activeSlot = tag.contains("ActiveSlot") ? tag.getInt("ActiveSlot") : -1;
        }

        @Override
        public int getContainerSize() {return SLOT_COUNT;}

        @Override
        public int getMaxStackSize() {return EnvironmentalMachines.CAPACITY;}

        @Override
        public boolean isEmpty() {return getItem(0).isEmpty() && getItem(1).isEmpty() && getItem(2).isEmpty();}

        @Override
        public ItemStack getItem(int slot) {
            if (slot < 0 || slot >= SLOT_COUNT || !(level instanceof ServerLevel))
                return ItemStack.EMPTY;
            var data = data();
            return data.slotSolutions[slot] == null || data.slots[slot] == 0 ? ItemStack.EMPTY : new ItemStack(data.slotSolutions[slot].item(), data.slots[slot]);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = getItem(slot);
            if (amount <= 0 || stack.isEmpty()) return ItemStack.EMPTY;
            stack.setCount(Math.min(amount, stack.getCount()));
            var data = data();
            data.slots[slot] -= stack.getCount();
            setChanged();
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {return removeItem(slot, EnvironmentalMachines.CAPACITY);}

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (!(level instanceof ServerLevel) || slot < 0 || slot >= SLOT_COUNT || (!stack.isEmpty() && !canPlaceItem(slot, stack)))
                return;
            var data = data();
            data.slotSolutions[slot] = stack.isEmpty() ? null : Solution.of(stack);
            data.slots[slot] = Math.min(EnvironmentalMachines.CAPACITY, stack.getCount());
            setChanged();
        }

        @Override
        public boolean stillValid(Player player) {return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= INTERACTION_DISTANCE_SQUARED;}

        @Override
        public void clearContent() {
            if (level instanceof ServerLevel) {
                Arrays.fill(data().slots, 0);
                setChanged();
            }
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            if (slot < 0 || slot >= SLOT_COUNT || !(level instanceof ServerLevel)) return false;
            Solution incoming = Solution.of(stack);
            var data = data();
            return incoming != null && (!incoming.advanced || ((EnvironmentalMachineBlock) getBlockState().getBlock()).world)
                    && (data.slots[slot] == 0 || data.slotSolutions[slot] == incoming);
        }

        @Override
        public int[] getSlotsForFace(Direction direction) {return new int[]{0, 1, 2};}

        @Override
        public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {return canPlaceItem(slot, stack);}

        @Override
        public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {return slot >= 0 && slot < SLOT_COUNT;}
    }

    public static class BItem extends TooltipBlockItem implements GeoItem {
        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        public BItem(EnvironmentalMachineBlock block) {
            super(block, new Properties(), ModRarity.LIME, List.of(Component.translatable(block.world
                    ? "tooltip.confluence.terraformer" : "tooltip.confluence.clentaminator").withStyle(ChatFormatting.GRAY)));
        }

        // 用途、消耗和操作分别成行，避免整段描述把提示框撑满屏幕。
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            boolean world = ((EnvironmentalMachineBlock) getBlock()).world;
            tooltip.add(Component.translatable("tooltip.confluence.environmental_machine.consumption",
                    world ? EnvironmentalMachines.TERRAFORMER_DAILY_COST : EnvironmentalMachines.CLENTAMINATOR_DAILY_COST).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.environmental_machine.insert").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.environmental_machine.withdraw").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.confluence.environmental_machine.toggle").withStyle(ChatFormatting.GRAY));
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(EnvironmentalMachineRenderer.itemRenderer(((EnvironmentalMachineBlock) getBlock()).world));
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
    }
}

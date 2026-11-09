package org.confluence.mod.integration.flowingfluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.Confluence;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/// Flowing Fluids（有限液体模组，可选）的桥：只用它公开的工具方法，反射调用，编译时不依赖它。
///
/// 均匀摊开：Flowing Fluids 每个液体刻只和“一个”随机挑出来的邻格对半分水，平地上倒一桶水会一阵一阵地随机往某个方向跑，
/// 各个方向的量也不一样。这里在平地上（4 格内没有可以流下去的落差）改成和所有比自己低的邻格一起平均分，
/// 除不尽的留在自己格里，所以水总是对称地向四周摊开，摊不开了就静止，不再来回抖。
/// 附近有落差时仍交给 Flowing Fluids 原来的逻辑（它会把水引向坑和边缘）。
public final class FlowingFluidsBridge {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final int DROP_SEARCH = 4;
    private static boolean resolved;
    private static MethodHandle canFlow;
    private static MethodHandle setAmount;

    private FlowingFluidsBridge() {}

    private static boolean resolve() {
        if (resolved) return canFlow != null && setAmount != null;
        resolved = true;
        try {
            Class<?> utils = Class.forName("traben.flowing_fluids.FFFluidUtils");
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            canFlow = lookup.findStatic(utils, "canFluidFlowFromPosToDirection", MethodType.methodType(boolean.class,
                    FlowingFluid.class, int.class, LevelAccessor.class, BlockPos.class, Direction.class));
            setAmount = lookup.findStatic(utils, "setFluidStateAtPosToNewAmount", MethodType.methodType(boolean.class,
                    LevelAccessor.class, BlockPos.class, Fluid.class, int.class));
        } catch (ReflectiveOperationException | LinkageError e) {
            Confluence.LOGGER.warn("Flowing Fluids found, but its utilities changed; even spreading is off", e);
            canFlow = null;
            setAmount = null;
        }
        return canFlow != null && setAmount != null;
    }

    private static boolean canFlow(FlowingFluid fluid, int amount, Level level, BlockPos from, Direction direction) {
        try {
            return (boolean) canFlow.invokeExact(fluid, amount, (LevelAccessor) level, from, direction);
        } catch (Throwable e) {
            return false;
        }
    }

    private static void setAmount(Level level, BlockPos pos, Fluid fluid, int amount) {
        try {
            boolean ignored = (boolean) setAmount.invokeExact((LevelAccessor) level, pos, fluid, amount);
        } catch (Throwable ignored) {
        }
    }

    /// 平地上的均匀摊开。返回 DEFER 表示不处理（交回 Flowing Fluids）；null 表示已经平了、没有流动；否则返回流向的一个方向
    public static Direction spreadEvenly(FlowingFluid fluid, Level level, BlockPos pos, FluidState state, int amount, BlockState blockState) {
        if (amount <= 1 || !(blockState.getBlock() instanceof LiquidBlock) || !resolve()) return DEFER;
        Fluid same = fluid.getSource();
        int[] amounts = new int[4];
        boolean[] open = new boolean[4];
        for (int i = 0; i < 4; i++) {
            Direction direction = HORIZONTAL[i];
            BlockPos next = pos.relative(direction);
            BlockState nextState = level.getBlockState(next);
            if (!canFlow(fluid, amount, level, pos, direction)) continue;
            FluidState nextFluid = nextState.getFluidState();
            // 只处理空气和同种液体；含水方块、可替换的植物等特殊情况交回 Flowing Fluids
            if (!nextState.isAir() && !(nextState.getBlock() instanceof LiquidBlock && nextFluid.getType().isSame(same))) return DEFER;
            if (hasDropAhead(fluid, level, pos, direction)) return DEFER;
            open[i] = true;
            amounts[i] = nextFluid.getType().isSame(same) ? nextFluid.getAmount() : 0;
        }
        // 和所有比自己少至少 2 的邻格一起平均（差 1 的分不开，就此静止）
        int total = amount, count = 1;
        boolean[] share = new boolean[4];
        for (int i = 0; i < 4; i++) {
            if (open[i] && amounts[i] <= amount - 2) {
                share[i] = true;
                total += amounts[i];
                count++;
            }
        }
        if (count == 1) return null;
        int each = total / count;
        int self = total - each * (count - 1);
        Direction flowed = null;
        for (int i = 0; i < 4; i++) {
            if (share[i] && each > amounts[i]) {
                setAmount(level, pos.relative(HORIZONTAL[i]), same, each);
                if (flowed == null) flowed = HORIZONTAL[i];
            }
        }
        if (self != amount) setAmount(level, pos, same, self);
        return flowed;
    }

    /// “交回 Flowing Fluids”的标记：侧向流动只会返回水平方向或 null，用 DOWN 不会和它们混淆
    public static final Direction DEFER = Direction.DOWN;

    /// 从 pos 往 direction 走最多 4 格：只要路上有一格下面是空的（或者没满的同种液体），就算前方有落差
    private static boolean hasDropAhead(FlowingFluid fluid, Level level, BlockPos pos, Direction direction) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        Fluid same = fluid.getSource();
        for (int step = 1; step <= DROP_SEARCH; step++) {
            cursor.move(direction);
            BlockState here = level.getBlockState(cursor);
            boolean passable = here.isAir() || (here.getBlock() instanceof LiquidBlock && here.getFluidState().getType().isSame(same));
            if (!passable) return false;
            BlockState below = level.getBlockState(cursor.below());
            FluidState belowFluid = below.getFluidState();
            if (below.isAir() || (below.getBlock() instanceof LiquidBlock && belowFluid.getType().isSame(same) && belowFluid.getAmount() < 8)) {
                return true;
            }
        }
        return false;
    }
}

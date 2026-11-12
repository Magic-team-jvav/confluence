package org.confluence.mod.common.event.game;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.block.natural.BranchesBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.entity.FallingTreeEntity;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/// Terraria 式伐木，带“支撑”：用斧头砍原木后，失去支撑的那部分树整体倒下（FallingTreeEntity），落地后碎成掉落物。
///
/// 砍的这一格照原版破坏、掉落；然后看树干断没断：
/// - 砍口这一层的树干截面（面相邻的竖直原木 + 之前砍出的缺口）剩下的不超过一半 → 断了：截面以上同一种木头的树倒下，
///   这一层剩下的几格跟着倒；挨着的别的树干（下面一直是原木、通到这一层但不在截面里）不跟着倒。2×2 砍 2 格、3×3 砍 5 格就倒；
///   这一层有柱直接立在地面上（台阶上的树），树还站着，不算断；
/// - 没断 → 按支撑算：原木直接立在实心的非原木方块上（地面、根扎进的土）就是“着地”的，和着地原木连着（含斜角）的都有支撑，
///   没有支撑的（砍断的树枝）倒下。所以在树旁边放一根原木再砍掉，树本身还立在地上，不会倒。
/// 倒下的部分带走离它们最近的树叶；只剩砍掉的那一格撑着的树先坐到砍口上，再倒。
/// 其他规则：潜行（Shift）或徒手只砍一格；没有天然树叶（玩家放的树叶是持久化的）的原木结构是建筑，不伐；
/// 生命树、生命桃花心木（Terraria 的“活树”）永远不会被整棵伐倒。
@EventBusSubscriber(modid = Confluence.MODID)
public final class TreeFelling {
    private static final int MAX_LOGS = 2500, MAX_LEAVES = 8000, MAX_RADIUS = 24, MAX_HEIGHT = 96;
    /// 树叶离原木最远多少格（按相邻方块走）；猴面包树等自定义地物放的树叶 DISTANCE 一直是 7，树冠可以离树干很远
    private static final int LEAF_REACH = 9;
    /// 至少要有这么多天然树叶（或 1 个枝杈）才算树
    private static final int MIN_NATURAL_LEAVES = 3;
    /// 树干截面最多多少格；砍过的位置记多久（tick，约 5 分钟）
    private static final int MAX_SECTION = 128;
    private static final long CHOP_MEMORY = 6000L;

    private TreeFelling() {}

    /// 最低优先级：别的处理（环境保护、祭坛等）取消了破坏就不伐
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        BlockState state = event.getState();
        if (!state.is(BlockTags.LOGS)) return;
        ItemStack tool = player.getMainHandItem();
        if (CommonConfigs.TREE_FELLING.get() && !player.isShiftKeyDown() && !isLivingTree(state)
                && (tool.canPerformAction(ItemAbilities.AXE_DIG) || tool.getDestroySpeed(state) > 1.5F)) {
            // 砍的这一格照原版破坏、掉落；失去支撑的部分整体倒下
            fell(level, player, event.getPos(), tool);
        }
        // 记住砍过的地方：之后在同一层再砍，这里算树干上的缺口
        remember(level, event.getPos());
    }

    // ---------------------------------------------------------------- 砍过的位置

    /// 每个维度最近砍掉的原木位置 → 游戏时间；只在服务端线程用
    private static final java.util.Map<net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level>, Long2LongOpenHashMap> CHOPPED = new java.util.HashMap<>();

    private static Long2LongOpenHashMap chopped(ServerLevel level) {
        return CHOPPED.computeIfAbsent(level.dimension(), key -> new Long2LongOpenHashMap());
    }

    private static void remember(ServerLevel level, BlockPos pos) {
        Long2LongOpenHashMap map = chopped(level);
        long now = level.getGameTime();
        if (map.size() > 4096) {
            map.long2LongEntrySet().removeIf(entry -> now - entry.getLongValue() > CHOP_MEMORY);
            if (map.size() > 4096) map.clear();
        }
        map.put(pos.asLong(), now);
    }

    /// 这根原木是不是挨着的另一棵树的树干：正下方一直是原木、通到砍口这一层，而那一格不在截面里
    private static boolean otherTrunk(LongOpenHashSet tree, LongOpenHashSet section, long key, int cutY) {
        int x = BlockPos.getX(key), z = BlockPos.getZ(key);
        for (int y = BlockPos.getY(key) - 1; y >= cutY; y--) {
            long below = BlockPos.asLong(x, y, z);
            if (!tree.contains(below)) return false;
            if (y == cutY) return !section.contains(below);
        }
        return false;
    }

    /// Terraria 的“活树”：生命树和生命桃花心木的木头与树叶
    private static boolean isLivingTree(BlockState state) {
        return inSet(state, NatureBlocks.LIVING_LOG_BLOCKS) || inSet(state, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS);
    }

    private static boolean inSet(BlockState state, LogBlockSet set) {
        Block block = state.getBlock();
        return (set.LOG != null && block == set.LOG.get()) || (set.WOOD != null && block == set.WOOD.get())
                || (set.STRIPPED_LOG != null && block == set.STRIPPED_LOG.get()) || (set.STRIPPED_WOOD != null && block == set.STRIPPED_WOOD.get())
                || (set.LEAVES != null && block == set.LEAVES.get());
    }

    private static boolean isLeaves(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.getBlock() instanceof LeavesBlock;
    }

    private static boolean fell(ServerLevel level, ServerPlayer player, BlockPos cut, ItemStack tool) {
        // 1) 和砍的这一格连着（含斜角）的所有原木
        LongOpenHashSet tree = new LongOpenHashSet();
        LongArrayList order = new LongArrayList();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        tree.add(cut.asLong());
        order.add(cut.asLong());
        queue.add(cut);
        LongOpenHashSet checked = new LongOpenHashSet();
        checked.add(cut.asLong());
        BlockPos.MutableBlockPos next = new BlockPos.MutableBlockPos();
        int minY = cut.getY(), maxY = cut.getY();
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        next.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                        if (!checked.add(next.asLong())) continue;
                        BlockState state = level.getBlockState(next);
                        if (isLivingTree(state)) return false;
                        if (!state.is(BlockTags.LOGS)) continue;
                        minY = Math.min(minY, next.getY());
                        maxY = Math.max(maxY, next.getY());
                        // 太大，多半不是树（或者是一片连在一起的建筑）
                        if (Math.abs(next.getX() - cut.getX()) > MAX_RADIUS || Math.abs(next.getZ() - cut.getZ()) > MAX_RADIUS
                                || maxY - minY > MAX_HEIGHT || tree.size() >= MAX_LOGS) return false;
                        tree.add(next.asLong());
                        order.add(next.asLong());
                        queue.add(next.immutable());
                    }
                }
            }
        }

        // 2) 砍口这一层的树干截面：从砍口出发，在这一层按面相邻（不含斜角，斜挨着的邻树不算）走过的
        //    - 竖着的原木（正上方或正下方也是原木；水平的树枝、贴地的根不算）；
        //    - 之前砍出来的缺口（记在 CHOPPED 里、现在空着的格子）。树干自然外凸出来的空位不算缺口，所以不规则的树干也按这一层真实的粗细算。
        //    剩下的原木（不算这一格）不超过截面的一半，就算断了
        List<BlockPos> falling = new ArrayList<>();
        int y = cut.getY();
        Long2LongOpenHashMap chopped = chopped(level);
        long now = level.getGameTime();
        LongOpenHashSet section = new LongOpenHashSet();
        LongArrayList rest = new LongArrayList();
        ArrayDeque<Long> fill = new ArrayDeque<>();
        section.add(cut.asLong());
        fill.add(cut.asLong());
        // 有的柱在这一层直接立在地面上（树干一半立在台阶上、一半往下多长一格）：树还站在地上，不算断，交给下面的支撑判断
        boolean standsOnGround = false, trunkAbove = tree.contains(cut.above().asLong());
        while (!fill.isEmpty() && section.size() < MAX_SECTION) {
            long key = fill.poll();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                int x = BlockPos.getX(key) + direction.getStepX(), z = BlockPos.getZ(key) + direction.getStepZ();
                long cell = BlockPos.asLong(x, y, z);
                if (section.contains(cell)) continue;
                boolean logAbove = tree.contains(BlockPos.asLong(x, y + 1, z));
                if (tree.contains(cell)) {
                    if (!logAbove && !tree.contains(BlockPos.asLong(x, y - 1, z))) continue;
                    section.add(cell);
                    rest.add(cell);
                    fill.add(cell);
                    trunkAbove |= logAbove;
                } else if (now - chopped.getOrDefault(cell, Long.MIN_VALUE / 2) < CHOP_MEMORY && level.getBlockState(BlockPos.of(cell)).isAir()) {
                    section.add(cell);
                    fill.add(cell);
                    trunkAbove |= logAbove;
                } else if (logAbove && grounded(level, BlockPos.of(cell).above())) {
                    standsOnGround = true;
                }
            }
        }
        // 倒向：粗树干按砍口决定 —— 从截面里剩下的原木（“合页”）指向砍掉的那一边，和真的伐树一样往开口那边倒；
        // 一格粗的树干、或者砍得前后对称看不出方向时，再按玩家的位置
        float notchX = 0.0F, notchZ = 0.0F;
        if (trunkAbove && !standsOnGround && rest.size() * 2 <= section.size()) {
            if (!rest.isEmpty()) {
                double restX = 0.0, restZ = 0.0, cutX = 0.0, cutZ = 0.0;
                int cuts = 0;
                LongOpenHashSet restSet = new LongOpenHashSet(rest);
                for (long key : section) {
                    if (restSet.contains(key)) {
                        restX += BlockPos.getX(key);
                        restZ += BlockPos.getZ(key);
                    } else {
                        cutX += BlockPos.getX(key);
                        cutZ += BlockPos.getZ(key);
                        cuts++;
                    }
                }
                notchX = (float) (cutX / cuts - restX / rest.size());
                notchZ = (float) (cutZ / cuts - restZ / rest.size());
            }
            // 断了：截面以上、同一种木头的整棵树倒下，这一层剩下的几格也跟着倒。
            // 往上走时不进入“别的树干”：一根原木下面一直是原木、一直通到砍口这一层，而那一格不在截面里 —— 那是挨着的另一棵树
            Block family = family(level.getBlockState(cut));
            LongOpenHashSet upper = new LongOpenHashSet();
            ArrayDeque<Long> walk = new ArrayDeque<>();
            for (long key : section) {
                long up = BlockPos.asLong(BlockPos.getX(key), y + 1, BlockPos.getZ(key));
                if (tree.contains(up) && upper.add(up)) walk.add(up);
            }
            while (!walk.isEmpty()) {
                BlockPos pos = BlockPos.of(walk.poll());
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            long key = BlockPos.asLong(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                            if (BlockPos.getY(key) <= y || !tree.contains(key) || upper.contains(key)) continue;
                            if (family(level.getBlockState(BlockPos.of(key))) != family) continue;
                            if (otherTrunk(tree, section, key, y)) continue;
                            upper.add(key);
                            walk.add(key);
                        }
                    }
                }
            }
            for (long key : order) {
                if (upper.contains(key)) falling.add(BlockPos.of(key));
            }
            for (long key : rest) falling.add(BlockPos.of(key));
        } else {
            // 3) 没断：按支撑算。从着地的原木出发，能连到的都有支撑；其余的（砍断的树枝、悬空的部分）倒下
            LongOpenHashSet supported = new LongOpenHashSet();
            ArrayDeque<Long> support = new ArrayDeque<>();
            for (long key : order) {
                if (key == cut.asLong()) continue;
                if (grounded(level, BlockPos.of(key))) {
                    supported.add(key);
                    support.add(key);
                }
            }
            while (!support.isEmpty()) {
                BlockPos pos = BlockPos.of(support.poll());
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            long key = BlockPos.asLong(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                            if (key != cut.asLong() && tree.contains(key) && supported.add(key)) support.add(key);
                        }
                    }
                }
            }
            for (long key : order) {
                if (key != cut.asLong() && !supported.contains(key)) falling.add(BlockPos.of(key));
            }
        }
        // 什么都没倒（比如粗树干还没砍够一半、砍的是贴在树旁的一根原木）：原版只砍这一格
        if (falling.isEmpty()) return false;

        // 4) 树叶：从倒下的原木和留下的原木同时往外走，先被哪边走到就归哪边（平局归倒下的一边）；
        //    离别的（没连在一起的）树更近的树叶（DISTANCE 属性）不动，DISTANCE = 7 的（没算过距离或本来就要凋落）一律带走
        List<BlockPos> leaves = new ArrayList<>();
        int natural = 0;
        Long2IntOpenHashMap owner = new Long2IntOpenHashMap();
        owner.defaultReturnValue(-1);
        ArrayDeque<long[]> leafQueue = new ArrayDeque<>();
        LongOpenHashSet fallingSet = new LongOpenHashSet();
        for (BlockPos pos : falling) fallingSet.add(pos.asLong());
        for (BlockPos pos : falling) {
            owner.put(pos.asLong(), 1);
            leafQueue.add(new long[]{pos.asLong(), 0, 1});
        }
        owner.put(cut.asLong(), 0);
        for (long key : order) {
            if (fallingSet.contains(key) || key == cut.asLong()) continue;
            owner.put(key, 0);
            leafQueue.add(new long[]{key, 0, 0});
        }
        while (!leafQueue.isEmpty()) {
            long[] entry = leafQueue.poll();
            BlockPos pos = BlockPos.of(entry[0]);
            int depth = (int) entry[1] + 1;
            if (depth > LEAF_REACH) continue;
            for (Direction direction : Direction.values()) {
                next.setWithOffset(pos, direction);
                if (owner.get(next.asLong()) >= 0) continue;
                BlockState state = level.getBlockState(next);
                if (isLivingTree(state)) return false;
                boolean branch = state.getBlock() instanceof BranchesBlock;
                if (!branch && !isLeaves(state)) continue;
                if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) continue;
                if (!branch && state.hasProperty(LeavesBlock.DISTANCE)) {
                    int distance = state.getValue(LeavesBlock.DISTANCE);
                    if (distance < LeavesBlock.DECAY_DISTANCE && depth > distance) continue;
                }
                owner.put(next.asLong(), (int) entry[2]);
                natural += branch ? MIN_NATURAL_LEAVES : 1;
                if (entry[2] == 1 && leaves.size() < MAX_LEAVES) leaves.add(next.immutable());
                if (!branch) leafQueue.add(new long[]{next.asLong(), depth, entry[2]});
            }
        }
        // 没有天然树叶：玩家用原木搭的东西，只砍一格
        if (natural < MIN_NATURAL_LEAVES) return false;

        // 5) 转轴：倒下部分最低一层朝倒向一侧的边上
        Vec3 from = player.position();
        float dirX = (float) (cut.getX() + 0.5 - from.x), dirZ = (float) (cut.getZ() + 0.5 - from.z);
        if (notchX * notchX + notchZ * notchZ > 0.04F) {
            dirX = notchX;
            dirZ = notchZ;
        } else if (dirX * dirX + dirZ * dirZ < 0.01F) {
            Vec3 look = player.getLookAngle();
            dirX = (float) look.x;
            dirZ = (float) look.z;
            if (dirX * dirX + dirZ * dirZ < 1.0E-4F) dirX = 1.0F;
        }
        float length = (float) Math.sqrt(dirX * dirX + dirZ * dirZ);
        dirX /= length;
        dirZ /= length;
        int bottom = Integer.MAX_VALUE;
        for (BlockPos pos : falling) bottom = Math.min(bottom, pos.getY());
        double sumX = 0.0, sumZ = 0.0, front = -Double.MAX_VALUE;
        int footprint = 0;
        for (BlockPos pos : falling) {
            if (pos.getY() != bottom) continue;
            double cx = pos.getX() + 0.5, cz = pos.getZ() + 0.5;
            sumX += cx;
            sumZ += cz;
            footprint++;
            front = Math.max(front, cx * dirX + cz * dirZ + 0.5);
        }
        double centerX = sumX / footprint, centerZ = sumZ / footprint;
        double reach = front - (centerX * dirX + centerZ * dirZ);
        BlockPos base = new BlockPos(cut.getX(), bottom, cut.getZ());
        // 树干只剩砍掉的这一格撑着：树先坐到砍口上（落一格）再倒
        float drop = bottom == cut.getY() + 1 && fallingSet.contains(cut.above().asLong()) ? 1.0F : 0.0F;
        float pivotX = (float) (centerX + dirX * reach - base.getX());
        float pivotZ = (float) (centerZ + dirZ * reach - base.getZ());

        // 6) 从世界里移走（含水的树叶留下水），交给倒下的树
        List<BlockPos> blocks = new ArrayList<>(falling.size() + leaves.size());
        List<BlockState> states = new ArrayList<>(falling.size() + leaves.size());
        for (BlockPos pos : leaves) {
            blocks.add(pos);
            states.add(level.getBlockState(pos));
        }
        for (BlockPos pos : falling) {
            blocks.add(pos);
            states.add(level.getBlockState(pos));
        }
        for (int i = 0; i < blocks.size(); i++) {
            level.setBlock(blocks.get(i), states.get(i).getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        }

        boolean creative = player.getAbilities().instabuild;
        ItemStack toolForDrops = tool.copy();
        if (!creative) {
            // 统计、饥饿按倒下的原木算；斧头每 3 根掉 1 点耐久
            for (int i = leaves.size(); i < states.size(); i++) player.awardStat(Stats.BLOCK_MINED.get(states.get(i).getBlock()));
            player.causeFoodExhaustion(0.005F * falling.size());
            tool.hurtAndBreak((falling.size() + 2) / 3, player, EquipmentSlot.MAINHAND);
        }
        level.addFreshEntity(new FallingTreeEntity(level, base, pivotX, 0.0F, pivotZ, dirX, dirZ, drop, cut, blocks, states, player, toolForDrops, creative));
        return true;
    }

    /// 同一种木头：原木、木头、去皮原木、去皮木头算一家（按注册名去掉 stripped_ / _log / _wood）
    private static Block family(BlockState state) {
        var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = id.getPath().replace("stripped_", "").replace("_log", "").replace("_wood", "").replace("_stem", "").replace("_hyphae", "");
        var key = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path + "_log");
        Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(key);
        return block == net.minecraft.world.level.block.Blocks.AIR ? state.getBlock() : block;
    }

    /// 着地：下面是实心的、不是原木也不是树叶的方块（地面、根扎进去的土）
    private static boolean grounded(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState state = level.getBlockState(below);
        if (state.isAir() || state.is(BlockTags.LOGS) || isLeaves(state)) return false;
        return state.isFaceSturdy(level, below, Direction.UP);
    }
}

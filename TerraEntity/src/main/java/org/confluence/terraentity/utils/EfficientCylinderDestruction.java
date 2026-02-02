package org.confluence.terraentity.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriConsumer;
import org.confluence.terraentity.TerraEntity;

import java.util.*;

import static net.minecraft.world.level.block.Block.getId;

public class EfficientCylinderDestruction {
    private final org.confluence.lib.util.TaskScheduler scheduler;
    private final int centerX;
    private final int centerZ;
    private final int minY;
    private final int maxY;
    private int currentRadius = 0;
    private int maxRadius;
    private long destructionTaskId = -1;

    // 存储每层的当前边缘方块
    private final Map<Integer, Set<BlockPos2D>> layerEdges = new HashMap<>();
    // 存储所有已破坏的方块（防止重复处理）
    private final Set<BlockPos> destroyedBlocks = new HashSet<>();
    // 每层处理的最大方块数（防止卡顿）
    private final int MAX_BLOCKS_PER_LAYER = 100;
    // 超时计数
    int outTime = 0;
    Level level;

    public EfficientCylinderDestruction(Level level, int centerX, int centerZ, int minY, int maxY, int maxRadius) {
        this.level = level;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.minY = minY;
        this.maxY = maxY;
        this.scheduler = new org.confluence.lib.util.TaskScheduler(1);
        this.maxRadius = maxRadius;

        // 初始化每层的边缘集合
        for (int y = minY; y <= maxY; y++) {
            layerEdges.put(y, new HashSet<>());
        }

        // 添加中心点作为初始边缘
        BlockPos2D center = new BlockPos2D(centerX, centerZ);
        for (int y = minY; y <= maxY; y++) {
            layerEdges.get(y).add(center);
        }
    }

    /**
     * 开始圆柱区域破坏任务
     */
    public void startDestruction() {
        if (destructionTaskId != -1) {
            TerraEntity.LOGGER.debug("Destruction already in progress!");
            return;
        }

//        System.out.println("Starting efficient cylinder destruction at (" + centerX + ", " + centerZ + ")");
        currentRadius = 0;

        // 每刻执行一次破坏任务
        destructionTaskId = scheduler.schedule(() -> {
//            currentRadius++;
//            System.out.println("Expanding destruction radius to: " + currentRadius);

            // 为每个Y层创建边缘扩展任务
//            if(currentRadius <= maxRadius) {
//                boolean hasMoreLayers = false;
                for (int y = minY+1; y <= maxY; y++) {
                    scheduler.schedule(new EdgeExpansionTask(y, 1, this::destroyBlock), 0);
                }
                scheduler.schedule(new EdgeExpansionTask(minY, 1, this::setBlock), 0);

//            }
            ++outTime;
        }, 0);
    }

    /**
     * 停止破坏任务
     */
    public void stopDestruction() {
        if (destructionTaskId != -1) {
            scheduler.cancel(destructionTaskId);
            destructionTaskId = -1;
//            System.out.println("Destruction stopped");
        }
    }

    /**
     * 在主循环中调用此方法推进调度器
     *
     * @param elapsed 经过的时间（毫秒）
     */
    public void onTick(long elapsed) {
        scheduler.tick(elapsed);
    }

    /**
     * 尝试停止破坏任务
     * @return 是否成功停止
     */
    public boolean tryStopDestruction(){
        if(outTime > 10){
            stopDestruction();
            return true;
        }
        return false;
    }



    /**
     * 边缘扩展任务（每层独立）
     */
    private class EdgeExpansionTask implements Runnable {
        private final int yLevel;
        TriConsumer<Integer, Integer, Integer> blockOperator;

        private int targetRadius;
        private Iterator<BlockPos2D> edgeIterator;
        Set<BlockPos2D> newEdge;

        private int blocksProcessedThisFrame = 0;

        public EdgeExpansionTask(int yLevel, int targetRadius, TriConsumer<Integer, Integer, Integer> blockOperator) {
            this.yLevel = yLevel;
            this.blockOperator = blockOperator;

            this.reset(targetRadius);
        }

        private void reset(int targetRadius){
            this.targetRadius = targetRadius;
            currentRadius = Math.max(currentRadius, targetRadius);
            List<BlockPos2D> currentEdgeList = new ArrayList<>(layerEdges.get(yLevel));
            Collections.shuffle(currentEdgeList); // 增加随机性
            Set<BlockPos2D> currentEdge = new HashSet<>(currentEdgeList);
            this.edgeIterator = currentEdge.iterator();
            newEdge = new HashSet<>();
        }

        @Override
        public void run() {
            while (edgeIterator.hasNext() && blocksProcessedThisFrame < MAX_BLOCKS_PER_LAYER * 0.3D) {
                BlockPos2D pos = edgeIterator.next();


                expandDirection(pos, 1, 0, newEdge);
                expandDirection(pos, -1, 0, newEdge);
                expandDirection(pos, 0, 1, newEdge);
                expandDirection(pos, 0, -1, newEdge);
                expandDirection(pos, 1, 1, newEdge);
                expandDirection(pos, -1, -1, newEdge);
                expandDirection(pos, 1, -1, newEdge);
                expandDirection(pos, -1, 1, newEdge);
            }

            // 如果还有更多方块需要处理，重新调度任务
            if (edgeIterator.hasNext()) {
                if(this.targetRadius <= maxRadius) {
                    scheduler.schedule(this, 1);
                }
                this.blocksProcessedThisFrame = 0;
                return;
            }
            // 更新该层的边缘

            layerEdges.put(yLevel, newEdge);
            if(this.targetRadius <= maxRadius) {
                this.reset(targetRadius + 1);
                scheduler.schedule(this, level.random.nextInt(2));
                this.blocksProcessedThisFrame = 0;
            }
        }

        private void expandDirection(BlockPos2D pos, int dx, int dz, Set<BlockPos2D> newEdge) {
            BlockPos2D newPos = new BlockPos2D(pos.x + dx, pos.z + dz);
            BlockPos blockPos = new BlockPos(newPos.x, yLevel, newPos.z);

            // 跳过已破坏的方块
            if (destroyedBlocks.contains(blockPos)) {
                return;
            }

            // 计算到中心的距离
            double distance = Math.sqrt(
                    Math.pow(newPos.x - centerX, 2) +
                            Math.pow(newPos.z - centerZ, 2)
            );

            // 如果在新半径范围内
            if (distance <= targetRadius) {
                // 破坏方块
//                destroyBlock(newPos.x, yLevel, newPos.z);
                blockOperator.accept(newPos.x, yLevel, newPos.z);
                destroyedBlocks.add(blockPos);
                newEdge.add(newPos);
                blocksProcessedThisFrame++;
            }
        }
    }

    public int getCurrentRadius() {
        return currentRadius;
    }

    private void destroyBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.AIR || state.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        if(level.random.nextFloat() < 0.01f) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.2f,0.6f);
//            ((ServerLevel)level).sendParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 1, 0.5, 0.1,0,0);
        }
        if(level.random.nextFloat() < 0.005f) {
            level.levelEvent(2001, pos, getId(state));
        }
    }

    private void setBlock(int x, int y, int z) {
        level.setBlock(new BlockPos(x, y, z), Blocks.NETHERRACK.defaultBlockState(), 3);
    }

    private record BlockPos2D(int x, int z) {

    }

}

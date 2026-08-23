package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/// 将地面生物移动到原版陆地随机算法给出的目标附近位置。
///
/// 这段行为对应 1.21 侧法师类生物的传送循环：最多尝试指定次数，遇到第一个可用候选点就立即传送。
/// {@link LandRandomPos} 负责按照陆地寻路条件选点，本节点只补充实际落点的区块与碰撞检查，
/// 避免候选点在未加载区域或实体碰撞箱插进方块时造成实测中的错误传送。
public class TeleportNearTargetAction extends BTNode {
    private final PathfinderMob mob;
    private final int horizontalRange;
    private final int verticalRange;
    private final int attempts;
    private boolean done;

    public TeleportNearTargetAction(PathfinderMob mob, int horizontalRange, int verticalRange, int attempts) {
        this.mob = mob;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;
        this.attempts = attempts;
    }

    @Override
    public void start() {
        done = false;
    }

    @Override
    public BTStatus execute() {
        if (done) return BTStatus.SUCCESS;

        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) return BTStatus.FAILURE;

        for (int attempt = 0; attempt < attempts; attempt++) {
            Vec3 candidate = LandRandomPos.getPosTowards(mob, horizontalRange, verticalRange, target.position());
            if (candidate == null) continue;
            if (!canTeleportTo(candidate)) continue;

            mob.teleportTo(candidate.x, candidate.y, candidate.z);
            done = true;
            return BTStatus.SUCCESS;
        }
        return BTStatus.FAILURE;
    }

    private boolean canTeleportTo(Vec3 candidate) {
        BlockPos position = BlockPos.containing(candidate);
        if (!mob.level().hasChunkAt(position)) {
            return false;
        }
        // 原版随机点只代表脚下坐标，真正传送前仍要验证整个实体碰撞箱是否能放下。
        AABB destinationBox = mob.getBoundingBox().move(candidate.x - mob.getX(), candidate.y - mob.getY(), candidate.z - mob.getZ());
        return mob.level().noCollision(mob, destinationBox);
    }
}

package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 方块碰撞接口，使用原版 Entity.collideBoundingBox 算法实现精确碰撞检测。
 *
 * @param <T> 附件实体类型
 */
public interface IBlockCollision<T extends AttachmentEntity & IMomentumAttachmentEntity> {

    @NotNull AABB getBlockCollisionBox();

    default boolean canCollideWithBlocks() {
        return true;
    }

    default void onBlockCollision(CollisionContext context) {
    }

    default float getElasticity() {
        return 0;
    }

    default void blockCollision(AttachmentEntity entity) {
        ArrayList<PathNode> history = entity.getHistoryNodes();
        if (entity instanceof IMomentumAttachmentEntity iMomentumAttachmentEntity && canCollideWithBlocks() && !history.isEmpty()) {
            Vec3 from = history.get(0).pos();
            Vec3 motion = entity.currentPathNode.pos().subtract(from);
            Level level = entity.getLevel();
            // 使用原版碰撞检测算法
            Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, getBlockCollisionBox().move(from), level, List.of());
            if (!correctedMotion.equals(motion)) {
                Vec3 correctedPos = from.add(correctedMotion);
                entity.currentPathNode = new PathNode(correctedPos, entity.currentPathNode.yaw(), entity.currentPathNode.pitch(), entity.currentPathNode.roll());
                boolean collisionX = correctedMotion.x != motion.x;
                boolean collisionY = correctedMotion.y != motion.y;
                boolean collisionZ = correctedMotion.z != motion.z;
                // 被底部方块支撑：原运动向下且 Y 轴被碰撞截断（即落地）
                boolean bottomSupported = motion.y < 0 && collisionY;
                Vec3 velocity = iMomentumAttachmentEntity.getVelocity();
                float elasticity = getElasticity();
                iMomentumAttachmentEntity.setVelocity(new Vec3(collisionX ? (Math.abs(velocity.x) * elasticity < 0.01 ? 0 : -velocity.x * elasticity) : velocity.x, collisionY ? (Math.abs(velocity.y) * elasticity < 0.01 ? 0 : -velocity.y * elasticity) : velocity.y, collisionZ ? (Math.abs(velocity.z) * elasticity < 0.01 ? 0 : -velocity.z * elasticity) : velocity.z));
                onBlockCollision(new CollisionContext(correctedPos, collisionX, collisionY, collisionZ, bottomSupported));
            }
        }
    }

    record CollisionContext(Vec3 position, boolean collisionX, boolean collisionY, boolean collisionZ, boolean bottomSupported) {
    }
}

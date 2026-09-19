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

    /**
     * 碰撞轴上的速度处理：只处理与碰撞方向一致的速度，反向的速度原样保留。
     */
    default double resolveVelocity(double velocity, double motion, float elasticity) {
        double result = velocity;
        if (velocity * motion > 0.0) {
            result = Math.abs(velocity) * elasticity < 0.01 ? 0.0 : -velocity * elasticity;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    default void blockCollision() {
        T entity = (T) this;
        ArrayList<PathNode> history = entity.getHistoryNodes();
        if (canCollideWithBlocks() && !history.isEmpty()) {
            Vec3 from = history.get(0).pos();
            Vec3 motion = entity.getCurrentPathNode().pos().subtract(from);
            Level level = entity.getLevel();
            // 使用原版碰撞检测算法
            Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, getBlockCollisionBox().move(from), level, List.of());
            Vec3 correctedPos = from.add(correctedMotion);
            entity.currentPathNode = new PathNode(correctedPos, entity.currentPathNode.yaw(), entity.currentPathNode.pitch(), entity.currentPathNode.roll());
            boolean collisionX = correctedMotion.x != motion.x;
            boolean collisionY = correctedMotion.y != motion.y;
            boolean collisionZ = correctedMotion.z != motion.z;
            // 被底部方块支撑：原运动向下且 Y 轴被碰撞截断（即落地）
            boolean onGround = motion.y < 0 && collisionY;
            if (onGround) {
                Vec3 velocity = entity.getVelocity();
                entity.setVelocity(new Vec3(velocity.x() * 0.8, velocity.y(), velocity.z() * 0.8));
            }
            float elasticity = getElasticity();
            Vec3 velocity = entity.getVelocity();
            entity.setVelocity(new Vec3(
                    collisionX ? resolveVelocity(velocity.x, motion.x, elasticity) : velocity.x,
                    collisionY ? resolveVelocity(velocity.y, motion.y, elasticity) : velocity.y,
                    collisionZ ? resolveVelocity(velocity.z, motion.z, elasticity) : velocity.z));
            if (collisionX || collisionY || collisionZ) {
                onBlockCollision(new CollisionContext(correctedPos, collisionX, collisionY, collisionZ, onGround));
            }
        }
    }

    record CollisionContext(Vec3 position, boolean collisionX, boolean collisionY, boolean collisionZ, boolean bottomSupported) {
    }
}

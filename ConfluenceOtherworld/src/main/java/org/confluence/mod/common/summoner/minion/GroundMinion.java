package org.confluence.mod.common.summoner.minion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 地面召唤物基类：位置即脚下，内置水平移动与飞行移动。
 */
public abstract class GroundMinion extends MomentumMinion implements IBlockCollision<GroundMinion> {

    private boolean flying = false;
    private boolean isWalking = false;
    private boolean onGround = false;

    public GroundMinion(RegistryObject<? extends AttachmentEntityType<?>> type) {
        super(type);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.BOOL, this::isWalking, this::setWalking);
        fields.field(LyraStreamCodecs.BOOL, this::isFlying, this::setFlying);
    }

    @Override
    public void tick() {
        setWalking(false);
        setPhysics(true);
        setFlying(false);
        super.tick();
        onGround = false;
    }

    /**
     * 默认移动：只尝试水平靠近目标位置
     */
    public void moveTo(Vec3 pos, float speed) {
        Vec3 horizontal = new Vec3(pos.x - getPos().x, 0.0, pos.z - getPos().z);
        if (horizontal.lengthSqr() > 1.0E-6) {
            setWalking(true);
            addVelocity(horizontal.normalize().scale(speed));
            lookAtPos(pos);
        }
    }

    public void flyTo(Vec3 pos, float speed) {
        setFlying(true);
        Vec3 subtract = pos.subtract(getPos()).normalize().scale(speed);
        if (pos.distanceTo(getPos()) < 1) {
            setVelocity(getVelocity().scale(0.8f));
        }
        addVelocity(subtract.add(0, -getGravity(), 0));
    }

    @Override
    public void lookAtDirection(Vec3 direction) {
        setDesiredRotation((float) Math.toDegrees(Math.atan2(-direction.x, direction.z)), 0, getRoll());
    }

    @Override
    public boolean canCollideWithBlocks() {
        return isPhysics();
    }

    @Override
    public double getStepHeight() {
        return 1.0;
    }

    @Override
    public void onBlockCollision(CollisionContext context) {
        onGround = context.bottomSupported();
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isFlying() {
        return flying;
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.3, 0, -0.3, 0.3, 0.8, 0.3);
    }

    public void blockCollision() {
        GroundMinion entity = this;
        ArrayList<PathNode> history = entity.getHistoryNodes();
        if (!isFlying() && canCollideWithBlocks() && !history.isEmpty()) {
            Vec3 from = history.get(0).pos();
            Vec3 motion = entity.getCurrentPathNode().pos().subtract(from);
            Level level = entity.getLevel();
            // 使用原版碰撞检测算法
            Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, getBlockCollisionBox().move(from), level, List.of());
            boolean collisionX = correctedMotion.x != motion.x;
            boolean collisionY = correctedMotion.y != motion.y;
            boolean collisionZ = correctedMotion.z != motion.z;
            double stepHeight = getStepHeight();
            boolean stepped = false;
            if (stepHeight > 0 && motion.y <= 0 && (collisionX || collisionZ)) {
                Vec3 horizontalMotion = new Vec3(motion.x, 0.0, motion.z);
                Vec3 steppedMotion = Entity.collideBoundingBox(null, horizontalMotion, getBlockCollisionBox().move(from.add(0.0, stepHeight, 0.0)), level, List.of());
                if (steppedMotion.x == motion.x && steppedMotion.z == motion.z) {
                    correctedMotion = new Vec3(motion.x, stepHeight, motion.z);
                    collisionX = false;
                    collisionY = false;
                    collisionZ = false;
                    stepped = true;
                }
            }
            Vec3 correctedPos = from.add(correctedMotion);
            entity.currentPathNode = new PathNode(correctedPos, entity.currentPathNode.yaw(), entity.currentPathNode.pitch(), entity.currentPathNode.roll());
            // 被底部方块支撑：原运动向下且 Y 轴被碰撞截断（即落地）
            boolean onGround = !stepped && motion.y < 0 && collisionY;
            if (onGround) {
                Vec3 velocity = entity.getVelocity();
                entity.setVelocity(new Vec3(velocity.x() * 0.5, velocity.y(), velocity.z() * 0.5));
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
}

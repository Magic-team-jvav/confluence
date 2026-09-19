package org.confluence.mod.common.summoner.minion;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class GroundMinion extends MomentumMinion implements IBlockCollision<GroundMinion>, IHeadRotatable {

    /** 起跳的向上动量，配合默认重力与阻力大约能升高 1.28 格 */
    private static final double DEFAULT_JUMP_VELOCITY = 0.58;
    /** 起跳时沿移动方向附加的水平动量 */
    private static final double DEFAULT_JUMP_FORWARD = 0.22;
    /** 两次起跳的最小间隔 */
    private static final int JUMP_COOLDOWN = 10;

    private final GroundPathNavigation navigation = new GroundPathNavigation(this);
    private final GroundMinionLookControl lookControl = new GroundMinionLookControl(this);
    private boolean onGround = false;
    private double jumpVelocity = DEFAULT_JUMP_VELOCITY;
    private double jumpForward = DEFAULT_JUMP_FORWARD;
    private int jumpCooldown;

    public GroundMinion(RegistryObject<? extends AttachmentEntityType<?>> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        super.tick();
        navigation.tick();
        lookControl.tick();
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LyraStreamCodecs.FLOAT, () -> getLookControl().getHeadYawO(), value -> getLookControl().setHeadYawO(value));
        fields.field(LyraStreamCodecs.FLOAT, () -> getLookControl().getHeadYaw(), value -> getLookControl().setHeadYaw(value));
        fields.field(LyraStreamCodecs.FLOAT, () -> getLookControl().getHeadPitchO(), value -> getLookControl().setHeadPitchO(value));
        fields.field(LyraStreamCodecs.FLOAT, () -> getLookControl().getHeadPitch(), value -> getLookControl().setHeadPitch(value));
    }

    @Override
    public void blockCollision() {
        ArrayList<PathNode> history = this.getHistoryNodes();
        onGround = false;
        if (canCollideWithBlocks() && !history.isEmpty()) {
            Vec3 from = history.get(0).pos();
            Vec3 motion = this.currentPathNode.pos().subtract(from);
            Level level = this.getLevel();
            // 使用原版碰撞检测算法
            Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, getBlockCollisionBox().move(from), level, List.of());
            Vec3 correctedPos = from.add(correctedMotion);
            this.currentPathNode = new PathNode(correctedPos, this.currentPathNode.yaw(), this.currentPathNode.pitch(), this.currentPathNode.roll());
            boolean collisionX = correctedMotion.x != motion.x;
            boolean collisionY = correctedMotion.y != motion.y;
            boolean collisionZ = correctedMotion.z != motion.z;
            // 被底部方块支撑：原运动向下且 Y 轴被碰撞截断（即落地）
            onGround = motion.y < 0 && collisionY;
            if (onGround) {
                Vec3 velocity = getVelocity();
                setVelocity(new Vec3(velocity.x() * 0.9, velocity.y(), velocity.z() * 0.9));
            }
            float elasticity = getElasticity();
            Vec3 velocity = getVelocity();
            setVelocity(new Vec3(
                    collisionX ? resolveVelocity(velocity.x, motion.x, elasticity) : velocity.x,
                    collisionY ? resolveVelocity(velocity.y, motion.y, elasticity) : velocity.y,
                    collisionZ ? resolveVelocity(velocity.z, motion.z, elasticity) : velocity.z));
            if (collisionX || collisionY || collisionZ) {
                onBlockCollision(new CollisionContext(correctedPos, collisionX, collisionY, collisionZ, onGround));
            }
        }
    }

    public boolean isOnGround() {
        return onGround;
    }

    /**
     * 起跳：贴地且不在冷却中时给一个向上动量，并沿 direction 的水平方向补一点动量，
     * 让召唤物能够登上一格高的方块。
     */
    public boolean jump(Vec3 direction) {
        boolean jumped = false;
        if (onGround && jumpCooldown <= 0) {
            addVelocity(new Vec3(0, jumpVelocity, 0));
            jumpCooldown = JUMP_COOLDOWN;
            jumped = true;
        }
        return jumped;
    }

    public double getJumpVelocity() {
        return jumpVelocity;
    }

    public void setJumpVelocity(double jumpVelocity) {
        this.jumpVelocity = jumpVelocity;
    }

    public double getJumpForward() {
        return jumpForward;
    }

    public void setJumpForward(double jumpForward) {
        this.jumpForward = jumpForward;
    }

    /**
     * 眼睛高度：地面召唤物的位置在脚下，需要额外抬高到头部。
     */
    public float getEyeHeight() {
        return 0;
    }

    /**
     * 眼睛在世界中的位置，供视角计算与调试绘制使用。
     */
    public Vec3 getEyePosition() {
        return getPos().add(0.0, getEyeHeight(), 0.0);
    }

    /**
     * 眼睛朝向：身体朝向叠加头部偏转后的单位向量。
     */
    public Vec3 getEyeDirection() {
        return Vec3.directionFromRotation(getHeadPitch(), getYaw() + getHeadYaw());
    }

    /**
     * 渲染插值后的眼睛朝向。
     */
    public Vec3 getEyeDirection(float partialTick) {
        return Vec3.directionFromRotation(getHeadPitch(partialTick), getRenderNode(partialTick).yaw() + getHeadYaw(partialTick));
    }

    /**
     * 地面召唤物只转头不带动身体俯仰，注视统一交给视角控制处理。
     */
    @Override
    public void lookAtPos(Vec3 targetPos) {
        lookControl.lookAt(targetPos);
    }

    @Override
    public void lookAtDirection(Vec3 direction) {
        lookControl.lookAt(getEyePosition().add(direction));
    }

    /**
     * 寻路实例，目标设置、移动速度与节点推进都由它负责。
     */
    public GroundPathNavigation getNavigation() {
        return navigation;
    }

    /**
     * 视角控制实例，观察目标与头部转动都由它负责。
     */
    public GroundMinionLookControl getLookControl() {
        return lookControl;
    }

    public float getHeadYaw() {
        return lookControl.getHeadYaw();
    }

    @Override
    public float getHeadYaw(float partialTick) {
        return lookControl.getHeadYaw(partialTick);
    }

    public void setHeadYaw(float headYaw) {
        lookControl.setHeadYaw(headYaw);
    }

    public float getHeadPitch() {
        return lookControl.getHeadPitch();
    }

    @Override
    public float getHeadPitch(float partialTick) {
        return lookControl.getHeadPitch(partialTick);
    }

    public void setHeadPitch(float headPitch) {
        lookControl.setHeadPitch(headPitch);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
    }
}

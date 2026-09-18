package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.attachment.TargetCache;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AttachmentEntity implements Immunity {

    protected final Holder<AttachmentEntityType<?>> type;
    protected final ArrayList<PathNode> historyNodes = new ArrayList<>();
    protected final AttachmentEntityGoalSelector goalSelector = new AttachmentEntityGoalSelector();
    protected final SyncFieldDispatcher syncFields = SyncFieldDispatcher.create(this::registerSyncFields);
    protected UUID uuid = UUID.randomUUID();
    protected Player owner = null;
    protected PlannedPath currentPlannedPath = null;
    protected boolean remove = false;
    protected int immunityDuration = 0;

    protected int tickCount = 0;
    protected float damage = 0;
    protected float knockback = 0;
    protected boolean clientInit = false;
    protected PathNode currentPathNode = null;

    protected void registerSyncFields(SyncFieldDispatcher fields) {
        fields.field(LyraStreamCodecs.FLOAT, this::getDamage, this::setDamage);
        fields.field(LyraStreamCodecs.FLOAT, this::getKnockback, this::setKnockback);
        fields.field(LyraStreamCodecs.INT, this::getTickCount, this::setTickCount);
        fields.field(LyraStreamCodecs.PATH_NODE, this::getCurrentPathNode, this::setCurrentPathNode);
    }

    public AttachmentEntity(Holder<AttachmentEntityType<?>> type) {
        this.type = type;
        init(new PathNode(Vec3.ZERO, 0, 0, 0));
        registerGoals(goalSelector);
    }

    public void registerGoals(AttachmentEntityGoalSelector goalSelector) {
    }

    @NotNull
    public DamageSource getDamageSource() {
        return new AttachmentEntityDamageSource(getLevel().damageSources().generic().typeHolder(), null, owner, getPos(), this);
    }

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        return immunityDuration;
    }

    public TargetCache getTargetCache() {
        return owner.getData(SummonerAttachmentTypes.TARGET_CACHE);
    }

    public void attack(@NotNull LivingEntity target, float damageAmount, int invincibleTime) {
        if (!Immunity.isActive(this, target)) {
            DamageSource damageSource = getDamageSource();
            immunityDuration = invincibleTime;
            int invulnerableTime = target.invulnerableTime;
            target.invulnerableTime = 0;
            boolean hurt = Immunity.withCause(this, () -> target.hurt(damageSource, damageAmount));
            target.invulnerableTime = invulnerableTime;
            if (hurt) {
                Immunity.apply(this, damageSource, target);
            }
            immunityDuration = 0;
        }
    }

    public void copyAttributes(AttachmentEntity other) {
        setDamage(other.getDamage());
        setKnockback(other.getKnockback());
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }

    public SyncFieldDispatcher getSyncFieldDispatcher() {
        return syncFields;
    }

    public void tick() {
    }

    public final void tickCurrentPathNode() {
        Level level = getLevel();
        if (!level.isClientSide()) {
            tickCount++;
            if (this instanceof IBlockCollision<?> blockCollision) {
                blockCollision.blockCollision(this);
            }
            if (this instanceof IEntityCollision<?> iEntityCollision) {
                iEntityCollision.entityCollision(this);
            }
        }
        historyNodes.add(0, currentPathNode);
        if (historyNodes.size() > 16) {
            historyNodes.remove(historyNodes.size() - 1);
        }
        if (!level.isClientSide() && currentPlannedPath != null && !currentPlannedPath.isFinished()) {
            currentPathNode = currentPlannedPath.advance();
        }
    }

    public void onRemove() {
    }

    public void onLevelChange() {
    }

    public void setPlannedPath(PlannedPath path) {
        currentPlannedPath = path;
    }

    public PlannedPath getCurrentPath() {
        return currentPlannedPath;
    }

    public PathNode getCurrentPathNode() {
        return currentPathNode;
    }

    public boolean isExecutingPath() {
        return this.currentPlannedPath != null && !this.currentPlannedPath.isFinished();
    }

    public void setPath(List<PathNode> nodes) {
        this.currentPlannedPath = new PlannedPath(nodes);
        if (!nodes.isEmpty()) {
            this.currentPathNode = nodes.get(0);
        }
    }

    public void setPath(PlannedPath plannedPath) {
        List<PathNode> nodes = plannedPath.getNodes();
        if (!nodes.isEmpty()) {
            this.currentPathNode = nodes.get(0);
        }
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove() {
        this.remove = true;
    }

    public void init(PathNode node) {
        this.clientInit = false;
        this.currentPathNode = node;
        this.historyNodes.clear();
        this.historyNodes.add(node);
    }

    public ArrayList<PathNode> getHistoryNodes() {
        return historyNodes;
    }

    public PathNode getRenderNode(float partialTick) {
        if (historyNodes.size() > 1) {
            return historyNodes.get(1).lerp(currentPathNode, partialTick);
        }
        return currentPathNode;
    }

    public void setCurrentPathNode(PathNode currentPathNode) {
        this.currentPathNode = currentPathNode;
    }

    /**
     * tick后进行的存在性检查，返回false移除
     */
    public boolean isAlive() {
        return true;
    }

    /**
     * @return 当前位置
     */
    public Vec3 getPos() {
        return currentPathNode.pos();
    }

    public void setPos(Vec3 pos) {
        currentPathNode = new PathNode(pos, getYaw(), getPitch(), getRoll());
    }

    /**
     * @return 当前偏航角（度）
     */
    public float getYaw() {
        return currentPathNode.yaw();
    }

    /**
     * @return 当前俯仰角（度）
     */
    public float getPitch() {
        return currentPathNode.pitch();
    }

    /**
     * @return 当前翻滚角（度）
     */
    public float getRoll() {
        return currentPathNode.roll();
    }

    /**
     * @return 实体 UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * 设置 UUID，用于从网络数据恢复。
     *
     * @param uuid UUID值
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return 虚拟实体所在世界
     */
    public Level getLevel() {
        return owner.level();
    }

    /**
     * @return 当前维度的随机源
     */
    public RandomSource getRandom() {
        return getLevel().getRandom();
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public boolean isClientInit() {
        return clientInit;
    }

    public void setClientInit(boolean clientInit) {
        this.clientInit = clientInit;
    }

    public Player getOwner(){
        return owner;
    }

    public void setOwner(Player owner){
        this.owner = owner;
    }

    /**
     * 计算贝塞尔曲线上的点（De Casteljau算法，支持任意数量控制点）
     */
    public Vec3 calculateBezierPoint(float delta, Vec3... P) {
        if (P.length == 0) {
            return Vec3.ZERO;
        }
        if (P.length == 1) {
            return P[0];
        }
        Vec3[] pts = P.clone();
        for (int k = P.length - 1; k > 0; k--) {
            for (int i = 0; i < k; i++) {
                pts[i] = pts[i].lerp(pts[i + 1], delta);
            }
        }
        return pts[0];
    }

    public Vec3 getLookAngle() {
        return Vec3.directionFromRotation(getPitch(), getYaw()).normalize();
    }

    /**
     * 获取当前速度向量
     */
    public Vec3 getCurrentVelocity() {
        Vec3 currentPos = getPos();
        ArrayList<PathNode> history = getHistoryNodes();
        if (history.size() > 1) {
            Vec3 rawVel = currentPos.subtract(history.get(0).pos());
            if (rawVel.lengthSqr() > 1e-5) {
                return rawVel.normalize();
            }
        }
        return Vec3.directionFromRotation(getPitch(), getYaw()).normalize();
    }

    /**
     * 获取当前法线向量（基于旋转）
     */
    public Vec3 getCurrentNormal() {
        Quaternionf q = new Quaternionf()
                .rotateY((float) Math.toRadians(-getYaw()))
                .rotateX((float) Math.toRadians(getPitch()))
                .rotateZ((float) Math.toRadians(getRoll()));
        Vector3f upV = new Vector3f(0, 1, 0).rotate(q);
        return new Vec3(upV.x(), upV.y(), upV.z()).normalize();
    }

    public PathNode getEulerNode(Vec3 pos, Vec3 direction, Vec3 normal) {
        direction = direction.normalize();
        normal = normal.normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) Math.toDegrees(Math.asin(-direction.y));
        // 已偏转yaw/pitch后，局部Y轴（无roll时法向量）的世界方向
        float pr = (float) Math.toRadians(pitch);
        float yr = (float) Math.toRadians(yaw);
        float cp = (float) Math.cos(pr);
        float sp = (float) Math.sin(pr);
        float cy = (float) Math.cos(yr);
        float sy = (float) Math.sin(yr);
        Vec3 localY = new Vec3(-sy * sp, cp, cy * sp);
        // 投影到垂直于direction的平面
        Vec3 projLocalY = localY.subtract(direction.scale(localY.dot(direction))).normalize();
        Vec3 projNormal = normal.subtract(direction.scale(normal.dot(direction))).normalize();
        // 不翻转projNormal：atan2自然处理正负，避免dot≈0时翻转振荡
        double d = projLocalY.dot(projNormal);
        Vec3 c = projLocalY.cross(projNormal);
        float roll = (float) Math.toDegrees(Math.atan2(c.dot(direction), d));
        return new PathNode(pos, yaw, pitch, roll);
    }

    public AttachmentEntityType<?> getType() {
        return type.value();
    }
}

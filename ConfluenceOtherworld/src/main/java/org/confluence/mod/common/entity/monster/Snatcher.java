package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.joml.Vector3f;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.ArrayList;
import java.util.List;

/// 固定根部、通过连接段伸缩接近目标的植物类敌怪基类。
///
/// 涵盖抓人草、食人怪、爬藤怪、真菌球怪和巨型真菌球怪；各物种通过 {@link Profile} 配置伸展范围。
/// 头部可以穿过方块，根部保持固定；无目标时在根部附近摆动，追击时受最大伸展距离约束。
/// 根部位置与静止伸展方向负责同步和存档，射弹攻击由 {@link SpittingPlant} 扩展。
public class Snatcher extends BaseMonster {
    private static final String ANCHORED_TAG = "Anchored";
    private static final String ANCHOR_X_TAG = "AnchorX";
    private static final String ANCHOR_Y_TAG = "AnchorY";
    private static final String ANCHOR_Z_TAG = "AnchorZ";
    private static final String REST_X_TAG = "RestX";
    private static final String REST_Y_TAG = "RestY";
    private static final String REST_Z_TAG = "RestZ";
    private static final String DIRECTION_VERSION_TAG = "AnchorDirectionVersion";
    private static final int CURRENT_DIRECTION_VERSION = 1;
    private static final String SURFACE_ANCHOR_TAG = "SurfaceAnchor";
    private static final double SEARCH_DISTANCE = 50.0;
    private static final EntityDataAccessor<Boolean> ANCHORED = SynchedEntityData.defineId(Snatcher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> ANCHOR = SynchedEntityData.defineId(Snatcher.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> REST_DIRECTION = SynchedEntityData.defineId(Snatcher.class, EntityDataSerializers.VECTOR3);
    private static final List<Vec3> SEARCH_DIRECTIONS = createSearchDirections();
    private final Profile profile;
    private Vec3 anchor = Vec3.ZERO;
    private Vec3 restDirection = new Vec3(0.0, 1.0, 0.0);
    private boolean legacyAnchor;

    public Snatcher(EntityType<? extends Snatcher> type, Level level) {
        this(type, level, Profile.SNATCHER);
    }

    public Snatcher(EntityType<? extends Snatcher> type, Level level, Profile profile) {
        super(type, level);
        this.profile = profile;
        noPhysics = true;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    @Override
    protected void registerGoals() {
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ANCHORED, false);
        entityData.define(ANCHOR, new Vector3f());
        entityData.define(REST_DIRECTION, new Vector3f(0.0F, 1.0F, 0.0F));
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide && legacyAnchor) {
            legacyAnchor = false;
            Vec3 surface = anchor.add(0.0, -0.5, 0.0);
            BlockHitResult hit = level().clip(new ClipContext(surface.add(restDirection), surface.subtract(restDirection),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK) {
                initializeAnchor(hit.getLocation(), Vec3.atLowerCornerOf(hit.getDirection().getNormal()));
            } else {
                entityData.set(ANCHORED, false);
            }
        }
        if (!level().isClientSide && !isAnchored() && !findAndSetAnchor()) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        noPhysics = true;
        if (level().isClientSide || !isAnchored()) {
            return;
        }
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return distanceToSqr(entity) < 32.0 * 32.0;
    }

    private boolean findAndSetAnchor() {
        List<Vec3> directions = new ArrayList<>(SEARCH_DIRECTIONS);
        for (int index = directions.size() - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            Vec3 previous = directions.set(index, directions.get(swapIndex));
            directions.set(swapIndex, previous);
        }
        Vec3 origin = position();
        for (Vec3 direction : directions) {
            BlockHitResult hit = level().clip(new ClipContext(origin, origin.add(direction.scale(SEARCH_DISTANCE)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.BLOCK) {
                continue;
            }
            Vec3 normal = Vec3.atLowerCornerOf(hit.getDirection().getNormal());
            Vec3 anchor = hit.getLocation();
            /// 射线方向指向被命中的方块；继续沿该方向伸展会让头部钻入墙体。
            /// 静止方向必须使用命中面的外法线，才能从根部朝开放空间摆动。
            initializeAnchor(anchor, normal);
            return true;
        }
        return false;
    }

    /// 设置根部和静止伸展方向。
    public void initializeAnchor(Vec3 anchor, Vec3 restDirection) {
        if (restDirection.lengthSqr() < 1.0E-8) {
            throw new IllegalArgumentException("Snatcher rest direction must not be zero");
        }
        this.anchor = anchor;
        this.restDirection = restDirection.normalize();
        entityData.set(ANCHOR, anchor.toVector3f());
        entityData.set(REST_DIRECTION, this.restDirection.toVector3f());
        entityData.set(ANCHORED, true);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == ANCHOR) anchor = new Vec3(entityData.get(ANCHOR));
        if (key == REST_DIRECTION)
            restDirection = new Vec3(entityData.get(REST_DIRECTION)).normalize();
    }

    public boolean isAnchored() {
        return entityData.get(ANCHORED);
    }

    public Vec3 getAnchor() {
        return anchor;
    }

    public Vec3 getRestDirection() {
        return restDirection;
    }

    /// 当前物种在普通阶段的最大伸展距离。
    double normalReach() {
        return profile.normalReach;
    }

    /// 返回延展阶段最大伸展距离。
    double extendedReach() {
        return profile.extendedReach;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return isAnchored()
                ? super.getBoundingBoxForCulling().minmax(new AABB(position(), getAnchor()).inflate(0.25D))
                : super.getBoundingBoxForCulling().inflate(10.0);
    }

    /// 头部使用接触攻击，检测范围由 contactAttackInflation 配置。
    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected double contactAttackInflation() {
        return 0.3;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new SnatcherMovementAction(Snatcher.this);
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(ANCHORED_TAG, isAnchored());
        if (isAnchored()) {
            Vec3 anchor = getAnchor();
            Vec3 rest = getRestDirection();
            tag.putDouble(ANCHOR_X_TAG, anchor.x);
            tag.putDouble(ANCHOR_Y_TAG, anchor.y);
            tag.putDouble(ANCHOR_Z_TAG, anchor.z);
            tag.putDouble(REST_X_TAG, rest.x);
            tag.putDouble(REST_Y_TAG, rest.y);
            tag.putDouble(REST_Z_TAG, rest.z);
            tag.putInt(DIRECTION_VERSION_TAG, CURRENT_DIRECTION_VERSION);
            tag.putBoolean(SURFACE_ANCHOR_TAG, true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean(ANCHORED_TAG)) {
            Vec3 restDirection = new Vec3(tag.getDouble(REST_X_TAG), tag.getDouble(REST_Y_TAG), tag.getDouble(REST_Z_TAG));
            if (tag.getInt(DIRECTION_VERSION_TAG) < CURRENT_DIRECTION_VERSION) {
                restDirection = restDirection.scale(-1.0);
            }
            initializeAnchor(new Vec3(tag.getDouble(ANCHOR_X_TAG), tag.getDouble(ANCHOR_Y_TAG), tag.getDouble(ANCHOR_Z_TAG)), restDirection);
            legacyAnchor = !tag.getBoolean(SURFACE_ANCHOR_TAG);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
    }

    private static List<Vec3> createSearchDirections() {
        List<Vec3> directions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        directions.add(new Vec3(x, y, z));
                    }
                }
            }
        }
        return List.copyOf(directions);
    }

    /// 各物种共用伸缩状态机，分别配置普通阶段与延展阶段的最大距离。
    public enum Profile {
        SNATCHER(9.4, 12.2),
        MAN_EATER(15.6, 20.3),
        CLINGER(10.9, 14.2),
        FUNGI_BULB(6.2, 8.1),
        GIANT_FUNGI_BULB(21.9, 28.4);

        private final double normalReach;
        private final double extendedReach;

        Profile(double normalReach, double extendedReach) {
            this.normalReach = normalReach;
            this.extendedReach = extendedReach;
        }
    }
}

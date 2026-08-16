package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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

/// 固定在地形表面、通过藤蔓伸缩接近目标的抓人草。
///
/// <p>实体头部可以穿过方块，但根部一经生成便不会移动。无目标时头部在根部前方缓慢摆动；
/// 发现目标后会朝目标伸长，并受最大藤蔓长度限制。根部与静止方向均同步并保存，因而客户端
/// 渲染、区块重载和服务端命中判定始终使用同一组数据。</p>
public class Snatcher extends BaseMonster {
    private static final String ANCHORED_TAG = "Anchored";
    private static final String ANCHOR_X_TAG = "AnchorX";
    private static final String ANCHOR_Y_TAG = "AnchorY";
    private static final String ANCHOR_Z_TAG = "AnchorZ";
    private static final String REST_X_TAG = "RestX";
    private static final String REST_Y_TAG = "RestY";
    private static final String REST_Z_TAG = "RestZ";
    private static final double SEARCH_DISTANCE = 50.0;
    private static final EntityDataAccessor<Boolean> ANCHORED =
            SynchedEntityData.defineId(
                    Snatcher.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Vector3f> ANCHOR =
            SynchedEntityData.defineId(
                    Snatcher.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> REST_DIRECTION =
            SynchedEntityData.defineId(
                    Snatcher.class, EntityDataSerializers.VECTOR3);
    private static final List<Vec3> SEARCH_DIRECTIONS =
            createSearchDirections();
    public Snatcher(EntityType<? extends Snatcher> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
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
        if (!level().isClientSide && !isAnchored()
                && !findAndSetAnchor()) {
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
            Vec3 previous = directions.set(
                    index, directions.get(swapIndex));
            directions.set(swapIndex, previous);
        }
        Vec3 origin = position();
        for (Vec3 direction : directions) {
            BlockHitResult hit = level().clip(new ClipContext(
                    origin,
                    origin.add(direction.scale(SEARCH_DISTANCE)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    this));
            if (hit.getType() != HitResult.Type.BLOCK) {
                continue;
            }
            Vec3 normal = Vec3.atLowerCornerOf(
                    hit.getDirection().getNormal());
            Vec3 anchor = hit.getBlockPos().getCenter()
                    .add(normal.scale(0.5))
                    .add(0.0, 0.5, 0.0);
            initializeAnchor(anchor, direction.normalize());
            return true;
        }
        return false;
    }

    /// 设置根部和静止伸展方向。
    public void initializeAnchor(Vec3 anchor, Vec3 restDirection) {
        if (restDirection.lengthSqr() < 1.0E-8) {
            throw new IllegalArgumentException(
                    "Snatcher rest direction must not be zero");
        }
        entityData.set(ANCHOR, anchor.toVector3f());
        entityData.set(REST_DIRECTION,
                restDirection.normalize().toVector3f());
        entityData.set(ANCHORED, true);
    }

    public boolean isAnchored() {
        return entityData.get(ANCHORED);
    }

    public Vec3 getAnchor() {
        return new Vec3(entityData.get(ANCHOR));
    }

    public Vec3 getRestDirection() {
        return new Vec3(entityData.get(REST_DIRECTION)).normalize();
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return isAnchored()
                ? new AABB(position(), getAnchor())
                : super.getBoundingBoxForCulling().inflate(10.0);
    }

    /// 捕人草在 1.21 中使用独立的五 tick 接触检测与 0.3 格扩展范围。
    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected int contactDetectionInterval() {
        return 5;
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
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean(ANCHORED_TAG)) {
            initializeAnchor(
                    new Vec3(
                            tag.getDouble(ANCHOR_X_TAG),
                            tag.getDouble(ANCHOR_Y_TAG),
                            tag.getDouble(ANCHOR_Z_TAG)),
                    new Vec3(
                            tag.getDouble(REST_X_TAG),
                            tag.getDouble(REST_Y_TAG),
                            tag.getDouble(REST_Z_TAG)));
        }
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
    }

    private static List<Vec3> createSearchDirections() {
        List<Vec3> directions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        directions.add(
                                new Vec3(x, y, z));
                    }
                }
            }
        }
        return List.copyOf(directions);
    }
}

package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.animation.LashAnimation;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.entity.ai.keyframe.dynamic_curve.SplineKeyframeDynamicCurve;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class WhipEntity extends AbstractHurtingProjectile {

    // todo 更换贴图和模型
    public ResourceLocation texture = Confluence.asResource("textures/entity/whip.png");

    int existTick = 20;
    Vec3KeyframeAnimation tail;

    // 初始位置
    public Vec3 initialPosition;
    // 初始方向
    public Vec3 initDirection;
    // 关键点位置，用于插值
    public List<Vector3f> keyPositions;
    // 用于客户端插值
    public List<Vector3f> keyPositionsO;
    // 关键点的本地坐标关键帧
    List<Vec3KeyframeAnimation> parts;
    // 关键点插值器
    public SplineKeyframeDynamicCurve<Vec3KeyframeAnimation> interpolator;

    public static final EntityDataAccessor<Vector3f> DATA_INITIAL_POSITION = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DATA_INITIAL_DIRECTION = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.VECTOR3);

    public WhipEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level level) {
        super(entityType, level);

        tail = Vec3KeyframeAnimation.fromAnimation(LashAnimation.animation.boneAnimations().get("bone1").getFirst());
        parts = List.of(
                tail,
                Vec3KeyframeAnimation.fromAnimation(LashAnimation.animation.boneAnimations().get("bone2").getFirst())

        );
        keyPositions = new ArrayList<>();
        keyPositionsO = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            keyPositions.add(new Vector3f());
            keyPositionsO.add(new Vector3f());
        }
        interpolator = new SplineKeyframeDynamicCurve<>(parts);
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            if (existTick < tickCount) {
                discard();
                return;
            }
        }
        if (getOwner() != null) {
            if (initialPosition != null && initDirection != null) {
                // 计算关键点位置
                float yaw = (float) (Math.PI - Math.atan2(initDirection.z, initDirection.x));
                float pitch = (float) (-Math.atan2(initDirection.y,
                        Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z)));
                Quaternionf q = new Quaternionf()
                        .rotateY(yaw)
                        .rotateZ(pitch);
                for (int i = 0; i < parts.size(); i++) {
                    // 世界坐标变换
                    Vec3KeyframeAnimation p = parts.get(i);
                    Vec3 pos = p.cal(tickCount).multiply(1, -1, 1);
                    Vector3f lp = pos.toVector3f();
                    q.transform(lp);
                    keyPositionsO.set(i, keyPositions.get(i));
                    keyPositions.set(i, lp);
                }

                if(!level().isClientSide){
                    List<Vec3> attackPoints = keyPositions.stream().map(Vec3::new).toList();
                    if(tickCount > 10){
                        // 过渡到player位置
                        double lerpx = Mth.lerp((tickCount - 10) / 10.0f, getX(), getOwner().getX());
                        double lerpy = Mth.lerp((tickCount - 10) / 10.0f, getY(), getOwner().getY());
                        double lerpz = Mth.lerp((tickCount - 10) / 10.0f, getZ(), getOwner().getZ());
                        setPos(lerpx, lerpy, lerpz);
                    }
                    // 攻击
                    float range = 2f;
                    for (Vec3 attackPoint : attackPoints) {
                        Vec3 pos = attackPoint.add(initialPosition);
                        AABB aabb = new AABB(pos.x - range, pos.y - range, pos.z - range,
                                pos.x + range, pos.y + range, pos.z + range);
                        for (var entity : level().getEntities(this, aabb, e -> e != getOwner())) {
                            if (entity instanceof LivingEntity) {
                                entity.hurt(damageSources().magic(), 1);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_INITIAL_POSITION, new Vector3f(0, 0, 0));
        builder.define(DATA_INITIAL_DIRECTION, new Vector3f(0, 0, 0));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> var1){
        if(level().isClientSide){
            if (var1 == DATA_INITIAL_POSITION) {
                initialPosition = new Vec3(this.entityData.get(DATA_INITIAL_POSITION));
            } else if (var1 == DATA_INITIAL_DIRECTION) {
                initDirection = new Vec3(this.entityData.get(DATA_INITIAL_DIRECTION));
            }
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        this.setDeltaMovement(0,0,0);
        this.initialPosition = position();
        this.initDirection = new Vec3(f, f1, f2);
        this.entityData.set(DATA_INITIAL_POSITION, initialPosition.toVector3f());
        this.entityData.set(DATA_INITIAL_DIRECTION, initDirection.toVector3f());
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return false;
    }

}

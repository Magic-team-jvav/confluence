package org.confluence.mod.common.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.entity.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;

import java.util.List;

public class DeadBodyPartEntity extends Entity {
    // 这些注解就是为了照顾那个永远用不到的构造方法
    @Nullable
    public final Entity dyingEntity;
    @Nullable
    public final Object cube;
    public List<Vector3f> boneRots;
    public List<Vector3f> bonePivots;
    public Vector3f boneOffset;
    public Vector3f modelPartRot;
    public int lifetime;
    public float rotX;
    public float rotY;
    public float rotZ;
    public float xOffset;
    public float yOffset;
    public float zOffset;
    public float minSide;
    public int animTick;
    public boolean stop = false;
    /**
     * true则停在原地不动，一般用来调试
     */
    public boolean still = false;
    public ModelPart modelPart;
    /**
     * 目前如果非空则说明是盔甲
     */
    public ResourceLocation texture;
    protected final EntityDimensions dimensions;

    public DeadBodyPartEntity(EntityType<?> entityType, Level level) {
        this(entityType, level, null, null, 0);
    }

    public DeadBodyPartEntity(EntityType<?> entityType, Level level, @Nullable Entity dyingEntity, @Nullable Object cube, float deathSpeed) {
        this(entityType, level, dyingEntity, cube, deathSpeed, 0.4f);
    }

    public DeadBodyPartEntity(EntityType<?> entityType, Level level, @Nullable Entity dyingEntity, @Nullable Object cube, float deathSpeed, float minSide) {
        super(entityType, level);
        this.dyingEntity = dyingEntity;
        this.cube = cube;
        this.minSide = minSide;
        if (dyingEntity instanceof AbstractTerraBossBase || dyingEntity instanceof Boss) {
            lifetime = level.random.nextInt(60, 75);
        } else {
            lifetime = level.random.nextInt(20, 30);
        }
//        lifetime = 200;
        float speed = deathSpeed * 1.36f + 1.5f;
        // 只转两个轴，但是看起来好像还是转了3个轴
        int stay = lifetime % 3;
        if (stay != 0) {
            rotX = level.random.nextFloat() * speed * 2 - speed;
        }
        if (stay != 1) {
            rotY = level.random.nextFloat() * speed * 2 - speed;
        }
        if (stay != 2) {
            rotZ = level.random.nextFloat() * speed * 2 - speed;
        }

        // 根据cube大小调整碰撞箱大小
        switch (cube) {
            case GeoCube geoCube -> {
                Vec3 size = geoCube.size();
                float min = (float) Math.max(0.1, Math.min(Math.min(size.x, size.y), size.z) / 16);
                this.dimensions = EntityDimensions.fixed(min, min);
            }
            case GeoBone bone -> {
                float finalMin = Float.POSITIVE_INFINITY;
                for (GeoCube boneCube : bone.getCubes()) {
                    Vec3 size = boneCube.size();
                    float min = (float) Math.max(0.1, Math.min(Math.min(size.x, size.y), size.z) / 16);
                    if (min <= 0.1) {
                        finalMin = 0.1f;
                        break;
                    }
                    finalMin = Math.min(finalMin, min);
                }
                this.dimensions = EntityDimensions.fixed(finalMin, finalMin);
            }
            case null, default -> this.dimensions = EntityDimensions.fixed(minSide, minSide);
        }
//        still();

        setId(getId() + 0x3F3F3F3F);
    }

    public void still() {
        still = true;
        lifetime = 100;
    }

    @Override
    public void tick() {
        if (tickCount >= lifetime) {
            discard();
            return;
        }
        refreshDimensions();
        if (still) return;
        applyGravity();
        move(MoverType.SELF, getDeltaMovement());
        // 摩擦力
        if (this.onGround()) {
            BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
            float friction = this.level().getBlockState(groundPos).getFriction(level(), groundPos, this) * 0.98F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 0.98, friction));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1, 0.9));
        }
        // TODO: 液体
//        updateFluidHeightAndDoFluidPushing();

        // 撞墙停转
        if (tickCount > 3 && !stop && (onGround() || verticalCollision || verticalCollisionBelow || horizontalCollision)) {
            stop = true;
        }
        if (!stop) {
            animTick = tickCount;
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.dimensions;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }
}

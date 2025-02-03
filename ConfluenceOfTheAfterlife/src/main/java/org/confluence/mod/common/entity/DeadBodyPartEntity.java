package org.confluence.mod.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;

import java.util.List;

public class DeadBodyPartEntity extends Entity {
    // 这些注解就是为了照顾那个永远用不到的构造方法
    @Nullable
    public final Entity dyingEntity;
    @Nullable
    public final Object bone;
    public List<Vector3f> boneRots;
    public List<Vector3f> bonePivots;
    public Vector3f boneOffset;
    private final int lifetime;
    public float rotX;
    public float rotY;
    public float rotZ;
    public int animTick;
    public boolean stop = false;

    public DeadBodyPartEntity(EntityType<?> entityType, Level level){
        this(entityType, level, null, null, 0);
    }

    public DeadBodyPartEntity(EntityType<?> entityType, Level level, @Nullable Entity dyingEntity, @Nullable Object bone, float deathSpeed){
        super(entityType, level);
        this.dyingEntity = dyingEntity;
        this.bone = bone;
        if(dyingEntity instanceof AbstractTerraBossBase || dyingEntity instanceof Boss){
            lifetime = level.random.nextInt(60, 75);
        }else{
            lifetime = level.random.nextInt(20, 30);
        }
        float speed = deathSpeed * 1.36f + 1.5f;
        // 只转两个轴，但是看起来好像还是转了3个轴
        int stay = lifetime % 3;
        if(stay != 0){
            rotX = level.random.nextFloat() * speed * 2 - speed;
        }
        if(stay != 1){
            rotY = level.random.nextFloat() * speed * 2 - speed;
        }
        if(stay != 2){
            rotZ = level.random.nextFloat() * speed * 2 - speed;
        }
    }

    @Override
    public void tick(){
        if(tickCount >= lifetime){
            discard();
            return;
        }
        refreshDimensions();
        applyGravity();
        move(MoverType.SELF,getDeltaMovement());
        // 摩擦力
        if (this.onGround()) {
            BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
            float friction = this.level().getBlockState(groundPos).getFriction(level(), groundPos, this) * 0.98F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 0.98, friction));
        }else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1, 0.9));
        }

        // 撞墙停转
        if(tickCount > 3 && !stop && (onGround() || verticalCollision || verticalCollisionBelow || horizontalCollision)){
            stop = true;
        }
        if(!stop){
            animTick = tickCount;
        }
    }

    @Override
    protected double getDefaultGravity(){
        return 0.05;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose){
        // 根据cube大小调整碰撞箱大小
        if(bone instanceof GeoCube cube){
            Vec3 size = cube.size();
            float min = (float) Math.max(0.1, Math.min(Math.min(size.x, size.y), size.z) / 16);
            return EntityDimensions.fixed(min, min);
        }

        return EntityDimensions.fixed(0.4f, 0.4f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder){

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound){

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound){

    }
}

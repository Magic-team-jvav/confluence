package org.confluence.terraentity.entity.proj;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.api.entity.animation.Curve;
import org.confluence.terraentity.entity.ai.motion.curve.Bezier3Curve;
import org.joml.Vector3f;


public class ThrowableProj extends BaseProj<ThrowableProj> {
    private float step = 0f;
    private Vec3 controlPos;
    private Vec3 targetPos;
    private Curve curse;

    public ThrowableProj(EntityType<? extends ThrowableProj> pEntityType, Level pLevel) {
        this(pEntityType,pLevel,null);
    }

    public ThrowableProj(EntityType<? extends ThrowableProj> pEntityType, Level pLevel, MobEffectInstance pEffect) {
        super(pEntityType,pLevel, pEffect);
    }


    public ThrowableProj setControlPosPos(Vec3 targetPos) {
        this.controlPos = targetPos;
        return this;
    }
    public ThrowableProj setTargetPos(Vec3 targetPos) {
        this.targetPos = targetPos;
        return this;
    }


    public Vec3 getNexPos(){
        step += 0.05f;
        return curse.cal(step);
    }

    @Override
    public int getLifetime() {return 100;}

    public static final EntityDataAccessor<Vector3f> DATA_HEIGHT = SynchedEntityData.defineId(ThrowableProj.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> DATA_TARGET = SynchedEntityData.defineId(ThrowableProj.class, EntityDataSerializers.VECTOR3);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HEIGHT, new Vector3f(0,0,0));
        builder.define(DATA_TARGET, new Vector3f(0,0,0));

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
        super.onSyncedDataUpdated(var1);
        if (var1 == DATA_HEIGHT) {
            controlPos = new Vec3(this.entityData.get(DATA_HEIGHT));
        }
        if (var1 == DATA_TARGET) {
            targetPos = new Vec3(this.entityData.get(DATA_TARGET));
        }
    }

    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        if(!level().isClientSide) {
            if(targetPos==null) {
                discard();
                return;
            }
            if(controlPos==null) this.controlPos = position().add(targetPos).multiply(0.5f,0.5f,0.5f).add(0,10f,0);
            this.entityData.set(DATA_HEIGHT, new Vector3f((float) controlPos.x,(float) controlPos.y,(float) controlPos.z));
            this.entityData.set(DATA_TARGET, new Vector3f((float)targetPos.x,(float)targetPos.y,(float)targetPos.z));
        }
    }

    @Override
    public void tick(){
        super.tick();

        if(curse==null) {
            if(targetPos==null || controlPos ==null) return;

            curse = new Bezier3Curve(position(), controlPos,targetPos);
        }
        this.setPos(getNexPos());
    }

}

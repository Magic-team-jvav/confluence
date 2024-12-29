package org.confluence.terraentity.entity.boss;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.utils.TEUtils;


public class EaterOfWorldSegment extends AbstractTerraBossBase {
    private static final float MAX_HEALTHS = 50f;
    private static final float DAMAGE = 4f;//接触伤害


    public float segmentInternal = 1f;
    public EaterOfWorld head;
    public AbstractTerraBossBase lastSegment;

    //public int segmentIndex;
    public boolean genNewHeadOnRemove = true;
    public boolean ifTail = false;
    public static final EntityDataAccessor<Boolean> DATA_TAIL = SynchedEntityData.defineId(EaterOfWorldSegment.class, EntityDataSerializers.BOOLEAN);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TAIL, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
         if(key == DATA_TAIL){
             ifTail = entityData.get(DATA_TAIL);
         }
    }
    public void setHead(EaterOfWorld head){
        this.head = head;
    }
    public void setLastSegment(AbstractTerraBossBase lastSegment){

        this.lastSegment = lastSegment;
    }


    public EaterOfWorldSegment(EntityType<? extends Monster> type, Level level) {
        super(type, level,MAX_HEALTHS);
        this.noPhysics = true;
    }

    public EaterOfWorldSegment(EaterOfWorld head, Level level) {
        super(TEEntities.EATER_OF_WORLD_SEGMENT.get(), level, MAX_HEALTHS);
        this.head = head;
        this.noPhysics = true;

    }

    public Vec3 getNextPos(){
        if(distanceToSqr(lastSegment)<1) return position();
        this.lookAt(lastSegment,500,500);
        Vec3 newPos = lastSegment.position().add(position().subtract(lastSegment.position()).normalize().scale(segmentInternal));
        return newPos;
    }

    public boolean isNoGravity(){
        return true;
    }

    @Override
    public void addSkills() { }

    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public void tick(){
        super.tick();
        if(!level().isClientSide) {
            if (lastSegment != null && lastSegment.isAlive())
                this.setPos(getNextPos());
        }
    }

    public boolean shouldShowBossBar(){return false;};

}

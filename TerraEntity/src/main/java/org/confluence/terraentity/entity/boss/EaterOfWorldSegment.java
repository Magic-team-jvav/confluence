package org.confluence.terraentity.entity.boss;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TESounds;


public class EaterOfWorldSegment extends AbstractTerraBossBase {
    private static final float MAX_HEALTHS = 50f;
    private static final float DAMAGE = 4f;//接触伤害

    float discardTimer = 0f;
    public float segmentInternal = 1f;
    public EaterOfWorld head;
    public AbstractTerraBossBase lastSegment;

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
        setAttactDamage(DAMAGE);
    }

    public EaterOfWorldSegment(EaterOfWorld head, Level level) {
        this(TEEntities.EATER_OF_WORLD_SEGMENT.get(), level);
        this.head = head;
    }

    public Vec3 getNextPos(){
        if(distanceToSqr(lastSegment)<1f) return position();
        this.lookAt(lastSegment,500,500);
        Vec3 newPos = lastSegment.position().add(position().subtract(lastSegment.position()).normalize().scale(segmentInternal));
        return newPos;
    }

    public boolean isNoGravity(){
        return true;
    }

    @Override
    public void addSkills() { }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean shouldBeSaved(){
        return false;
    }

    @Override
    public void tick(){
        super.tick();
        if(!level().isClientSide) {
            if (lastSegment != null && lastSegment.isAlive())
                this.setPos(getNextPos());
            if(head==null || !head.isAlive()){
                discardTimer += 1f;
                if(discardTimer>20f) discard();
            }
        }
    }
    @Override // 受伤音效
    protected SoundEvent getHurtSound(DamageSource damageSource) {return TESounds.ROUTINE_HURT.get();}

    @Override
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    public boolean shouldShowBossBar(){return false;};

}

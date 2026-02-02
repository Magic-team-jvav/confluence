package org.confluence.terraentity.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.terraentity.entity.ai.fsm.MobSkill;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 克脑残影
 */
public class BrainFake extends BrainOfCthulhu {

    public int tag;
    BrainOfCthulhu owner;
    MobSkill<BrainFake> first_spawn;

    public BrainFake(EntityType<BrainFake> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        if(!level.isClientSide())
            this.setBoundingBox(new AABB(0, 0,0,0,0,0));

        collisionProperties.detectInternal = 9999;
    }

    public static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(BrainFake.class, EntityDataSerializers.INT);

    public void setOwner(BrainOfCthulhu owner) {
        this.owner = owner;
        this.getEntityData().set(DATA_OWNER_ID, owner.getId());
    }

    @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        return false;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_ID, 0);
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if(level().isClientSide()  && key == DATA_OWNER_ID){
            owner = (BrainOfCthulhu) level().getEntity(this.getEntityData().get(DATA_OWNER_ID));
        }

    }

    @Override
    public boolean isMainBody(){
        return false;
    }

    @Override
    public void addSkills() {
        RawAnimation open = RawAnimation.begin().thenPlay("open");
        first_spawn = new MobSkill<BrainFake>(open, 50, 20);
        addSkill(first_spawn);
    }

    @Override
    public boolean shouldShowBossBar() {
        return false;
    }

    @Override
    public float getFadeProgress(){
        if(owner != null) {
            float v =  Math.min(1 - (owner.getHealth() / owner.getMaxHealth()) * 0.5f,owner.getFadeProgress());
            return v;
        }
        return 1;
    }

    @Override
    public float getDissolveProgress(){
        if(owner != null)
            return owner.getDissolveProgress();
        return 1;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount > 10 && owner == null || owner!= null && !owner.isAlive()){
            discard();
            return;
        }
        if(!level().isClientSide() && owner!= null && owner.isAlive()){
            if (owner.target == null) {
                setPos(owner.getX(), owner.getY(), owner.getZ());
                return;
            }
            if(!level().isClientSide()){
                setTarget(owner.target);
                lookAt(10);
                double y = owner.getY();
                double x;
                double z;

                x = (tag & 1) == 1? owner.target.getX() * 2 - owner.getX() : owner.getX();
                z = (tag & 2) == 2? owner.target.getZ() * 2 - owner.getZ() : owner.getZ();

                setPos(x, y, z);
            }
        }
    }

    @Override
    public boolean isNoGravity(){ return true; }

    // 转换阶段
    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

}

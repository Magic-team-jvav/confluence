package org.confluence.mod.common.entity.monster;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.prefab.AbstractPrefab;
import org.confluence.mod.common.init.ModSoundEvents;

/**
 * 克脑召唤的飞眼怪
 */
public class VisualNeuron extends AbstractMonster{

    private BrainOfCthulhu owner;
    public Vec3 homePos;
    public LivingEntity target;
    public boolean ready = true;
    private static final float MOVE_SPEED = 0.5f;
    private int backDelay = 5;

    // 0 为攻击， 1 为返回
    public int state = 1;
    public VisualNeuron(EntityType<? extends Monster> type, Level level) {
        super(type, level, new AbstractPrefab()
                .getPrefab().setNoGravity());
        this.noPhysics = true;
        this.collisionProperties = new CollisionProperties(1,20,0.2F);
    }

    public void setOwner(BrainOfCthulhu owner) {
        this.owner = owner;
    }

    public BrainOfCthulhu getOwner() {
        return owner;
    }

    public void attack(LivingEntity target){
        this.target = target;
        state = 0;
    }

    public void tick(){
        super.tick();

        if(!level().isClientSide &&  (owner == null || !owner.isAlive()))
            discard();

        if(!level().isClientSide && isAlive()){

            if(state == 0){
                ready = false;
                if(target != null && target.isAlive()) {
                    setTarget(target);
                    this.addDeltaMovement(target.getEyePosition().subtract(position()).normalize().scale(MOVE_SPEED/ 5));
                    if(TEUtils.angleBetween(getDeltaMovement(), target.getEyePosition().subtract(position())) > Math.PI / 4 ) {
                        state = 1;
                        backDelay = 5;
                    }
                }else {
                    state = 1;
                }
            }else {
                backDelay--;
                if(homePos != null){
                    if(position().distanceToSqr(homePos) < 4f)
                        ready = true;
                    addDeltaMovement(homePos.subtract(position()).normalize().scale(MOVE_SPEED / 10));
                }
            }

            if(target != null && target.isAlive()){
                lookAt(target, 30, 30);
            }else{
                lookAt(EntityAnchorArgument.Anchor.EYES, position().scale(2).subtract(owner.position()));
            }
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        return false;
    }

    @Override
    public void firstSpawn(){
        TEUtils.multiplePlayerEnhance(this);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

    }

    public boolean isReady() {
        return ready && state == 1;
    }

    public boolean hurt(DamageSource source, float amount) {
        if(state == 0) {
            state = 1;
            setDeltaMovement(owner.position().subtract(position()).normalize());
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.VISUAL_NEURON_DEATH.get();
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSoundEvents.VISUAL_NEURON_HURT.get();
    }

}

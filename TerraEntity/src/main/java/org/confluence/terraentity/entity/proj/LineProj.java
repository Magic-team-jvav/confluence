package org.confluence.terraentity.entity.proj;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LineProj extends BaseProj<LineProj> {
    private int existTick;

    @Override
    public int getLifetime() {
        return existTick;
    }

    public LineProj(EntityType<? extends LineProj> pEntityType, Level pLevel) {
        this(pEntityType,pLevel,(MobEffectInstance)null);
    }
    public LineProj(EntityType<? extends LineProj> pEntityType, Level pLevel,MobEffectInstance effect) {
        super(pEntityType,pLevel,effect);
        this.existTick = 20 * 5;
    }
    public LineProj(EntityType<? extends LineProj> pEntityType, Level pLevel,List<MobEffectInstance> effects) {
        super(pEntityType,pLevel,effects);
        this.existTick = 20 * 5;
    }

    public LineProj setExistTick(int existTick) {
        this.existTick = existTick;
        return this;
    }


    @Override
    public void tick(){

        Vec3 vec3 = this.warpSpeed(initSpeed);
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;

        setDeltaMovement(vec3);
        setPos(offX, offY, offZ);
        super.tick();
    }

    protected Vec3 warpSpeed(Vec3 speed) {
        return speed;
    }

    @Override
    public void onAddedToLevel(){
        super.onAddedToLevel();
        this.setDeltaMovement(getDeltaMovement().scale(0.01f));
    }
}

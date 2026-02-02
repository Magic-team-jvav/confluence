package org.confluence.terraentity.entity.animation;

import net.minecraft.util.Mth;
import org.joml.Math;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * 状态机优化骨骼动画插值
 */
public class BoneStateMachine<S> extends AbstractStateMachine<GeoBone, S> {

    private double curRotX;
    private double curRotY;
    private double curRotZ;


    private double transitionTime = 5;
    public  double partialTickTotal;

    public double fromRotX;
    public double fromRotY;
    public double fromRotZ;

    private double toRotX;
    private double toRotY;
    private double toRotZ;


    public BoneStateMachine(S initialState){
        super(initialState);
    }


    public void apply(double partialTick, GeoBone bone){
        updateRot(partialTick);
        applyRot(bone);
    }

    public void updateState(S state, double transitionTime, double toRotX, double toRotY, double toRotZ){
        setState(state);
        setTransitionTime(transitionTime);
        setToRot(toRotX, toRotY, toRotZ);
    }

    public void updateState(double transitionTime, double toRotX, double toRotY, double toRotZ){
        setTransitionTime(transitionTime);
        setToRot(toRotX, toRotY, toRotZ);
    }

    private void updateRot(double partialTick){
        partialTickTotal += partialTick;
        this.curRotX = lerp(partialTickTotal, transitionTime, fromRotX, toRotX);
        this.curRotY = lerp(partialTickTotal, transitionTime, fromRotY, toRotY);
        this.curRotZ = lerp(partialTickTotal, transitionTime, fromRotZ, toRotZ);
    }
    private void applyRot(GeoBone bone){
        bone.setRotX((float) curRotX);
        bone.setRotY((float) curRotY);
        bone.setRotZ((float) curRotZ);
    }

    public void setState(S state){
        if(state!= this.state){
            this.fromRotX = this.curRotX;
            this.fromRotY = this.curRotY;
            this.fromRotZ = this.curRotZ;
            this.partialTickTotal = 0;
        }
        this.state = state;
    }


    private void setTransitionTime(double transitionTime){
        this.transitionTime = transitionTime;
    }
    private void setToRot(double toRotX,double toRotY,double toRotZ){
        this.toRotX = toRotX;
        this.toRotY = toRotY;
        this.toRotZ = toRotZ;
    }

    private double lerp(double partialTickTotal, double transitionTime, double start, double end){
        return Mth.lerp(Math.min(partialTickTotal  / transitionTime,1), start, end);
    }

}

package org.confluence.terraentity.client.post;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.ai.keyframe.Keyframe;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.mixin.accessor.CameraAccessor;

/**
 * boss召唤镜头过场动画
 */
public enum BossSpawnCameraManager {
    INSTANCE;

    Vec3KeyframeAnimation animation;
    Vec3 backPosFrom;
    float time;
    float backTime;
    float _backTime;

    private void setPos(Camera camera, Vec3 pos){
        ((CameraAccessor)camera).callSetPosition(pos);
    }

    private void setPos(Camera camera, double x, double y, double z){
        ((CameraAccessor)camera).callSetPosition(x, y, z);
    }

    public boolean isAnimating(){
        return animation!= null || backTime > 0;
    }

    public void reset(){
        this.animation = null;
        this.backPosFrom = null;
        this.time = 0;
        this.backTime = 0;
        this._backTime = 0;
    }

    public void update(float time){
        if(Minecraft.getInstance().isPaused()){
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if(animation!= null){
            this.time += time;
            double endTime = animation.getEndTime();
            if(endTime >= this.time) {
                Vec3 pos = animation.cal(this.time);
                setPos(camera, pos);
            }else{
                this.time = 0;
                this.backPosFrom = animation.cal(endTime);
                setPos(camera, backPosFrom);
                animation = null;
            }
        }else{
            if(this.backTime > 0 && this.backPosFrom != null){
                this.backTime -= time;
                Vec3 lerp = this.backPosFrom.lerp(
                        new Vec3(Minecraft.getInstance().player.xo, Minecraft.getInstance().player.yo, Minecraft.getInstance().player.zo)
                                .lerp(Minecraft.getInstance().player.position(), time )
                                .add(0,Minecraft.getInstance().player.getEyeHeight(),0),
                        Mth.clamp(1- this.backTime / _backTime, 0, 1));
                setPos(camera, lerp.x, lerp.y, lerp.z);
            }
        }
    }


    public void bakeBossSpawn(LivingEntity entity, float distance){
        this.bakeBossSpawn(entity.getEyePosition(), distance);
    }

    public void bakeBossSpawn(Vec3 targetPos, float distance){
        if(animation == null) {
            Vec3 startPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Vec3 dir = targetPos.subtract(startPos);
            Vec3 endPos = targetPos.subtract(dir.normalize().scale(distance));
            float tend = 15;
            Vec3KeyframeAnimation.Builder builder = Vec3KeyframeAnimation.builder()
                    .addKeyframe(new Keyframe(0,0,0,tend,0,tend), startPos)
                    .addKeyframe(new Keyframe(50, 0, 0, tend, 0, tend), endPos)
                    .addKeyframe(70, endPos);
            this.animation = builder.build();
            this.time = 0;
            this.backTime = 20;
            this._backTime = 20;
        }
    }
}

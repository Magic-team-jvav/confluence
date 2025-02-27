package org.confluence.mod.common.item.sword.stagedy.projectile;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.confluence.mod.common.init.ModEntities;

import java.util.ArrayList;
import java.util.List;

public class StarFuryProjContainer extends AbstractProjContainer {
    protected float maxAngle = 30;//索敌最大角度
    protected float range = 30;//索敌范围
    protected float predict = 10;//预判量
    protected float inAccuracy = 0.5f;
    protected float offsetV = 20;//发射时的高度偏移
    protected float offsetH = 5;//发射时的xy偏移
    protected float getOffsetV() {
        return offsetV;
    };

    protected float getOffsetH() {
        return offsetH;
    };

    protected void init(){};

    public StarFuryProjContainer(){
        init();
    }
    public StarFuryProjContainer(float damage,float knockBack,int cd,float v,SoundEvent sound){
        super(damage,knockBack,cd,v,sound);
        init();
    }


    @Override
    public Projectile getProjectile(Player player, ItemStack weapon) {
        Projectile proj = ModEntities.STAR_FURY_PROJECTILE.get().create(player.level()).addKnockBack(knockBack).addAttackDamage(damage);
        proj.setOwner(player);
        return proj;
    }

    @Override
    public void genProjectile(Player owner, ItemStack weapon) {
        owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), getSound(), SoundSource.AMBIENT, 1.0F, 1.0F);
        Vec3 eye = owner.getEyePosition();
        LivingEntity target = getTargets(eye,eye.add(owner.getForward().normalize().scale(range)),owner.level(), owner);
        Vec3 waveTarget;
        float angle;
        float actualInaccuracy;
        if(target!=null){
            //周围有目标 预判
            waveTarget = target.getEyePosition().add(target.getDeltaMovement().scale(predict));
            //根据夹角减少不精准度
            angle = (float) angle(target.getEyePosition().subtract(owner.getEyePosition()),owner.getForward());
            actualInaccuracy = inAccuracy * Mth.lerp(angle/maxAngle,0,inAccuracy) * 5;
        }else{
            //周围无目标 获取视线指向点
            Vec3 ori = owner.getEyePosition().add(0,1,0);
            Vec3 end = ori.add(owner.getForward().normalize().scale(range));
            BlockHitResult blockHitResult = owner.level().clip(new ClipContext(ori,end, ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE, owner));
            waveTarget = blockHitResult.getLocation();
            //取中值
            actualInaccuracy = inAccuracy / 2;
        }
        var proj = getProjectile(owner,weapon);
        proj.setPos(waveTarget.add(Math.random() * getOffsetH() - getOffsetH(), getOffsetV() ,Math.random() * getOffsetH() - getOffsetH()));
        proj.shoot(waveTarget.x - proj.getX(),waveTarget.y- proj.getY(),waveTarget.z - proj.getZ(),getBaseVelocity(),actualInaccuracy);
        owner.level().addFreshEntity(proj);
    }


    private LivingEntity getTargets(Vec3 ori, Vec3 end, Level level, Entity entity){
        //扩大包围盒
        AABB range = entity.getBoundingBox().inflate(this.range);
        List<HitResult> hits = new ArrayList<>();
        List<HitResult> subHits = new ArrayList<>();
        List<? extends Entity> entities = level.getEntities(entity,range,entity1 -> entity1.isPickable() && entity1.isAlive());
        for(var e : entities){
            //获取视线交点
            Vec3 vec3 = e.getBoundingBox().clip(ori,end).orElse(null);
            //优先指向的目标
            if(vec3!=null){
                //System.multiOut.println("point directly");
                EntityHitResult entityHitResult = new EntityHitResult(e,vec3);
                hits.add(entityHitResult);
            }//自瞄其他夹角小于一定度数的目标
            else if(hits.isEmpty() && angle(e.position().subtract(ori),end.subtract(ori)) < maxAngle *  Math.PI/180){
                EntityHitResult entityHitResult = new EntityHitResult(e,e.position());
                subHits.add(entityHitResult);
            }
        }


        if(!hits.isEmpty()){
            //射线命中的目标 按距离排序
            hits.sort((o1,o2)->o1.getLocation().distanceToSqr(ori) < o2.getLocation().distanceToSqr(ori)?-1:1);
            HitResult hitResult = hits.get(0);
            if(hitResult instanceof  EntityHitResult entityHitResult &&
                    entityHitResult.getEntity() instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }else if(!subHits.isEmpty()){
            //未命中的目标 按距离排序
            subHits.sort((o1,o2)->o1.getLocation().distanceToSqr(ori) < o2.getLocation().distanceToSqr(ori)?-1:1);

            HitResult hitResult = subHits.get(0);
            if(hitResult instanceof  EntityHitResult entityHitResult &&
                    entityHitResult.getEntity() instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }
        return null;
    }

    public double angle(Vec3 line1,Vec3 line2){
        return Math.acos(line1.dot(line2)/line1.length()/line2.length());
    }

}

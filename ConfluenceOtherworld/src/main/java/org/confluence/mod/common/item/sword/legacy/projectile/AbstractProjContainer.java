package org.confluence.mod.common.item.sword.legacy.projectile;

import net.minecraft.sounds.SoundEvent;

/**
 * 弹幕容器定义弹幕类别、弹幕的基本属性和生成位置
 */
public abstract class AbstractProjContainer implements IProjContainer {
    public float damage;
    public float knockBack;
    public int cd;
    public float v;
    public SoundEvent sound;

    // 用于特定伤害判断逻辑
    public AbstractProjContainer(){
        this(0,0,0,0,null);
    }

    public AbstractProjContainer(float damage,float knockBack,float cd,float v){
        this(damage,knockBack,cd,v,null);
    }
    public AbstractProjContainer(float damage,float knockBack,float cd,float v,SoundEvent sound){
        this.damage = damage;
        this.knockBack = knockBack;
        this.cd = (int) cd;
        this.v = v;
        this.sound = sound;
    }

    public int getCooldown(){
        return cd;
    }
    public float getDamage(){
        return damage;
    }

    public float getBaseVelocity(){
        return v;
    }

    public SoundEvent getSound(){
        return sound;
    }

    public AbstractProjContainer setDamage(float damage){
        this.damage = damage;
        return this;
    }

    public AbstractProjContainer setCooldown(int cd){
        this.cd = cd;
        return this;
    }

    public AbstractProjContainer setVelocity(float v){
        this.v = v;
        return this;
    }

    public AbstractProjContainer setSound(SoundEvent sound){
        this.sound = sound;
        return this;
    }

}

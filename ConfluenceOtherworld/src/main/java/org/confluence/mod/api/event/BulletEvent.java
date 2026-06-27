package org.confluence.mod.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.BaseBullet;

public abstract class BulletEvent extends Event {
    private final BaseBulletEntity bulletEntity;
    private final BaseBullet bullet;

    public BulletEvent(BaseBulletEntity bulletEntity, BaseBullet bullet) {
        this.bulletEntity = bulletEntity;
        this.bullet = bullet;
    }

    public BaseBulletEntity getBulletEntity() {
        return bulletEntity;
    }

    public BaseBullet getBullet() {
        return bullet;
    }

    public static class Tick extends BulletEvent {
        public Tick(BaseBulletEntity bulletEntity, BaseBullet bullet) {
            super(bulletEntity, bullet);
        }

        public static class Pre extends BulletEvent {
            public Pre(BaseBulletEntity bulletEntity, BaseBullet bullet) {
                super(bulletEntity, bullet);
            }
        }

        public static class Post extends BulletEvent {
            public Post(BaseBulletEntity bulletEntity, BaseBullet bullet) {
                super(bulletEntity, bullet);
            }
        }
    }

    @Cancelable
    public static class HitEvent extends BulletEvent {
        private final HitResult hitResult;

        public HitEvent(BaseBulletEntity bulletEntity, BaseBullet bullet, HitResult hitResult) {
            super(bulletEntity, bullet);
            this.hitResult = hitResult;
        }

        public HitResult getHitResult() {
            return hitResult;
        }

        public static class Block extends HitEvent {
            public Block(BaseBulletEntity bulletEntity, BaseBullet bullet, BlockHitResult hitResult) {
                super(bulletEntity, bullet, hitResult);
            }
        }

        public static class Entity extends HitEvent {
            public Entity(BaseBulletEntity bulletEntity, BaseBullet bullet, EntityHitResult hitResult) {
                super(bulletEntity, bullet, hitResult);
            }
        }
    }

    public static class PenetrateEvent extends BulletEvent {
        private int penetrate;

        public PenetrateEvent(BaseBulletEntity bulletEntity, BaseBullet bullet, int penetrate) {
            super(bulletEntity, bullet);
            this.penetrate = penetrate;
        }

        public void setPenetrate(int penetrate) {
            this.penetrate = penetrate;
        }

        public int getPenetrate() {
            return penetrate;
        }
    }

    public static class KnockbackEvent extends BulletEvent {
        private float scale;
        private float motionY;

        public KnockbackEvent(BaseBulletEntity bulletEntity, BaseBullet bullet, float scale, float motionY) {
            super(bulletEntity, bullet);
            this.scale = scale;
            this.motionY = motionY;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public float getScale() {
            return scale;
        }

        public void setMotionY(float motionY) {
            this.motionY = motionY;
        }

        public float getMotionY() {
            return motionY;
        }
    }

    public static class DamageEntityEvent extends BulletEvent {
        private final Entity shooter;
        private final Entity target;

        public DamageEntityEvent(BaseBulletEntity bulletEntity, BaseBullet bullet, Entity shooter, Entity target) {
            super(bulletEntity, bullet);
            this.shooter = shooter;
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }

        public Entity getShooter() {
            return shooter;
        }
    }
}

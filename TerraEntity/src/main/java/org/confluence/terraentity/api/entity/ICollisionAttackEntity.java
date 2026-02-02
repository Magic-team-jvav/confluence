package org.confluence.terraentity.api.entity;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <h3>包围盒碰撞接口</h3>
 * 必须由Entity实现
 */
public interface ICollisionAttackEntity {

    default Entity collision$getSelf(){
        return (Entity) this;
    }

    CollisionProperties getCollisionProperties();

    // 开启碰撞伤害
    default boolean canCollisionHurt() {
        return true;
    }

    boolean shouldDoCollision();

    default void doCollisionAttack(Predicate<Entity> filter, Consumer<Entity> attackCallback){
        if(!this.shouldDoCollision() || this.collision$getSelf().level().isClientSide) return;
        CollisionProperties properties = this.getCollisionProperties();
        properties.reduceAttackInterval();
        if (this.canCollisionHurt() && !this.collision$getSelf().level().isClientSide && properties.canAttack()) {
            // 包围盒检测造成伤害
            List<Entity> entities = this.collision$getSelf().level().getEntities(this.collision$getSelf(), this.collision$getSelf().getBoundingBox().inflate(properties.attackRangeExtent), e-> e!= this.collision$getSelf());
            if (!entities.isEmpty()) {
                for (var e : entities) {
                    if (filter.test(e) ){
                        attackCallback.accept(e);
                        properties.rewind();
                    }
                }
            }else{
                properties.reDetect();
            }
        }
    }

    /**
     * AABB攻击检测属性
     */
    class CollisionProperties{
        public int detectInternal;
        public int attackInternal;
        public float attackRangeExtent;
        public int actualAttackInterval;

        /**
         * AABB攻击检测属性
         * @param detectInternal 检测的间隔
         * @param attackInternal 攻击的间隔
         * @param attackRangeExtent 攻击范围
         */
        public CollisionProperties(int detectInternal, int attackInternal, float attackRangeExtent) {
            this.detectInternal = detectInternal;
            this.attackInternal = attackInternal;
            this.attackRangeExtent = attackRangeExtent;
            this.actualAttackInterval = attackInternal;
        }

        /**
         * 重置攻击间隔
         */
        public void rewind() {
            actualAttackInterval = attackInternal;
        }

        /**
         * 重新检测间隔
         */
        public void reDetect() {
            actualAttackInterval = detectInternal;
        }

        /**
         * 减少攻击间隔
         */
        public void reduceAttackInterval() {
            actualAttackInterval --;
        }

        /**
         * 是否可以攻击
         */
        public boolean canAttack() {
            return actualAttackInterval <= 0;
        }

        public CollisionProperties setAttackInterval(int attackInterval){
            this.attackInternal = attackInterval;
            return this;
        }

        public CollisionProperties setDetectInterval(int detectInterval){
            this.detectInternal = detectInterval;
            return this;
        }

        public CollisionProperties setAttackRange(float attackRange) {
            this.attackRangeExtent = attackRange;
            return this;
        }
    }
}

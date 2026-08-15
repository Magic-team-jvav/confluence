package org.confluence.mod.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 微光物品转化事件，仅在服务端触发。
public abstract class ShimmerItemTransmutationEvent extends ItemEvent {
    protected int coolDown;
    protected int shrink = 0;
    protected double speedY;

    public ShimmerItemTransmutationEvent(ItemEntity source) {
        super(source);
        this.coolDown = source.lifespan;
        this.speedY = 0.1;
    }

    public ItemEntity getSource() {
        return getEntity();
    }

    /// 设置源物品堆需要消耗的数量，默认不消耗。
    public void setShrink(int count) {
        this.shrink = count;
    }

    public int getShrink() {
        return shrink;
    }

    /// 设置该物品实体下次允许转化前的冷却时间。
    ///
    /// 默认值取物品实体自身寿命；多数情况下为 6000 tick。
    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedY() {
        return speedY;
    }

    /// 物品实体刚进入微光并准备开始转化时触发。
    ///
    /// 该事件可取消；取消后不会进入本次转化流程。
    @Cancelable
    public static class Pre extends ShimmerItemTransmutationEvent {
        private int transformTime = 20;

        public Pre(ItemEntity source) {
            super(source);
        }

        /// 设置物品实体在真正转化前需要等待的时间。
        public void setTransformTime(int transformTime) {
            this.transformTime = transformTime;
        }

        public int getTransformTime() {
            return transformTime;
        }
    }

    /// 物品实体完成等待并尝试生成转化结果时触发。
    public static class Post extends ShimmerItemTransmutationEvent {
        private @Nullable List<ItemStack> targets;

        public Post(ItemEntity source) {
            super(source);
        }

        public void setTargets(@Nullable List<ItemStack> targets) {
            this.targets = targets;
        }

        /// 获取最终转化目标；若事件监听者未设置目标，系统会继续按内置规则生成。
        public @Nullable List<ItemStack> getTargets() {
            return targets;
        }
    }
}

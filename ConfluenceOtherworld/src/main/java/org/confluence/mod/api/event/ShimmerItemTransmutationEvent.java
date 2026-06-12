package org.confluence.mod.api.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// This event is Server side only.
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

    /// Shrink item stack's count. Defaults to 0.
    public void setShrink(int count) {
        this.shrink = count;
    }

    public int getShrink() {
        return shrink;
    }

    /// Determines how long could ItemEntity transmutation next time.
    ///
    /// Defaults to ItemEntity's lifespan(most of the time it is 6000).
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

    /// This event fired when an ItemEntity toss in shimmer.
    ///
    /// This event is [ICancellableEvent]
    public static class Pre extends ShimmerItemTransmutationEvent implements ICancellableEvent {
        private int transformTime = 20;

        public Pre(ItemEntity source) {
            super(source);
        }

        /// Determines how long should ItemEntity transmutation before.
        public void setTransformTime(int transformTime) {
            this.transformTime = transformTime;
        }

        public int getTransformTime() {
            return transformTime;
        }
    }

    /// This event fired when an ItemEntity trying to transmutation.
    public static class Post extends ShimmerItemTransmutationEvent {
        private @Nullable List<ItemStack> targets;

        public Post(ItemEntity source) {
            super(source);
        }

        public void setTargets(@Nullable List<ItemStack> targets) {
            this.targets = targets;
        }

        /// Determines what will ItemEntity transmutation to,
        ///
        /// If targets have not set yet, it will try to generate targets.
        public @Nullable List<ItemStack> getTargets() {
            return targets;
        }
    }
}

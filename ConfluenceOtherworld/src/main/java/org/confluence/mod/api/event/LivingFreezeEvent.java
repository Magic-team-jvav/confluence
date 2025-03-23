package org.confluence.mod.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public abstract class LivingFreezeEvent extends LivingEvent {
    public LivingFreezeEvent(LivingEntity entity) {
        super(entity);
    }


    public static class Pre extends LivingFreezeEvent {
        private boolean canFreeze = true;

        public Pre(LivingEntity entity) {
            super(entity);
        }

        public boolean canFreeze() {
            return canFreeze;
        }

        public Pre setCanFreeze(boolean canFreeze) {
            this.canFreeze = canFreeze;
            return this;
        }
    }

    public static class Post extends LivingFreezeEvent {
        public Post(LivingEntity entity) {
            super(entity);
        }
    }


}

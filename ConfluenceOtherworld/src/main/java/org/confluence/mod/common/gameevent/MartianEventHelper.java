package org.confluence.mod.common.gameevent;

import net.minecraft.world.entity.Entity;

/** 火星入侵事件中所有实体的共同成员判定契约。 */
public final class MartianEventHelper {
    public static final String ENTITY_TAG = "confluence:martian_madness";

    private MartianEventHelper() {}

    public static void markEventEntity(Entity entity) {
        if (entity != null) {
            entity.addTag(ENTITY_TAG);
        }
    }

    public static void inheritEventMembership(Entity parent, Entity child) {
        if (isMartianEventEntity(parent)) {
            markEventEntity(child);
        }
    }

    public static boolean isMartianEventEntity(Entity entity) {
        return entity != null && entity.getTags().contains(ENTITY_TAG);
    }
}

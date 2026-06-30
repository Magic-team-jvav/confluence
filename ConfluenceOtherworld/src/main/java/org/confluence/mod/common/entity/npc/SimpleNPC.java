package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 默认 NPC 实现 —— 覆盖 15 个标准 NPC（Guide, Merchant, Nurse...）。
 */
public class SimpleNPC extends BaseNPC {

    public SimpleNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }
}

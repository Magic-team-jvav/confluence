package org.confluence.mod.common.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;

/// 默认 NPC 实现。
public class SimpleNPC extends BaseNPC {

    public SimpleNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }
}

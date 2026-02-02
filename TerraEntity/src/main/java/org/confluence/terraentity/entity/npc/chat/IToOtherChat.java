package org.confluence.terraentity.entity.npc.chat;

import net.minecraft.world.entity.LivingEntity;

public interface IToOtherChat {

    ChatHolder getChatHolder(LivingEntity entity);

}

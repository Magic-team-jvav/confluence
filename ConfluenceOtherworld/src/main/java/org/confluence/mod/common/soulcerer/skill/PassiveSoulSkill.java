package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/// 被动技能：无需装备也无法主动释放，效果由 `PlayerUtils` 与各类事件按“是否已学会”结算。
public class PassiveSoulSkill extends SoulSkill {
    public PassiveSoulSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, 0.0F, Kind.PASSIVE, 0.0F, 0);
    }

    @Override
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return false;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {}
}

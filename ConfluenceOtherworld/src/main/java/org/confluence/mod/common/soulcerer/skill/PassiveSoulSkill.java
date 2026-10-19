package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/// 被动技能：不能主动释放，但需要装备到灵魂技能槽后效果才生效；
/// 判定入口统一为 `PlayerUtils.isPassiveSoulSkillActive`（已学会 + 已装备）。
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

package org.confluence.mod.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.jetbrains.annotations.Nullable;

/// 只向公共库提供主模组特有的 NPC 和 PvP 限制，不重复索敌算法。
public final class TargetingIntegration {
    private TargetingIntegration() {}

    public static void init() {
        LibEntityUtils.setTargetingExtension(new LibEntityUtils.TargetingExtension() {
            @Override
            public boolean allowsAttack(@Nullable Entity attacker, Entity target) {
                return !(attacker instanceof BaseNPC npc && target instanceof LivingEntity living)
                        || npc.canAttack(living);
            }

            @Override
            public boolean allowsPvP(Player attacker, Player target) {
                return PlayerSpecialData.of(attacker).isPvP() && PlayerSpecialData.of(target).isPvP();
            }
        });
    }
}

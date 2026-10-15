package org.confluence.mod.common.soul.skill;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibDamageTypes;

public record SoulSkillCastContext(ServerPlayer player, ItemStack weaponStack, SoulSkillStack skillStack, float damage) {
    public DamageSource getDamageSource() {
        return LibDamageTypes.of(player.level(), LibDamageTypes.SOUL, player);
    }
}

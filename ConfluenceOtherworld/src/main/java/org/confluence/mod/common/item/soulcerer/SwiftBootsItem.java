package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.soulcerer.artifact.ArtifactEffects;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;

/// 迅捷之靴：3 秒内移动速度 +15%，冷却 3 秒，消耗 2 魂。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SwiftBootsItem extends SoulArtifactItem {
    public SwiftBootsItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.SWIFT_BOOTS_SOUL_COST;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.SWIFT_BOOTS_COOLDOWN_TICKS;
    }

    @Override
    public void useArtifact(ServerPlayer player, ItemStack stack) {
        ArtifactEffects.applySwiftBoots(player);
    }
}

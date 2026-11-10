package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.soulcerer.artifact.ArtifactEffects;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;

/// 浮流之羽：沿视线向前冲刺一小段距离，冷却 3 秒，消耗 2 魂。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LightFeatherItem extends SoulArtifactItem {
    public LightFeatherItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.LIGHT_FEATHER_SOUL_COST;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.LIGHT_FEATHER_COOLDOWN_TICKS;
    }

    @Override
    public void useArtifact(ServerPlayer player, ItemStack stack) {
        ArtifactEffects.dash(player);
    }
}

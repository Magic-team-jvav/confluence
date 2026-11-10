package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.soulcerer.artifact.ArtifactEffects;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;

/// 雷霆之杖：向前指定一个位置，召唤一道泰拉雷电，冷却 40 秒，消耗 60 魂。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LightningRodItem extends SoulArtifactItem {
    public LightningRodItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.LIGHTNING_ROD_SOUL_COST;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.LIGHTNING_ROD_COOLDOWN_TICKS;
    }

    @Override
    public void useArtifact(ServerPlayer player, ItemStack stack) {
        ArtifactEffects.beginLightningStrike(player);
    }
}

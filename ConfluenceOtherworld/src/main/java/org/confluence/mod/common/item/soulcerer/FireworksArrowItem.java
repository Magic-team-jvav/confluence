package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.soulcerer.artifact.ArtifactEffects;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import javax.annotation.ParametersAreNonnullByDefault;

/// 烟火之箭：使用后的下一次弓箭额外携带一枚大范围烟火箭，冷却 30 秒，消耗 10 魂。
///
/// 箭矢照常消耗弹药（因此概率不消耗弹药的效果照常生效），烟火箭本身在命中时爆炸。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FireworksArrowItem extends SoulArtifactItem {
    public FireworksArrowItem(Properties properties, ModRarity rarity) {
        super(properties, rarity);
    }

    @Override
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.FIREWORKS_ARROW_SOUL_COST;
    }

    @Override
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.FIREWORKS_ARROW_COOLDOWN_TICKS;
    }

    @Override
    public void useArtifact(ServerPlayer player, ItemStack stack) {
        ArtifactEffects.markFireworkPayload(player);
    }
}

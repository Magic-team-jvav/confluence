package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class SoulArtifactItem extends CustomRarityItem {
    public SoulArtifactItem(Properties properties, ModRarity rarity) {
        super(properties.stacksTo(1), rarity);
    }

    /// 返回一次使用需要的灵魂点数。
    public float getSoulCost(ItemStack stack) {
        return SoulSkillParameters.ACTIVE_SOUL_COST;
    }

    /// 返回原版同物品类型冷却，单位为 tick。
    public int getCooldown(ItemStack stack) {
        return SoulSkillParameters.ACTIVE_COOLDOWN_TICKS;
    }

    /// 供具体法器补充服务端使用条件。
    public boolean canUse(ServerPlayer player, ItemStack stack) {
        return true;
    }

    /// 公共入口确认使用成功后执行具体法器效果。
    public abstract void useArtifact(ServerPlayer player, ItemStack stack);

    /// 右键通过服务端公共入口支付并使用法器。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.tryUseSoulArtifact(serverPlayer, stack)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}


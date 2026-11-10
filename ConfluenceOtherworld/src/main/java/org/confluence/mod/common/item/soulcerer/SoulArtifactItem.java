package org.confluence.mod.common.item.soulcerer;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.SoulcererUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

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

    /// 消耗提示：默认按 {@link #getSoulCost} 写死数值，浮动消耗的法器（零食箩筐）覆盖它。
    protected MutableComponent soulCostTooltip(ItemStack stack) {
        return Component.translatable("tooltip.confluence.artifact.soul_cost", formatSoul(getSoulCost(stack)));
    }

    /// 玩法说明行由语言文件按 `tooltip.item.confluence.<注册名>.0` 提供。
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(soulCostTooltip(stack).withStyle(ChatFormatting.BLUE));
        int cooldown = getCooldown(stack);
        if (cooldown > 0) {
            tooltipComponents.add(Component.translatable("tooltip.confluence.artifact.cooldown", formatSeconds(cooldown)).withStyle(ChatFormatting.GRAY));
        }
        tooltipComponents.add(Component.translatable("tooltip.item.confluence." + BuiltInRegistries.ITEM.getKey(this).getPath() + ".0")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static String formatSoul(float soul) {
        return soul == Math.round(soul) ? Integer.toString(Math.round(soul)) : String.format("%.1f", soul);
    }

    private static String formatSeconds(int ticks) {
        float seconds = ticks / (float) SharedConstants.TICKS_PER_SECOND;
        return seconds == Math.round(seconds) ? Integer.toString(Math.round(seconds)) : String.format("%.1f", seconds);
    }

    /// 右键通过服务端公共入口支付并使用法器。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!SoulcererUtils.tryUseSoulArtifact(serverPlayer, stack)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}


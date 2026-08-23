package org.confluence.mod.common.item.yoyo;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/// 悠悠球物品。
///
/// <p>物品只持有不可变定义；输入状态由玩家会话维护，运动、碰撞和网络同步由共享实体负责。</p>
public class YoyoItem extends CustomRarityItem {
    private static final int USE_DURATION = 72_000;
    private final YoyoDefinition definition;

    public YoyoItem(Properties properties, ModRarity rarity, YoyoDefinition definition) {
        super(properties.stacksTo(1), rarity);
        this.definition = Objects.requireNonNull(definition, "Yoyo definition must not be null");
    }

    /// 主动作按键按下时由服务端输入包调用；每名玩家同时只保留一个悠悠球。
    public final void press(ServerPlayer player, ItemStack stack) {
        if (stack.getItem() == this && YoyoSession.of(player).press(player, stack))
            AchievementUtils.awardAchievement(player, "throwing_lines");
    }

    /// 主动作按键松开时按玩家所有权查找实体，并让现有悠悠球进入收回流程。
    public static void release(ServerPlayer player) {
        YoyoSession.of(player).release();
    }

    /// 右键被配置为主要动作时，复用原版物品使用流程，以保留方块交互优先级。
    ///
    /// <p>客户端进入持续使用姿态，服务端创建或恢复当前玩家的悠悠球。左键配置时，该入口会被客户端输入层跳过，
    /// 改由固定控制包调用 {@link #press(ServerPlayer, ItemStack)} 与 {@link #release(ServerPlayer)}。</p>
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            press(serverPlayer, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    /// 松开右键或切换物品时，让服务端现有悠悠球进入收回状态。
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int remainingUseDuration) {
        if (living instanceof ServerPlayer player) {
            release(player);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public final void applyHitEffect(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        definition.applyHitEffects(yoyo, owner, target);
    }

    /// 主动作由悠悠球控制，不允许左键配置时同时进入原版挖掘状态。
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("attribute.name.generic.attack_damage")
                .append(Component.literal(" " + definition.attackDamage()))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.confluence.yoyo.max_range")
                .append(Component.literal(" " + definition.maximumRange()))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.confluence.yoyo.exist_time")
                .append(Component.literal(" " + definition.lifetimeTicks() / 20.0F))
                .withStyle(ChatFormatting.GREEN));
    }

    public final float attackDamage() {
        return definition.attackDamage();
    }

    public final float maximumRange() {
        return definition.maximumRange();
    }

    public final int stringColor() {
        return definition.stringColor();
    }

    public final int lifetimeTicks() {
        return definition.lifetimeTicks();
    }
}

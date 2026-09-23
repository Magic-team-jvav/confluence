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
import org.confluence.mod.mixed.Immunity;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 悠悠球物品。
///
/// 基类保存通用数值，子类实现特殊能力；输入、运动和网络同步由会话与实体负责。
public class YoyoItem extends CustomRarityItem {
    private static final int USE_DURATION = 72_000;
    private final float attackDamage;
    private final float maximumRange;
    private final int lifetimeTicks;
    private final float knockback;

    /// 射程以方块计，持续时间以 tick 计；零表示无限滞空。击退保留泰拉数值，由实体换算。
    public YoyoItem(Properties properties, ModRarity rarity, float attackDamage, float maximumRange, int lifetimeTicks, float knockback) {
        super(properties.stacksTo(1), rarity);
        this.attackDamage = attackDamage;
        this.maximumRange = maximumRange;
        this.lifetimeTicks = lifetimeTicks;
        this.knockback = knockback;
    }

    /// 主动作按键按下时由服务端输入包调用，每名玩家同时控制一组有绳悠悠球。
    public final void press(ServerPlayer player, ItemStack stack) {
        if (stack.getItem() == this && YoyoSession.of(player).press(player, stack))
            AchievementUtils.awardAchievement(player, "throwing_lines");
    }

    /// 主动作松开时结束当前一组；魔法悠悠球线改为脱手，切换武器仍收回。
    public static void release(ServerPlayer player) {
        YoyoSession.of(player).release(player);
    }

    /// 右键被配置为主要动作时，复用原版物品使用流程，以保留方块交互优先级。
    ///
    /// 客户端进入持续使用姿态，服务端创建或恢复当前玩家的悠悠球。左键配置时，该入口会被客户端输入层跳过，
    /// 改由固定控制包调用 {@link #press(ServerPlayer, ItemStack)} 与 {@link #release(ServerPlayer)}。
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
        onHitTarget(yoyo, owner, target);
    }

    /// 具体悠悠球负责命中特效，普通悠悠球无需实现。
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {}

    /// 连击强化的本次接触伤害倍率，平衡锤不调用。
    public float hitMultiplier(ServerPlayer owner) {return 1;}

    /// 服务端持续攻击逻辑，仅对未收回的主球、副球和脱手球调用。
    public void tickAttack(YoyoEntity yoyo) {}

    /// 客户端外观更新，不参与伤害结算。
    public void tickVisual(YoyoEntity yoyo) {}

    /// 同一玩家的普通悠悠球共用命中间隔，特殊悠悠球可提供自己的来源。
    public Immunity hitImmunity(ServerPlayer owner) {return YoyoSession.of(owner);}

    /// 接触判定在实体半宽之外增加的半径。
    public double hitRadius() {return 0.3;}

    /// 手套副球的移动速度倍率，不影响收回速度。
    public float duplicateSpeedMultiplier() {return 1;}

    /// 是否使用满亮度渲染球体，不改变世界照明。
    public boolean fullBright() {return false;}

    /// 额外暴击概率，0.2 表示增加 20 个百分点。
    public float bonusCriticalChance() {return 0;}

    /// 暴击时使用的总伤害倍率。
    public float criticalDamageMultiplier() {return 2;}

    /// 特殊能力说明及其参数由具体悠悠球提供。
    protected @Nullable Component effectTooltip() {return null;}

    /// 主动作由悠悠球控制，不允许左键配置时同时进入原版挖掘状态。
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("attribute.name.generic.attack_damage")
                .append(Component.literal(" " + attackDamage))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.confluence.yoyo.max_range")
                .append(Component.literal(" " + maximumRange))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.confluence.yoyo.exist_time")
                .append(Component.literal(lifetimeTicks == 0 ? " ∞" : " " + lifetimeTicks / 20.0F))
                .withStyle(ChatFormatting.GREEN));
        Component effect = effectTooltip();
        if (effect != null)
            tooltip.add(effect.copy().withStyle(ChatFormatting.GRAY));
    }

    public final float attackDamage() {
        return attackDamage;
    }

    public final float maximumRange() {
        return maximumRange;
    }

    public final int lifetimeTicks() {
        return lifetimeTicks;
    }

    public final float knockback() {return knockback;}
}

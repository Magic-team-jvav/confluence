package org.confluence.mod.common.item.common;

import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.lib.api.projectile.ProjectileFireAction;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ProjectileItemCosts;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.api.projectile.ProjectileWeaponAction;
import org.confluence.lib.api.projectile.ServerProjectileFireService;
import org.confluence.mod.common.entity.projectile.SpikyBallProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.List;

/**
 * 普通尖刺球的不可变发射动作声明。
 *
 * <p>原有 3.2 基础伤害、0.625 弹速、0.5 散布和 5 tick 冷却保持不变；MagicLib 只补充
 * 远程属性、暴击、护甲穿透、同 tick 幂等以及成本回滚。超级尖刺球是机关弹幕，不经过本物品。</p>
 */
public class SpikyBallItem extends Item implements ProjectileWeaponAction {
    private static final float BASE_DAMAGE = 3.2F;
    private static final float BASE_VELOCITY = 0.625F;
    private static final float INACCURACY = 0.5F;
    private static final int COOLDOWN_TICKS = 5;

    public SpikyBallItem() {
        super(new Properties().stacksTo(MAX_STACK_SIZE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }
        ServerProjectileFireService.fire(serverPlayer, usedHand, ProjectileFireTrigger.USE_PRESSED);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /**
     * 为一次服务端使用请求创建独立动作，不在物品实例中保存玩家运行状态。
     */
    @Override
    public ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.RANGED,
                        ProjectileItemCosts.oneHeldItem(),
                        (fireContext, snapshot) -> List.of(createLaunch(fireContext, snapshot)))
                .baseDamage(BASE_DAMAGE)
                .baseVelocity(BASE_VELOCITY)
                // 实体仍使用原有固定 0.1/0.02 击退，避免再次叠加远程武器击退。
                .baseKnockback(0.0F)
                .triggers(ProjectileFireTrigger.USE_PRESSED)
                .cooldownTicks(COOLDOWN_TICKS)
                .successAction(this::onSuccessfulThrow)
                .build();
    }

    /**
     * 预构建弹幕并保留原版 {@code shootFromRotation} 的随机散布方向。
     */
    private ProjectileLaunch createLaunch(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot
    ) {
        SpikyBallProjectile projectile = new SpikyBallProjectile(context.player());
        projectile.shootFromRotation(
                context.player(), context.pitch(), context.yaw(), 0.0F,
                snapshot.resolvedVelocity(), INACCURACY);
        return new ProjectileLaunch(projectile, projectile.position(), projectile.getDeltaMovement());
    }

    /**
     * 只有成本、冷却和世界生成全部提交后才播放声音并记录使用统计。
     */
    private void onSuccessfulThrow(ProjectileFireContext context) {
        context.level().playSound(
                null,
                context.player().getX(),
                context.player().getY(),
                context.player().getZ(),
                ModSoundEvents.WAVING.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (context.level().getRandom().nextFloat() * 0.4F + 0.8F));
        context.player().awardStat(Stats.ITEM_USED.get(this));
    }
}

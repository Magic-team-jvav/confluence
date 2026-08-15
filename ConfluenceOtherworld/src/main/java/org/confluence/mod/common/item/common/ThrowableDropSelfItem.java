package org.confluence.mod.common.item.common;

import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
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
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 可回收投掷武器的不可变动作声明。
 *
 * <p>物品不再自行生成实体或提前扣除堆叠，而是把伤害、弹速、成本、冷却与弹幕布局交给
 * MagicLib 的服务端事务。这样暴击和远程属性只解析一次，同时获得成本回滚、同 tick 幂等和
 * 冻结战斗快照；声音与使用统计也只在实体确实加入世界后提交。</p>
 */
public class ThrowableDropSelfItem extends Item implements ProjectileWeaponAction {
    protected final Supplier<EntityType<? extends ThrowableDropSelfProjectile>> typeSup;
    protected final boolean dropSelf;
    protected final float inaccuracy;
    protected final float power;
    protected final int cooldown;
    protected final float damage;
    protected final int flyTicks;

    public ThrowableDropSelfItem(Supplier<EntityType<? extends ThrowableDropSelfProjectile>> typeSup, float damage, float power, float inaccuracy, int cooldown, int flyTicks, boolean dropSelf) {
        super(new Properties());
        this.typeSup = Objects.requireNonNull(typeSup, "Throwable projectile type supplier must not be null");
        this.dropSelf = dropSelf;
        this.inaccuracy = requireFinite(inaccuracy, 0.0F, 180.0F, "Throwable inaccuracy");
        this.power = requireFinite(power, Math.nextUp(0.0F), Float.MAX_VALUE, "Throwable velocity");
        this.cooldown = requireNonNegative(cooldown, "Throwable cooldown");
        this.damage = requireFinite(damage, 0.0F, Float.MAX_VALUE, "Throwable damage");
        this.flyTicks = requireNonNegative(flyTicks, "Throwable fly delay");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }
        ServerProjectileFireService.fire(serverPlayer, hand, ProjectileFireTrigger.USE_PRESSED);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /**
     * 为本次服务端使用请求创建一份没有可变共享状态的发射动作。
     */
    @Override
    public ProjectileFireAction createProjectileFireAction(ProjectileFireContext context) {
        return ProjectileFireAction.builder(
                        ProjectileDamageChannel.RANGED,
                        ProjectileItemCosts.oneHeldItem(),
                        (fireContext, snapshot) -> List.of(createLaunch(fireContext, snapshot)))
                .baseDamage(damage)
                .baseVelocity(power)
                // 实体沿用原有固定 0.5 命中击退，不在 MagicLib 属性链中重复叠加。
                .baseKnockback(0.0F)
                .triggers(ProjectileFireTrigger.USE_PRESSED)
                .cooldownTicks(cooldown)
                .successAction(this::onSuccessfulThrow)
                .build();
    }

    /**
     * 创建并配置一枚尚未加入世界的投掷物，保留原有散布计算。
     */
    private ProjectileLaunch createLaunch(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot
    ) {
        EntityType<? extends ThrowableDropSelfProjectile> type = Objects.requireNonNull(
                typeSup.get(), "Throwable projectile type supplier returned null");
        ThrowableDropSelfProjectile projectile = type.create(context.level());
        if (projectile == null) {
            throw new IllegalStateException("Throwable projectile factory returned null");
        }
        projectile.setOwner(context.player());
        if (dropSelf) {
            // 保留旧实现：回收时只返回干净默认物品，不复制前缀、改名或其他手持组件。
            projectile.setItem(getDefaultInstance());
        }
        projectile.setFlyTicks(flyTicks);
        projectile.shootFromRotation(
                context.player(), context.pitch(), context.yaw(), 0.0F,
                snapshot.resolvedVelocity(), inaccuracy);
        return new ProjectileLaunch(projectile, projectile.position(), projectile.getDeltaMovement());
    }

    /**
     * 仅在成本、冷却和实体生成全部成功后播放原有声音并记录统计。
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

    private static float requireFinite(float value, float minimum, float maximum, String fieldName) {
        if (!Float.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    fieldName + " must be finite and within [" + minimum + ", " + maximum + "]");
        }
        return value;
    }

    private static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
        return value;
    }
}

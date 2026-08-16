package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileFireContext;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.common.component.ModRarity;

import java.util.List;

/// 持续使用时从准星上方降下箭雨的代达罗斯风暴弓。
///
/// <p>箭雨中心只取玩家视线与方块的交点，不搜索、不预测生物目标。
/// 弹药查找、无限弹药语义、事务成本、战斗快照与实体入世仍由 {@link BaseTerraBowItem} 统一处理。</p>
public class DaedalusStormbow extends BaseTerraBowItem {
    private static final double AIM_RANGE = 60.0;
    private static final double SPAWN_HEIGHT = 25.0;
    private static final double HORIZONTAL_SPREAD = 5.0;
    private static final float INACCURACY = 0.0F;

    public DaedalusStormbow(float baseDamage, ModRarity rarity) {
        super(baseDamage, new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public void onUseTick(Level level, LivingEntity owner, ItemStack weapon, int remainingUseDuration) {
        if (!level.isClientSide && owner instanceof ServerPlayer player
                && remainingUseDuration % 4 == 0) {
            InteractionHand hand = player.getUsedItemHand();
            fireBowAction(
                    player, hand, weapon, ProjectileFireTrigger.CONTINUOUS_USE_TICK, 1.0F);
        }
    }

    /// 将普通正向箭替换为高空降落布局。
    ///
    /// <p>雨箭的随机性体现在生成点围绕准星落点散布，飞行速度保持垂直向下。
    /// 这样既保留“天空箭雨”的覆盖范围，又不会因为水平回拉到准星而看起来像被附近实体吸过去。</p>
    @Override
    protected List<ProjectileLaunch> createProjectileLaunches(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            ItemStack projectileAmmo,
            boolean fullPull,
            boolean intangible,
            ProjectileFireTrigger trigger
    ) {
        Projectile projectile = createArrowProjectile(
                context.level(),
                context.player(),
                context.weapon(),
                projectileAmmo,
                fullPull,
                intangible);
        projectile.setOwner(context.player());

        Vec3 eyePosition = context.player().getEyePosition();
        Vec3 sightEnd = eyePosition.add(
                context.player().getViewVector(1.0F).normalize().scale(AIM_RANGE));
        Vec3 rainCenter = context.level().clip(new ClipContext(
                eyePosition,
                sightEnd,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                context.player())).getLocation();
        Vec3 spawnOffset = new Vec3(
                randomSpread(context),
                SPAWN_HEIGHT,
                randomSpread(context));
        Vec3 spawnPosition = rainCenter.add(spawnOffset);
        Vec3 direction = new Vec3(0.0, -1.0, 0.0);
        projectile.shoot(
                direction.x,
                direction.y,
                direction.z,
                snapshot.resolvedVelocity(),
                INACCURACY);
        return List.of(new ProjectileLaunch(
                projectile,
                spawnPosition,
                projectile.getDeltaMovement()));
    }

    /// 返回以零为中心的水平随机偏移；覆盖范围由生成点控制，而不是由箭矢横向修正控制。
    private static double randomSpread(ProjectileFireContext context) {
        return (context.player().getRandom().nextDouble() * 2.0 - 1.0)
                * HORIZONTAL_SPREAD;
    }

    /// 风暴弓雨箭保持固定基础弹速，不套用普通弓的蓄力曲线。
    @Override
    protected float getShotVelocity(ProjectileFireTrigger trigger, float power) {
        return 2.0F;
    }

    /// 每次雨箭脉冲只消耗弹药，不额外损耗耐久。
    @Override
    protected int getShotDurabilityUse(ProjectileFireTrigger trigger, ItemStack projectileAmmo) {
        return 0;
    }

    /// 雨箭沿用原实现的零快照击退，箭实体自身的附魔处理保持不变。
    @Override
    protected float getShotKnockback(ProjectileFireTrigger trigger, ItemStack weapon) {
        return 0.0F;
    }

    /// 风暴弓只在持续使用 tick 降下雨箭，松手时不额外发射普通箭。
    @Override
    public void releaseUsing(ItemStack weapon, Level level, LivingEntity living, int timeLeft) {}
}

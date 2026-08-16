package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.*;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.SkyFractureProjectile;

import java.util.List;

/// 以一次魔力成本发射四枚、间隔三 tick 的裂天剑弹幕。
///
/// <p>首枚弹幕进入完整法杖事务并冻结伤害、暴击、穿甲和弹速；成功后玩家服务端状态仅保存
/// 当前格式的延迟批次。后三枚在第 3、6、9 tick 通过 MagicLib 的可信延迟批次入口生成，复用
/// 同一战斗快照，不重新扣魔力、抽暴击或叠加冷却。批次不写入武器栈，避免把计划递归嵌套进
/// 快照中的武器副本。</p>
public class SkyFractureItem extends ManaStaffItem<SkyFractureProjectile> {
    private static final String BURSTS_TAG = "SkyFractureBursts";
    private static final int BURST_FORMAT_VERSION = 1;
    private static final int FOLLOW_UP_COUNT = 3;
    private static final int FOLLOW_UP_INTERVAL = 3;

    public SkyFractureItem() {
        super(ModRarity.LIGHT_RED, SkyFractureProjectile::new, 24, 17, 17.5F, 6, 0.24);
    }

    /// 首发使用原有随机近身生成位置。
    @Override
    protected Vec3 launchPosition(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            SkyFractureProjectile projectile
    ) {
        return randomLaunchPosition(context.player());
    }

    /// 首发与后续弹幕共享施法瞬间由服务端确定的锁定方向。
    @Override
    protected Vec3 launchDirection(
            ProjectileFireContext context,
            ProjectileCombatSnapshot snapshot,
            SkyFractureProjectile projectile
    ) {
        return findAimDirection(context);
    }

    /// 使用武器声明的六 tick 冷却，不再由旧实现额外硬编码十 tick。
    @Override
    protected int resolveCooldown(ProjectileFireContext context) {
        return cooldown;
    }

    /// 原实现不会在裂天剑批次开始时播放普通法杖声音。
    @Override
    protected void playSuccessfulShot(
            ProjectileFireContext context,
            SkyFractureProjectile projectile
    ) {}

    /// 首发确认加入世界后才写入可继续的当前格式批次状态。
    @Override
    protected void onSuccessfulShot(
            ProjectileFireContext context,
            SkyFractureProjectile projectile
    ) {
        ProjectileCombatSnapshot snapshot = projectile.getProjectileCombatSnapshot();
        if (snapshot == null) {
            throw new IllegalStateException("Sky Fracture first projectile is missing its combat snapshot");
        }
        Vec3 direction = projectile.getDeltaMovement().normalize();
        CompoundTag burst = new CompoundTag();
        burst.putInt("Version", BURST_FORMAT_VERSION);
        burst.putLong("NextTick", context.gameTime() + FOLLOW_UP_INTERVAL);
        burst.putInt("Remaining", FOLLOW_UP_COUNT);
        burst.putDouble("DirectionX", direction.x);
        burst.putDouble("DirectionY", direction.y);
        burst.putDouble("DirectionZ", direction.z);
        burst.put("CombatSnapshot", snapshot.toTag());
        CompoundTag root = context.player().getPersistentData();
        ListTag bursts = new ListTag();
        bursts.addAll(root.getList(BURSTS_TAG, Tag.TAG_COMPOUND));
        bursts.add(burst);
        root.put(BURSTS_TAG, bursts);
    }

    /// 只在精确到期 tick 生成后续弹幕；玩家切走武器时对应时间点会按旧行为直接错过。
    ///
    /// <p>重新选中时会先跳过全部已经错过的时间点，防止集中补发。批次保存在玩家服务端状态，
    /// 因而同种武器的另一物品栈不能复制计划或绕过物品级冷却。</p>
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof ServerPlayer player) || level.isClientSide || !isSelected) {
            return;
        }
        CompoundTag root = player.getPersistentData();
        if (!root.contains(BURSTS_TAG, Tag.TAG_LIST)) {
            return;
        }
        ListTag storedBursts = root.getList(BURSTS_TAG, Tag.TAG_COMPOUND);
        if (storedBursts.isEmpty()) {
            root.remove(BURSTS_TAG);
            return;
        }

        long now = level.getGameTime();
        ListTag nextBursts = new ListTag();
        for (Tag stored : storedBursts) {
            if (!(stored instanceof CompoundTag burst)) {
                continue;
            }
            BurstState state = readBurstState(burst);
            if (state == null) {
                continue;
            }
            long nextTick = state.nextTick();
            int remaining = state.remaining();
            while (remaining > 0 && nextTick < now) {
                remaining--;
                nextTick += FOLLOW_UP_INTERVAL;
            }
            if (remaining <= 0) {
                continue;
            }
            if (nextTick == now) {
                // 到期时无论是否选中都消费一个时间点，确保不会在重新选中后补发旧弹幕。
                if (!continueBurst(player, state)) {
                    continue;
                }
                remaining--;
                nextTick += FOLLOW_UP_INTERVAL;
            }
            if (remaining > 0) {
                writeSchedule(burst, nextTick, remaining);
                nextBursts.add(burst);
            }
        }
        if (nextBursts.isEmpty()) {
            root.remove(BURSTS_TAG);
        } else {
            root.put(BURSTS_TAG, nextBursts);
        }
    }

    /// 生成一个已经付费批次的单枚后续弹幕。
    private boolean continueBurst(ServerPlayer player, BurstState state) {
        SkyFractureProjectile projectile = factory.create(player);
        projectile.setDefaultVelocity(state.snapshot().resolvedVelocity());
        ProjectileLaunch launch = new ProjectileLaunch(
                projectile,
                randomLaunchPosition(player),
                state.direction());
        ProjectileFireResult result = ServerProjectileFireService.continueBurst(
                player,
                InteractionHand.MAIN_HAND,
                state.snapshot(),
                List.of(launch));
        return result != ProjectileFireResult.INVALID_ACTION_RESULT
                && result != ProjectileFireResult.PLAYER_UNAVAILABLE
                && result != ProjectileFireResult.WEAPON_CHANGED;
    }

    private static BurstState readBurstState(CompoundTag burst) {
        try {
            if (burst.getInt("Version") != BURST_FORMAT_VERSION) {
                return null;
            }
            int remaining = burst.getInt("Remaining");
            if (remaining < 1 || remaining > FOLLOW_UP_COUNT) {
                return null;
            }
            Vec3 direction = new Vec3(
                    burst.getDouble("DirectionX"),
                    burst.getDouble("DirectionY"),
                    burst.getDouble("DirectionZ"));
            if (!Double.isFinite(direction.x) || !Double.isFinite(direction.y)
                    || !Double.isFinite(direction.z) || direction.lengthSqr() <= 1.0E-12) {
                return null;
            }
            ProjectileCombatSnapshot snapshot = ProjectileCombatSnapshot.fromTag(
                    burst.getCompound("CombatSnapshot"));
            return new BurstState(
                    burst.getLong("NextTick"), remaining, direction.normalize(), snapshot);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private static void writeSchedule(CompoundTag burst, long nextTick, int remaining) {
        burst.putLong("NextTick", nextTick);
        burst.putInt("Remaining", remaining);
    }

    /// 仅在首发时执行一次的服务端视线锁定。
    private static Vec3 findAimDirection(ProjectileFireContext context) {
        double reach = 64.0;
        double squaredReach = Mth.square(reach);
        Vec3 from = context.eyePosition();
        HitResult blockHit = context.player().pick(reach, 1.0F, false);
        double blockDistance = blockHit.getLocation().distanceToSqr(from);
        if (blockHit.getType() != HitResult.Type.MISS) {
            squaredReach = blockDistance;
            reach = Math.sqrt(blockDistance);
        }
        Vec3 to = from.add(context.viewVector().scale(reach));
        AABB searchBox = context.player().getBoundingBox()
                .expandTowards(context.viewVector().scale(reach))
                .inflate(1.0);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                context.player(),
                from,
                to,
                searchBox,
                target -> !target.isSpectator() && target.isPickable(),
                squaredReach);
        if (entityHit != null
                && entityHit.getLocation().distanceToSqr(from) < blockDistance
                && entityHit.getEntity() instanceof LivingEntity living) {
            return new Vec3(
                    living.getX() - context.player().getX(),
                    living.getEyeY() - context.player().getEyeY(),
                    living.getZ() - context.player().getZ());
        }
        return context.viewVector();
    }

    private static Vec3 randomLaunchPosition(ServerPlayer player) {
        return new Vec3(
                player.getRandomX(2.0),
                player.getY(player.getRandom().nextFloat() * 1.5) + 0.5,
                player.getRandomZ(2.0));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    /// 已校验并归一化的当前格式延迟批次状态。
    private record BurstState(
            long nextTick,
            int remaining,
            Vec3 direction,
            ProjectileCombatSnapshot snapshot
    ) {}
}

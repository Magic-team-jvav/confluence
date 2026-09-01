package org.confluence.mod.common.item.common;

import com.google.common.collect.Streams;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.gameevent.LanternNightGameEvent;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class BossSummoningItem extends TooltipItem {
    // 需要随机落点的 Boss 在玩家周围 48 方块半径内选取召唤位置。
    private static final double RANDOM_SUMMON_RADIUS = 48.0D;

    private final Predicate<Player> condition;
    private final Function<Level, Mob> factory;

    public BossSummoningItem(Predicate<Player> condition, Function<Level, Mob> factory, List<Component> tooltips) {
        super(new Properties(), ModRarity.BLUE, tooltips);
        this.condition = condition;
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel && condition.test(player)) {
            Mob mob = factory.apply(level);
            if (mob == null) {
                return InteractionResultHolder.fail(itemStack);
            }
            if (Streams.stream(serverLevel.getAllEntities())
                    .anyMatch(entity -> entity.getType() == mob.getType())) {
                return InteractionResultHolder.fail(itemStack);
            }

            moveToRandomSummonPos(level, player, mob);
            if (!prepareSummonedMob(serverLevel, mob)) {
                return InteractionResultHolder.fail(itemStack);
            }
            bindSummoner(player, mob);
            if (serverLevel.addFreshEntity(mob)) {
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
                player.invulnerableTime = 100;
                LanternNightGameEvent.INSTANCE.forceEnd();
                return InteractionResultHolder.consume(itemStack);
            }
            mob.discard();
            return InteractionResultHolder.fail(itemStack);
        }
        return InteractionResultHolder.success(itemStack);
    }

    /// 执行 Forge 生物生成初始化。
    ///
    /// 在实体进入世界前执行生成收尾，保证生成事件、难度初始化和实体内部状态完整建立。
    private static boolean prepareSummonedMob(ServerLevel serverLevel, Mob mob) {
        mob.yHeadRot = mob.getYRot();
        mob.yBodyRot = mob.getYRot();
        ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.SPAWNER, null, null);
        if (mob.isSpawnCancelled()) {
            mob.discard();
            return false;
        }
        return true;
    }

    /// 将成功生成的 Boss 或普通怪物绑定到召唤者。
    ///
    /// 绑定发生在生成初始化之后、加入世界之前，因此任何实体事件和首个服务端 tick 都能看到
    /// 已建立的权威目标。创造和旁观玩家只观察遭遇，不能成为攻击目标。
    private static void bindSummoner(Player player, Mob mob) {
        if (mob instanceof BaseBoss boss) {
            boss.initializeSummonedCombat(player);
        } else if (player.canBeSeenAsEnemy() && mob.canAttack(player)) {
            mob.setTarget(player);
        } else {
            mob.setTarget(null);
        }
    }

    /// 在玩家周围选择随机召唤落点。
    ///
    /// 这里不再要求视线无遮挡。Boss 召唤道具本身就是强制开战入口，额外的视线检测会让测试场、
    /// 洞穴或复杂建筑中的召唤直接失败。
    private static void moveToRandomSummonPos(Level level, Player player, Mob mob) {
        double angle = level.random.nextDouble() * Mth.TWO_PI;
        // sqrt 使候选点在圆盘内均匀分布；nextDouble 永远小于一，因此最大距离严格小于 48 格。
        double radius = Math.sqrt(level.random.nextDouble()) * RANDOM_SUMMON_RADIUS;
        mob.moveTo(
                player.getX() + Math.cos(angle) * radius,
                player.getY(),
                player.getZ() + Math.sin(angle) * radius,
                player.getYRot(),
                0.0F);
    }

}

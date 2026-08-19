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
    private static final int RANDOM_SUMMON_RANGE = 50;

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
            if (serverLevel.addFreshEntity(mob)) {
                bindSummoner(player, mob);
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
    /// <p>1.20 合并侧没有 TerraEntity 的 internalSpawnEntity，因此在实体进入世界前补上
    /// finalizeSpawn，保证生成事件、难度初始化和实体内部生成状态完整执行。</p>
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
    /// <p>绑定时机必须晚于实体加入世界，避免目标被生成初始化覆盖。否则飞行 Boss 的首轮行为会进入无目标分支，
    /// 客户端看起来就像沉底、贴地滑行或完全不追击玩家。</p>
    private static void bindSummoner(Player player, Mob mob) {
        if (mob instanceof BaseBoss boss) {
            boss.initializeSummonedCombat(player);
        } else {
            mob.setTarget(player);
        }
    }

    /// 使用 1.21 侧的随机召唤落点。
    ///
    /// <p>这里不再要求视线无遮挡。Boss 召唤道具本身就是强制开战入口，额外的视线检测会让测试场、
    /// 洞穴或复杂建筑中的召唤直接失败。</p>
    private static void moveToRandomSummonPos(Level level, Player player, Mob mob) {
        mob.moveTo(
                player.getX() + Mth.randomBetweenInclusive(level.random, -RANDOM_SUMMON_RANGE, RANDOM_SUMMON_RANGE),
                player.getY(),
                player.getZ() + Mth.randomBetweenInclusive(level.random, -RANDOM_SUMMON_RANGE, RANDOM_SUMMON_RANGE),
                player.getYRot(),
                0.0F);
    }

}

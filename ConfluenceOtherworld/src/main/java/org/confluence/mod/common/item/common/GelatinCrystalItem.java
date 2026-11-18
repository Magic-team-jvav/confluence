package org.confluence.mod.common.item.common;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.EventHooks;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossMultiplayerEnhancement;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.OverworldUtils;

/// 明胶水晶：在神圣之地（含其子群系）右键召唤史莱姆皇后。
///
/// 召唤失败（不在神圣之地 / 该维度已有史莱姆皇后 / 生成被事件取消）时返回 fail，
/// 物品不会被消耗，只在实际召唤成功后扣 1 个并进入短暂冷却。
public class GelatinCrystalItem extends CustomRarityItem {
    /// 召唤落点与玩家的最小 / 最大水平距离。
    private static final int MIN_DISTANCE = 30;
    private static final int MAX_DISTANCE = 50;
    /// 使用后的冷却刻数。
    private static final int COOLDOWN_TICKS = 20;

    public GelatinCrystalItem() {
        super(new Properties(), ModRarity.BLUE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResultHolder.success(itemStack);
        if (!OverworldUtils.isHallow(level.getBiome(player.blockPosition()))) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (ModUtils.hasBossPresent(serverLevel, BossEntities.QUEEN_SLIME.get())) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (!summonQueenSlime(serverLevel, player)) {
            return InteractionResultHolder.fail(itemStack);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSoundEvents.ITEM_QUEEN_SLIME.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!player.hasInfiniteMaterials()) itemStack.shrink(1);
        player.getCooldowns().addCooldown(itemStack.getItem(), COOLDOWN_TICKS);
        return InteractionResultHolder.consume(itemStack);
    }

    /// 与机械 Boss 召唤装置 summonSingle 一致：先完成生成事件与人数缩放，
    /// 再把召唤者登记为本次遭遇参与者，最后带乘客加入世界。
    private static boolean summonQueenSlime(ServerLevel level, Player player) {
        EntityType<? extends Mob> type = BossEntities.QUEEN_SLIME.get();
        Mob boss = type.create(level);
        if (boss == null) return false;
        double x = player.getX() + Mth.randomBetweenInclusive(level.random, MIN_DISTANCE, MAX_DISTANCE) * (level.random.nextBoolean() ? 1 : -1);
        double z = player.getZ() + Mth.randomBetweenInclusive(level.random, MIN_DISTANCE, MAX_DISTANCE) * (level.random.nextBoolean() ? 1 : -1);
        double y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z)) + 0.5;
        if (Math.abs(player.getY() - y) > 50.0) y = player.getY();
        boss.setPos(x, y, z);
        EventHooks.finalizeMobSpawn(boss, level, level.getCurrentDifficultyAt(boss.blockPosition()), MobSpawnType.SPAWN_EGG, null);
        if (boss.isSpawnCancelled()) {
            boss.discard();
            return false;
        }
        BossMultiplayerEnhancement.apply(boss, true);
        boss.setHealth(boss.getMaxHealth());
        if (boss instanceof BaseBoss baseBoss) baseBoss.initializeSummonedCombat(player);
        level.addFreshEntityWithPassengers(boss);
        return true;
    }
}

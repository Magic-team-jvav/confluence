package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.monster.SimpleWormMonster;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;

/// 血肉墙的嘴；每张嘴独立分批吐出水蛭。
public final class WallOfFleshMouth extends WallOfFleshPart {
    /// 嘴部本地护甲在伤害转发前生效；每 400 tick 开始一轮，并以 10 tick 间隔分批生成水蛭。
    private static final float LOCAL_ARMOR = 12.0F;

    /// 肉墙嘴巴召唤水蛭时搜索目标的最远距离，单位格。
    private static final double MAX_TARGET_DISTANCE = 120.0;
    /// 两轮水蛭召唤的随机间隔宽度，单位 tick。
    private static final int SUMMON_INTERVAL_VARIATION = 200;
    /// 二阶段低血量可额外召唤的最大水蛭数量。
    private static final float MAX_PHASE_TWO_EXTRA_LEECHES = 4.0F;
    /// 嘴部召唤敌怪的基础间隔，单位 tick。
    private static final int BASE_SUMMON_INTERVAL = 400;
    /// 连续生成召唤物的间隔，单位 tick。
    private static final int SPAWN_INTERVAL = 10;

    /// 肉墙嘴巴首次召唤前的随机提前时间范围，单位 tick。
    private static final int INITIAL_SUMMON_VARIATION = 100;

    private int summonTimer = BASE_SUMMON_INTERVAL
            + random.nextInt(INITIAL_SUMMON_VARIATION) - INITIAL_SUMMON_VARIATION;
    private int pendingSpawns;
    private int spawnTimer;

    public WallOfFleshMouth(WallOfFlesh parent, int index) {
        super(parent, index, 3.0F, 4.0F);
    }

    @Override
    protected void tickAttack(WallOfFlesh master, @Nullable LivingEntity target) {
        if (target == null || !master.isValidFrontTarget(target)) {
            return;
        }
        if (distanceToSqr(target) > MAX_TARGET_DISTANCE * MAX_TARGET_DISTANCE * 1.2) {
            return;
        }

        if (pendingSpawns > 0 && --spawnTimer <= 0) {
            spawnLeech(master, target);
            pendingSpawns--;
            spawnTimer = pendingSpawns > 0 ? SPAWN_INTERVAL : 0;
        }
        if (--summonTimer > 0) {
            return;
        }

        summonTimer = BASE_SUMMON_INTERVAL
                + random.nextInt(SUMMON_INTERVAL_VARIATION) - 100;
        if (pendingSpawns == 0) {
            pendingSpawns = getSummonCount(master);
            spawnTimer = SPAWN_INTERVAL;
        }
    }

    private int getSummonCount(WallOfFlesh master) {
        if (!master.isPhaseTwo()) {
            return 1;
        }
        float progress = Mth.clamp((0.5F - master.getHealth() / master.getMaxHealth()) / 0.5F, 0.0F, 1.0F);
        return 1 + Mth.floor(progress * MAX_PHASE_TWO_EXTRA_LEECHES);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        float appliedDamage = source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount
                : CombatRules.getDamageAfterAbsorb(getParent(), amount, source, LOCAL_ARMOR, 0.0F);
        return appliedDamage > 0.0F && super.hurt(source, appliedDamage);
    }

    private void spawnLeech(WallOfFlesh master, LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        SimpleWormMonster leech = MonsterEntities.LEECH.get().create(serverLevel);
        if (leech == null) {
            return;
        }
        leech.setPos(position().add(master.getForwardVector().normalize()));
        leech.setTarget(target);
        leech.setBossOwner(master);
        if (!serverLevel.addFreshEntity(leech)) {
            leech.discard();
        }
    }
}

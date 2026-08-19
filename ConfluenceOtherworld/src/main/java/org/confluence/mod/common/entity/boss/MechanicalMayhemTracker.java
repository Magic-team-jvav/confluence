package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.init.entity.BossEntities;

import java.util.*;

/// 在机械三王同时存活期间冻结共同参战玩家。
///
/// <p>这里记录的是“玩家曾在三场遭遇重叠时同时参与”这一事实，而不是最终胜利。最终是否把
/// 某一种机械 Boss 计入机甲混战，仍由该 Boss 的正常死亡结算决定；脱战丢弃不会进入结算。</p>
final class MechanicalMayhemTracker {
    private MechanicalMayhemTracker() {
    }

    static void observe(BaseBoss observer) {
        if (!isMechanicalBoss(observer) || !(observer.level() instanceof ServerLevel level)) {
            return;
        }

        List<BaseBoss> twins = new ArrayList<>();
        List<BaseBoss> destroyers = new ArrayList<>();
        List<BaseBoss> skeletronPrimes = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof BaseBoss boss) || !boss.isAlive()) {
                continue;
            }
            if (boss.getType() == BossEntities.THE_TWINS.get()) {
                twins.add(boss);
            } else if (boss.getType() == BossEntities.THE_DESTROYER.get()) {
                destroyers.add(boss);
            } else if (boss.getType() == BossEntities.SKELETRON_PRIME.get()) {
                skeletronPrimes.add(boss);
            }
        }
        if (twins.isEmpty() || destroyers.isEmpty() || skeletronPrimes.isEmpty()) {
            return;
        }

        // 同类 Boss 允许同时存在；逐个组合求交集，避免把玩家登记到未参与的另一场同类遭遇。
        for (BaseBoss twinsBoss : twins) {
            for (BaseBoss destroyer : destroyers) {
                for (BaseBoss skeletronPrime : skeletronPrimes) {
                    confirmCommonParticipants(twinsBoss, destroyer, skeletronPrime);
                }
            }
        }
    }

    private static void confirmCommonParticipants(BaseBoss twins, BaseBoss destroyer, BaseBoss skeletronPrime) {
        Set<UUID> commonParticipants = new LinkedHashSet<>(twins.combatParticipantIdsSnapshot());
        commonParticipants.retainAll(destroyer.combatParticipantIdsSnapshot());
        commonParticipants.retainAll(skeletronPrime.combatParticipantIdsSnapshot());
        if (!commonParticipants.isEmpty()) {
            twins.confirmMechanicalMayhemParticipants(commonParticipants);
            destroyer.confirmMechanicalMayhemParticipants(commonParticipants);
            skeletronPrime.confirmMechanicalMayhemParticipants(commonParticipants);
        }
    }

    private static boolean isMechanicalBoss(BaseBoss boss) {
        return boss.getType() == BossEntities.THE_TWINS.get()
                || boss.getType() == BossEntities.THE_DESTROYER.get()
                || boss.getType() == BossEntities.SKELETRON_PRIME.get();
    }
}

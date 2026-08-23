package org.confluence.mod.common.advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.attachment.PlayerAchievementProgress;
import org.confluence.mod.common.init.entity.BossEntities;

/// 结算单名参战玩家在一次 Boss 正常死亡中获得的成就进度。
///
/// 调用方负责先冻结遭遇参与者快照；本类只处理个人进度，不更新世界击杀账本、不生成
/// 宝藏袋，也不会被 Boss 脱战路径调用。这样个人奖励范围与世界阶段推进保持明确分离。
public final class BossAchievementSettlement {
    private BossAchievementSettlement() {
    }

    public static void settle(ServerPlayer player, EntityType<?> bossType, boolean stickySituation, boolean mechanicalMayhemParticipant) {
        if (bossType == BossEntities.EATER_OF_WORLDS.get()) {
            AchievementAwardService.award(player, "worm_fodder");
        }
        if (stickySituation) {
            AchievementAwardService.award(player, "sticky_situation");
        }
        if (bossType == BossEntities.WALL_OF_FLESH.get() || bossType == BossEntities.HILL_OF_FLESH.get()) {
            AchievementAwardService.award(player, "still_hungry");
        }
        if (bossType == BossEntities.PLANTERA.get()) {
            AchievementAwardService.award(player, "the_great_southern_plantkill");
        }
        if (bossType == BossEntities.LUNATIC_CULTIST.get()) {
            AchievementAwardService.award(player, "obsessive_devotion");
        }
        PlayerAchievementProgress progress = PlayerAchievementProgress.of(player);
        if (progress.recordMechanicalBoss(bossType)) {
            AchievementAwardService.award(player, "buckets_of_bolts");
        }
        if (mechanicalMayhemParticipant && progress.recordMechanicalMayhemBoss(bossType)) {
            AchievementAwardService.award(player, "mecha_mayhem");
        }
    }
}

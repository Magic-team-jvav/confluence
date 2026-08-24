package org.confluence.mod.common.entity.npc.ai;

import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.entity.BossEntities;

/// 按当前世界难度和已完成进度计算城镇 NPC 的实时战斗成长。
public final class NPCCombatProgression {
    private NPCCombatProgression() {}

    /// 返回基础攻击伤害最终使用的倍率；进度加值先相加，再乘世界难度倍率。
    public static double damageMultiplier(BaseNPC npc) {
        double difficulty = LibUtils.switchByDifficulty(npc.level(), npc.blockPosition(), 1.0, 1.5, 1.75, 2.0);
        return difficulty * (1.0 + bonuses().damage());
    }

    /// 返回在 Attribute 护甲之外追加的进度防御值。
    public static int defenseBonus() {
        return bonuses().defense();
    }

    /// 将注册或数据包给出的基础攻击间隔换算为当前进度下的实际间隔。
    public static int attackInterval(int baseInterval) {
        double speed = bonuses().attackSpeed();
        NPCSpawner spawner = NPCSpawner.INSTANCE;
        if (spawner.isAdvancedCombatTechniquesUsed()) speed *= 1.2;
        if (spawner.isAdvancedCombatTechniquesVolumeTwoUsed()) speed *= 1.2;
        return Math.max(1, (int) Math.round(baseInterval / speed));
    }

    /// 汇总当前已实现 Boss 对应的独立成长项；邪恶 Boss 组只计一次。
    private static Bonuses bonuses() {
        KillBoard board = KillBoard.INSTANCE;
        Bonuses bonuses = Bonuses.EMPTY;
        if (board.isDefeated(BossEntities.KING_SLIME.get())) bonuses = bonuses.add(2, 0.05);
        if (board.isDefeated(BossEntities.EYE_OF_CTHULHU.get())) bonuses = bonuses.add(2, 0.05);
        if (board.isDefeated(BossEntities.DEERCLOPS.get())) bonuses = bonuses.add(3, 0.10);
        if (board.isAnyDefeated(BossEntities.EATER_OF_WORLDS.get(), BossEntities.BRAIN_OF_CTHULHU.get())) {
            bonuses = bonuses.add(3, 0.10);
        }
        if (board.isDefeated(BossEntities.QUEEN_BEE.get())) bonuses = bonuses.add(3, 0.10);
        if (board.isDefeated(BossEntities.SKELETRON.get())) bonuses = bonuses.add(3, 0.10);
        if (board.getGamePhase().isHardmode()) bonuses = bonuses.add(12, 0.40);
        if (board.isDefeated(BossEntities.THE_TWINS.get())) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(BossEntities.THE_DESTROYER.get())) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(BossEntities.SKELETRON_PRIME.get())) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(BossEntities.PLANTERA.get())) bonuses = bonuses.add(8, 0.15);
        if (board.isDefeated(BossEntities.LUNATIC_CULTIST.get())) bonuses = bonuses.add(20, 0.15);
        return bonuses;
    }

    /// 已累计的防御、伤害和乘算攻击速度；每个已击败 Boss 都提供 1.5% 攻速。
    private record Bonuses(int defense, double damage, double attackSpeed) {
        private static final Bonuses EMPTY = new Bonuses(0, 0, 1);

        /// 追加一项 Boss 成长并乘入该 Boss 的攻击速度奖励。
        private Bonuses add(int defense, double damage) {
            return new Bonuses(this.defense + defense, this.damage + damage, attackSpeed * 1.015);
        }
    }
}

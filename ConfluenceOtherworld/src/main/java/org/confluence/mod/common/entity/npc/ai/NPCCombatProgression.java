package org.confluence.mod.common.entity.npc.ai;

import org.confluence.mod.Confluence;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.spawner.NPCSpawner;
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
        if (board.isDefeated(Confluence.asResource("king_slime"))) bonuses = bonuses.add(2, 0.05);
        if (board.isDefeated(Confluence.asResource("eye_of_cthulhu"))) bonuses = bonuses.add(2, 0.05);
        if (board.isDefeated(Confluence.asResource("deerclops"))) bonuses = bonuses.add(3, 0.10);
        if (board.isAnyDefeated(Confluence.asResource("eater_of_worlds"), Confluence.asResource("brain_of_cthulhu"))) {
            bonuses = bonuses.add(3, 0.10);
        }
        if (board.isDefeated(Confluence.asResource("queen_bee"))) bonuses = bonuses.add(3, 0.10);
        if (board.isDefeated(Confluence.asResource("skeletron"))) bonuses = bonuses.add(3, 0.10);
        if (board.getGamePhase().isHardmode()) bonuses = bonuses.add(12, 0.40);
        if (board.isDefeated(Confluence.asResource("the_twins"))) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(Confluence.asResource("the_destroyer"))) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(Confluence.asResource("skeletron_prime"))) bonuses = bonuses.add(6, 0.15);
        if (board.isDefeated(Confluence.asResource("plantera"))) bonuses = bonuses.add(8, 0.15);
        if (board.isDefeated(Confluence.asResource("lunatic_cultist"))) bonuses = bonuses.add(20, 0.15);
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

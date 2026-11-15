package org.confluence.mod.common.entity.monster.difficulty;

/// 全部敌怪数值共用的难度表
public final class CreatureDifficultyRules {
    public static final int MAX_PENETRATION = 12;
    public static final int MAX_TOUGHNESS = 8;

    private CreatureDifficultyRules() {}

    /// 穿透依据转换后的专家基础攻击计算，低伤害至少保留 1。
    public static double penetration(double damage) {return clamp(Math.ceil(damage * 0.35) - 2, 1, MAX_PENETRATION);}

    private static double clamp(double value, double minimum, double maximum) {return Math.max(minimum, Math.min(maximum, value));}

    public enum Difficulty {
        CLASSIC(0.50, 0.50, 0.50, 0.50, 0.50, -1, -2),
        EXPERT(1, 1, 1, 1, 1, 0, 0),
        MASTER(1.50, 1.50, 1.50, 1.275, 1.50, 1, 2),
        LEGENDARY(2, 2.667, 1.50, 1.700, 2.667, 2, 2);

        private final double health, attack, specialAttack, bossHealth, bossAttack;
        private final int penetrationBonus, toughnessBonus;

        Difficulty(double health, double attack, double specialAttack, double bossHealth, double bossAttack, int penetrationBonus, int toughnessBonus) {
            this.health = health;
            this.attack = attack;
            this.specialAttack = specialAttack;
            this.bossHealth = bossHealth;
            this.bossAttack = bossAttack;
            this.penetrationBonus = penetrationBonus;
            this.toughnessBonus = toughnessBonus;
        }

        public double health(boolean boss) {return boss ? bossHealth : health;}

        public double attack(boolean boss) {return boss ? bossAttack : attack;}

        public double specialAttack(boolean boss) {return boss ? bossAttack : specialAttack;}

        /// 将已包含接触倍率的伤害换成特殊攻击倍率，避免射弹重复缩放。
        public double projectileDamage(double value, boolean boss, boolean fromAttackAttribute) {
            return value * specialAttack(boss) / (fromAttackAttribute ? attack(boss) : 1);
        }

        public double penetration(double base) {return clamp(base + penetrationBonus, 1, MAX_PENETRATION);}

        public double toughness(double base) {
            return clamp(base + toughnessBonus, base >= 1 ? 1 : 0, MAX_TOUGHNESS);
        }
    }
}

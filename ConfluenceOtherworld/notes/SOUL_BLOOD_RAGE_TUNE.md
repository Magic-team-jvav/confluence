# 血怒数值调整

## 1. 旧 → 新

| 项 | 旧 | 新 |
| --- | --- | --- |
| 冷却 | 15 秒 | **3 秒** |
| 持续时间 | 15 秒 | **8 秒** |
| 灵魂消耗 | 3 | **0** |
| 生命代价 | 最大生命的 20% | **固定 10 点生命** |
| 增伤 | 近战/远程 +40% | **近战/远程 +7%** |
| 击杀额外收魂 | +1 | 不变 |

## 2. 实现

### 2.1 生命代价从「比例」改成「固定值」

`SoulBuffSkill` 原来的第 6 个参数是 `healthCostRatio`（按最大生命的比例扣），
只有血怒用到非 0 值。现在改成固定值：

```java
// SoulSkillParameters
public static final float BLOOD_RAGE_HEALTH_COST = 10.0F;

// SoulBuffSkill
private final float healthCost;
...
if (healthCost > 0.0F) {
    serverPlayer.setHealth(Math.max(1.0F, serverPlayer.getHealth() - healthCost));
}
```

扣血方式不变（直接 `setHealth`，不经伤害结算，所以不吃护甲/减伤，也不会触发受击事件），
只剩 1 点血时不会再扣。

### 2.2 增伤走效果属性

`ModEffects.BLOOD_RAGE` 的 `addAttributeModifier(攻击伤害 / 远程伤害, BLOOD_RAGE_DAMAGE_BONUS, ADD_MULTIPLIED_TOTAL)`，
所以只改常量 `0.4F → 0.07F`，效果本身不用动。

### 2.3 0 消耗的连带效果

- 技能 tooltip 的「所需消耗的灵魂数」行**不会出现**（该行只在消耗 > 0 时生成），
  因此生命代价写进说明文案里，否则玩家看不到成本；
- `prepareSoulConsumption(player, 0)` 一切正常（不会走生命代偿那条路，扣血由技能自己完成）；
- 血怒属于 BUFF 类，仍会触发「附魂激荡」的自动灵魂激荡（上一批已改为任意附魂增益生效）。

## 3. 文案

| 键 | 新文案 |
| --- | --- |
| `blood_rage.desc` | 消耗 10 点生命，近战与远程伤害提高 7%；期间击杀额外获得灵魂。 |
| `tooltip.effect.confluence.blood_rage.0` | 近战与远程伤害提高7%（原写 40%） |

## 4. 文件索引（两版同名同改）

```
common/soulcerer/skill/SoulSkillParameters.java            BLOOD_RAGE_*（0 魂 / 3 秒 / 8 秒 / 10 点生命 / 7%）
common/soulcerer/skill/SoulBuffSkill.java                  healthCostRatio → 固定 healthCost
common/init/ModSoulSkills.java                             传 BLOOD_RAGE_HEALTH_COST
common/data/gen/language/InterfaceLanguageSubProvider.java 技能说明
common/data/gen/language/TooltipLanguageSubProvider.java   效果说明
```

## 5. 仍未做

两版都没有进游戏实测。提醒一点：3 秒冷却 + 8 秒持续 + 0 魂消耗，
意味着血怒可以靠每 8 秒扣 10 血永久维持，7% 增伤会变成常驻加成；
如果这不是预期手感，可以再调冷却或让它不可叠加刷新。

# 魂师技能数值调整 + 两处机制改动

本轮按需求表调整全部魂师技能数值，另有两处结构性改动（灵魂枪手改为增益、附魂激荡改为对全部附魂类生效）。
数值仍然只写在 `SoulSkillParameters`，技能类只读常量。

## 1. 数值总表（旧 → 新）

| 技能 | 项目 | 旧 | 新 |
| --- | --- | --- | --- |
| 强力附魂 | 持续时间 | 15 秒 | **25 秒** |
| | 冷却 | 15 秒 | 15 秒（不变） |
| | 灵魂消耗 | 1 | **3** |
| 强力引魂 | 冷却 | 25 tick（1.25 秒） | **16 tick（0.8 秒）** |
| 强力激荡 | 伤害 | 12 | **8** |
| | 冷却 | 60 tick（3 秒） | **16 tick（0.8 秒）** |
| | 灵魂消耗 | 4 | **3** |
| 灵魂激荡冲击 | 伤害 | 10 | **12** |
| | 三次后终结伤害 | 20 | **24** |
| | 灵魂消耗 | 1 | **3** |
| | 冷却 | 20 tick | 20 tick（不变） |
| 灵魂引发 | 伤害 | 6 | **7** |
| | 冷却 | 60 tick（3 秒） | **20 tick（1 秒）** |
| | 灵魂消耗 | 4 | **3** |
| 亵渎之魂 | 伤害 | 8 | **7** |
| | 冷却 | 40 tick（2 秒） | **20 tick（1 秒）** |
| | 灵魂消耗 | 3 | **4** |
| 自然之惩戒 | 伤害 | 4 | **5** |
| | 冷却 | 40 tick（2 秒） | **30 tick（1.5 秒）** |
| | 灵魂消耗 | 3 | **2** |
| 迷魂孢雾 | 冷却 | 25 tick（1.25 秒） | **10 tick（0.5 秒）** |
| | 伤害 | 2 | **4** |
| | 灵魂消耗 | 2 | **1** |
| 掠夺之魂 | 持续时间 | 15 秒 | **35 秒** |
| | 冷却 | 15 秒 | **25 秒** |
| | 灵魂消耗 | 2 | **5** |
| 血液沸腾 | 冷却 | 15 秒 | **4 秒** |
| 天穹倒转 | 冷却 | 15 秒 | **30 秒** |
| | 持续时间 | 15 秒 | **8 秒** |
| 魂引天星 | 灵魂消耗 | 2 | **12** |
| | 持续时间 | 15 秒 | **20 秒** |
| 灵魂透支 | 冷却 | 15 秒 | **3 秒** |
| | 免消耗时长 | 15 秒 | **20 秒** |
| | 枯竭（禁用主武器） | 15 秒 | **40 秒** |
| 生命归还 | 冷却 | 15 秒 | **5 秒** |
| | 灵魂消耗 | 2 | **5** |

另外：**灵魂激荡冲击**原本用的是通用 `ACTIVE_SOUL_COST`（1），现在独立为 `SURGE_BLAST_SOUL_COST = 3`；
**强力附魂**原本 1.20.1 侧误用了 `ACTIVE_COOLDOWN_TICKS`（20 tick），本轮统一成 `SOUL_BUFF_COOLDOWN_TICKS`（15 秒）。

## 2. 灵魂枪手：主动技能 → 增益技能

| 项 | 旧 | 新 |
| --- | --- | --- |
| 类型 | `ACTIVE`，按键一次打一轮 | **`BUFF`**，按键挂 25 秒增益 |
| 冷却 | 60 tick（3 秒） | **15 秒** |
| 灵魂消耗 | 0（按子弹数另扣） | **10**（施放时扣） |
| 持续时间 | — | **25 秒** |
| 开火 | 按键时 | **增益期间每 10 tick 自动一轮** |

- 新增效果 `ModEffects.SOUL_GUNNER`（「灵魂枪手」，`RegistryObject`/`DeferredHolder<MobEffect>` 按版本）；
- 开火逻辑抽成 `SoulGunnerSkill.fireBurst(ServerPlayer)`，由 `PlayerUtils.tickSoulSkillStates` 在增益期间按间隔调用；
- 一轮开火仍然是「最多 10 发、每点灵魂换一发、命中概率回魂」，灵魂不足 1 点时这一轮跳过；
- 临时贴图：`textures/mob_effect/soul_gunner.png`（与其他占位图相同的问号，290 字节）。

## 3. 附魂激荡：改为对全部「附魂」类增益生效

原来只在玩家身上有 `enhanced_soul` 效果时才定时自动放灵魂激荡。现在：

```java
// SoulSkill 新增钩子，默认 false；SoulBuffSkill / StatefulSoulSkill / EnhancedSoulSkill /
// BoilingBloodSkill / SoulGunnerSkill 各自返回「自己的效果是否在身」
public boolean isBuffActive(Player player);

// PlayerUtils
if (gameTime % (ACTIVE_COOLDOWN_TICKS * 3) == 0
        && isPassiveSoulSkillActive(player, SOUL_SURGE)
        && hasAnySoulBuffActive(player)) { ... }
```

`hasAnySoulBuffActive` 遍历全部注册技能，`isBuff()` 且 `isBuffActive()` 为真即视为“挂着附魂类增益”。
判定放到时间闸门之后，避免每刻遍历。

**注意**：这条需求原文是「对所有强力附魂类的技能生效」。由于“强力附魂类”只对应一个技能，
本实现按「所有附魂（BUFF 类）技能」理解——
即血怒、掠夺之魂、灵魂透支、魂引天星、生命归还、天穹倒转、血液沸腾、强力附魂、灵魂枪手任意一个在身时都会触发。
如果本意只要「强力」系列，请说一声，改判定范围即可。

## 4. 顺带对齐的文案

数值行是自动生成的，说明里再写一遍数字就会自相矛盾，因此改了三处：

| 键 | 旧 | 新 |
| --- | --- | --- |
| `enhanced_soul.desc` | 灵魂伤害提高12%，持续15秒。 | 灵魂伤害提高 12%。（时长由 BUFF持续时间 行给出） |
| `firmament_reversed.desc` | 15 秒内… | **8 秒内**… |
| `soul_gunner.desc` | 消耗灵魂转化为等量灵魂子弹… | **增益期间持续**把灵魂转化为等量灵魂子弹… |

## 5. 文件索引（两版同名同改）

```
common/soulcerer/skill/SoulSkillParameters.java   全部数值
common/soulcerer/skill/SoulSkill.java             isBuffActive 钩子
common/soulcerer/skill/{SoulBuffSkill,StatefulSoulSkill,EnhancedSoulSkill,BoilingBloodSkill}.java  isBuffActive
common/soulcerer/skill/SoulGunnerSkill.java       改为增益 + fireBurst
common/soulcerer/skill/SurgeBlastSkill.java       独立耗魂 3
common/init/ModEffects.java                       SOUL_GUNNER 效果
common/util/PlayerUtils.java                      hasAnySoulBuffActive + 灵魂枪手定时开火
common/data/gen/language/InterfaceLanguageSubProvider.java  三条说明文案
resources: textures/mob_effect/soul_gunner.png    占位图
```

## 6. 仍未做

1. 两版都没有进游戏实测：数值是否落在手感区间、灵魂枪手增益期间的开火频率与耗魂速度、附魂激荡在多个增益叠加时是否会“连发”。
2. 灵魂枪手、附魂印记（`SOUL_MARK`）这类通过“命中/定时”挂增益的技能，tooltip 的 BUFF持续时间行只在
   `SoulBuffSkill`/`StatefulSoulSkill` 体系里实现；`SoulProjectileSkill` 的 `selfEffect` 还没有暴露时长，
   所以附魂印记的 tooltip 暂不显示它那 25 秒。需要的话可以补。

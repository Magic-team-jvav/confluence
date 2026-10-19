# 技能 tooltip 数值化

把各技能的数值按统一格式标进 tooltip（C 键总览节点与 HUD 技能框共用同一份行序）。

## 1. 行序（固定）

```
技能名称
伤害：5                  ← 仅当该技能有伤害
冷却：1 秒               ← 仅当该技能有冷却
BUFF持续时间：15 秒（强力附魂）   ← 每条自增益一行，可重复
所需消耗的灵魂数：1
已学会 / 已装备 / 临时 …        ← 原有状态行
技能描述：释放一道远程单体灵魂弹幕。  ← 统一话术，永远最后一行
```

- 数值为 0 的项不出现（被动技能通常只剩「名称 + 状态 + 技能描述」）；
- **BUFF持续时间**可以有多行：技能给自己挂几个增益就列几行，格式 `秒数（效果名）`；
- 「技能描述：」是统一话术，用同一个翻译键拼接，各技能不再各写各的前缀。

## 2. 数值来源

| 行 | 取值 | 说明 |
| --- | --- | --- |
| 伤害 | `SoulSkill#getDamage(stack)` | 技能基础伤害；不含玩家属性加成 |
| 冷却 | `SoulSkill#getCooldown(stack)` / 20 | tick 换算成秒 |
| BUFF持续时间 | `SoulSkill#getBuffDurations(stack)` | 新增钩子，返回 `BuffDuration(效果名, 持续tick)` 列表 |
| 灵魂消耗 | `SoulSkill#getSoulCost(stack)` | |

`getBuffDurations` 默认返回空表，只有真正给自己挂增益的类覆盖：

| 类 | 覆盖内容 |
| --- | --- |
| `SoulBuffSkill` | 血怒、掠夺之魂、天穹倒转、强力附魂（自身效果 + 构造参数里的时长） |
| `StatefulSoulSkill` | 灵魂透支、魂引天星、生命归还 |
| `EnhancedSoulSkill` | 强力附魂（`ENHANCED_SOUL_DURATION_TICKS`） |
| `BoilingBloodSkill` | 血液沸腾（`BOILING_BLOOD_DURATION_TICKS`） |

覆盖了 `use()` 却**没有**列出的：

- `NaturesWrathSkill`：给敌人挂中毒 5 秒，属于命中附带，不是自身 BUFF，故不进「BUFF持续时间」；
- `SurvivalInstinctSkill`：只缩短耐药性剩余时长，没有固定持续时间。

## 3. 实现位置

- `SoulSkillStack`：新增 `getSkillHeaderLines()`（名称 + 数值）、`getSkillDescriptionLine()`（统一话术的说明行），
  `getSkillDescriptionLines()` 改为两者拼接；数值格式化 `formatNumber` 整数不带小数点，其余保留一位。
- `SoulSkillBox#renderTooltip`：改为 **名称+数值 → 状态 → 技能描述** 的顺序
  （原来是把状态行接在说明后面，导致说明不在最后一行）。
- `SoulSkill`：新增 `BuffDuration` record、`getBuffDurations` 钩子与 `buffDuration(...)` 构造辅助。
- `InterfaceLanguageSubProvider`：新增 5 个数值行翻译键。

翻译键：

```
confluence.soulcerer_skill.tooltip.damage        = 伤害：%s
confluence.soulcerer_skill.tooltip.cooldown      = 冷却：%s 秒
confluence.soulcerer_skill.tooltip.buff_duration = BUFF持续时间：%s 秒（%s）
confluence.soulcerer_skill.tooltip.soul_cost     = 所需消耗的灵魂数：%s
confluence.soulcerer_skill.tooltip.description   = 技能描述：%s
```

## 4. 版本差异

- 1.20.1 的效果注册是 `Supplier<MobEffect>`（1.21.1 是 `Supplier<Holder<MobEffect>>`），
  因此 `buffDuration(...)` 的签名两版不同，取显示名的方式也不同（`.get()` vs `.get().value()`）；
- 状态行、说明行在两版都是同一套 `SoulSkillStack` 逻辑。

## 5. 仍未做 / 待确认

1. 说明文案里的「被动：」前缀**原样保留**（这是内容层的措辞，不是行结构）；
   「统一话术」本轮理解为**行标签统一**（全部用「技能描述：」）。
   若需要连措辞一起统一（例如去掉「被动：」前缀、统一句式），需要改 31 条 desc，可以再来一轮。
2. 两版都没有进游戏实测：行序、数值换算、多 BUFF 技能的显示。

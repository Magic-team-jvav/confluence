# 三处调整：轮转不再跳冷却 / 刻骨的记忆改为随机放行一个 / 总览装备栏下移

## 1. 轮到冷却中的技能时不再跳过

「遇到空槽或被动就跳到下一个技能」这条逻辑里，原本还多过滤了一层**冷却中**：

```java
// 旧：冷却中也当成“用不了”，继续往后找
if (!isSoulSkillAvailable(player, id) || getSoulSkillCooldown(player, id) > 0) continue;
```

现在只按「空槽 / 被动 / 当前不可用」筛选，**冷却中的技能就是落点**：

```java
if (candidate.isEmpty() || candidate.getSoulSkill().isPassive()) continue;
/// 冷却中的技能不跳过
if (!isSoulSkillAvailable(player, candidate.getSoulSkill().getId())) continue;
return index;
```

- 术式轮转（`rotateToNextSkill`）当时不看冷却，行为不变；**已作废**：后续按新需求改为自动跳过冷却中的技能，
  见 `SOUL_STAR_CALL_STARFURY_ROTATION_COOLDOWN.md`；
- 滚轮/快捷切换只看空槽与被动，也不看冷却，三者口径现在一致；
- 按施法键落到一个正在冷却的技能上时，会照常失败（冷却提示由技能框自己画），不再把选中位甩到别的技能。

## 2. 刻骨的记忆：一次只放行一个「武器临时技能」

### 2.1 原来的问题

戴着饰品时，`tryUseSoulSkill` 直接放行了**任意**已装备技能：

```java
if (!isHoldingSoulWeapon(player) && !TCUtils.hasType(player, MEMORY$OF$BONEBRAND)) return false;
```

### 2.2 现在的规则

| 环节 | 行为 |
| --- | --- |
| 候选池 | 背包（主背包 + 副手）里所有 `SoulWeaponItem` 授予的技能，**排除已永久学会的** |
| 选择 | 从候选里**均匀随机**取一个，存进 `SoulSkillStorage.memoryOfBonebrandSkill` |
| 放行 | 该技能会被加进 `available`（可装备、可释放），不持武器时**只有它以**能放 |
| 重掷 | 每次成功施放技能后重新随机 |
| 失效 | 选择不在候选里（换了装备/学会了）→ 下次刷新自动重掷；脱下饰品 → 清空 |

关键代码（`PlayerUtils`）：

```java
// refreshSoulSkills 里
ResourceLocation memorySkill = resolveMemoryOfBonebrandSkill(player);
if (memorySkill != null) available.add(memorySkill);

// tryUseSoulSkill 里
if (!isHoldingSoulWeapon(player) && !isMemoryOfBonebrandSkill(player, skill)) {
    return rejectSoulSkillUse(player, availabilityChanged);
}
...
rerollMemoryOfBonebrandSkill(player);   // 施放成功后重掷
```

- 持久化：`SoulSkillStorage` 新增 NBT 字段 `memoryOfBonebrandSkill`；
- 候选去重后随机，`player.getRandom()` 取样；
- 只要求「不**手持**武器」，武器仍需在背包里（"不用特定的武器"按“不拿在手上”理解）。

### 2.3 需要你确认的一处理解

需求里说“我获得过星魄武器过后…可以视为解锁并装备的”，我按**当前背包里带着**来判定候选，
而不是“曾经获得过”（后者要额外记录每把武器是否曾拥有，且武器进箱子后仍能放行会偏强）。
如果需要“获得过就永久计入候选”，我再补一个持久集合。

另外：**法器（SoulArtifactItem）不参与**重掷——需求写的是“每次使用技能后”，法器不算技能。

## 3. 总览装备栏下移

`SoulOverviewScreen#updateHotbarPos`：

```diff
- hotbar.setPosition(width / 2 - hotbar.getWidth() / 2, (int) (height * 0.82f));
+ hotbar.setPosition(width / 2 - hotbar.getWidth() / 2, (int) (height * 0.87f));
```

1080p 下从 y≈886 移到 y≈940，快捷栏高 32 + 槽位内缩 9，底部仍在屏幕内；
同时给技能树多留了约 5% 的竖向空间。

## 4. 文件索引（两版同名同改）

```
common/attachment/SoulSkillStorage.java         memoryOfBonebrandSkill 字段 + NBT
common/util/PlayerUtils.java                    resolveMemoryOfBonebrandSkill / reroll / isMemoryOfBonebrandSkill /
                                                候选收集、施法门槛、冷却不再跳过
client/gui/container/soulcerer/SoulOverviewScreen.java  装备栏 0.82 → 0.87
common/data/gen/language/TooltipLanguageSubProvider.java 饰品说明
```

## 5. 仍未做

两版都没有进游戏实测：随机放行的观感（每次施法后换技能）、不持武器时的技能栏状态、
装备栏下移后与技能树的遮挡情况。

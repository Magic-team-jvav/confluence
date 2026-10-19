# 被动技能改为装备生效 + 精致样式灵魂条（1.21.1 ⇄ 1.20.1 双版本同步，本文为 1.20.1 侧副本）

本轮两件事：

1. 纠正魂师**被动技能**的生效条件：不是「解锁即生效」，而是「解锁 + 装在灵魂技能槽」才生效。
2. 应用提交 `74a83bbd0 魂條精緻紋理` 新画的灵魂条图元，让**精致样式**（`manaStyle=legacy`）的灵魂条用上它。

## 0. 验证

| 侧 | compileJava |
| --- | --- |
| 1.21.1 NeoForge | `BUILD SUCCESSFUL` |
| 1.20.1 Forge | `BUILD SUCCESSFUL`（首次偶发 `javac OutOfMemoryError`，原样重跑即通过，属已知现象） |

本轮没有新增数据生成产物，`runData` 无需重跑。

## 1. 被动技能：解锁 → 装备后生效

### 1.1 原来错在哪

- `PlayerUtils.setSoulSkillStack` 里有一条 `|| skillStack.getSoulSkill().isPassive()`，**直接拒绝把被动装进槽**；
- 所有被动效果都用 `isSoulSkillLearned(...)`（只判断「是否学会」）放行，
  所以被动一解锁就永久生效，装备栏对它没有任何意义。

### 1.2 现在的规则

新增唯一判定入口（`PlayerUtils`）：

```java
/// 被动技能是否正在生效：既已学会（可用），又装备在灵魂技能槽里。
public static boolean isPassiveSoulSkillActive(@Nullable Player player, ResourceLocation skillId) {
    return isSoulSkillAvailable(player, skillId) && isSoulSkillEquipped(player, skillId);
}

public static boolean isPassiveSoulSkillActive(@Nullable Player player, SoulSkill skill);  // 重载
```

- `isSoulSkillAvailable` 同时覆盖「永久学会」「武器/法器临时授予」「技能自身条件」三层，
  所以临时来源失效时被动也跟着失效；
- `isSoulSkillEquipped` 复用既有的按 ID 扫描装备槽实现；
- 装备槽不再拒绝被动（`setSoulSkillStack` 去掉了 `isPassive()` 判断）。

### 1.3 改成新判定的位置

| 被动 | 效果 | 位置 |
| --- | --- | --- |
| 分流管控 `flow_control` | 技能伤害 ×2、冷却 ×1.5 | `PlayerUtils.setSoulSkillCooldown`、`getSoulSkillDamage` |
| 星魂联结 `star_link` | 灵魂伤害共用 50% 魔法伤害加成 | `PlayerUtils.getSoulSkillDamage` |
| 自然法则 `law_of_nature` | 20% 概率不耗魂 | `PlayerUtils.prepareSoulConsumption` |
| 诅咒抵抗 `curse_resistance` | 生命代偿伤害 −60% | `PlayerUtils.applySoulOverdraft` |
| 术式轮转 `spell_rotation` | 用完技能自动切下一个可释放槽 | `PlayerUtils` 三处（`consumeSoul` / `tryUseSoulSkill` / 法器释放） |
| 附魂激荡 `soul_surge` | 强力附魂期间定时自动激荡 | `PlayerUtils.tickSoulSkillStates` |
| 噬魂提气 `soul_devour` | 召唤物攻击耗 1 魂换 50% 增伤 | `LivingEntityEvents.soulSkillSummonDamage` |
| 代行收割 `soul_harvest` | 召唤物命中 5% 回 1 魂 | `LivingEntityEvents.soulSkillSummonDamage` |

`star_reversal`（星魂逆转）本来就没有实现（`convertManaToSoul` / `convertSoulToMana` 仍是返回 false 的占位），本轮不动。

### 1.4 没有改的东西

- 被动仍然**不能主动释放**：`SoulSkill.canUse` 默认 `!isPassive()`，`PassiveSoulSkill.canUse` 恒 false；
- 术式轮转与「当前槽」判定继续跳过被动（`rotateToNextSkill` 的 `!isPassive()` 保留），
  所以轮转不会把光标停在不能放的被动上；
- 技能书类被动（引渡者等 `SoulBooks`）仍走 `isSoulBookLearned`，不属于「技能」，不受本轮影响；
- 技能槽数量规则不变（初始 3 槽，可扩到 6）。被动开始占槽后，玩家需要自己权衡槽位分配。

### 1.5 顺带更新的说明注释

`SoulSkill.Kind`、`PassiveSoulSkill`、`ModSoulSkillTagsProvider` 的 `PASSIVE` 标签注释由
「学会即生效 / 无法装备」改为「可装备、装备后生效」。

## 2. 精致样式（LEGACY）灵魂条

### 2.1 贴图来源

提交 `74a83bbd0 魂條精緻紋理` 只改了一张图：`assets/confluence/textures/gui/sprites/hud/icon.png`，
把原来两块**纯色占位方块**画成了两个图元（图集 128×128）：

| 图元 | 图集坐标 | 尺寸 | 用途 |
| --- | --- | --- | --- |
| 魂器底座（暗紫圆盘 + 玫瑰金描边） | `u=33, v=35` | 14×15 | 空槽 / 背景 |
| 骷髅（淡紫白） | `u=51, v=37` | 10×11 | 已填充 |

配置项 `confluence.configuration.manaStyle.legacy` 的中文名正是「魔力值样式:精致」，
所以「精致样式灵魂条」= `TerraStyleManaHud.Mana.LEGACY` 分支。

### 2.2 实现

`TerraStyleManaHud` 新增：

```java
private static void renderLegacySoulBar(GuiGraphics, float currentSoul, int maxSoul, int x, int y);
private static void blitSoulFrame(GuiGraphics, int x, int y);   // 底座
private static void blitSoulFill(GuiGraphics, int x, int y);    // 骷髅
```

- 灵魂模式下 `LEGACY` **提前返回**，不再走「魔力条 + 青色染色」的老路
  （新图元自带配色，染色只会把它压成青色，所以灵魂条不再调用 `setColor`）；
- 排版沿用魔力条的既有口径：每 **20 灵魂**一格（`RESOURCE_PER_LEGACY_ICON`），
  自右上向下每 12 像素一格（`LEGACY_ICON_SPACING`），锚点同为 `guiWidth - 21`、`y = 4` + 配置偏移；
- 底座在魔力条 17 像素宽的口径内居中（`+1`），骷髅在底座内再内缩 `(2, 2)`，
  正好落在底座的暗色区域内（14−10=4、15−11=4，各留 2 像素）；
- 不满一格的余量沿用魔力条做法：骷髅按比例从右下角缩放，读起来仍是「快满了」。

### 2.3 资源同步

1.20.1 的 `hud/icon.png` 还是旧版（两块纯色方块）。逐像素比对确认两版图集**只有**这块区域不同，
因此直接把 1.21.1 的新图集复制到 1.20.1，其余像素完全一致。

### 2.4 没有改的东西

- 叠加样式（`manaStyle=overlay`，默认值）的灵魂条仍走 `hud/overlay.png` 的 `v=60` 行 + 青色染色，
  这是另一套图集，本轮没有重绘；
- 魔力条本身的图形与位置未变（只是把 `i * 12` 换成同值常量 `LEGACY_ICON_SPACING`）。

## 3. 按施法键时遇到空槽 / 被动：直接跳到下一个技能

被动开始占槽后，选中的槽可能是被动（也可能是空槽）。原来按施法键会落进
`skillStack.isEmpty()` 或 `!skill.canUse(...)` 分支**静默失败**（按键没反应）。
现在 `PlayerUtils.tryUseSoulSkill` 在取到当前技能栈后先判一次：

```java
/// 当前槽是空槽或被动时，同一次按键直接跳到下一个可释放的技能，避免按键毫无反应。
if (skillStack.isEmpty() || skillStack.getSoulSkill().isPassive()) {
    int next = findNextCastableSoulSkillIndex(player);
    if (next < 0) return rejectSoulSkillUse(player, availabilityChanged);
    setCurrentSoulSkillIndex(player, next);
    SoulSkillIndexSyncPacketS2C.sendToClient(player);
    skillStack = getSkillStack(player, next);
}
```

`findNextCastableSoulSkillIndex`：从当前槽向后（含回绕，最多绕一圈）找第一个
「非空、非被动、`isSoulSkillAvailable`、冷却为 0」的槽位；找不到返回 `-1`。

要点：

- **同一次按键就完成跳转并施法**，不用按两次；跳转通过 `SoulSkillIndexSyncPacketS2C` 同步到客户端，
  HUD 当前技能图标会立刻跟上（该包的 `work()` 里已经带 `updateSoulSkillHuds()`）。
- 跳过的是"当前用不了的"，不只是空槽/被动：若下一个槽在冷却中，会继续往后找，避免又落进静默失败。
- 一圈都找不到可释放的槽时**不移动选中索引**，直接老样子静默失败
  （此时多半是全空 / 全在冷却 / 全是被动，按施法键挪动选中没有意义；
  注意这一条与 §3.1 的切换键规则**不同**：切换键在无可用槽时仍要让高亮动起来）。
- 施法成功后若装了「术式轮转」，仍按原逻辑再轮转一次，两者不冲突（先跳到可释放槽，用完再按轮转规则走）。

### 3.1 快速切换键同样跳过空槽与被动

滚轮 / 快捷面板切换走 `ClientSoulHandler.adjustSoulSkillTarget(offset)`，现在也跳过空槽与被动槽：

```java
int effectiveOffset = SoulQuickSkillHudUtils.calculateSkippingOffset(offset, currentIndex, maxNumber,
        index -> {
            SoulSkillStack stack = PlayerUtils.getSkillStack(player, index);
            return stack.isEmpty() || stack.getSoulSkill().isPassive();
        });
if (effectiveOffset == 0) return;               // 全是空槽 / 被动：保持不动
ROULETTE_WHEEL_SMALL_HUD_INSTANCE.adjustTarget(effectiveOffset);
CARD_HORIZONTAL_L_HUD_INSTANCE.adjustTarget(effectiveOffset);
CARD_HORIZONTAL_R_HUD_INSTANCE.adjustTarget(effectiveOffset);
setCurrentSoulSkillIndex(SoulQuickSkillHudUtils.calculateSkillIndex(effectiveOffset, currentIndex, maxNumber));
```

新增 `SoulQuickSkillHudUtils.calculateSkippingOffset(offset, currentIndex, totalSkills, skip)`：

- 沿 `offset` 的方向逐槽前进，遇到 `skip` 为真的槽直接越过，直到落够 `|offset|` 个可用槽；
- 返回的是**实际滑过的槽位数**（带符号），因此既能用 `calculateSkillIndex` 求落点，
  也能直接喂给 HUD 的 `adjustTarget(...)` —— 三个快捷面板的 `targetRotation/targetAngle` 都按这个格数转，
  跳格后**动画与选中一致**，不会出现"转一格却跳过两格"的错位；
- 有可用槽但绕满一圈凑不够 `|offset|` 时，停在最后一个可用落点；
- **一个可用槽都没有时退化为不跳过**：直接返回传入的 `offset`，高亮照常移动
  —— 新角色技能槽全空时滚轮不会毫无反应，回退到本轮之前的手感。
  只有 `totalSkills <= 0` 或 `offset == 0` 时才返回 0（真正不动）。

大轮盘 `RouletteWheelBigHud` 保持原样：它是**鼠标指针直接点选**，指着哪个槽就选哪个，
不做跳过（选到空槽或被动后按施法键仍会跳到下一个可释放技能）。

## 4. 文件索引

两版同名同改：

```
common/util/PlayerUtils.java                      判定入口 + 8 处被动效果改判定 + 放开被动装备 + 施法键跳过空槽/被动
common/event/game/entity/LivingEntityEvents.java  噬魂提气 / 代行收割改判定
common/soulcerer/skill/SoulSkill.java             Kind 说明
common/soulcerer/skill/PassiveSoulSkill.java      类说明
common/data/gen/tag/ModSoulSkillTagsProvider.java PASSIVE 标签说明
client/handler/soulcerer/ClientSoulHandler.java   快速切换跳过空槽/被动
client/util/soulcerer/SoulQuickSkillHudUtils.java calculateSkippingOffset
client/gui/hud/TerraStyleManaHud.java             精致样式灵魂条
resources/.../textures/gui/sprites/hud/icon.png   1.20.1 侧同步新图集
```

## 5. 仍未做

1. 两版都没有进游戏实测：被动装/卸的即时生效与失效、8 条被动逐个手感、
   精致样式灵魂条在 0 / 半格 / 满格 / 多格下的显示、空槽/被动槽按施法键与快速切换键的跳转手感。
2. 大轮盘（指针点选）仍可把选中停在空槽或被动上；按施法键时会自动跳到下一个可释放技能。

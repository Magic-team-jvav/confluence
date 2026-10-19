# 天穹倒转补入技能总览

`firmament_reversed`（天穹倒转）此前能通过技能书学会、也进了技能表，但 **C 键技能总览里没有它的节点**，
所以玩家在总览中既看不到也点不到它。本轮把它补进树里，并把说明文案补精确。

## 1. 现状核对（结论：只缺节点）

| 项目 | 状态 |
| --- | --- |
| 技能注册 `ModSoulSkills.FIRMAMENT_REVERSED` | 已有（`SoulBuffSkill`，星引分类） |
| 技能书 `SoulBooks.FIRMAMENT_REVERSED` | 已有，且可获取（宝箱 + 天师 3 级交易） |
| 一览节点 | **缺失** |
| 名称 `confluence.soulcerer_skill.firmament_reversed.name` | 已有：「天穹倒转」 |
| 说明 `...firmament_reversed.desc` | 已有，但只写了「一段时间内摔落伤害转化为回复血量」，没有时长与转化比例 |
| 临时贴图 `hud/soulcerer_skill/firmament_reversed.png` | **已存在**，16×16、与其余占位问号图**逐字节相同**（sha256 `A422B05B…`），文件名就是技能 id，替换时直接覆盖同名 png 即可 |

## 2. 补的节点

```java
// 天穹倒转
addNode(OverviewNode.Builder.of(ModSoulSkills.FIRMAMENT_REVERSED)
        .pos(-120, -80)
        .connections(ModSoulSkills.STAR_CALL));
```

- 位置 `(-120, -80)`：左列（增益/被动树）里 `灵魂透支 (-120, -40)` 的下方一格，此前为空；
- 连线到 `魂引天星 (-80, -40)`：两者同属星引分类，且是天空/星辰一脉；
- 与 `自然法则 (-80, -80)` 的既有连线不交叉，节点间距仍是 40（节点 32 + 8 间隙）。

节点一旦存在，`OverviewNode` 的默认提示就会自动带上「名称 + 说明 + 状态（已学会/临时/未解锁/已装备/不可用）」，
不需要额外写 tooltip 行（其它节点也没有调用 `.tooltip(...)`）。

## 3. 补的文案

```diff
- "15 秒内摔落伤害转化为回复血量。"   // 旧
+ "15 秒内，摔落伤害完全转化为生命回复。"   // 新（en: For 15 seconds, fall damage becomes healing instead.）
```

与实现对齐：`FIRMAMENT_REVERSED_DURATION_TICKS = 15 秒`，
`FIRMAMENT_REVERSED_CONVERT_RATIO = 1.0`，且结算后 `event.setCanceled(true)`（伤害被吃掉，不是同时受伤又回血）。

## 4. 替换临时贴图的方法

占位问号图与其他占位技能图完全相同，替换时：

1. 直接覆盖 `assets/confluence/textures/gui/sprites/hud/soulcerer_skill/firmament_reversed.png`（16×16）；
2. 正式图标建议保持 16×16 的 GUI sprite（会按 `SKILL_SIZE` 缩放绘制）；
3. 若也想要正式的法术效果图标，同名覆盖 `assets/confluence/textures/mob_effect/firmament_reversed.png`
   （当前同样是问号占位）。

## 5. 文件索引（两版同名同改）

```
client/gui/container/soulcerer/SoulOverviewScreen.java  新增 firmament_reversed 节点
common/data/gen/language/InterfaceLanguageSubProvider.java  说明文案补精确
```

## 6. 仍未做

两版都没有进游戏实测：节点位置观感、连线是否穿插、提示文本是否完整。

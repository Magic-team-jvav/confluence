# WP2 批次 11 · 蜂群 / 沙虫 / 海盗入侵三簇 + 环境事件生成否决钩子

> 对象：`LittleHornet`(191) `SimpleWormMonster`(220) `PirateRangedMonster`(156) 三个「闭包只剩 2 个新增」的种子
> —— 实际展开成 8 个文件 / 1398 非空行，因为它各自牵出一个更大的支撑面。

## 一、开工测量

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --alias "org.confluence.lib.util.LibEntityUtils=org.confluence.lib.util.LibUtils" `
  --seed …/LittleHornet.java --seed …/SimpleWormMonster.java --seed …/PirateRangedMonster.java
# candidates 8 / kept 8 / new 8 / removed 0
```

展开出的 8 个文件与「谁牵出谁」：

| 种子 | 牵出的支撑 |
|---|---|
| `LittleHornet` | `entity/boss/BossOwnerTracker`(127)、`entity/boss/QueenBee`(407)——小黄蜂是蜂后的仆从，用 `BossOwnerTracker` 认领 |
| `SimpleWormMonster` | `gameevent/SandstormGameEvent`(86) + 需要注册层的 `registerWorm` 系列与段体 |
| `PirateRangedMonster` | `entity/projectile/PirateShot`(64)、`gameevent/PirateInvasionGameEvent`(147) |

`QueenBee` 属 WP3（Boss）的目录，但它是 `LittleHornet` 的**硬依赖**（`LittleHornet` 用
`BossOwnerTracker` + 蜂后所有权），所以随本批搬入；它的**注册**（`BossEntities.QUEEN_BEE`）不在本批
—— 1.20 侧蜂后的注册在 `BossEntities` 里，等 WP3 的注册层批次一起做，`QueenBee` 自己也不引用它。

## 二、本批落地

| 项 | 内容 |
|---|---|
| 新增 8 个文件 / 1398 行 | 上表 8 个 |
| `ModEntities` | +3 条海盗弹幕（`PIRATE_BULLET` 0.15×0.15 / `PIRATE_FLAMING_ARROW` 0.2×0.2 / `PIRATE_CANNONBALL` 0.4×0.4，id 与 `PirateShot.Kind` 照 1.20 `ModEntities.java:186-188`） |
| `MonsterEntities` | +`registerWormSegment` + 2 个 `registerWorm` 重载 + **10 条注册**：`LITTLE_HORNET` `TOMB_CRAWLER_SEGMENT` `TOMB_CRAWLER` `PIRATE_DECKHAND` `PIRATE_CORSAIR` `PIRATE_DEADEYE` `PIRATE_CROSSBOWER` `PIRATE_CAPTAIN` `PIRATE_PARROT` `PIRATES_CURSE` |
| `ModTags` | +`EntityTypes.FLESH_ALLIANCE`（**只建键、不加成员**，理由见第四节） |
| `GameEvent` | +`default boolean canSpawnEntity(ServerLevel, EntityType<?>)`（1.20 `GameEvent.java:35`） |
| `GameEventSystem` | `customSpawner` 补回 1.20 的两道判断：`DevelopmentSpawnPolicy.allowsAutomaticSpawn(...)` 与 `event.canSpawnEntity(...)`（1.20 `GameEventSystem.java:243` / `:247`） |

### 顺带把批次 10 的 `DevelopmentSpawnPolicy` 接上了

批次 10 新建 `DevelopmentSpawnPolicy` 时它只是个空壳（没有任何读取点）。本批按 1.20 的位置
把 `allowsAutomaticSpawn` 的调用补进 `GameEventSystem.customSpawner`，这个类才真正生效 ——
「未发布生物的自动生成受限」现在是真的了。

## 三、本批挖出的两处 API 差异（都已成规则）

### 1. `Entity#setSecondsOnFire(int)` → `igniteForSeconds(float)`（1.21.1 `Entity.java:539`）

改名 + 改型（`int` → `float`）。实证：`PirateShot.java:35` 报 `cannot find symbol: method setSecondsOnFire(int)`。
同一批新增 `igniteForTicks(int)`（`:543`），`setRemainingFireTicks(int)`（`:549`）保留。
规则 `vanilla-setsecondsonfire-igniteforseconds`（1.20 侧共 8 处），pattern 带前置 `.` 以免误伤同名标识符。

### 2. 港口/环境事件的生成否决钩子在 1.21 侧整个缺失

1.20 的分叉后架构给 `GameEvent` 加了 `canSpawnEntity(ServerLevel, EntityType<?>)`（默认 `true`），
并在 `GameEventSystem.customSpawner` 里调用（同处还有 `allowsAutomaticSpawn` 判断）；
1.21 侧的 `GameEvent` 是**旧版接口**，这两道都没有。这是「1.21 侧基础层比 1.20 旧」的又一处，
本批按 1.20 补齐（默认返回值保证既有事件语义不变）。

## 四、有意留的过渡态：`FLESH_ALLIANCE` 只有键、没有成员

1.20 的 `ModEntityTypeTagsProvider.java:167` 给这个标签列了 6 个实体：
`LEECH` / `FLESH_SLIME` / `THE_HUNGRY` / `HILL_HUNGRY` / `HILL_OF_FLESH` / `WALL_OF_FLESH` ——
**1.21 侧一个都还没有**（前四个是 slime/TheHungry 族，后两个是肉山 Boss）。
所以本批只建标签键（`SimpleWormMonster.java:167` 要读它），**不往 provider 里加条目**：
标签在游戏里不存在时 `is(tag)` 恒为 false，不会崩也不会报错。
那六个实体落地时把 provider 条目一起补上。这是有意的过渡态，不是遗漏。

## 五、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| `BossEntities.QUEEN_BEE`（蜂后的注册） | 1.20 侧在 `BossEntities` 里；1.21 侧还没有这个类，属 WP3 的注册层批次 |
| `FLESH_ALLIANCE` 的 6 个成员 | 见第四节 |
| `Gnome` | 卡方块层 `DecorativeBlocks.GARDEN_GNOME` |
| slime 全族 + `Werewolf` / `VisualNeuron` / `TheHungry` / `HillHungry` / `HungryMovementAction` / `DungeonSpirit` / `WildyBalloon` / `MonsterAttributeScaling` | 卡 `TownSlimeNPC` → NPC 层（WP4），见 `notes/WP2I-SUBSET.md` 第五节 |

## 六、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器：三次转换 `uncovered.json` 都是 `[]`；最后一次 `leftovers.txt` 空
（第一次有 1 行 `PortTags.Biomes.IS_DESERT`，由新增的 `porttags-to-neoforge-tags` 规则消掉）。
残留自查：本批 8 个 java 文件里 `portlib|PortLib|Port[A-Z]\w+|PortTags` **0 命中**。
规则文件：23 条，`json.load` 通过、无空 pattern/replace，两条新规则均做了正/反例自检。

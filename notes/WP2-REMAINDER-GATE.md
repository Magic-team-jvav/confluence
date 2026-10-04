# WP2 剩余部分的前置：不是「物种没搬完」，而是**依赖顺序**

> 本文件是 2026-09-27 第 8 轮的测量结论，用来修正 `notes/WORK-QUEUE.md` 的执行顺序。
> 所有数字都是 `tools/port2native/dep_subset.py` 的输出，可复现。

## 一、结论（先说清楚）

WP2 的**怪物**目录还剩 20 个类、**动物**目录 40 个类一个都没搬。这两块的剩余部分**互相不构成阻塞**，
但**都被同一层挡住**：`common/entity/npc/**`（1.20 有 66 个类，1.21 只有 3 个）与注册层。
换句话说：**WP4（NPC 体系）是 WP2 收尾的前置**，而不是「WP2 还有活没干」。

## 二、三个可复现的测量

### 1. 怪物目录剩 20 个，其中 18 个的闭包是 189~191 个文件

```powershell
python tools/port2native/seed_closures.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --seed-dir common/entity/monster `
  --assume-present org.confluence.mod.common.init.entity.MonsterEntities `
  --alias "…ModEntities=…ModEntities" --alias "…LibEntityUtils=…LibUtils"
# {"seeds": 20, "light": 2, "heavy": 18, "min_new": 0, "max_new": 191}
```

- 轻量 2 个：`WindyBalloon`(88)、`Gnome`(37) —— 前者引用 `MonsterEntities.BLUE_SLIME` → `BaseSlime`，
  后者卡方块层的 `DecorativeBlocks.GARDEN_GNOME`，**都不是自身工作量问题**
- 重量 18 个：slime 全族 + `Werewolf` `VisualNeuron` `TheHungry` `HillHungry`
  `HungryMovementAction` `DungeonSpirit` `Slimer` `MonsterAttributeScaling`，闭包都是 ~189
- 报告：`notes/WP2K-CLOSURES.md`（批次 11 之前那份是 `notes/WP2I-CLOSURES.md`，那时的数是 199，
  差值 10 就是批次 11 落地的 `BossOwnerTracker` 等文件）

### 2. 那个 189 的入口点叫 `TownSlimeNPC`

```powershell
python tools/port2native/dep_subset.py … --seed org/confluence/mod/common/entity/npc/TownSlimeNPC.java
# {"candidates": 189, "kept": 189, "new": 189, "removed": 0}
```

`TownSlimeNPC` **一个类**的闭包就是 189 个文件 —— 它就是 NPC 框架的入口
（链：`TownSlimeNPC` → `BaseNPC` → `TradeCondition` → `ModCustomRegistries` → `IGeneration` / `ITrackType` /
`BulletEvent` …，另加 `MoodEnvironment` / `house` / `dialog` / `chat` / NPC 界面）。

而 `BaseSlime` 需要它只是因为**一行**：

```java
// 1.20 BaseSlime.java:200
if (!level().isClientSide && tickCount % 5 == 0 && TownSlimeNPC.tryEquipSquire(this)) return;
```

「城镇史莱姆给史莱姆穿侍从装备」这一个机制，把整条 NPC 框架挂到了史莱姆族上。
**不建议为它做桥接**（1.21 侧不引入半成品的原则），也不建议把这一行删掉 —— 那是丢功能。

### 3. 动物目录（WP2 的另一半）同样被挡

1.20 的 `common/entity/animal/**` 有 41 个 java、1.21 侧 **0 个**。基类 `BaseCritter` 自己的直接依赖是干净的
（`IVariant` + BT 节点 + `ModSoundEvents`），但种子集 `BaseCritter`+`Bunny`+`Bird`+`Crab` 实测：

```powershell
… --defer org.confluence.mod.common.init.entity.CritterEntities `
   --seed …/animal/BaseCritter.java --seed …/animal/Bunny.java --seed …/animal/Bird.java --seed …/animal/Crab.java
# {"candidates": 230, "kept": 222, "new": 222, "removed": 8}
```

222 个新增里 **62 个是 `common/entity/npc/**`**、37 个 boss、20 个弹幕、18 个枪械物品 ——
同样落到 NPC / 注册层 / 子弹体系上。

### 4. 顺带测了「先做 Boss（WP3）能不能绕开」

```powershell
… --defer org.confluence.mod.common.init.entity.BossEntities `
   --seed …/boss/EyeOfCthulhu.java --seed …/boss/KingSlime.java `
   --seed …/boss/ServantOfCthulhu.java --seed …/boss/BossMultiplayerEnhancement.java
# {"candidates": 189, "kept": 38, "new": 38, "removed": 151}
```

38 个 kept 里仍有 **9 个 NPC 文件** + 11 个弹幕 + 4 个 boss 文件 + 枪械/坐骑/数据文件若干。
即「先做 boss」**能推进**（38 个文件可控），但**绕不开 NPC 层**。

## 三、对执行顺序的修正（已同步进 `notes/WORK-QUEUE.md`）

原计划顺序是 `WP1 → WP2/WP3/WP4/WP5 各自独立推进`。实测表明 WP2/WP3 的**剩余物种**与 WP4 有硬依赖：

| 谁 | 前置 | 依据 |
|---|---|---|
| WP2 怪物剩余 18 个 | WP4 的 `BaseNPC` 框架（经 `TownSlimeNPC`） | 第 2 节 |
| WP2 动物 40 个 | WP4 + 注册层（`CritterEntities`） | 第 3 节 |
| WP3 Boss 剩余 38 个 | 可先做 38 文件切片，但完整落地仍要 NPC 层 | 第 4 节 |
| WP2 `Gnome` | 方块层（`GARDEN_GNOME`） | `notes/WP2G-SUBSET.md` |

所以修正为：**WP4 的 NPC 基座（`BaseNPC` + 贸易/心情/房屋/对话的最小可编译集）应当提前**，
它一次性解开 WP2 剩余 18 个怪物 + 40 个动物 + WP3 的一部分。

## 四、下一步的建议入口（供下一个执行者直接用）

1. 先用 `dep_subset.py --seed org/confluence/mod/common/entity/npc/BaseNPC.java` 量 `BaseNPC` 的闭包
   （预期 ≈ 60~190），按「最大可编译子集」切 2~3 批；`TownSlimeNPC` / `TownSlimeRescue` 跟着进。
2. `BaseSlime` 那一行 `TownSlimeNPC.tryEquipSquire(this)` **等 NPC 层落地后再搬史莱姆族**，
   不要为了提前搬 slime 而删功能或加桥接。
3. 想要「本轮就有代码进账」的话，`notes/WP2K-CLOSURES.md` 里 `Gnome` 之外还有可做的：
   WP3 的 38 文件切片（`EyeOfCthulhu` + `KingSlime` + `ServantOfCthulhu` + `BossMultiplayerEnhancement`），
   其中 `EyeOfCthulhu`/`KingSlime` 是 1.21 侧 `BossEntities` 尚未落地时也能编译的
   （`BossEntities` 只被 `--defer` 掉，没被这些类引用）。

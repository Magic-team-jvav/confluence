# WP2 批次 9 · 注册层首片 + 三个天然依赖它的物种

> 对象：1.20.1 分叉后 `common/init/entity/MonsterEntities`（945 行 / 94 条注册）的第一片，
> 以及 `EaterOfSouls` / `BloodTumor` / `BloodySpore` 三个物种。
> 本文件记开工测量、落地内容、新挖出的差异、推迟项与原因。

## 一、开工前先回答「注册层放行之后还差多少」

`tools/port2native/seed_closures.py` 新增 `--assume-present`：把这些 FQN 当作**已满足**
（不扩张、不计入闭包），用来回答「某个还没搬的层落地之后，这个物种还差多少」。
对 32 个待移植怪物文件跑一遍（报告 `notes/WP2I-CLOSURES.md`）：

| 分组 | 数量 | 说明 |
|---|---:|---|
| 闭包=1（零新增依赖） | 5 | `BloodySpore`(200) `WindyBalloon`(88) `BloodTumor`(71) `EaterOfSouls`(50) `Gnome`(37) |
| 闭包 3~5 | 9 | `LittleHornet`(191) `SimpleWormMonster`(220) `PirateRangedMonster`(156) `Corruptor`(55) `WaterBoltMimic`(145) `Gastropod`(183) `GoblinWarlock`(130) `DarkCaster`(94) `FireImp`(45) |
| 闭包 199~202 | 18 | slime 全族 + `Werewolf` + `VisualNeuron` + `TheHungry` 族 + `MonsterAttributeScaling` |

**关键读法**：`--assume-present` 只把「注册层这个类型」当作已满足，**不解决成员级**需求 ——
物种仍会 `MonsterEntities.X.get()`。所以本批同时把需要的条目写进注册层。
（这两层盲区的区别见 `notes/WP2C-SUBSET.md:73-76`。）

## 二、本批落地

| 项 | 内容 |
|---|---|
| 新增物种 | `common/entity/monster/EaterOfSouls`(50) `BloodTumor`(71) `BloodySpore`(200) = 321 非空行 |
| 新增注册类 | `common/init/entity/MonsterEntities.java`：5 条注册（`BLOOD_CRAWLER` `CRIMERA` `BLOODY_SPORE` `BLOOD_TUMORS` `FACE_MONSTER`）+ 4 个 `registerLand` 重载 + `registerEntity` + `withAttributes` 转发 |
| 既有文件改动 | `Confluence.java`：+`MonsterEntities.ENTITIES.register(eventBus)` 与 import |

**为什么这三条注册不止 3 条**：`BloodTumor` 的掉落/分裂逻辑引用
`MonsterEntities.BLOOD_CRAWLER` / `FACE_MONSTER` / `CRIMERA`（`BloodTumor.java:38-40`），
其中 `BloodCrawler` 与 `BaseWarriorMonster` 两类**已经**在 1.21 侧存在，所以顺带把它们的注册补上
（不补就编不过）。`EaterOfSouls` 自己就是 `CRIMERA` 的类（`EaterOfSouls.java:49` 用
`getType() == MonsterEntities.CRIMERA.get()` 判形态）。

## 三、两处结构性决定

### 1. 注册类放 `common/init/entity/`，不沿用 1.21 的扁平 `common/init/`

1.20 的注册层是 7 个文件同住 `common/init/entity/`；1.21 的 `ModEntities` 住在扁平的
`common/init/`。按「1.21 对齐 1.20」，**新搬来的注册类一律放 `common/init/entity/`**：
好处是 31 个待移植物种里 `import org.confluence.mod.common.init.entity.MonsterEntities;`
可以原样照搬，不必逐个改包路径（`ModEntities` 那个扁平位置是 1b 的历史包袱，靠 `--alias` 处理）。

### 2. 属性声明汇入 `ModEntities.withAttributes`，不另开一套

`MonsterEntities.withAttributes` 只是转发到 `ModEntities.withAttributes(type, attributes)`，
写进同一个 `ATTRIBUTES` 表，由 `ModEvents.entityAttributeCreation` →
`ModEntities.registerAttributes(event)` 一次注册。这样注册层分文件、属性注册仍然只有一条路径。

## 四、新挖出的 API 差异：`ItemStack#hurtAndBreak` 第三参从 lambda 变成 `EquipmentSlot`

| 版本 | 签名 |
|---|---|
| 1.20.1 | `hurtAndBreak(int, LivingEntity, Consumer<LivingEntity> onBroken)` |
| 1.21.1 | `hurtAndBreak(int, LivingEntity, EquipmentSlot slot)`（`ItemStack.java:490`） |

1.20 的写法是 `stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand))`，
1.21 里第三个参数要的是「哪一格坏了」，碎掉时的广播由方法自己发。
实证：本批 `BloodySpore.java:129` 首次报 `incompatible types: EquipmentSlot is not a functional interface`。

已固化成规则 `vanilla-hurtandbreak-slot-instead-of-lambda`，替换为
`LivingEntity.getSlotForHand(hand)` —— 这是 1.21 分支自己的既有惯用法
（`PineSaplingBlock.java:34`、`WhitePumpkinBlock.java:46`、`BaseTerraBowItem.java:122`、
`BaseTerraRepeaterItem.java:413`、`AbstractFishingPole.java:73`）。
1.20 侧这个模式共 3 处（另有 `WhipAttackEntity.java:444`、`BoomerangItem.java:56`，随各自批次走）。
规则已做 json 校验与正/反例自检，重跑转换后本文件已变成 `getSlotForHand(hand)`。

## 五、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| slime 全族（10 个类，含 `BaseSlime` 420 行） | **依赖 NPC 层**：`BaseSlime` 引用 `common.entity.npc.TownSlimeNPC`，后者把 WP4 的 NPC 层（`NPCAttackBlacklist` / `NPCNames` / `chat` / `dialog` / `house` / `mood` 等）整片拉进来。不是「注册层」的问题，是**依赖顺序**：slime 族要排在 WP4 之后（或与它同批）。同一片里还有 `Werewolf` `VisualNeuron` `TheHungry` 族（闭包 200 左右）。 |
| `WindyBalloon`(88) | 引用 `MonsterEntities.BLUE_SLIME`，而它指向 `BaseSlime` → 被上一条挡住。 |
| `Gnome`(37) | 卡在 `DecorativeBlocks.GARDEN_GNOME`（方块 + 方块实体 + 客户端模型，属方块工作包），见 `notes/WP2G-SUBSET.md`。 |
| 施法者群（`DarkCaster` `FireImp` `Gastropod` `GoblinWarlock` `WaterBoltMimic` `Corruptor`） | 各自还需 3~4 个文件，公共枢纽是 `common.entity.projectile.HostileParticleProjectile`（306 行，**24 个种子都要**）。这是下一批的自然入口：先落这个枢纽，六个施法者会一起变轻。 |

## 六、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器报告：`uncovered.json` = `[]`（三次转换都是）。
规则文件：21 条，`json.load` 通过、无空 pattern/replace、新规则正反例自检通过。
新注册类自查：`MonsterEntities` 的 5 条注册都指向**已存在**的类
（`BloodCrawler` `EaterOfSouls` `BloodySpore` `BloodTumor` `BaseWarriorMonster`），没有为未落地物种留占位。

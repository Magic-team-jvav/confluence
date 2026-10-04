# WP2 批次 21 · 侏儒 `Gnome` + 花园侏儒方块 `GardenGnomeBlock`（自包含轻量叶子）

> 承接批次 20（动物半边收口）。本批吃的是 `notes/ALL-COMMON-CLOSURES.md` 轻量名单里的自包含叶子：
> 侏儒是「白天露天时把自己变成花园侏儒方块」的怪物，**实体与方块必须同批**（`Gnome.java:24` 直接用
> `DecorativeBlocks.GARDEN_GNOME`），所以本批 = 1 个新实体 + 1 个新方块 + 3 条注册。

## 一、开工测量

```powershell
# 全 common/ 扫描（notes/ALL-COMMON-CLOSURES.md）
python tools/port2native/seed_closures.py --seed-dir common ... --small 4
# -> {"seeds": 425, "light": 109, "heavy": 316, "hubs": 299}
#   Gnome                 闭包 1 / 0 新增 / 37 非空行
#   GardenGnomeBlock      闭包 1 / 0 新增 / 51 非空行

python tools/port2native/dep_subset.py --seed ...Gnome --seed ...GardenGnomeBlock ...
# 落地后重算：{"candidates": 2, "kept": 2, "new": 0, "already": 2}  —— 两个文件都已在树里
```

## 二、本批落地（2 个新文件 + 2 个既有文件改动）

| 文件 | 行数 | 说明 |
|---|---:|---|
| `common/entity/monster/Gnome`（新） | 37 | 转换器直出（`filesChanged: 0`，无 PortLib 词汇）；补 `MonsterEntities.GNOME` 注册 |
| `common/block/natural/GardenGnomeBlock`（新） | 68 | 手写：1.20 原文 + `CODEC`（见第三节）+ `MobEffectInstance` 取 `Holder` |
| `common/init/entity/MonsterEntities`（改） | +5 | `GNOME`：照 1.20 `MonsterEntities.GNOME` 逐条搬（0.5×0.8、追踪 10、`developmentOnly`；maxHealth 13 / armor 0 / attackDamage 6 / 护甲穿透 3 / movementSpeed 0.3 / followRange 24 / knockbackResistance 0.1） |
| `common/init/block/DecorativeBlocks`（改） | +4 | `GARDEN_GNOME`（方块 + 物品，`registerWithItem`）+ `GARDEN_GNOME_ENTITY`（方块实体类型，写法照既有 `MURAL_BLOCK`/`MURAL_ENTITY_BLOCK` 一对） |

## 三、两处 API 差异（都是本批实测）

### 1. `BaseEntityBlock#codec()` 在 1.21 是 **abstract**（1.20 不是）

`build/_nfsrc_219/net/minecraft/world/level/block/BaseEntityBlock.java:19`：

```java
protected abstract MapCodec<? extends BaseEntityBlock> codec();
```

所以**任何**从 1.20 搬来的 `BaseEntityBlock` 子类都必须补 `CODEC` + `codec()` 覆写。本仓库既有写法
（`TuffBoothBlock.java:46/90`）是：

```java
public static final MapCodec<GardenGnomeBlock> CODEC = simpleCodec(GardenGnomeBlock::new);
@Override protected MapCodec<? extends BaseEntityBlock> codec() {return CODEC;}
```

> 这条对后续所有带方块实体的搬运（WP3/WP4/WP6 的机器/祭坛类）都适用，建议先按此写。
> 注：`RelicBlock` 那种 `implements EntityBlock`（不用 `BaseEntityBlock`）的写法没有这个要求；
> 1.21 侧两种写法都在用，取舍取决于是否需要 `BaseEntityBlock` 的 `createTickerHelper`。

### 2. `new MobEffectInstance(MobEffect, …)` → 需要 `Holder<MobEffect>`

1.20 写 `new MobEffectInstance(ModEffects.GARDEN_GNOME_LUCK.get(), 40, 0, true, false)` 编不过
（`incompatible types: MobEffect cannot be converted to Holder<MobEffect>`）。
1.21 的 `ModEffects.X` 本身就是 `DeferredHolder<MobEffect, MobEffect>`（即 `Holder`），
**直接传、不要 `.get()`** —— 与仓库统一写法一致（`PooBlock.java:22`、`GreenDumplingBlock.java:99`），
也与 WP2 批次 8 加过的 `libeffects-holder-unwrap` 规则是同一件事（本批是手写文件，规则不会碰它）。

另外 1.20 的 `BlockBehaviour.Properties.copy(STONE)` 在 1.21.1 是 `ofFullCopy(STONE)`
（`DecorativeBlocks` 既有代码统一用后者）。

## 四、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 第一轮：1 处错误（GardenGnomeBlock.java:53 MobEffect → Holder）—— 本批修掉
# 第二轮：总错误数 0，涉及 0 个文件；[build] exit=0
```

转换器：`uncovered.json` = `[]`；`leftovers.txt` 0 行；`manual-todo` 0 处（`Gnome` 无 PortLib 词汇，
`GardenGnomeBlock` 是手写的、没走转换器）。

## 五、本批未做（明确推迟，附原因）

| 推迟项 | 原因 |
|---|---|
| `WindyBalloon`（88 行，同样在轻量名单里） | 它的 `finalizeSpawn` 要 `MonsterEntities.BLUE_SLIME.get().create(...)`（1.20 `MonsterEntities` 的史莱姆条目）—— 该成员在 1.21 **不存在**（又一次成员级盲区：`dep_subset` 报它闭包 1）。史莱姆簇（`BaseSlime` 420 行 + 各变体）与 18 个怪物共用 156 文件大团，必须单独成批 |
| 花园侏儒的 lang / 刷怪蛋 / 图鉴条目 | 1.20 的键在 `ModChineseProvider.java:2867`（方块名）、`:4996`（实体名）、`:6116`（刷怪蛋）、`ModClientBestiaryEntryProvider.java:194`（图鉴）。1.21 侧**前几批搬进来的怪物同样都还没补这些**（`blood_crawler` 在 1.21 lang 里只找到字幕键），属既有约定：内容接线（lang/刷怪蛋/图鉴/生成数据）随 WP7 数据生成批次统一处理 |
| 方块的模型/贴图/动画资源（geckolib） | 与动物渲染器同一条：随 WP2 渲染器批次统一处理；不影响编译与注册 |
| `AntlionEggBlock` / `TorchBlocks` 等其它轻量叶子 | 其消费端（`Antlion`、火把物品簇）尚未落地，单独搬会造出孤儿类；等对应内容批次一起做 |

## 六、下一批建议：WP5 召唤体系**基座**（已勘明）

`ALL-COMMON-CLOSURES.md` 的轻量名单里有一组**同源**的自包含叶子，全在
`common/summoner/attachmentEntity/` 下（1.20 的 AttachmentEntity 体系，1.21 目前**零基础**）：

| 叶子 | 非空行 |
|---|---:|
| `OBB` | 62 |
| `Ellipse` | 122 |
| `PathNode` | 37 |
| `SyncFieldDispatcher` | 114 |
| `IMomentumAttachmentEntity` | 12 |
| `SummonerModels` | 10 |
| `SummonerParticleTypes` | 16 |
| `SummonerSoundEvents` | 23 |
| `SummonerBatchedParticlesPayload` | 55 |

它们单独搬是孤儿（本批已实测确认没有别的批次需要它们），但合起来正好是 WP5 的
**几何/数据/注册基座** —— 与批次 19 落 `CritterEntities` 是同一个手法：先把基座放下去，
后面 92+49 个文件的闭包才会从「156 文件大团」降下来。
**下一批开工前必须先做成员级核对**：`ModParticleTypes` / `ModSoundEvents` /
`ModEvents.registerPayloadHandlers` 这三个 1.21 既有注册表里是否已有同义的召唤粒子/音效/网络包
（1.21 现在用的是 TerraEntity 的旧召唤实现，重名或语义重复都要先 diff 再决定）。

---

# 附：`dep_subset.py` 原始报告（落地后重算，两个文件都已在树里）

- 种子 2 个：`Gnome`, `GardenGnomeBlock`
- `{"candidates": 2, "kept": 2, "new": 0, "already": 2, "removed": 0, "unused_defer": 0}`
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

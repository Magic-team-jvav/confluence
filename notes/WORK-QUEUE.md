# 1.20.1 → 1.21.1 工作队列（按依赖排序的工作包）

> 本文是 `notes/PORT-LEDGER.md`（386 行逐提交台账）的**执行视图**。
> 台账回答「每个 1.20 提交怎么判」，本文回答「先干什么、干完长什么样」。
> 生成依据：`notes/TRIAGE-PASS1.md`、`notes/TRIAGE-PASS2.md`、`notes/FILE-LAG.md`、`notes/COMMIT-LAG.md`。

## 一、结论摘要（先把话说清楚）

第二轮分诊（Port 免疫的 file-lag 度量）之后，1.21 相对 1.20 的欠账被量化出来：

| 指标 | 数值 |
|---|---|
| 1.20 侧参与比对的 java 文件 | 3082 |
| 已同步（overlap ≥85%） | 1154 |
| 部分同步（50~85%） | 599 |
| 明显落后（<50%） | 367 |
| **1.21 完全没有对应文件** | **781** |
| 仍在 TODO 的提交行 | 271 |
| TODO 欠账权重合计（≈ 需搬运的代码行数） | **111929** |

> 📊 **2026-09-27 第 15 轮重算（本节上表是第 2 轮的数字，已过期）**：本会话又落了 8 批
> （WP5 核心 28 文件、动物刷怪放置层、枪械 G0/G1′/G2′ 等），重跑 `file_lag.py` + `triage_pass2.py`
> 得到新数字：
>
> | 指标 | 第 2 轮（上表） | **第 15 轮（最新）** |
> |---|---:|---:|
> | 1.20 参与比对文件 | 3082 | 3082（不变） |
> | 1.21 侧参照文件 | — | **3624** |
> | 已同步 IN-SYNC | 1154 | **1452** |
> | 部分同步 PARTIAL | 599 | **600** |
> | 明显落后 LAGGING | 367 | **315** |
> | **1.21 完全没有 MISSING** | **781** | **531** |
> | 提交行判定 | NEEDS-PORT 172 / COVERED 27 | **NEEDS-PORT 161 / COVERED 30** |
> | 第二轮分诊 | TODO 271 | **TODO 255 / REVIEW 10 / COVERED 6** |
>
> 重算命令（`numstat-all.txt` 的 1.20 区间是固定的 `795ac9ccc..18221c338`，可直接复用）：
> ```powershell
> python tools/port2native/file_lag.py --src D:\Minecraft\1.20forge\confluence --ref . --out notes `
>     --log %TEMP%\port-ledger\numstat-all.txt --range 795ac9ccc..18221c338
> python tools/port2native/triage_pass2.py --repo . --out notes
> python tools/port2native/batch_provenance.py --repo . --root121 ConfluenceOtherworld/src/main/java `
>     --src120 <1.20 src/main/java> --log %TEMP%\port-ledger\numstat-all.txt `
>     --range 795ac9ccc..18221c338 --since 14fee792a --out notes/BATCH-PROVENANCE.md
> ```
> **MISSING 从 781 掉到 531**（−250）：本会话的 WP5 核心 28 文件、动物刷怪放置层、
> 枪械前置/基类，以及更早批次的物种一起贡献的。
> ⚠️ 工具小瑕疵：`batch_provenance.py:16` 的 docstring 里 `\p` 不是合法转义（Python 报 SyntaxWarning），
> 不影响产物，但下次改这个脚本时顺手改成 raw string。

> 📊 **2026-09-30 第 24 轮重算（TE 退役收尾之后，命令同上 `--out notes`）**：
> 参照集本身变了——TE 子模块已摘除（`8003db250`），它的 java 不再进参照；同一区间（`2ac38c57f..HEAD`，58 个提交）
> 主模组**新增 392 个 java / 删除 90 个**。三张表全部重跑后的数字：

| 指标 | 第 2 轮 | 第 15 轮 | 第 23 轮（`2ac38c57f` 提交的 `FILE-LAG.md`） | **第 24 轮（本次）** |
|---|---:|---:|---:|---:|
| 1.20 侧参与比对 java | 3082 | 3082 | 3082 | **3082** |
| 1.21 侧参照 java | — | 3624 | 3744 | **3205**（TE 摘除后其 java 离场） |
| MISSING | 781 | 531 | 393 | **66** |
| LAGGING | 367 | 315 | 255 | **183** |
| PARTIAL | 599 | 600 | 599 | **602** |
| IN-SYNC | 1154 | 1452 | 1650 | **2044** |
| TINY | — | — | 185 | **187** |
| 提交级 `NEEDS-PORT` | 172 | 161 | 104 | **34** |
| 提交级 `COVERED` | 27 | 30 | 63 | **146** |
| 分诊 TODO / REVIEW / COVERED | 271 / — / — | 255 / 10 / 6 | 223 / 33 / 15 | **141 / 72 / 58** |

> **回归核验**：本区间删除的 90 个 java **没有一个落进 MISSING**（0 回归），
> 即 MISSING 的下降来自"新文件补齐"，不是"参照集缩水"。
> `fix_eol.py --check` 候选 0；`registered_ids.py --kind entity --check-lang` 可平移 0 / 应删 0。

**还剩的 66 个 MISSING（全量，未截断）——按模块与类别：**

| 模块 | 数 | 具体还缺什么 |
|---|---:|---|
| 主模组 | 33 | `client/renderer/entity/hook/*Renderer` **10 个钩爪渲染器**、`common/enchantment/*` **10 个附魔**、`mixin/**` 6、2 个 `client/handler`（`ScryingOrb`/`SoulSkill`）、`common/block/natural/*Grower` 2、弹幕模型/渲染器 3 |
| Confluence-Magic-Lib | 17 | `LibFeatureUtils` / `LibEntityUtils` / `LibStructureUtils` / `LibEnchantmentUtils`、`Sup*Block`、`*FinishedRecipe`、`ILib*` mixed 接口、2 个 mixin |
| TerraCurio | 13 | `ParticleTrigger(s)` / `ParticlePlacements` 粒子触发三件套、`TCGameClientEvents` / `TCModClientEvent`、2 个 S2C 包、`ITC*` 接口、2 个 mixin |
| TerraFurniture | 3 | `OneLegTableBlock` + geo 模型/渲染器 2 |

> ⚠️ 引用小表时注意：`FILE-LAG.md` 的 LAGGING / PARTIAL 段**每段只列前 200 行**（`file_lag.py:300`，
> 底部有「另有 N 个」一行），**头部汇总数字才是全量**。
>
> ✅ **主模组那 33 个已逐条查证完毕**：**真缺口 0**——19 个是「1.21 已有等价实现」（改名/参数化/内联），
> 13 个「不移植」（10 个附魔类 + `ProtectionEnchantmentMixin` 为用户裁定的 1.20 权宜写法、
> `SpearProjectileModels` 是 Forge DistCleaner 专用拆分、`TCHelper` 属 integration 层），
> 1 个死代码（`BaseContainerBlockEntityMixin` 两侧都无调用者）。
> **逐条证据见 `notes/CO-MISSING-TRIAGE.md`**；剩余 33 个 MISSING 全在子模块。
> 也就是说 `MISSING` 这类按**文件名**匹配的指标会把「改名/合并/内联」全算成欠账，引用时以判定表为准。

也就是说：**这不是「把 PortLib 架构搬过去」的量级，而是「1.20 分支分叉后新写了大约一半的内容层」**。
分叉点（`795ac9ccc`，2026-05-31）之后 1.20 侧做了：

- 一整套**新的生物架构**：`common/entity/**`（1.20 552 个 java 文件 vs 1.21 137 个），含 BT 行为树 AI（`common/entity/ai/bt/**` 36 个 leaf 节点）、
  怪物/ Boss / NPC / 动物 / 弹幕 / 坐骑 / 悠悠球 / 连枷的新基类与实现
- **AttachmentEntity 召唤体系**（`common/summoner/**` 92 个 + `client/summoner/**` 49 个**全新增**，1.21 侧一个文件都没有）
- 内容侧：新怪物、新 Boss、新 NPC、新物品/武器/枪械、新效果、新血条/图鉴/商店等客户端系统
- 平台侧：PortLib（这一层**不移植**，1.21 保持原生）

好消息是：**欠账分布极度集中**——前 32 行占 70.8%，前 50 行占 82.5%，前 100 行占 94.6%。
所以按工作包推进，而不是按台账顺序一行一行啃。

## 二、先别做什么

1. **PortLib 本身的 8 个提交**（`SKIP-PORTLIB`）—— PortLib 是「在 1.20.1 上模拟 NeoForge 1.21.1 API」的模拟层，1.21 侧保持原生（决策 Q2）。
   工具判据：`COMMIT-LAG.md` 里 `PORTLIB-ONLY`。
2. **1.20 自己后来删掉的 6 个提交**（`SKIP-1.20-REVERTED`）—— 改动文件在 1.20 HEAD 已不存在。
3. **integration 相关的改动**（Q12：1.20 刻意删、1.21 保留自己的）。
4. **`dfcc5c041`**（`DO-NOT-PORT`，作者当面提醒的污染提交）。

## 三、工作包（WP）

权重 = 该工作包认领的「缺失/滞后文件」的新增行数和；来源行 = `PORT-LEDGER.md` 里的行号。
**每个工作包内部仍按台账顺序逐提交推进，每个源提交单独落一个 1.21 提交**（保留 `1.20 侧 hash` 在提交信息里）。

### WP1 · 生物架构基座（必须最先，其余全部依赖它）

| 项 | 内容 |
|---|---|
| 目录 | `common/entity/`（根）、`common/entity/ai/**`、`common/data/map/**`、`common/init/entity/**` |
| 权重 | ≈ 9000（`ai` 3374 + `init/entity` 3397 + `entity` 根 997 + `data/map`） |
| 代表来源行 | `dffefea8d`（feat: 添加多个怪物实体类、AI 系统和相关工具类，4321）→ `348c877a4`（统一生物属性与状态参数声明，2190）→ `6568d3ad1` → `741f98d1e` |
| 依赖 | 新基类 `BaseMonster` / `BaseCaveMonster` / `CreatureDefinition` / `CreatureAttributeBuilder` / BT 节点 / `SpawnPlacementChecks` / `ModEntities` 系列注册类 |
| 出口 | 1.21 能编译通过（新基类就位、注册类存在），此阶段允许物种尚未补齐 |
| ⚠️ 已查明 | 1.21 的 `ConfluenceOtherworld` 里**没有** `common/entity/ai/`；1.21 的行为树在 **TerraEntity** 的 `terraentity.entity.ai.goal.behavior.**`（即将退役，Q3/Q4）。1.20 是内联+重写版（重叠仅 8~42%）→ **按新代码搬，不与 TerraEntity 版本 diff**；且必须**先加后删**（新物种补齐后再退役 TerraEntity）。详见 `notes/WP1-INVENTORY.md` 第六节 |

### WP2 · 怪物与动物物种

> ✅ **2026-09-27（goal round 1）WP2 剩余物种批：狼人 + 大风气球怪（`notes/WP2-MONSTER-LEFTOVER.md`）**。
> 3 新 java（`Werewolf` 逐字一致、`WindyBalloon` 仅 `finalizeSpawn` 4 参、`VanillaHumanoidGeoModel` 仅
> geckolib 包名迁移）+ `MonsterEntities` 2 条注册 + 2 条 `GeoNormalRenderer` 注册 + 6 个资源（与 1.20 同哈希）。
> **同批带上放置规则**（1.20 里这两半是一次提交）：`SpawnPlacementChecks` 补「地表敌怪基础规则」4 支谓词
> （`:789`/`:314`/`:463`/`:677`）、`CreatureSpawnPlacements` 新增两组增量登记（`:112-113`/`:179`）、
> 并补 `CommonConfigs.SPAWN_WITHOUT_LIGHT`（`:63`，**成员级盲区**）。编译门 4→0。
>
> ⚠️ **顺带修掉 4 行被 GBK 往返损坏的注释**（单独提交 `b7694b4f7`）：`ad89b41ba`（包移动重做）那一步用
> PowerShell 按 GBK 读回 UTF-8 源码，`ModEvents:220/307`、`TickEvents:50/57` 变乱码；原文从损坏前版本
> （`c4e808093`/`7b5c5e8ce`）取回，全仓已用逆变换扫描确认无其他残留。
>
> 📋 **本批排查后延后**（诊断见笔记第四节）：`DungeonSpirit`（要 `BossEntities.PLANTERA`）、
> `MonsterAttributeScaling`（28 错：PortLib UUID 属性 + `ForgeConfigSpec`→`ModConfigSpec`）、
> `TheHungry` 族 + `WallOfFlesh`/`BrainOfCthulhu`（15 文件并集）、`ANGRY_DANDELION`（类与弹幕注册都在，
> 差弹幕资源 → 随资源批一起上）。
>
> 🔍 **新发现：资源审计**（910 处 `asResource` 引用 × 磁盘存在性）→ **35 处引用得到、文件不存在**，
> 分三类：**A 可直接从 1.20 拷 17 处**（含恶魔眼模型/动画、连枷 6 件、小动物 6 张、界面 3 张）、
> **B 1.20 自己也缺 6 处**（上游既有缺陷，照实保留）、**C 1.21 独有路径 12 处**（新连枷/光剑实现，非移植欠账）。
> ✅ **A 类已收口（`notes/ASSET-GAP-A.md`）**：17 个文件与 1.20 同哈希拷入，审计缺口 **37 → 18**
> （B 6 + C 12 留档）。
>
> ✅ **WP2「大风天」段收尾：愤怒蒲公英落地**（`notes/WP2-MONSTER-LEFTOVER.md` 第六节）。
> `MonsterEntities.ANGRY_DANDELION` 注册（属性逐字含 `.projectile(DANDELION_SEED, …)`）+
> `GeoNormalRenderer` + **`DANDELION_SEED` 弹幕渲染器**（1.21 此前该弹幕完全没有渲染器）+
> 放置规则并进 `checkAngryDandelionSpawn` 组 + 6 个资源。编译门 0/0。
> **「大风天」段（大风气球怪 + 愤怒蒲公英）与「满月」段（狼人）至此与 1.20 成员级一致。**
>
> 🎯 **下一批候选**：① `MonsterAttributeScaling`（28 错：PortLib UUID 属性 + `ForgeConfigSpec`→`ModConfigSpec`，
> 顺带补齐 `EntityEvents.joinLevel` 前半段）；② WP2 剩余物种里闭包小的（先跑闭包实测再定）；
> ③ WP7 datagen（`runData`）。
>
> 📊 **第 22 轮普查：注册层缺口才是当前最大的结构性欠账（`notes/WP2-MONSTER-REGISTRY-GAP.md`）**。
> 成员级差集：`MonsterEntities` 1.20/1.21 = **213/63（缺 150）**、`BossEntities` = 32/13（缺 19）、
> `CritterEntities` = 41/40（缺 `MYSTIC_FROG`）、`NpcEntities` = 36/36（齐）。
> 而 `common/entity/**` 的**类文件只差 10 个** → 也就是说**一百多个怪物的类早就在 1.21、能编译、
> 能 `new`，但没有 `EntityType`，在游戏里根本不存在（连 `/summon` 都不行）**。
> 150 个缺失成员按成本分三类：**A 类「一句话注册」123 个**（已按 1.20 段落列出完整名单）、
> **B 类需先补 `ModEntities` 弹幕成员 25 个**（逐条列出依赖）、**C 类被未移植的类挡住 2 个**。
> **推荐下一批 = 蝙蝠族 9 个 id**（同段 + 同渲染器家族 + 资源齐备，前置 4 件：`BatRenderer`（1.20 27 行）、
> 5 支放置谓词、7 条放置登记、22 个资源）—— 六节已写成可直接执行的清单。
>
> ✅ **蝙蝠族 9 个 id 已落地（`notes/WP2-BAT-FAMILY.md`）**：1 新 java（`BatRenderer`）+ 4 改 + 25 资源，
> 编译门**一次通过 0/0**。9 条注册 + 9 条渲染器 + 5 支谓词（`checkUnderground/Cave/Nether/PostMechanicalNether/
> SurfaceNightMonsterSpawn`）+ 7 条放置登记；`MonsterEntities` 成员数 63 → **72**。
> 两条纪律（注册必有渲染器、必有放置规则）已用脚本逐条核对。
> 坑：`giant_flying_fox` 不含「bat」子串 → 按名字过滤会漏资源，**资源清点必须用代码里的 `asResource` 路径做基准**
> （`asset_gap.py` 已复算：914 处引用 / 缺口仍 18）。
> **下一批**：水母族（3 个 id）或宝箱怪族（7 个 id）；纯注册批已证明可一次通过，可适当放大单批规模。
>
> ✅ **宝箱怪族 8 个 id 已落地（`notes/WP2-MIMIC-FAMILY.md`）**：2 改 + 24 资源（**无新 java**），
> 编译门**一次通过 0/0**。小型 `WoodenMimic` 四只（木质/黄金/冰/暗影）+ 大型 `BaseMimic` 四只
> （丛林/腐化/猩红/神圣，含三段战斗状态）+ 8 条渲染器（大型四只 `withScale(2.0F)`）+
> `checkBelowSurfaceMonsterSpawn` 谓词 + 4 条放置登记。`MonsterEntities` 成员数 72 → **80**。
> 只需 1 支新谓词的原因：宝箱怪用的 `checkGroundSpawn`/`checkCaveMonsterSpawn`/`checkNetherMonsterSpawn`
> 在蝙蝠批已落地。**注册层缺口 150 → 133**。
>
> 🎯 **注册层的最高杠杆下一步（已量化，`notes/WP2-MONSTER-REGISTRY-GAP.md` 第九节）**：
> 1.20 `ModClientEvents` 的渲染器注册复用度统计 → **缺的共享类**里最值钱的是
> `VanillaHumanoidRenderer`（20 处复用）/ `VanillaSkeletonGeoModel`（10）/ `VanillaGoblinGeoModel`（6）/
> `VanillaZombieGeoModel`（3）；四者都只依赖**已经踩过**的 geckolib 改名（`CoreGeoBone`→`GeoBone`、
> `RenderUtils`→`RenderUtil`、`AnimationState` 换包）+ 已在 1.21 的基类 `VanillaHumanoidGeoModel`。
> → **一次可解锁「骨骼族 + 僵尸族 + 哥布林族」约 17~20 个 id**。
> 另需同批补 9 个缺失的 `MonsterEntities` 注册辅助（`registerSkeleton`/`registerGoblinLand`/
> `registerJellyFish`/`registerFlyingFish`/`registerCharger`/`registerHumanoidLand`/`registerJumpingLand`/
> `registerAcceleratingLand`/`registerSnatcher`）。
> ⚠️ 附带发现：`GeoNegativeVolumeRenderer`（主模组 5 处注册在用）目前**只存在于 TerraEntity 子模块**里
> （该模块在退役清单上）→ 需在合适批次搬进主模组。
>
> ✅ **骨骼族 + 共享「原版人形」客户端两件套已落地（`notes/WP2-SKELETON-FAMILY.md`）**：
> 新 java 2（`VanillaHumanoidRenderer` 83 行、`VanillaSkeletonGeoModel` 84 行）+ 3 改 + 33 资源，
> 编译门**一次通过 0/0**。12 条注册 + 12 条渲染器 + `registerSkeleton` 辅助 + 2 支谓词
> （`checkRoutineMonsterSpawn`/`checkDungeonMonsterSpawn`）+ 放置并入既有组。
> `MonsterEntities` 80 → **92**；注册层缺口 150 → **121**。
> 两个「像漏拷其实是设计」的点已记录：`BIG_BONES` 复用 `ANGER_BONES` 的模型 id；
> `SPORE_SKELETON` 用的是 `MeleeSkeleton` 而非独立类。
> **下一批**：同杠杆的 `VanillaGoblinGeoModel`（6 处复用）+ `VanillaZombieGeoModel`（3 处）→
> 吃完哥布林入侵 6 个 id + 僵尸族 3 个 id，需补 `registerGoblinLand`。
>
> ✅ **哥布林入侵族 + 孢子僵尸已落地（`notes/WP2-GOBLIN-ZOMBIE-FAMILY.md`）**：新 java 2
> （`VanillaGoblinGeoModel` / `VanillaZombieGeoModel`）+ 4 改 + 24 资源，编译门 2→0。
> 8 条注册 + 8 条渲染器 + `registerGoblinLand` 辅助 + 3 支谓词 + 2 条放置登记。
> `MonsterEntities` 92 → **100**（跨过 100 成员线）；注册层缺口 150 → **113**。
> 坑：哥布林资源在 **`goblin/` 子目录**（`getId().withPrefix("goblin/")`），按实体 id 推路径会全部落空；
> `ZOMBIE` 用的是另一个类 `ZombieGeoModel`（1.21 尚未移植）→ 留待下一批。
>
> ✅ **僵尸族收口（`notes/WP2-ZOMBIE-FAMILY.md`）**：新 java 1（`ZombieGeoModel`，32 行，1.21 侧零适配）
> + `ZOMBIE` 注册/渲染器/`checkZombieSpawn`/放置登记 + 3 资源（变体共享的 `blood_zombie`）。
> 编译门 1→0，唯一差异是 **`LibDateUtils.isDay` 参数从 `LevelAccessor` 收窄成 `Level`**（只有编译门能发现）。
> `MonsterEntities` 100 → **101**；注册层缺口 150 → **112**；僵尸族 3/3 齐。
>
> ✅ **陆行族收口（`notes/WP2-LAND-FAMILY.md`）**：**零新 java 类**批次（宿主是档案参数化的
> `BaseWarriorMonster`/`FlyingFishMonster`）。8 个 id（骷髅博士/雪精灵/蛇蜥/血僵尸/新郎/新娘/飞鱼/蚁狮幼虫）
> + `registerFlyingFish`/`registerAcceleratingLand` 两个辅助 + 5 支谓词 + 21 资源。编译门 2→0。
> `MonsterEntities` 101 → **109**；注册层缺口 150 → **104**。
> ⚠️ **方法教训**：判「类是否缺失」要看注册表达式里的构造器（`X::new`/`new X(...)`），
> **不能看注册项名** —— `SnowFlinx`/`Basilisk`/`TheGroom` 这些名字对应的类文件本来就不存在。
> ⚠️ **`ANTLION_LARVA` 在 1.20 本身没有放置规则**（由蚁狮召唤）→ 只注册 + 给渲染器，不虚构生成规则。
>
> ✅ **地下沙漠族（`notes/WP2-DESERT-FAMILY.md`）**：**一个模型类换 10 个 id**
> （木乃伊×4 + 食尸鬼×4 + 拉米亚×2）。新 java 1（`ContactHumanoidGeoModel`，43 行）+ `registerJumpingLand`
> 五个重载 + 11 支沙漠谓词（含按沙块分型的四支木乃伊）+ 10 条放置登记 + 30 资源。
> 编译门**一次通过 0/0** —— 因为上轮预案已先把三个不确定点（`withCutout` / `withAltAnimations` 来自 geckolib /
> `JumpingWarriorMonster` 的 7 参可空 `JumpProfile` 构造器）核掉。
> `MonsterEntities` 109 → **119**；注册层缺口 150 → **94**（**已跌破 100**）。
>
> 📋 **再下一批预案已摸底（`notes/WP2-MONSTER-REGISTRY-GAP.md` 第十一节）：蠕虫族 9 组 18 个 id**。
> 注册侧**已就绪**（`registerWormSegment`、连 `Anatomy` 参数都在的 `registerWorm`、六个 `Role` 值全在 1.21），
> 真正的成本是 **3 个客户端渲染器类**（`WormPartRenderer` 124 行 / `WyvernRenderer` 70 / `WormHeadRenderer` 39；
> 1.21 目前只有 Boss 版 `BossWormPartRenderer`）+ 5 支谓词 + 7 条放置登记。
>
> ✅ **蠕虫族 18 个 id 已落地（`notes/WP2-WORM-FAMILY.md`）**：新 java 3（体节/飞龙头/普通蠕虫头）
> + 18 条注册 + 18 条渲染器 + 5 支谓词 + 6 条放置登记 + 50 资源，编译门最终 0/0。
> 三处 1.21 签名适配（`preRender`/`renderRecursively` 颜色尾参收成 `int`、`applyRotations` 改覆写 6 参）
> 与一处 tag 合并（`PortTags.Biomes.IS_SNOWY/IS_ICY` → `OverworldUtils.isSnowy`）。
> `MonsterEntities` 119 → **137**；注册层缺口 150 → **76**。
>
> ✅ **散件批 10 个 id（`notes/WP2-SCATTER-FAMILY.md`）**：`GRANITE_GOLEM`/`DERPLING`/`ICE_TORTOISE`/
> `SAND_POACHER`/`HERPLING`/`METEOR_HEAD`/`GHOST`/`DRIPPLER`/`SHARK`/`CURSED_SKULL` —— **零新类**
> （谓词与渲染器全部复用已有；`CURSED_SKULL` 用 TerraEntity 的 `GeoNegativeVolumeRenderer`）。
> `MonsterEntities` 137 → **147**；注册层缺口 150 → **66**。
> 本批起按用户口径写注释：**源码注释照抄 1.20，不写溯源/说明性内容**。
>
> ✅ **双色变种与洞穴散件批 4 个 id（`notes/WP2-VARIANT-FAMILY.md`）**：`GIANT_SHELLY`/`CRAWDAD`/
> `ANGLER_FISH`/`GRANITE_ELEMENTAL` —— 新类 1（`VariantTextureGeoModel`）+ 4 谓词（含发光渲染器两处复用
> TerraEntity 的 `GeoNegativeVolumeRenderer`）。`MonsterEntities` 147 → **153**；缺口 150 → **60**。











>
> 📊 **第 23 轮台账重算（压完历史之后）**：文件级 **MISSING 393 / LAGGING 255 / PARTIAL 599 /
> IN-SYNC 1650 / TINY 185**（上一轮 415/266/589/1627/185）；提交级 **TODO 223 / REVIEW 33 / COVERED 15**。
> `notes/FILE-LAG.md`、`notes/COMMIT-LAG.md`、`notes/TRIAGE-PASS2.md` 已刷新。
>
> ## 当前位置：还剩什么（2026-09-27 盘点）
>
> | 层面 | 现状 | 缺口 |
> |---|---|---|
> | **注册层**（最直观） | `NpcEntities` 36/36 ✓、`CritterEntities` 40/41 | `MonsterEntities` **4**（`DUNGEON_SPIRIT`/`VISUAL_NEURON`/`THE_HUNGRY`/`HILL_HUNGRY`）、`BossEntities` **19**、`MYSTIC_FROG` |
> | **java 文件** | 1.21 完全缺失 **449 个 / 37837 行** | 见下表按目录 |
> | **资源** | `asset_gap.py`：978 处引用 / 缺 19 | 18 处是既有 B/C 类，1 处 `geo/animal/dummy` 是 geckolib 占位（1.20 也没有） |
> | **提交级台账** | TODO 223 行 | 多数落在上面这些目录里 |
>
> 缺失文件按目录（前 12，括号内为行数）：
> `common/entity/boss` 25(7179) · `common/summoner/minion` 48(3215) · `common/entity/projectile` 28(2858) ·
> `common/data/gen` 14(2319) · `client/entity/renderer` 25(2101) · `client/renderer/entity` 32(1853) ·
> `common/entity/monster` 8(974) · `common/entity/yoyo` 7(933) · `client/summoner/rendererHelper` 3(805) ·
> `client/summoner/trail` 5(764) · `common/init/entity` 1(740) · `common/item/sword` 12(732)。
>
> ### 还差的四大块
> ⚠️ **2026-09-29（第 22 轮）进度更新**：
> - **第 1 块「BOSS 批」已完成**：`d5d6e8520` 落地 19 个 Boss + 客户端族（65 java + 42 资源），
>   `BossEntities` 13 → 31、`MonsterEntities` 209 → 213、`ModEntities` +14 弹幕、
>   `ModClientEvents` +22 Boss 渲染器注册；**注册层最后一个物种缺口 `MYSTIC_FROG` 也已补上**
>   （`CritterEntities` 40 → 41）。逐类 API 适配与已知偏差见 `notes/WP3-BOSS-BATCH-SCOPE.md` 开头。
> - **新增第 0 块「客户端渲染接线」已完成**：`4fa5170bf` 补 70 条 `registerEntityRenderer`
>   （CritterEntities 40 / NpcEntities 26 / MonsterEntities 4）——上一批把渲染器类拷进来了但没接线，
>   不补的话所有这些实体在 1.21 都没有渲染器。
> - **`integration/terra_entity` 桥接层已删除**：`70686b138`，详见 `notes/WP8-TE-LAYER-REMOVAL.md`。
> - 剩余块：WP5 召唤余下（进行中）、WP6 物品层、WP7 数据生成。
>
> 1. ~~**BOSS 批（最大单块）**~~ —— ✅ 已完成（见上）。
> 2. **WP5 召唤体系余下**：`common/summoner` 61 文件（minion 48 + projectile/particle/register）+
>    `client/summoner` 45 文件（renderer/model/trail/rendererHelper）≈ **7983 行**。
> 3. **物品层（WP6）**：`common/item` 60 文件 2647 行（sword 12 / yoyo 12+7 / bow 10 / arrow 10 / whip 7 /
>    flail 6 / summon）、`common/init/item` 5 文件 676 行（含 `SpawnEggItems` 刷怪蛋层，
>    `MYSTIC_FROG` 等刷怪蛋缺口在这里）。
> 4. **WP7 数据生成**：`common/data/gen` 15 文件 + `runData` 验收（含被删掉的 `NPCShopProvider` 重写）。
>
> 另有零散项：`checkMysticFrogSpawn`（1.20 `SpawnPlacementChecks`，用 PortTags.Biomes.IS_JUNGLE）、
> `common/data/spawner` 3(410)、`common/entity/storage` 3(365)、`client/security` 8(675)、
> `api/whip` 12(455)、`common/enchantment` 11(272)、`mixin/**` 残留 14 文件、`util/generation` 4(187)。
> **按既定决策不移植**：integration（`mixin/integration` 5 文件的其余部分）、`dfcc5c041`、PortLib 层。
>
> ✅ **第 21 轮（2026-09-27）**：`integration/terra_entity` 桥接层已整体删除（44 + 14 文件，含 14 条
> mixin 配置），改用 1.20 原生 NPC/交易/房屋实现，并同批移植了 1.20 的 NPC 交易界面
> （`client/gui/container/npc_screen/**` + 轮廓着色器 + 10 张交易贴图）。编译门 0 错误。
> 新增欠账：`NPCShopProvider` 待随 WP6 的 whip/yoyo 物品层一起按 1.20 重写（POS 见
> `notes/WP8-TE-LAYER-REMOVAL.md` 第四节）。
> 待办性质：TerraEntity / TerraGuns（已退役）/ TerraFurniture 子模块的最终退役与 `GeoNegativeVolumeRenderer`
> 等"暂居子模块"的类迁回主模组。

> 📊 **第 22 轮台账重算（`file_lag.py` + `triage_pass2.py`）**：文件级 **MISSING 415 / LAGGING 266 / PARTIAL 589 / IN-SYNC 1627 / TINY 185**
> （上一轮：421 / 267 / 588 / 1621 / 185）；提交级 **TODO 228 / REVIEW 32 / COVERED 11**
> （上一轮：255 / 10 / 6 —— `REVIEW` 上升是本轮 `file_lag` 的重判口径把更多行移入复核，不是新欠账）。
> `notes/FILE-LAG.md`、`notes/COMMIT-LAG.md`、`notes/TRIAGE-PASS2.md` 均已刷新。
> ⚠️ **注意口径**：台账只数 java 文件行，**看不见本轮普查出的「注册层缺口」**（123 个可一句话注册的怪物）
> —— 那是「文件已同步、内容不可达」，所以下一步优先级应以 `notes/WP2-MONSTER-REGISTRY-GAP.md` 为准。



>
> ✅ **`MonsterAttributeScaling` 已落地（`notes/WP2-MONSTER-ATTR-SCALING.md`）**。1 新 java + 3 改，
> 编译门 8→0。它一次补上**两条一直缺失的链路**：所有实体入世界路径的敌怪倍率入口
> （`EntityEvents.joinLevel` 前半段，BMPE 批刻意留的缺口）+ 配置加载/重载后的重算
> （`CommonConfigs.onLoad` → `reload()`）；并补 `CommonConfigs` 的 `MonsterAttributes` 整段
> （9 成员 + `SPEC` 字段）。API 差异 5 处，其中**唯一需要改语义签名**的是
> `Attributes.*` 在 1.21.1 是 `Holder<Attribute>`（7 个 lambda + 1 处调用）。
> 顺带做了**配置成员级正则对账**（`public static \w+ (\w+);` 两侧比对）：1.21 端零多余成员，
> 1.20 端只剩 4 个「消费者未移植」的（`AUTO_SWING_ALL_SWORDS` 的消费者在 integration 层 → 永不移植）。
> **建议把这条对账做成每批收尾的常规检查。**



>
> 🟩 **2026-09-27（第 20 轮）WP2 史莱姆族落地 —— `notes/WP2-REMAINDER-GATE.md` 的闸门正式解除**。
> WP4（NPC 基座，批次 25）落地后，史莱姆族闭包从 **189 塌到 1~13**；本批补齐 **8 个史莱姆类 + `Slimer`**
> + `MonsterEntities` 史莱姆整族 **30 个注册成员**（此前只有 1 条）+ `ModEntities.SLIME_SPIKE` 注册
> + **`TownSlimeNPC` 的判据回填**（按 id 判等 → 1.20 的 9 成员比对）+ 普通渲染层（模型/渲染器/外壳层/尖刺弹射物渲染器，
> 25 条渲染器注册）+ 8 张缺失贴图。**成员级对账：1.20 = 1.21 = 30 个史莱姆 id，零差异**。编译门 0/0。
> 批次记录见 `notes/WP2-SLIME-SUBSET.md`。
>
> ⚠️ **留给下一批（史莱姆族的「特效渲染」尾巴）**：`GeoSpecialSlimeRenderer`（尖刺×3 与 `Slimer`）
> 继承 1.20 自有的 `GeoNormalRenderer` —— 它绑 `common/entity/boss/BaseWormBoss`（**WP3**）且需要
> `client/effect/RenderStateShardAccessor`（**整类未迁**）；`TownSlimeRenderer` + `DivaSlimeVertexConsumer`
> 同吃这两个依赖；`CrownOfKingSlimeModel*`（3 文件）属 **WP3 史莱姆王**（要 `BossEntities.CROWN_OF_KING_SLIME_MODEL`）。
> 这四类实体已注册（成员级对齐），暂不可见。
>
> ⚠️ **测量口径（本批踩过）**：`MonsterEntities`/`BossEntities`/`NpcEntities`/`CritterEntities` **两侧同 FQN**
> （都在 `common/init/entity/`），**不要**给它们写 `--alias`；只有 `ModEntities` 搬到了 `common/init/`。
> 写错会让闭包虚高（`SpikedSlime` 因此从 1 虚涨到 77）。
>

> ✅ **2026-09-27（第 15 轮 + 1）WP3/WP4 交叉：坐骑子树落地（批次 23，`affa4d08e`）。**
> 4 个坐骑实体（`Rideable{Bee,LavaShark,Slime,Unicorn}MountEntity`）+ `AbstractMountEntity` + `MountItem` +
> `MountManager` + `MountItems` = 9 新文件 / 973 非空行；顺带补上 1.21 侧缺失的
> `ModEntities.RIDEABLE_*`（成员盲区）、`ExtraInventory.getMount`、`ModKeyBindings.MOUNT`、
> `PrefixUtils.attributeWithoutHeldItem/heldItemContribution`。编译门 22→0。
> 批次记录（含 9 处 API 差异与一个新发现的转换器缺陷）见 `notes/WP3A-MOUNT-SUBSET.md`。
>
> ✅ **2026-09-27（第 15 轮）WP2 批次 22：动物刷怪放置层落地（`55d1eecb8`）。**
> `CreatureSpawnPlacements` 的小动物半边（`registerCritters`：12 处 `group` + 6 处直接注册，覆盖 38 只小动物）
> + `SpawnPlacementChecks` 的**子集**（22 个方法，其中 19 个与 1.20 逐字一致、2 个只差墓地判定换实现），
> 接进既有的 `RegisterSpawnPlacementsEvent` 处理器。怪物那一半（slimes / preHardmode / hardmode 三组 +
> 约 85 个怪物谓词）仍在同一个 159 文件闭包里，随怪物批次走。批次记录见 `notes/WP2U-SUBSET.md`。
>
> ✅ **2026-09-27（第 13 轮）WP2 动物半边收口：40/41。**
> 注册层 `CritterEntities` 按增量方式落地（批次 19，`07e4a927b`，只写「类已存在于 1.21」的条目，
> 单点闭包从 171 掉到 3），其余 13 个动物一批吃完（批次 20，`bee3361ce`），并给 `MonsterEntities`
> 补了 4 条金鱼/企鹅的邪恶转化注册。`common/entity/animal` 现在只剩 **`MysticFrog`**（闭包 157 文件，
> 单独成批）。逐种子闭包见 `notes/WP2-ANIMAL-CLOSURES.md`（已重算），批次边界与实测的 API 差异见
> `notes/WP2Q-SUBSET.md` / `notes/WP2R-SUBSET.md`。
>
> **下一个建议批次**（按依赖与现值排序）：
> 1. **`CreatureSpawnPlacements`（刷怪放置层）** —— 动物已全部注册但还没有放置规则；该文件整篇是
>    PortLib 词汇（`PortRegisterSpawnPlacementsEvent` / `PortSpawnPlacementTypes`），要按 1.21 的
>    `RegisterSpawnPlacementsEvent` 整体改写，并核对 `SpawnPlacementChecks` 的成员。
> 2. **WP4 的 NPC 基座** —— `notes/WP2-REMAINDER-GATE.md` 的结论仍然成立：它一次解开 WP2 剩余的
>    18 个怪物 + WP3 的一部分（`common/entity/npc` 1.20 66 个 vs 1.21 12 个）。
> 3. 怪物侧剩余 20 个类（含 `MysticFrog` 的重闭包与 4 条已补注册的转化目标之外的部分）。

| 项 | 内容 |
|---|---|
| 目录 | `common/entity/monster`（**19924，最大单包**）、`common/entity/animal`（4926）、`client/entity/renderer`（3709）、`client/entity/model`（22 个文件） |
| 代表来源行 | `b05c8dc3f`（9677）→ `741f98d1e`（5834）→ `dffefea8d` → `6568d3ad1` → `124baf17d`/`64b7bbe9e`/`157730424`/`9a3c5798b`/`1d8a00610` 等「完成 XXX」系列 |
| 注意 | 1.20 用 geckolib 的静态渲染器与 `.animation.json`；`0fe39ec92`「移除Geo静态渲染器」把 1.21 侧的老渲染器形态改了，**先确认渲染器目标形态再动手** |

### WP3 · Boss

> ✅ **2026-09-27（第 20 轮）WP3 客户端族批·客户端半完成 —— 该批 42 处错误全部清零**。
> 新增 9 个客户端族文件（`GeoNormalRenderer`/`BossGeoRenderer`/`BossWormPartRenderer`/`EntityLightSampler`/
> `EyeOfCthulhuRenderer`/`MissingModelRenderer`/`GeoNormalModel`/`WormPartGeoModel`/`ExplicitGeoModel`），
> 修 4 类 geckolib/vanilla API 差异（`CoreGeoBone` 已删除、`preRender` 颜色收成 `int`、
> **`applyRotations` 必须改覆写 6 参版**否则死代码、`renderNameTag` 多 `partialTick`），
> 接 7 条渲染器注册，**同批**完成克苏鲁之眼 8 处改指与 `BossDelaySpawner` 类型对齐（避免隐形 boss），
> 补 38 个资源。编译门 0/0。记录见 `notes/WP3-CLIENTFAMILY-SUBSET.md`。
>
> 🎯 **下一步最划算**：**史莱姆族特效渲染尾巴**（`GeoSpecialSlimeRenderer` 尖刺×3 + `Slimer`、
> `TownSlimeRenderer` + `DivaSlimeVertexConsumer`）—— 它的两个依赖（`GeoNormalRenderer`、
> `client/effect/RenderStateShardAccessor`）本批与上一批都已就位，可直接照
> `notes/WP2-SLIME-SUBSET.md` 第四节做，做完 WP2 史莱姆族即完整。
>
> 🧱 **2026-09-27（第 21 轮）Lib `LibGeometryUtils` 整类落地（子模块 `6cd857d`）**。
> 它是重算后 `FILE-LAG.md` 的 **MISSING 榜首**（379 有效行），闭包实测 = 1（自包含），
> 但被 1.20 侧一大票世界生成（carver/feature/structure 共 20+ 文件）与 Lib VFX（`ThunderboltVFX`、
> `LibStructureUtils`）共用。同批给 Lib `LibMathUtils` 补了它依赖的 4 个方法
> （`toVector3f`/`getDistanceToLineSegment`/`isProjectionBetweenPoints`/`getProjectionOnLineSegment`）。
> 按「先整类就位、后接消费方」落地，Lib 编译门 0/0。
>
> ✅ **2026-09-27（第 21 轮）WP2 史莱姆族收口：特效/城镇史莱姆渲染层落地（`5a4d8259f`）**。
> 尖刺×3 + `Slimer`（`GeoSpecialSlimeRenderer`）、城镇×8（`TownSlimeRenderer` + `DivaSlimeVertexConsumer`）
> + 13 条渲染器注册 + 36 个资源。两个依赖（`GeoNormalRenderer`、`RenderStateShardAccessor`）此前已落地，
> 故闭包从当初测出的 59/61 收敛到 **3 个文件**。
> **至此 WP2 史莱姆族的 30/30 注册成员全部可见**（此前 25 个普通史莱姆可见、其余 9 类不可见）。
> API 差异：`VertexConsumer` 顶点 API 整族改名 + `endVertex/defaultColor/unsetDefaultColor` 三方法**已删除**（19 处）；
> geckolib 三处 `int colour` 收敛（8 处）。记录见 `notes/WP2-SLIME-TAIL.md`。
>
> 📊 **2026-09-27（第 21 轮）台账重算**：`file_lag.py` + `triage_pass2.py` 重跑。
> 文件级：**MISSING 421 / LAGGING 267 / PARTIAL 588 / IN-SYNC 1621 / TINY 185**；
> 提交级分诊：**TODO 255 / REVIEW 10 / COVERED 6**。`notes/FILE-LAG.md` 已更新（`TRIAGE-PASS2.md` 无变化）。
> 距上次重算（第 15 轮）之间落地了 G4′~G6′、WP2 史莱姆族、WP3 切片/客户端族批、Boss 多人强化、台账相关整类等 9 批。
>
> 🪱 **2026-09-27（第 20 轮）WP3 客户端族批·服务端半：蠕虫 Boss 族落地**。
> 子模块先行：Lib `a762fe6` 补 `interpolateBasis` 家族 4 个方法（1.20 `LibMathUtils:385/451/465/474` 逐字，
> 1.21 此前**四个都没有**，`TheDestroyer` 需要）。根仓库：5 个服务端文件
> （`BaseWormBoss`/`BossWormPart`/`EaterOfWorlds`/`TheDestroyer`/`TheDestroyerProbe`）
> + `BossEntities` 蠕虫 5 成员（体节注册 id 是 `boss_worm_segment`）+ `ModEntities.DESTROYER_LASER`
> + `MonsterEntities.EATER_OF_SOULS`（**两处成员级盲区**）。
> API 差异 7 类已逐条改写（`lerpTo` 去掉 `teleport`、`getAddEntityPacket(ServerEntity)`、
> `getDimensions` final → `getDefaultDimensions`、`CombatRules.getDamageAfterAbsorb` 新签名、
> `LootContextParams` 改名、`reloadableRegistries()`、`Holder<Attribute>`）。
> 编译门 0/0（主模组 + Lib）。记录见 `notes/WP3-WORM-SERVER-SUBSET.md`。
> **只注册不接线 ⇒ 零回归**（生成点仍指 TE 旧实体）。
>
> 🚧 **交接单剩余 = 客户端半**（`notes/WP3-CLIENTFAMILY-HANDOFF.md`）：`GeoNormalModel`(3) /
> `GeoNormalRenderer`(4) / `MissingModelRenderer`(1) + 其余客户端族文件 + 5 条渲染器注册 +
> 克苏鲁之眼 8 处改指 + 资源。**改指必须与渲染器同批**（否则隐形 boss）。
>
> 💪 **2026-09-27（第 20 轮）Boss 多人/难度属性强化接入（`BossMultiplayerEnhancement`）**。
> 从 WP3 客户端族交接单里**单拆出来的自包含窄批**：该文件在 1.21 侧此前**完全不存在**，
> 即「按难度与参战人数强化 Boss 属性」这个玩法**一直是关的**。本批把 1.20 原文改写成 1.21 形状
> （PortLib 的 UUID id → `AttributeModifier` record 的 `ResourceLocation`；`MULTIPLY_*` → `ADD_MULTIPLIED_*`；
> `getAttribute` 吃 `Holder<Attribute>`），并补上**两条**生成路径的接线：
> `EntityEvents.joinLevel`（**1.21 此前连这个处理器都没有**，覆盖区块恢复/脚本生成/直接 `addFreshEntity`）
> 与 `LivingEntityEvents.finalizeSpawn`（`GamePhase2AttributeModifiers` 之后）。另补
> `CommonConfigs.BOSS_ATTRIBUTES_MULTIPLIER_{HEALTH,DAMAGE}`（**成员级盲区**）。
> 编译门 0/0。记录见 `notes/BOSS-MULTIPLAYER-SUBSET.md`；交接单 §3.1 已划掉（剩 22 处）。
>
> 🧩 **2026-09-27（第 20 轮）`client/effect/RenderStateShardAccessor` 整类落地（G5′ 遗留项收口）**。
> 1.20 的该类（115 行 / 9 成员）此前在 1.21 侧**完全不存在**（G5′ 记为整类盲区）。本批整类搬入，
> 并把 G5′ 临时塞在 `ModClientSetups` 的 `TRAIL_RENDER_TYPE` **回迁**到新类，
> `TERRA_SWORD_RENDER_TYPE` 与两个 `GLINT_*` 也改为**改指/委托**（不再有第二份同名渲染类型）。
> 编译门 0/0。记录见 `notes/WP6C-RENDERSTATE-SUBSET.md`。
>
> 🚧 **同轮启动但主动回退的批次：WP3 客户端族批（并集 15 文件 / 42 处编译错）**。
> 目标是一次性解锁克苏鲁之眼+仆从的渲染与 8 处改指、史莱姆族特效渲染尾巴、`BossMultiplayerEnhancement`。
> 实测错误密度过高（含 PortLib→1.21 `AttributeModifier` record 的 21 处重写），
> **为不留破损工作树，已回退到能编译的提交**，并把**全部 42 处的逐条修法**写进交接单
> `notes/WP3-CLIENTFAMILY-HANDOFF.md`（含 Lib 需先补 `interpolateBasis` 家族、
> `ModEntities.DESTROYER_LASER`、`MonsterEntities.EATER_OF_SOULS`、`BossEntities` 蠕虫 5 成员、
> 5 条渲染器注册与克苏鲁之眼 8 处改指）。**下一批直接照单执行即可。**
>
> 👑 **2026-09-27（第 20 轮）WP3 切片批：史莱姆王完整落地（服务端 + 客户端 + 接线）+ 克苏鲁之眼「先加不接线」**。
> `notes/WP2-REMAINDER-GATE.md` 预估的 38 文件切片，实测服务端只剩 **5 个**；客户端按渲染器成本切分：
> `KingSlimeRenderer` 只要 3 个文件（复用史莱姆批的模型/外壳层），而 `EyeOfCthulhuRenderer` 继承
> 1.20 自有的 `BossGeoRenderer` → `GeoNormalRenderer` → **`BaseWormBoss`**（蠕虫 boss 族）+
> **整类未迁的 `client/effect/RenderStateShardAccessor`**。
> 本批：**史莱姆王 + 王冠（服务端 2 + 客户端 3 + 9 处 TE 改指 + 4 张资源）**、`BossEntities` +5 成员
> （含 `DEERCLOPS`，类早在 WP2 批次 14 就有、注册条目一直缺）、`CommonConfigs.KING_SLIME_LARGE_MINIONS`。
> **克苏鲁之眼/仆从只加实体与注册、暂不改指**（改指会让 boss 隐形 = 可见回归）。
> 编译门 0/0。批次记录见 `notes/WP3-EYE-KING-SUBSET.md`。
>
> ⚠️ **下一批（客户端族批次）**：`BossGeoRenderer` + `GeoNormalRenderer` + `GeoNormalModel` + `EntityLightSampler`
> + `RenderStateShardAccessor`（整类）+ **蠕虫 boss 族**（`BaseWormBoss`/`BossWormPart`/`EaterOfWorlds`/
> `TheDestroyer`/`TheDestroyerProbe`，`BossWormPart` 直接 instanceof 后两者，紧耦合）。做完同时解锁：
> ① 克苏鲁之眼/仆从的 8 处改指与渲染；② **史莱姆族的特效渲染尾巴**（`GeoSpecialSlimeRenderer` 尖刺×3 + `Slimer`、
> `TownSlimeRenderer` + `DivaSlimeVertexConsumer`）；③ `BossMultiplayerEnhancement`（PortLib 的 UUID
> `AttributeModifier` → 1.21 record 形状，21 处）。
>
> ⚠️ **踩坑**：本批一度用 PowerShell `Get-Content -Raw` + `WriteAllText` 批改 java，UTF-8 中文注释被按 GBK 读入
> 变乱码并吞换行 → 已回退改用 Python。**java/json 批量改写一律走 Python 或 `edit` 工具**。
>

> ✅ **2026-09-27（goal round 3）批次 24：骷髅王组落地（`8a7fdf29c`）。**
> `Skeletron`（420 行）+ `SkeletronHand`（385 行）+ **增量版 `BossEntities`**（只两个条目）+ 补成员
> `ModEntities.SKELETRON_SKULL`，编译门 10→2→0。两个必须先查出来的坑：① 1.21 现有 `skeletron_hand`
> 是**钩爪**、而 1.20 让 **Boss 的手**占用该 id（钩爪改叫 `skeletron_hand_hook`）→ 照单直搬会**重复注册同 id 崩服**；
> ② `SkeletronSkullProjectile` 类早有、实体类型成员没有（成员级盲区）。本批**解掉了 WP4 的唯一 Boss 侧硬依赖**
> （`NPCSpawner.oldManSummoned(OldManNPC, Skeletron)`）。记录与 4 处 API 差异见 `notes/WP3B-SKELETRON-SUBSET.md`。
> **下一批**：批次 25 = `NPCSpawner` 包移动（`data/saved` → `data/spawner`，17 处引用改指）+ 53 个 npc 文件
> + 增量 `NpcEntities` + 约 21 处击杀判据改 id 版（前置 `5d83b5236` 已落）。
>
> ⚠️ **2026-09-27（第 1 轮 goal）实测：Boss 切片与 WP4 是同一个 155 文件强连通团。**
> `--defer BossEntities` 后只剩 5 个文件（坐骑四件套 + `BossMultiplayerEnhancement`），而那 5 个踩中
> 成员级盲区（`ModEntities.RIDEABLE_*`）；不 defer 时 4 个 boss 种子 + `BossEntities` = 156 候选。
> 详见 `notes/WP4-NPC-GATE.md` 第五节。
>
> ✅ **其中「坐骑」这一块已单独落地（批次 23，`affa4d08e`）**：4 个坐骑实体 + 坐骑物品 + `MountManager`
> 共 9 新文件，并**清掉了 `ModEntities.RIDEABLE_*` 成员盲区**。见 `notes/WP3A-MOUNT-SUBSET.md`。
>
> ⚠️ 同一次实测的另一个结论：**这个 155 文件团里已经没有别的自包含切片了**。
> 逐个查过 `notes/ALL-COMMON-CLOSURES.md` 轻量名单里仍缺失的「闭包=1」种子
> （`GuardianFlailEntity` / `SpikyBallRuntime` / `SwordProjectileAppearance` / `YoyoEquipment` /
> `WhipCollisionGeometry` / `SwordDefinition` / `FlailAuxiliaryProjectile` / `StickyBlockPersistence` /
> `DriveAwayController` 等）：它们在 1.21 侧**引用点全为 0**（消费者尚未移植），单独落地就是死代码；
> 而它们的消费者（剑/悠悠球/连枷/枪械物品）本身又落在同一大团里。→ **结论：再往前只能走路线 B
> 或整包搬，没有「再捡一块」的空间**。

| 项 | 内容 |
|---|---|
| 目录 | `common/entity/boss`（**10233**）、`client/renderer/entity`（1480）、`client/bossbar/**` |
| 代表来源行 | `fac72523a`（part critters，5731，其中 boss 3609）→ `92e38df06`（4323，boss 2412）→ `ef1a4d138`（1933，肉山肉墙）→ `9bc04295b`（3014） |
| 关联 | `3e7cc41a9` Boss 血条系统（单独一行，含着色器与 18 张纹理） |

### WP4 · NPC 体系

> ✅ **2026-09-27（goal round 13 / 批次 25）WP4 整层落地**：54 个 `common/entity/npc/**` 文件 +
> **42 条目的 `NpcEntities` 注册层**（已在 `Confluence` 构造器接线）+ `NPCSpawner` 真迁移
> （`data/saved` → `data/spawner`，`git mv` + 换 1.20 内容 + 17 处引用改指）+
> 贸易层（`trade/**`、`NPCMood`、`NPCSpawner`）+ **新建 `ModCustomRegistries`/`ModTradeConditions`**
> （1.21 侧整个不存在，原生重写）+ 对话/服务/召唤网络包。编译门 0 错误。
>
> **顺带并入**：枪械内联 **G3′ 主体**（弹幕/子弹层 21 文件）—— 因为 `BaseBulletEntity -> BaseNPC` 是同一团。
>
> ⚠️ **两条必读的教训（写进了 `tools/port2native/README.md`）**：
> 1. **javac 默认只报 100 条错误**（`-Xmaxerrs`）→ 前 4 轮把「100 错误 / 46 文件」当总量，
>    真实是 **150 / 65**。大批次用 `build_errors.py --maxerrs 2000`。
> 2. **闭包工具的第二类盲区**：不只是「成员级」，还有**整个类缺失**看不见 ——
>    `ModCustomRegistries`/`ModTradeConditions` 从未被 `dep_subset`/`stage_batch` 点名
>    （`uncovered` 还是 `[]`），却一个根因带出 38 处错误。
>
> 逐轮过程、全部 API 差异与**偏差清单**见 `notes/WP4-BATCH25-WIP.md`（第十三节是收口结论）。
> **下一批**：枪械 G4′（`GunItems` 整层 + `TGTags`/`TGSoundEvents`/`TGTrailColors`/`TGGunSounds`）。

| 项 | 内容 |
|---|---|
| 目录 | `common/entity/npc`（**6420**）、`common/entity/npc/trade/conditions`（25 个文件）、`client/gui/**` 商店/对话屏 |
| 代表来源行 | `231c505ca`（part npc，1369）→ `9645da98c`（1798）→ `9a48d8619`（part npc1）→ 台账 99~130 行的「NPC 对齐」长系列（每行很小，但**是一串成体系的行为修复，建议整体处理**） |


### WP5 · 召唤与 AttachmentEntity 体系（全新子系统）

> ✅ **2026-09-27（第 14 轮）WP5 核心 28 文件落地：提交 `fe98072a3`，编译门 0 错误。**> `AttachmentEntityData` 单点闭包（28 文件 / 2374 非空行）已一次性搬完：附件实体基类、目标缓存、
> 鞭痕标记、同步字段分发、召唤标记类型、两个 payload（改走 `IPacketS2C` + `ParticleTypes.STREAM_CODEC`）、
> 信息/粒子数据附件、**两个自定义注册表**（`DeferredRegister` + `makeRegistry(sync)`，已在 `Confluence` 构造器接线）。
> 批次记录 / 实测 API 差异 / 推迟清单见 `notes/WP5D-SUBSET.md`；前情与根因清单见 `notes/WP5B-SUBSET.md`。
>
> **WP5 剩余（按依赖排序）**：
> 1. **注册条目 + tick 接线**：`SummonerAttachmentEntityTypes` / `SummonerSummonMarks` / `SummonerSoundEvents`
>    + `SummonerEvents` / `SummonerHelper`（`InfoData.tick` / `SummonerParticleData.tick` / `WhipMarkTracker.tick`
>    目前无人调用 —— 基座先落、消费点后接）；
> 2. **物种**：22 个 minion 实现 + 各自 goal（`minion/**`、`minion/goal/**`）、`MomentumAttachmentEntity`、
>    4 个召唤 projectile、`ParticleHelper`；
> 3. **客户端渲染层**：`client/summoner/**` 渲染器与 trail；
> 4. **词缀**：`ModPrefix.Summon` 组 + `WhipMarkTracker.tagDamageOf` 回填（属 WP6，见下）。

| 项 | 内容 |
|---|---|
| 目录 | `common/summoner/**`（minion 3605 + attachmentEntity 1523 + attachment 916 + projectile/particle/trail）、`client/summoner/**`（1296 + rendererHelper 805 + trail 764） |
| 代表来源行 | `f4ffadaa7`（AttachmentEntity体系迁移，5315，**72 个新文件 / +10081 行**）→ `7169379cd`（1495）→ `46e5e8626`（移除旧架构召唤体系）→ `641467c87`/`33d89dd63`（黄蜂）→ `c0e8c4c75`（多标记叠加）|
| 说明 | 这是 1.20 侧**最完整的新架构**，1.21 侧**零基础**。它取代了 1.21 现有的 TerraEntity 召唤实现（Q3 决定 1.21 向 1.20 对齐），因此本工作包结束后需要处理 `TerraEntity`/`TerraGuns` 的退役 |

### WP6 · 物品 / 武器 / 枪械 / 效果

> 🔫 **2026-09-27（第 19 轮）枪械内联 G6′ 落地 —— 枪械内联（G0~G6′）全链收口，TerraGuns 子模块退役**。
> 根仓库：`TerraGuns.MODID` 4 处改指 + `settings.gradle`/`build.gradle`/`.gitmodules` 清理 + **gitlink 移除**
> + 成就 json 改指 + **三份手写 lang 的 92 改名 / 9 删除**（整块 TG 时代死键 → 1.20 的 `confluence.*` 键名）；
> 子模块 Lib `c519b6e`：`LibDamageTypeTagsProvider` 补 `IS_PROJECTILE`（接替被删的 TG data 文件）。
> 编译门 0 错误 / 0 文件（主模组 + Lib，且是在 TerraGuns 已从 gradle 配置移除的状态下）。批次记录见 `notes/GUNS-G6P.md`。
>
> ⚠️ **G6′ 的教训（两条运行时连带影响，编译完全看不见）**：
> ① TG 的 `data/minecraft/tags/damage_type/is_projectile.json` 是 `gun_bullet` 进 `#minecraft:is_projectile` 的**唯一来源**；
> ② 三份 lang 里枪械/子弹的物品名、实体名、键位、tooltip 全是 `*.terra_guns.*` **死键**（玩家看到英文回退或原始键）。
> **退役一个模块必须先做「命名空间引用清点」：gradle / toml / java / 手写资源 / 数据包 / i18n 六面都要扫。**
>
> ⚠️ **故意保留**：`i18n/{en_us,zh_cn}.json` 的 102 处 TG 键（**1.20 同样有 70 处**，属翻译同步任务）、
> 变更日志 `rename.md` 的历史条目、各 java 文件的溯源注释。见 `notes/GUNS-G6P.md` 第四节。
>
> 🔫 **2026-09-27（第 19 轮）枪械内联 G5′ 落地**：**客户端层收口 = 枪终于能在游戏里开火**。
> `client/handler/GunHandler`（`ShootPacketC2S`/`InspectPacketC2S` 的唯一发送方）+ 整套
> `client/renderer/entity/bullet/**`（渲染器/拖尾/5 类命中特效）+ `client/renderer/item/{GunRenderer,TaczFirstPersonTransform}`、
> `ModKeyBindings.{GUN_SHOOT,GUN_AIM,GUN_INSPECT}` + `GameClientEvents` 四处接线
> （`handle`/`reset`/`BulletVfxManager.render`/`BulletEvent.ImpactEffectEvent` 订阅，另补 1.20 `:260` 的「持枪取消原版攻击」）、
> `ModClientEvents` 的 `TGUtil` 改指 + 补 `BASE_BULLET_ENTITY`/`GRAVITY_BULLET_ENTITY` 渲染器。
> **并搬回枪械家族全部资源**（78 新 + 7 覆盖，TerraGuns 命名空间 → 主模组；不搬则 10 把枪无模型）。
> 编译门 0 错误、PortLib 残留 0。批次记录见 `notes/GUNS-G5P.md`。
>
> ⚠️ **G5′ 新发现 3 个整类/成员级盲区**（编译几乎看不见，只能靠 1.20↔1.21 对照量出来）：
> ① `client/particle/LuminiteImpactParticle` **整类**缺 + `ModParticleTypes.LUMINITE_IMPACT` 成员缺 + 粒子注册缺 + 资源缺（四件套）；
> ② `client/effect/RenderStateShardAccessor` **整类**缺（1.20 的 115 行 / 9 成员，1.21 只在早期内联了其中 2~3 个；
> 本批仅按 1.21 惯例把枪械所需的 `TRAIL_RENDER_TYPE` 放进 `ModClientSetups`）；
> ③ `ModParticleTypes.NO_TRAIL` 成员缺（1.20 还被 `SpearProjectile:137`/`SwordProjectile:358` 消费）。
>
> ⚠️ **G6′ 只剩模块退役** → **已于第 19 轮完成**（见上方 G6′ 条目与 `notes/GUNS-G6P.md`）。
> 剩余与枪械无关的三项已单列：`ModParticleTypes.NO_TRAIL`+矛/剑弹射物尾迹、`RenderStateShardAccessor` 整类搬迁、
> 1.21 Lib 伤害类型只 5 个（1.20 有 13 个）导致 `LibDamageTypeTagsProvider` 仍有 4 条 1.20 tag 行无法照搬。
>
> 🔫 **2026-09-27（第 19 轮）枪械内联 G4′ 落地**：注册层（1.20 `GunItems` 整层 13 枪 + 19 子弹、
> `common/init/gun/{GunSounds,GunTrailColors}`、`ModGunProperties`、标签层 4 个块）
> + **服务端开火管线**（`common/combat/gun/{ShootingService,GunFiringService,GunProjectileFactory}` +
> `util/ModGunUtils`）+ **网络层**（4 个包）+ `ItemEvents` 枪械处理器按 1.20 事件形状重写
> （7 个 TE 形状 → 6 个 1.20 形状，`ProjectileCreation` 移到新的 `event/game/GunEvents`）。
> **93 处** `TGItems.`/`TGTags.` 改指；编译门 0 错误、PortLib 残留 0。批次记录见 `notes/GUNS-G4P-SUBSET.md`。
>
> ⚠️ **G5′ 只剩客户端触发器**（`GunHandler`/`BulletRenderer`/`BulletVfxManager` + 5 处 `TerraGuns` 引用）——
> ~~**G4′ 之后枪还不能在游戏里开火**~~ → **已由 G5′ 于第 19 轮关闭**（见上方 G5′ 条目）。
>
> ⚠️ **本批新发现 3 个成员级盲区**（`LibMathUtils.criticalDamageTotal`、`PrefixUtils.calculateUseTime`、
> `ModTags.Items.{MANUAL_GUN,SEED_AMMO,SNOW_AMMO}`）—— 工具只看类型级边，仍然只能靠编译门暴露。
>
> 🔫 **2026-09-27（第 15 轮）枪械内联迁移（TerraGuns → 主模组）已开工，进度与修正后的顺序见
> `notes/GUNS-INLINE-MIGRATION.md`**：G0（Magic-Lib 手部动画 API，子模块 `a19d894` + 根 `cde95394d`）、
> G1′（`GunDefinition` + `GUN/BULLET_PROPERTY` + `autoFireAllGuns` + `AUTOMATIC_GUN` 标签，`20cbc0204`）、
> G2′（`BaseGun` + `GunEvent`，`3a9048386`）已落地。
> ⚠️ **关键结论（影响排序）**：G2′ 之后的改指被两堵墙挡住 —— ① `ItemEvents` 仍绑 TerraGuns 的
> `GunEvent`（两边嵌套事件名不同，7 个处理器要按 1.20 形状重写）；② 弹幕基类
> `common/entity/projectile/BaseBulletEntity` 在 **156 文件闭包**里，其入口之一是
> **`common/entity/npc/BaseNPC`（WP4）**。→ **枪械内联的下一步实际取决于 WP4 是否先做**。

| 项 | 内容 |
|---|---|
| 目录 | `common/init/item`（3902）、`common/init/block`（1360）、`common/item/**`（sword 1092、yoyo、gun、flail）、`common/effect/**` |
| 代表来源行 | `f4b42537c`（part8，2886，item 1562 + block 1032）→ `00b72167d`（枪械合并，1382）→ `90dfd7804`（重构剑类/剑气/枪械，2357）→ `9378868e5`（重构光剑、悠悠球，含 10 处改名） |

#### WP6a · 效果层迁移（1.21 的 TerraCurio → Confluence-Magic-Lib）—— **已裁决：完整对齐 1.20**

2026-09-27 用户裁决 **A**：1.21 把效果层按 1.20 的归属搬到 Magic Lib，
不做「桥接到 `TCEffects`」的临时方案、也不做两套并存。

| 项 | 内容 |
|---|---|
| 决策内容 | 5 个效果 `confused` / `gravitation` / `paladins_shield` / `cerebral_mindtrick` / `honey` 归 `org.confluence.lib.common.LibEffects`（注册命名空间 `confluence_magic_lib`），1.21 TerraCurio 的 `org.confluence.terra_curio.common.init.TCEffects` **退役** |
| 1.20 依据 | `Confluence-Magic-Lib` 里有 `lib/common/LibEffects.java` + `lib/common/effect/{GravitationEffect,HoneyEffect,PublicMobEffect}` + `lib/client/LibKeyBindings.java` + `lib/client/handler/GravitationHandler.java` + `lib/network/{c2s/GravitationPacketC2S,s2c/BroadcastGravitationRotPacketS2C}.java` + 5 张 `textures/mob_effect/*.png`；1.20 的 TerraCurio 里**已无** `TCEffects`，且它自己引用 Lib 的 `LibEffects`（7 个文件，如 `PaladinsShield.java:27`、`TCUtils.java:145/183`）——即「TerraCurio 依赖 Lib 的效果」正是分叉后的新架构 |
| 1.21 现状 | 效果全在 TerraCurio：`TCEffects`（terra_curio 命名空间）+ 重力栈（GravitationEffect/HoneyEffect/GravitationHandler/2 个网络包/`IEntity.terra_curio$setShouldRot`/`TCKeyBindings`），主模组侧 14 个文件 + TerraCurio 侧 13 个文件引用 `TCEffects` |
| 不移植的部分 | 1.20 Lib 的网络包用 `IPortPacket`/`PortStreamCodec`/`PortByteBufCodecs`（PortLib），1.21 侧保持自己的 `IPacketC2S`/`IPacketS2C` + 原生 `StreamCodec`；1.20 的 `GravitationEffect.GRAVITY` 用 `UUID`+`Multimap<Attribute,…>`（PortLib 形态），1.21 用 `ResourceLocation`+`Holder<Attribute>`（原生形态，`Attributes.GRAVITY` 在 1.21.1 是原版属性：`Attributes.java:74`） |
| 为什么挡着 WP2 | `MonsterEntities`（826 行注册层）会引用全部物种，而 `Nymph`/`SpittingPlant`/`ClimbingSpider`/`SpiderWebSpit` 引用 `LibEffects.CONFUSED`；注册层又是其余 31 个「重量」怪物种子的共同前置。所以 WP6a 是 WP2 收尾的前置 |
| 批次 | 批次 2 = Lib 侧落地（效果 + 重力栈 + 按键 + 贴图 + lang，编译 `:Confluence-Magic-Lib:compileJava`）；批次 3 = 消费侧迁移（TerraCurio 退役 `TCEffects` 等 + 27 个引用点 + datamap/lang，编译三个模块） |
| 已完成 | 批次 2（Magic-Lib `480f342`）与批次 3（TerraCurio `bc41b10` + 根仓库 `0cd33b090`）已落地，三模块编译 0 错误；详见 `notes/WP6a-EFFECTS-MIGRATION.md` |

#### WP6c · 重力反转整条特性搬到 Lib（WP6a 的第二半）—— **已完成（第 19 轮）**

✅ 已落地：Lib 子模块 `659c8c8`（`ILibEntity`、`client/LibKeyBindings`、`client/handler/GravitationHandler`、
2 个网络包、11 个 mixin、`LibLanguageProvider` 2 个 lang 键）、TerraCurio `fc709c5`（8 删除 + `ITCEntity` +
~54 处改指 + `terra_curio.mixins.json` −4）、根仓库 `c589bdf05`（`LibKeyBindingsMixin` 新增 /
`TCKeyBindingsMixin` 删除 / lang ×3 / `InverseEnderMan`）。**三模块编译门 0 错误 / 0 文件**。
完整记录（含偏差与「TC 侧保留的 4+3 处半成品，Lib 后续补齐后必须删除」清单）见 `notes/WP6C-GRAVITATION.md`。
待游戏内验收：重力翻转、相机 180°、步骤凳、重力球。

以下为开工前的摸底结论（保留作背景）：

效果注册迁完之后，重力**特性**仍留在 TerraCurio，1.20 侧它在 Lib 里。开工前先读
`notes/WP6a-EFFECTS-MIGRATION.md` 第五节（硬约束）与第一次摸底的全部行号。要点：

| 待办 | 关键约束 |
|---|---|
| `lib/client/LibKeyBindings`（`FLIP_GRAVITATION`）+ `lib/client/handler/GravitationHandler`（1.20 版有 `forceEnable`/`forceCancel`，1.21 版有 `hasGlobe` + `StepStoolHandler.onStool()` 耦合）+ `lib/network/{c2s/GravitationPacketC2S,s2c/BroadcastGravitationRotPacketS2C}`（在 `LibModEvents.registerPayloadHandlers` 里注册，Lib 目前没有任何 C2S 包） | 依赖方向安全：Magic-Lib HEAD 对 `org.confluence.terra` **零编译期引用**，反向依赖已成立 |
| `lib/mixed/ILibEntity`（`confluence$` 前缀，3 个成员）+ 拆 1.21 的 `terra_curio/mixed/IEntity`（6 个成员：3 个走、cthulhu 一对在 1.20 归 `ITCEntity`、`isPlayer` 在 1.20 无对应物） | **不能两套并存**：1.21 TC 的 `mixin/EntityMixin.java`（`@Mixin(Entity.class) implements IEntity`）里的 `@Unique` 字段名与 1.20 Lib 版**同名**（`terra_curio$isShouldRot`、`terra_curio$dimensionHeight`）→ 两个 mixin 注入同一个 `Entity` 会崩。必须原子切换 |
| TC 侧 4 个「反转 AI」mixin（`GroundPathNavigationMixin`/`LandRandomPosMixin`/`MoveControlMixin`/`WalkNodeEvaluatorMixin`） | 1.20 Lib **无对应物**（1.21 里仍是 `// todo 反转AI`），需单独决策归属 |
| `mixin/EntityMixin` 的拆分 | 1.21 版同时承载 cthulhu 冲刺、`isPlayer`、以及 `resetLavaImmune` 里与 `TCUtils.applyLavaImmune` 的耦合；1.20 Lib 版是独立的 `@Inject baseTick` 缓存 `dimensionHeight`，拆的时候要解开 |
| 资源与集成 | 按键 lang 键 `key.terra_curio.flip_gravitation` → `key.confluence_magic_lib.flip_gravitation`（主模组 4 处 + TC 生成器 1 处）；主模组现有 `mixin/integration/terracurio/TCKeyBindingsMixin`（改 category）在 1.20 对应的是 `mixin/integration/magiclib/LibKeyBindingsMixin`，1.21 需新增；主模组 `GameClientEvents.java:548` 与 TC `GravityGlobe.java:22` 都要换类 |


### WP7 · 数据生成与资源（跟着内容走，不单独提前做）

> ⚠️ **2026-09-27 顺序修正（第 8 轮实测）**：下文第四节的顺序「WP1 → WP2/WP3/WP4/WP5 各自独立推进」
> 在**物种的剩余部分**上不成立。实测：
> - WP2 怪物目录剩 20 个类，其中 **18 个的闭包是 189~191 个文件**，入口是 **`TownSlimeNPC`** →
>   `BaseNPC` 框架（`TownSlimeNPC` 单个种子的闭包就是 189）；
> - WP2 动物目录（1.20 有 41 个、1.21 有 0 个）的种子集闭包 222 个文件，其中 **62 个是 `common/entity/npc/**`**；
> - WP3 的 boss 切片（`EyeOfCthulhu`+`KingSlime`+`ServantOfCthulhu`+`BossMultiplayerEnhancement`）
>   在 `--defer BossEntities` 后仍留 38 个文件，**其中 9 个是 NPC 层**。
>
> **结论：WP4 的 NPC 基座应当提前**，它一次性解开 WP2 剩余 18 个怪物 + 40 个动物，以及 WP3 的一部分。
> 完整测量与可复现命令见 `notes/WP2-REMAINDER-GATE.md`。

| 项 | 内容 |
|---|---|
| 目录 | `common/data/gen`（**10317**）、`common/data/**`、`ConfluenceOtherworld/src/main/resources/**`、`TerraCurio/.../datagen`（1091） |
| 说明 | 这批 provider 的欠账**不是独立内容**，而是随 WP1~WP6 的新物品/新生物一起长出来的。做法：先搬 provider，再在 1.21 侧跑 `runData`；
只搬「1.20 写得比 1.21 新」的那部分，**不要**把 1.20 生成出来的 json 直接拷进 1.21（两侧 datagen 入口与命名空间有差异） |

### 旁支 · Confluence-Magic-Lib / TerraCurio / TerraFurniture（子模块）

| 项 | 内容 |
|---|---|
| 权重 | `Confluence-Magic-Lib/lib/util` 1167、`TerraCurio/datagen` 1091、`TerraFurniture` 若干（`a45ef0a13` / `6db8be065` / `8bb33454a` / `fa147c869`） |
| 说明 | 子模块侧同样有 Post-fork 内容（饰品 datamap 化 `21b060ec6`、动态光源进 lib `a6819f175`、家具 `a45ef0a13`）。可独立并行推进 |

## 四、执行约定

1. **顺序**：WP1 → WP2/WP3/WP4/WP5 可各自独立推进 → WP6 → WP7 → 子模块旁支。跨工作包允许并行，工作包内严格按台账顺序。
2. **粒度**：每个 1.20 源提交 → 1.21 一个提交；提交信息写 `port(1.20 <hash>): <原说明>`；跨提交的机械改名单独成提交。
3. **验证门**：工作包出口跑 `gradlew --offline build` + `runData`（WP7）+ `runClient` 手工验收。
4. **转换工具**：Port→原生的机械替换交给 `tools/port2native/port2native.py`（规则数据 954 条类型 + 208 条调用点 + 426 条事件总线）。
   **未覆盖规则必须为 0**、`BUS` 占位残留必须为 0 才允许继续；Mixin 层不走机器转换，按 `notes/` 里的 Mixin 审计清单人工处理。
5. **禁止**：整体替换整棵树（会吃掉 1.21 侧独有的 harvest 内容）；直接拷 1.20 的 Datagen 产物；移植 integration。
   > 🔻 **2026-09-27 第 21 轮补充**：1.21 分支特有的 `integration/terra_entity` 桥接层（44 文件）
   > 连同 `mixin/integration/terraentity`（14 mixin）已按用户指令**整体删除**，消费点改回 1.20 原生形态
   > （原生 `BaseNPC`/`NPCSpawner`/`HouseHandler`/`HouseValidater`、`NPC_TRADE` 菜单 + 1.20 的
   > `client/gui/container/npc_screen/**` 界面）。`ModEffectStrategies` 迁到 `common/init/`（物品层退役前先用着）。
   > 记录与 5 条后续欠账见 `notes/WP8-TE-LAYER-REMOVAL.md`。1.21 侧其余 integration 包（子模块适配）不动。
6. **注释口径（用户 2026-09-27 明确）**：**不要添油加醋地写注释**。规则：
   1.20 原文有注释就**照抄**，1.20 没有就不加；不写「1.20 `:行号` 出处」「与某批同一处理」「坑/教训」
   这类溯源与说明性内容，也不写长 `///` 文档块。确有必要记录的批次结论写进 `notes/`，不写进源码。
   （此前批次已写的注释不回头改，只从新批次开始按本口径执行。）

## 七、TE 退役里程碑（第 17 轮；第 18 轮收口）

**已达成（全部三部分完成）**：主模组在**没有 TE 子模块**的情况下编译通过，且全仓已无任何 TE 代码引用。
验收命令与结果（必须真执行，缓存不算）：

```
gradlew.bat --offline --no-build-cache :ConfluenceOtherworld:compileJava
→ > Task :ConfluenceOtherworld:compileJava  （实际执行，非 UP-TO-DATE/FROM-CACHE）
→ BUILD SUCCESSFUL / gradle exit=0 / 0 error / 0 warning
```

核验快照（第 18 轮终态）：

| 项 | 结果 |
|---|---|
| 主模组 java 里 `org.confluence.terraentity` | **2 处，全是 `///` 注释里的类名**（`AnglerDialogScreen:21`、`HouseSelectPacketC2S:28`），无代码引用 |
| 主模组 java 里 `TerraEntity`（裸类名，含注释） | 6 处 / 5 文件：`AnglerDialogScreen:21`、`HouseSelectPacketC2S:28`、`ModTradeConditions:16`、`ModCustomRegistries:21`、`EaterOfWorlds:714,831`——**全部是注释里的出处说明**（注释口径见 §六.6） |
| 主模组 `resources` 里 `terra_entity` | 15 处 / 8 文件，全在 `assets/confluence/ageratum/zh_cn/**`（命名空间迁移的变更日志与属性文档，历史资料，保留） |
| java 里 `terra_entity` 键字面量 | **0**（批 12 收口：`BestiaryLanguageSubProvider` 1119 / `DialogsLanguageSubProvider` 86 / `ModChineseProvider` 61 / `ModEnglishProvider` 61 / `BoomBunnyRecipe` 1 → 全 0） |
| 语区 json 里的 `terra_entity` | **0**（`registered_ids.py --kind entity --check-lang` → 可平移 0 / 应删 0） |
| `TerraEntity/` 目录 / gitlink / `.gitmodules` 段 | 全部不存在（提交 `8003db250`） |
| `settings.gradle` / `ConfluenceOtherworld/build.gradle` 的 `projectName` | 均已移除 `"TerraEntity"` |
| TerraCurio / TerraFurniture / Confluence-Magic-Lib 对 TE 引用 | 0 |

**提交序列（本轮 TE 退役，按批次）**：`167afd4a1`(决策表) → `1c36de93b`(批次1 机械改指) →
子模块 `36ac216`/`6931993`/`d253767` + 对应 gitlink → `d90abedef`(R4) → `e219b155e`(批次2) →
`5be04ec0a`(资源 data) → `f4d037ec4`(语言键) → `984291192`(批次4) → `08e1f12eb`(存储簇) →
`6cb812329`(批次3) → `b0dcc72ac`(通配符盲区三文件) → `47282a425`(批次9 库依赖) →
`f433105fb`(批次11 图鉴变体层) → `34511249b`(批次10 九文件 + 117 战利品表键) →
`edf2755a4`(史莱姆条目) → `8003db250`(摘子模块) → `db387e69a`(里程碑 §七 首次记录) →
`6606c37d6`(批次12 datagen provider 语言键收口) → `fa3b8da0a`(修 `registered_ids.py` 跨行盲区
＋恢复被误删的 23 条语言键＋`fealing.0` 收口) 及若干 `docs(port)` 记录提交。

### 7.1 因退役而**不再工作**的 1.21 独有能力（需用户确认是否接受）

这些在 1.20 侧**本来就不存在**（故按"对齐 1.20"属正当取舍），但都是玩家可感知的变化：

1. **Curios 的 pet / light_pet / mount 槽不再被包成附件视图**：`ExtraInventory.initialize()` 按 1.20 删掉同步后，
   放进 Curios 坐骑槽的坐骑不再被 `getMount(false)` 返回（`MountManager` 只读本模组附件槽）→ 表现为"没装"，
   直到玩家挪进本模组槽位。**这是决策表 A5 要求游戏内实测的点。**
2. **reforge 价格不再随 NPC 心情浮动**：`PrefixUtils.getReforgeCost` 回到 `price / 3`。
   心情 100 不变；心情 80 相当于比原先便宜 20%；心情 120 贵 20%。
3. **重铸界面前缀不再上浮淡入**（`KeyframeAnimation` 删除）→ 只保留文字滚动反馈（1.20 行为）。
4. **蜜蜂/蜜蜂箭弹幕失去出生 10 tick 的"由小变大"**（TE `BaseEntityRenderer.preRender` 的缩放渐变），
   改为立刻满尺寸（1.20 行为）；同时获得 1.20 的零速度守卫。
5. **两个图鉴键消失**：恶魔眼的 `.minion` 键（原生无 `OwnedSummon` 实现者）、僵尸的 `.slime` 键
   （原生无 IZombie；本模组僵尸不是 vanilla Zombie）→ `ModClientEvents` 的 `SlimeZombieRenderer` 注册
   **不可达**（类与注册保留）。
6. **`#curios:pet|light_pet|mount` 不再被本模组喂条目**（原先靠可选引用 TE 的 `#terra_entity:curios_*`）：
   若 TerraCurio 的槽位注册以这些标签判"可放入"，那些槽会变成**放不进任何东西**；
   同时 `#confluence:pet|light_pet|mount` 由空标签变为有内容。
7. **矛的 AABB 碰撞攻击未按 1.20 结构改写**（1.20 没有这套机制）：为免双重伤害/静默改变伤害投递，
   本批把 TE 的 `ICollisionAttackEntity` API **就地原生实现**在 `SpearProjectile` 内
   （两个禁改文件的子类靠继承使用 `CollisionProperties`）。**建议与 WP6-B 剑/矛层一并裁定**。

### 7.2 顺带修掉的两个既有功能性 bug（与 TE 退役无关但同批发现）

1. **战利品表键错位（怪物不掉落）**：`EntitySubProvider` 的 117 处显式键写成
   `confluence:entities/terra_entity/<id>`，而实体派生名是 `confluence:entities/<id>` → 那 116 张表
   永远取不到；`ModLootModifiersProvider` 等按派生名取的基表也不存在。已按 1.20 去掉显式键（`34511249b`）。
2. **图鉴变体 NBT 写 int 导致静默回落**：原生变体用 `StringRepresentable` codec 解析该键，int 解析失败
   → 48 条变体预览**全部显示默认皮肤**（蝶回 RED_ADMIRAL、蜻蜓回 BLUE、鸭回 MALLARD…）。
   改为字符串序列化后预览才正确（`f433105fb`）。

### 7.3 仍未决的 3 件事

1. 决策表 ⑨（图鉴进度存档兼容，见 §6.9 末）——带 TE 时代世界的存档其图鉴进度会重新开始。
   图鉴键由 `RegisterBestiaryKeyEvent.getKey(living)`（即 description id）派生，所以 TE 时代的
   `terra_entity:*` 键在新版对不上。三种改法待选：**按 1.20 现状放着** / 在 `Bestiary.decode` 做前缀改写迁移 /
   仿 `registerEntityAliases()` 注册别名。
2. 图鉴"数字条目键 vs 序列化名运行时键"错位（**1.20 也有同一分裂**）：这 48 条变体条目无法靠击杀解锁；
   两种改法（datagen 改序列化名 / `ModEvents` 补数字键注册）需择一。
3. `EntitySubProvider` 的隐式战利品表键改完后**须重跑 datagen**（`src/generated` 不入库，由用户自行执行）；
   另 `src/generated` 里 `data/terra_entity/**`（7 路径）与生成物内容里的 2871 处 `terra_entity`
   （226 个 json）会随重跑与注册 id 改写自然消失。**本目标明确不跑 runData。**

### 7.4 批次 12 收口与语区核对（第 18 轮实测）

1. **datagen provider 侧 1328 处 `terra_entity` 键字面量全部收口为 `confluence.`**，其中
   `BestiaryLanguageSubProvider` 是重头：449 行活动键改名 + **650 行注释键只换命名空间**（不删，与 1.20 同位同文）
   + 20 行删除（对应 1.20 也没有的键）。口径：**注释语句只换命名空间、绝不删除**。
2. **`registered_ids.py` 的多行盲区**（正则只匹配同行 `register*("id"`）曾导致 `f4d037ec4` 误删
   11 组 (family,id) 组合。工具已改为"把文件压平后再匹配"，并按 1.20 从 `f4d037ec4^` 恢复
   **23 条键**（de/es/lzh/pt，命名空间为 `confluence.`）。复核：`--kind entity --check-lang` → **可平移 0 / 应删 0**。
3. **语区图鉴键复核（本次逐条比对 1.20）**：
   - 4 个语区活动图鉴键里**孤儿键为 0**（每条都能落到 1.21 已注册实体 id）。
   - `bestiary.entity.confluence.fealing.desc` 在 de/es/lzh/pt **均存在**（此前"缺失"的判断作废）；
     `fealing.0` 形态已按 1.20 收口为 `fealing`。
   - 1.21 独有、1.20 没有图鉴文案的 4 键是 `anger_bones` / `fealing` / `hell_bat` / `possess_armor`——
     这 4 个正是 **1.21 侧注册 id 本身**（1.20 用 `angry_bones` / `faeling` / `hellbat`），
     **键名跟随 1.21 注册 id，属正确，保留**（改 id 是另一次带存档影响的改动）。
   - 反向：1.20 语区多出的 48–75 条图鉴文案，**其 id 在 1.21 全部未注册**（旧 id 与变体 id）→ 不是欠账，无需补。
   - 结论：语区侧**无本次退役引入的缺口**；`entity.confluence.*` 名称键的翻译完整度是既有欠账，与 TE 无关。


## 五、与旧计划的关系

`notes/PORTLIB_ARCH_MIGRATION_PLAN.md` 的 Phase 3「逐提交移植」仍然成立，但它的**循环顺序需要按本文修正**：
Phase 3 原设计是「从分叉点向新，一格一格推」，实测下来分叉点附近有 100+ 行是平台对齐噪音（多半会判 `SKIP`/`COVERED`），
真正的内容欠账集中在 2026-06 之后的 `part*` 与 `feat:*` 提交里，且**互相有编译依赖**（新物种必须在基座之后）。
因此：**判定仍逐提交做（台账），执行按工作包做（本文）**。

## 六、R 批次收尾与 TE 退役（第 23 轮实测）

### 6.1 TE 退役面的真实规模（原审计低估了 5 倍）

`notes/TE-RETIREMENT-AUDIT.md` 里「105 文件 / 206 条引用」是**按 import 行**数的；
按**使用行**实测（`tools/port2native/te_refs.py`）：**105 文件 / 1200 条引用 / 82 个 TE 类**。
其中约 **1045 条是注册表纯改指**（`TEMonsterEntities.X` → `MonsterEntities.X` 一类），C 类（无同名对应物）只有约 155 条。

**1.20 侧全仓对 terraentity 的引用数 = 0**（整仓 grep 实测）——所以 TE 退役不是架构改造，
每条引用的终点就是「同一位置的 1.20 原生写法」，判定有据可依。

### 6.2 退役工具（本仓库自带，均已实测）

| 工具 | 用途 |
|---|---|
| `te_refs.py` | 盘点：按 TE 类汇总、`--file` 逐条对照 1.20（difflib 对齐同一行）、`--resolve` 解析缺名成员、`--worksheet` 生成逐条施工依据 |
| `te_imports.py` | 列出全部 TE 全限定名（82 个），供映射表使用 |
| `te_repoint.py` | 执行：映射驱动的定点改指（import 原位替换、成员改名、目标已存在则删重复 import），默认 dry-run；`--apply` 落盘 |

首轮 dry-run：**38 文件 / 47 import 行 / 452 处使用**可立即机械改指；8 文件卡在成员未解析；
57 文件卡在 C 类判定。工具会拒绝在「成员未解析」的文件上落盘（除非 `--force`）。

### 6.3 待判定成员（8 文件 39 条，已有 1.20 证据）

| 1.21 引用 | 1.20 证据 | 结论 |
|---|---|---|
| `TEProjectileEntities.FIRE_IMP_PROJ` | `ModEntities.FIRE_IMP_PROJECTILE` | 改名改指（已进 MEMBER_OVERRIDES） |
| `TEMonsterEntities.SERVANT_OF_CTHULHU` | 1.21 已有 `BossEntities.SERVANT_OF_CTHULHU` | 改指 BossEntities |
| `TESummonItems.HORNET_STAFF` | 1.20 是 `SummonItems.NEW_HORNET_STAFF`，物品 id 为 `new_hornet_staff` | 改名改指（注意 id 差异） |
| `TEEnchantments.WHIP_SWEEP` | 1.20 有 `ModEnchantments.WHIP_SWEEP` | R3 落地后改指 |
| `TETags.DamageTypes` | 1.20 `ModTags` 无同名成员 | 需按语义查 1.20 对应标签字段 |
| `TEEffects.SUMMON_FOCUS`、`TEPetItems.WALLET`、`TEMonsterEntities.HONEY_SLIME`、`TEItems.HOUSE_DETECTOR`/`DEBUG_ITEM`/`NEO_TERRA`、`TEArmors.POSSESSED_ARMOR`、`TEBoomerangItems.BeiDou_BOOMERANG`、`TEFigureBlocks.FIGURE*` | 1.20 对应注册表**都没有** | 疑为 TE 独有内容 → 退役时须「补注册」或「删引用」，逐条定（见 `notes/TE-RETIREMENT-DECISIONS.md`） |

### 6.4 R4 的真实范围（原列 7 个，实测差集 14 个）

`mixin/**` 两代按路径差集：1.20 侧 134 个、1.21 侧 165 个；**1.20 有而 1.21 无的共 14 个**：

- 已在 R4 清单（7）：`integration/geckolib/{FileLoaderMixin, KeyFramesAdapterMixin}`、
  `integration/magiclib/LibEntityUtilsMixin`、`integration/terracurio/TCKeyBindingsMixin`、
  `neoforge/client/model/ForgeItemModelShaperMixin`、`resources/RegistryDataLoader$RegistryDataMixin`、
  `world/entity/ExperienceOrbAccessor`
- 改名/移动（2，不需搬）：`client/gun/ItemInHandRendererMixin` → 1.21 `client/renderer/ItemInHandRendererMixin`；
  `integration/magiclib/EnvironmentLevelAccess$MatcherMixin` → 1.21 `integration/magiclib/common/recipe/…`
- **尚未判定（5）**：`world/item/BucketItemMixin`、`world/item/SnowballItemMixin`、
  `world/item/enchantment/ProtectionEnchantmentMixin`、`world/level/block/entity/BaseContainerBlockEntityMixin`、
  `world/level/chunk/WallTrackingMixin` —— 需逐条判「1.21 已由平台/原生化取代」还是「真缺口」（并入 R4 处理）。
  注：1.21 附魔已数据驱动，`ProtectionEnchantment` 类本身可能已不存在。

### 6.5 文件级 MISSING 清单里哪些**不是**缺口（判定样例）

`tools/port2native/file_lag.py` 报 MISSING 76 个（跨 6 模块），但其中成簇的是**有意替换**，不是欠账：

- **10 个逐钩爪渲染器**（`AntiGravityHookRenderer` 等）：1.21 已换成通用渲染器体系
  （`AbstractHookRenderer`/`BaseHookRenderer`/`SimpleHookRenderer`/`DualHookRenderer`/`LunarHookRenderer`/`MimicHookRenderer`），
  钩爪实体也通用化（`BaseHookEntity`/`MimicHookEntity`/`HookOfDissonanceEntity`）→ **有意删除**。
- **2 个树苗种植器**（`SimpleTreeGrower`/`SimpleMegaTreeGrower`）：1.20 的 vanilla 父类在 1.21 已删 → 已宣布不搬。
- **4 个存储伙伴类**（`StorageCompanion*`/`FlyingPiggyBankEntity`/`ChesterEntity`）：已宣布不搬。

仍待定性的主模组残留（R3/R4 之后）：`client/handler/ScryingOrbHandler`(27)、`client/handler/SoulSkillHandler`(9)、
`client/model/entity/projectile/SpearProjectileModels`(38)、`client/renderer/entity/projectile/ForwardProjectileRenderer`(33)、
`integration/terra_curio/TCHelper`(19)、`common/data/gen/ModOpalDataProvider`(31，依赖 OpalLight，待用户裁定)。

### 6.6 连枷遗留附魔（待用户裁定）

`FLAIL_TURBINE` / `FLAIL_WIND_BURST`：1.20 **两者都不存在**；1.21 侧注册在 `ModDataProvider`（1987/2005 行），
标签在 `ModEnchantmentTagsProvider`（31/32/39/49/50 行），创造栏书条目在 `ModTabs`（1823–1827 行），
实现类 `common/enchantment/{TurbineEnchantments, WindBurstEnchantments}.java`。
`TurbineEnchantments.getLevel/getBonus` **无任何调用点**（连枷已换成 1.20 那代），即二者已是**惰性内容**。
按「1.21 对齐 1.20」应删；因涉及用户占用的 `ModEnchantments.java`（第 81 行 `wind_burst_at_hit` 效果类型注册），
删除动作需用户确认并由用户改该文件。
（用户 2026-09-30 裁定：**保留**这两个附魔，不删。）

### 6.7 本轮新增工具与实测结果

| 工具 | 用途 / 关键口径 |
|---|---|
| `te_imports.py` | 列出全部 TE 全限定名（起始 82 个） |
| `te_refs.py` | 盘点：按类汇总 + `--file` 逐条对照 1.20（difflib 对齐）+ `--resolve` + `--worksheet` |
| `te_repoint.py` | 映射驱动定点改指；**保留原换行风格**；成员校验会先定位目标文件的嵌套类块（见坑 4） |
| `registered_ids.py` | 抽注册 id（entity/item/block/effect）+ `--check-lang` 逐键判定语言键 |
| `te_lang_keys.py` | 语言键批次：`--dry` 先看族分布再落盘；写盘后做 json 解析校验；删除项留档 |

语言键批次实测（已提交 `f4d037ec4`）：provider 删 68 条 `terra_entity.subtitle.*`（全仓 0 引用）；
5 个已入库语区 **2067 条复活改指 + 1467 条删除**（删除项留档 `notes/TE-LANG-KEYS-REMOVED.md`）。
**关键认识**：这些键在 1.21 里本就失效（游戏查的是 `confluence.` 命名空间），改名是「复活」、删除无损失。

⚠️ **该批次当时并不完整**（第 15 轮才暴露）：它只改了**已入库的译文文件**与两个 provider 的
`terra_entity.subtitle.*`，**没改生成这些键的 datagen provider**——实测仍有
`BestiaryLanguageSubProvider` **1119** 处、`DialogsLanguageSubProvider` 86 处、
`ModChineseProvider`/`ModEnglishProvider` 各 61 处 `terra_entity.` 键字面量，
另有 `EntitySubProvider` 117 处 `entities/terra_entity/<id>` 战利品表键、`BoomBunnyRecipe` 1 处
实体 id 字符串比较。若不改这些源头，`runData` 会重新生成死键、把 `f4d037ec4` 的成果回退。
教训：**改"已入库产物"时必须同时改生成它的 provider**；盘点时按"目录/文件类型"补扫（本次即靠
`terra_entity` 全字符串扫描才发现，逐符号的 import 扫描完全看不到键字面量）。

### 6.8 本轮踩到的坑（新增 5 条，前 5 条见 §五）

1. **门禁假绿**：`build_errors.py` 报「0 错误」但 `[build] exit=0` 也可能是 `UP-TO-DATE` / `FROM-CACHE`
   的缓存命中，什么都没编。怀疑时删 `ConfluenceOtherworld\build\tmp\compileJava\previous-compilation-data.bin`
   并加 `--no-build-cache` 重跑（R4 实测抓到两次）。
   另：**增量编译还会掩盖别人正在改的文件的错误**（子代理实测：增量跑绿，整编立刻暴露他人 4 处错误）。
   并发跑门禁还可能撞上 `Unable to delete directory …build\classes\java\main`（另一进程在写）——重跑即可。
2. **门禁是 `--offline` 的**：新增依赖（如 OpalLight）必须先联网编译一次，否则报
   `No cached version available for offline mode`，而解析器仍打印「总错误数 0」→ 又一个假绿组合。
3. **新文件是 LF、仓库存 CRLF，而 `fix_eol.py` 只看已跟踪文件**：必须先 `git add` 再 `--check`，
   否则新文件带着 LF 进库（`SummonItems`/`EffectStrategyComponent`/`ModOpalDataProvider` 都中过）。
   提交消息文件也别用 PowerShell 的 `Out-File -Encoding utf8`（会写 BOM，`git commit -F` 后标题带
   `\ufeff`）——用 write/Python 写，并可用 `%TEMP%\bomscan.py` 复查。
4. **`te_repoint.py` 的嵌套类盲区（已修）**：成员校验原先只捕获 `TEsimple\.(\w+)`，对
   `TETags.Items.CURIOS_*` 只查到 `Items`（在 `ModTags` 里存在）而空过，把 4 行改成不存在的
   `ModTags.Items.CURIOS_*`。修法：先在目标文件里按花括号定位嵌套类块，再在块内查成员；
   定位不到就按方法链只查第一段。**新增族/新类时先跑 dry-run**。
5. **并发子代理会污染索引**：子代理按纪律 `git add`（为跑 EOL 检查），此时主代理提交会把它们的
   文件卷进自己的提交（本轮已发生一次，用 `git reset --soft HEAD~1` + `git restore --staged` 排除）。
   今后主代理提交用 `git commit -F <msg> -- <显式路径>`，或提交前先核对 `git diff --cached --numstat`。
6. **通配符 import 盲区（已修）**：`ValueSubProvider` / `GiftSubProvider` / `ConsumableItems` 用
   `import org.confluence.terraentity.*;` **通配符**引用 TE 的注册表类（`TEWhipItems`/`TEYoyosItems`/
   `TEBoomerangItems`/`TESummonItems`/`TERideableItems`/`TEPetItems`）与实体类（6 个 TE Boss），
   而 `te_imports.py`/`te_refs.py`/`te_repoint.py` 的正则只认具名 import → 这三个文件**从未进入任何清单**
   （盘点时少算 3 文件 / 数十条引用，直到 TE 只剩 7 个 FQN 时才暴露）。修法：正则补上 `[\w\.]*\*?`；
   `te_refs` 现在单独列出通配符 import；`te_repoint` 遇到这类文件直接判「不可机械改指」交人工。
   教训：我最初判断「通配符 import 未被使用」而删掉三行，门禁立刻报 **59 个缺符号**错误——
   它们正靠通配符引用 TE 类。**「看起来冗余的 import」必须先由门禁证伪再删**。
   对应提交 `b0dcc72ac`（三文件 + 工具）。
7. **库侧缺方法会成批卡住主模组**：1.21 库丢了 1.20 有的
   `LibUtils.{getAABBAngleTarget,getPlayerHandPos}`、`LibMathUtils.{angleBetween,getDirection}`、
   `LibRenderUtils.renderDebugBlock`、`AimUtils`，一次性卡住主模组 8 个文件。前四个我按 1.20 回补
   （子模块 `6931993`）；`AimUtils` 265 行**逐字节复制**（唯一改动 `getGravity1211()`→`getGravity()`），
   其依赖的 `LibMathUtils.getDirection` 三个重载一并回补（子模块 `d253767`）。
   凡 1.20 主模组调用 `Lib*` 而 1.21 库报缺符号，先怀疑「库侧欠账」而不是主模组写法错。

### 6.9 摘除 gitlink 的前置（已逐项核实）

- 主模组 java 里 **无** `"terra_entity:` 资源命名空间引用；`resources` 侧已清零
  （`5be04ec0a` 语言键、`c287d4184` 资源包与语区尾键）。
- `META-INF/accesstransformer.cfg` 与 `enumextensions.json` **无** terra* 条目；
  `neoforge.mods.toml` 未声明对 TE 的运行期依赖（只有 neoforge/minecraft/curios/geckolib/TLM/create）。
- **其他子模块对 TE 零依赖**：TerraCurio / TerraFurniture / Confluence-Magic-Lib 的构建文件与源码里
  均无 `terraentity` 引用 → 摘除不会连锁破坏。
- ⚠️ **更正**：此前记的「`build.gradle` 无引用」是错的——`ConfluenceOtherworld/build.gradle:10`
  自己**另有一份 `projectName` 列表**（含 `"TerraEntity"`），它驱动 `jarJar(project(":" + name))`。
  因此摘除必须**同批**改两处：`settings.gradle:16` 与 `ConfluenceOtherworld/build.gradle:10`，
  再加 `.gitmodules` 的 `[submodule "TerraEntity"]` 与 gitlink（`git rm --cached TerraEntity`
  + 清理 `.git/modules/TerraEntity`）。
- 连带清理（TE 时代的死代码，已删）：`common/data/Keys.java`（只剩 `MAID_SHOP`，全仓零引用，
  1.20 无此类）见 `149202504`；`mixin/integration/touhoulittlemaid/EntityMaidMixin` 见 `984291192`。
- 收尾后必须做的核验：①全仓 `terraentity` 归零（仅剩两处移植期 `///` 说明注释提到类名：
  `AnglerDialogScreen:21`、`HouseSelectPacketC2S:28`，按注释口径不回溯改）；
  ②`build_errors.py` 全量整编 0 错误——它同时证明"没有任何代码还依赖 TE"。
- **待裁定（决策表 ⑨，第 16 轮查清）**：图鉴进度的存档兼容。`Bestiary` 把进度存成
  `Map<String, BestiaryEntry>`（`CODEC = unboundedMap(Codec.STRING, …)`），键由
  `RegisterBestiaryKeyEvent.getKey(living)` 产出 = **实体描述 id 字符串**（如 `entity.confluence.blue_slime`，
  变体再加 `.variant`）。TE 时代该键是 `entity.terra_entity.<id>` → 退役后同一实体的键变了，
  **带 TE 时代世界的存档其图鉴进度会重新开始**。三种处理：
  ①按 1.20 不动（1.20 无 TE、也没有迁移代码；1.21 分支尚未发布）；
  ②在 `Bestiary.decode` 里加"前缀重写"迁移（`entity.terra_entity.` → `entity.confluence.`，约 5 行）；
  ③像 `registerEntityAliases()`（已有 11 条实体 id 别名）那样统一做别名。
  本次未擅自实现（属 1.21 独有代码，且用户口径是"对齐 1.20"）——**需用户裁定**。
  注：实体 id 本身没变（`confluence:blue_slime` 一直是它），所以战利品表/数据包引用不受影响；
  只有"按描述 id 存的图鉴进度"这一处受影响。
- 1.20 与 1.21 的分歧点（本轮有意保留，需用户裁定）：`ModEnglishProvider` 的两个格式化助手
  在 1.20 是**注释掉的**，1.21 用 TE 助手启用了它；本批改为**内联 TE 的实现**（逐字等价）而非删块，
  以免停止生成月亮相位/默认维度名译名（见提交 `4904bee10`）。



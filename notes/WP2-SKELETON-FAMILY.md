# WP2 骨骼族（12 个 id）+ 共享「原版人形」客户端两件套

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（含 2 个新客户端类，一次通过）。
> 这是注册层普查（`notes/WP2-MONSTER-REGISTRY-GAP.md`）里**杠杆最高**的一批：
> 先落共享渲染器/模型类，再一次性吃完依赖它的整族怪物。

## 一、本批内容（2 新 java 客户端 + 3 改 + 33 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/renderer/VanillaHumanoidRenderer`（新，83 行） | 1.20 同名文件；`GeoNormalRenderer<T>` + `HeldItemLayer`（按 `Vleft_arm`/`Vright_arm` 前缀挂手持物）。适配：`util.RenderUtils` → `util.RenderUtil`（`translateToPivotPoint(PoseStack, GeoBone)` 已对 `geckolib-neoforge-1.21.1-4.8.2.jar` 核过）；`javax.annotation.Nullable` → `org.jetbrains.annotations.Nullable`；`BlockAndItemGeoLayer` 四个覆写点签名与 1.20 **完全一致**（未改） |
| `client/entity/model/VanillaSkeletonGeoModel`（新，84 行） | 1.20 同名文件；复用原版 `HumanoidModel(SKELETON)` 的走路/头部/近战/拉弓姿势。适配：`CoreGeoBone`→`GeoBone`、`RenderUtils`→`RenderUtil`、`AnimationState` 换包；并**直接复用已在 1.21 的 `VanillaHumanoidGeoModel.applyBowPose`**（狼人批落地） |
| `common/init/entity/MonsterEntities` | **+12 条注册** + **`registerSkeleton` 两个重载**（1.20 `:842/846`，逐字）；属性/尺寸/`BehaviorProfile`/`developmentOnly` 全照 1.20 |
| `client/event/ModClientEvents` | **+12 条渲染器注册**（1.20 `:649-659/805`），实参含 `withScale(0.8~1.25F)` 与文化差异逐字保留 |
| `common/entity/SpawnPlacementChecks` | **+2 支谓词**：`checkRoutineMonsterSpawn`（`:303`）、`checkDungeonMonsterSpawn`（`:614`，要求先击败 `BossEntities.SKELETRON`） |
| `common/init/entity/CreatureSpawnPlacements` | +2 条 `group(...)`；`SPORE_SKELETON` 并入地下组、`ARMORED_VIKING`/`UNDEAD_VIKING` 并入困难模式洞穴组、`ARMORED_SKELETON` 并入困难模式洞穴组（与 1.20 的组内成员一致） |
| 资源 33 | 11 个 id × (geo + animation + texture)，与 1.20 逐字节相同 |

## 二、为什么这批「一次通过」

事先做了两张表，把风险全部前置消掉：

1. **共享类复用度统计**（1.20 `registerEntityRenderer` 行）：`VanillaHumanoidRenderer` 20 处、
   `VanillaSkeletonGeoModel` 10 处 —— 说明先做这两个类能把 10~20 个 id 的渲染器一次解决。
2. **逐个 id 的前置核对**：类是否存在（`MeleeSkeleton`/`Decayeder` ✓）、谓词是否已有、
   资源是否在 1.20 磁盘（33/33）、以及**渲染器实际使用的模型 id**（见下条坑）。

## 三、本批查出的两个「看起来像漏拷，其实是设计」的点

1. **`BIG_BONES` 没有自己的模型资源**：1.20 `ModClientEvents:653` 写的是
   `new VanillaSkeletonGeoModel<>(c, MonsterEntities.ANGER_BONES.getId())` —— **它复用 `ANGER_BONES` 的
   geo/贴图/动画**（只是 `withScale(1.1F)` 放大）。所以 `assets/**/big_bones.*` 在 1.20 也不存在，
   拷贝清单里**故意不含它**。
2. **`SPORE_SKELETON` 没有专属类**：1.20 `:495` 用的是 `MeleeSkeleton` + `OPEN_DOORS` 档案，
   不是独立类（我最初的「类缺失」判断是错的，核对注册行后纠正）。

## 四、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（含 2 个新类，一次通过） |
| 渲染器覆盖 | **12/12**（脚本逐条计数） |
| 放置规则覆盖 | **12/12**（脚本逐条计数） |
| `check_duplicates.py`（2 个新类） | 0 处「疑似移动/重复」 |
| 资源 | 33/33 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 918 处引用 / 缺口仍 **18**（= B 类 6 + C 类 12），本批未新增 |
| 待游戏内验收 | 骷髅王的右键开门/跃扑档案、`ANGER_BONES` 系列放大比例、手持武器挂点（`HeldItemLayer`）、地牢骷髅需先击败骷髅王才生成 |

## 五、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠族 `b78cd7d8b` | 9 | 72 |
| 宝箱怪族 `a30777650` | 8 | 80 |
| **骨骼族（本批）** | **12** | **92** |
| 缺口 | — | 150 → **121** 待补 |

## 六、下一批

同一条杠杆还剩两个模型没做：**`VanillaGoblinGeoModel`（1.20 里 6 处复用）+ `VanillaZombieGeoModel`（3 处）**，
两者都只依赖 `VanillaHumanoidGeoModel` 家族与已踩过的 geckolib 改名，落地后即可吃完
「哥布林入侵 6 个 id」+「僵尸族 3 个 id（`ZOMBIE`/`SPORE_ZOMBIE`/`HAT_SPORE_ZOMBIE`）」，
并补 `registerGoblinLand`（1.20 有、1.21 无）。

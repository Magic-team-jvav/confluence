# WP2 蠕虫族（9 组 18 个 id）：飞龙 / 巨型蠕虫 / 挖掘者 / 吞噬者 / 世界吞噬怪 / 骨蛇 / 枯萎骨蛇 / 大飞龙 / 水蛭

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（4 → 0 → 18 → 0：先是 3 个渲染器等注册、后是 `ModClientEvents` 缺 3 个 import）。
> 注册层第 8 批，按 `notes/WP2-MONSTER-REGISTRY-GAP.md` 第十一节的预案执行 —— **3 个客户端渲染器换 18 个 id**。

## 一、本批内容（3 新 java + 4 改 + 50 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/renderer/WormHeadRenderer`（新，39 行） | 普通蠕虫头部；`usesInterpolatedLight` + `getWormModelCenter` 两个钩子（1.21 `GeoNormalRenderer:93/146` 都在）**无差异照搬**；`sharedModelCenter` 按 `GIANT_WORM`/`DIGGER` 与 `DEVOURER`/`WORLD_FEEDER` 返回不同主干中心 |
| `client/entity/renderer/WyvernRenderer`（新，70 行） | 飞龙头部：头部模型同时含三种体节，渲染头部时隐藏 `Bone2..4`；三处 1.21 签名适配（见下） |
| `client/entity/renderer/WormPartRenderer`（新，124 行） | 体节渲染：按家族名推 `_segment`/`_tail` 资源、飞龙按节段角色隐藏 `Bone/Bone2/Bone3/Bone4`、`getPackedOverlay` 用 `isHurtFlashing` |
| `common/init/entity/MonsterEntities` | **+18 条注册**（9 个体节类型 + 9 个主体），复用已在 1.21 的 `registerWormSegment` 与 `registerWorm(name, w, h, Role, Anatomy, segmentType)`；`Wyvern` 单独用 `registerEntity`（构造器吃体节类型） |
| `client/event/ModClientEvents` | **+18 条渲染器注册**（1.20 `:668-687`，其中 `:676-677` 的 `TOMB_CRAWLER` 尚未注册故不接） |
| `common/entity/SpawnPlacementChecks` | **+5 支谓词**：`checkDiggerSpawn`(`:583`)、`checkGiantWormSpawn`(`:591`)、`checkCorruptionWormSpawn`(`:603`)、`checkHighLevelMonsterSpawn`(`:621`)、`checkArchWyvernSpawn`(`:626`) |
| `common/init/entity/CreatureSpawnPlacements` | **+6 条 `group(...)`**（1.20 `:104/145/149/173/174/184/185`），`BONE_SERPENT`/`WITHER_BONE_SERPENT` 并入已有的地狱组 |
| 资源 50 | 9 个家族的 `geo`/`textures`/`animations`（与 1.20 逐字节相同）。**`ARCH_WYVERN` 没有自己的资源**：它用 `wyvernGeometry=true` 复用 `wyvern` 模型/贴图（不是漏拷） |

## 二、1.20 → 1.21.1 的三处签名适配（本仓库已踩过同类）

| 1.20 | 1.21.1 | 落在哪些文件 |
|---|---|---|
| `preRender(..., float r, float g, float b, float a)` | 尾参收成 **`int colour`** | `WormPartRenderer`、`WyvernRenderer` |
| `renderRecursively(..., float r,g,b,a)` | 同上 | `WormPartRenderer`、`WyvernRenderer` |
| `applyRotations(..., float age, float yaw, float partialTick)` 5 参 | 基类调的是 **6 参**版（`GeoNormalRenderer:180-185` 有说明） | 同上（改覆写 6 参并把 `scale` 透传） |

另有一处**语义等价替换**：1.20 的 `PortTags.Biomes.IS_SNOWY`/`IS_ICY` 两个 tag，
1.21 侧合并成已有的 `OverworldUtils.isSnowy(biome)`（`checkDiggerSpawn`/`checkGiantWormSpawn` 各一处，已写进注释）。

## 三、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件** |
| 渲染器覆盖 | **18/18**（9 主体 + 9 体节，脚本逐条计数） |
| 放置规则覆盖 | **6/6 主体**（体节是 `MISC` + `noSave`，1.20 也不登记） |
| `check_duplicates.py`（3 个新渲染器） | 0 处「疑似移动/重复」 |
| 资源 | 50/50 与 1.20 同哈希 |
| `asset_gap.py` 复算 | 923 处引用 / 缺口仍 **18**，未新增 |
| 待游戏内验收 | 飞龙的节段隐藏与翼骨开关、巨型蠕虫/吞噬者的主干中心对齐、骨蛇的地狱生成、大飞龙的春节限定、水蛭的移速 |

## 四、注册层进度

| 批次 | id 数 | `MonsterEntities` 成员数 |
|---|---:|---:|
| 普查起点 | — | 63 |
| 蝙蝠 / 宝箱怪 / 骨骼 / 哥布林+孢子僵尸 / 僵尸族 / 陆行族 / 地下沙漠族 | 56 | 119 |
| **蠕虫族（本批）** | **18** | **137** |
| 缺口 | — | 150 → **76** 待补 |

## 五、下一批候选

1. **宝箱怪/生怪蛋之外的水族**（`ANGLER_FISH`/`WALL_CREEPER`/`SHARK`/`SAND_SHARK`/`ZOMBIE_MERMAN`/`WANDERING_EYE_FISH`）
   —— 多为 `registerEntity` + 已有渲染器，属零新类路线。
2. **食人花族（3 个 id）**：`SnatcherRenderer` + `TetheredPlantRenderer` 两个类。
3. **水母族（3 个 id）**：`JellyFishRenderer` + `EntityGlowingGeoLayer` + `registerJellyFish` + 2 支水谓词。
4. **`GeoNegativeVolumeRenderer` 迁入主模组**（现只在 TerraEntity），解开 `CURSED_SKULL` 等 5 处注册。

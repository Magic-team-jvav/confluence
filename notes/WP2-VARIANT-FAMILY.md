# WP2 双色变种与洞穴散件批（4 个 id）

`GIANT_SHELLY` / `CRAWDAD` / `ANGLER_FISH` / `GRANITE_ELEMENTAL`，一个模型类换 2 个 id，另 2 个复用 TerraEntity 的发光渲染器。

## 本批内容

| 项 | 内容 |
|---|---|
| 新类 | `client/entity/model/VariantTextureGeoModel`（28 行，1.20 逐字）：共用几何与动画、按实体同步的变种状态切换纹理，不引入第二套变种状态 |
| 注册 | `MonsterEntities` +4：`GIANT_SHELLY`（四态）与 `CRAWDAD`（爪击态）用 `registerEntity`；`ANGLER_FISH` 与 `GRANITE_ELEMENTAL` 属性逐字 |
| 辅助 | +`registerJellyFish`（水母族随后要用） |
| 渲染器 | +4：双色两只用新的 `VariantTextureGeoModel`（`purple/yellow`、`blue/red`）；`GRANITE_ELEMENTAL`（发光骨骼 `Core`）与 `ANGLER_FISH`（`light`）用 TerraEntity 提供的 `GeoNegativeVolumeRenderer` |
| 谓词 | +5：`checkWaterMonsterSpawn`、`checkUndergroundWaterMonsterSpawn`、`checkCrimsonWaterMonsterSpawn`、`checkAnglerFishSpawn`、`checkGraniteElementalSpawn`（含 `GRANITE_POPULATION_*` 两个常量） |
| 放置 | +5 条 `group(...)` |
| 资源 | 14 个（含 `giant_shelly/`、`crawdad/` 两个变种贴图目录） |

`checkAnglerFishSpawn` 里 1.20 的 `PortTags.Biomes.IS_JUNGLE`/`IS_LUSH` 换成 NeoForge 的 `Tags.Biomes.IS_JUNGLE`/`IS_LUSH`（同名同义）。

## 验证

| 项 | 结果 |
|---|---|
| `build_errors.py --maxerrs 2000` | 0 错误 / 0 文件（8 → 2 → 0；期间修掉一处把方法声明与注释挤到同一行的换行事故） |
| 渲染器覆盖 | 4/4 |
| 放置覆盖 | 4/4 |
| `check_duplicates.py` | 0 处「疑似移动/重复」 |
| 资源 | 14/14 与 1.20 同哈希 |
| `asset_gap.py` | 930 处引用 / 缺口仍 18，未新增 |

`MonsterEntities` 147 → **153**；注册层缺口 150 → **60**。

## 注释

新代码只保留 1.20 的注释原文（`VariantTextureGeoModel` 的两段、各谓词的一行说明），未再加溯源与说明性内容。

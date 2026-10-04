# WP2 散件批（10 个 id）：花岗岩洞 / 丛林 / 冰雪 / 地下沙漠 / 猩红 / 海洋 / 地牢 / 陨石 / 墓地 / 血月

按 `notes/WP2-MONSTER-REGISTRY-GAP.md` 的「零新类」路线挑出一批**谓词与渲染器都已就绪**的散件，
一次吃掉 10 个 id：`GRANITE_GOLEM` / `DERPLING` / `ICE_TORTOISE` / `SAND_POACHER` / `HERPLING` /
`METEOR_HEAD` / `GHOST` / `DRIPPLER` / `SHARK` / `CURSED_SKULL`。

## 本批内容

| 项 | 内容 |
|---|---|
| 注册 | `MonsterEntities` +10 条，属性/状态机逐字照 1.20（`GraniteGolem` 四态、`GiantTortoise` 五态、`SandPoacher` 攀爬态） |
| 渲染器 | `ModClientEvents` +10 条，全部用已在 1.21 的类；`CURSED_SKULL` 用 TerraEntity 提供的 `GeoNegativeVolumeRenderer` + `addBoneToGlow("outline")` |
| 放置 | +10 条 `group(...)`，谓词**全部复用已有**（`checkGhostSpawn`/`checkRoutineMonsterSpawn`/`checkCaveMonsterSpawn`/`checkSurfaceWaterMonsterSpawn`/`checkSurfaceDayMobSpawn`/`checkPureOrHallowDesertUndergroundSpawn`/`checkDungeonMonsterSpawn`/`checkGroundSpawn`） |
| 资源 | 30 个（9 个 id × geo/animation/texture）+ `giant_tortoise` 的 geo/animation |

`ICE_TORTOISE` **复用 `giant_tortoise` 的模型与动画**，自己只有贴图 —— 1.20 就是这么写的，
所以 `geo/entity/ice_tortoise.geo.json` 与 `animations/entity/ice_tortoise.animation.json` 在 1.20 本来就不存在。

## 验证

| 项 | 结果 |
|---|---|
| `build_errors.py --maxerrs 2000` | 0 错误 / 0 文件（5 → 2 → 0，缺 `SandPoacher`/`CursedSkull` import） |
| 渲染器覆盖 | 10/10 |
| 放置覆盖 | 10/10 |
| 资源 | 30/30 与 1.20 同哈希 |
| `asset_gap.py` | 926 处引用 / 缺口仍 18，未新增 |

`MonsterEntities` 137 → **147**（本次会话累计 63 → 147），注册层缺口 150 → **66**。

## 注释口径（用户本轮明确）

源码注释**照抄 1.20**，1.20 没有就不加；不写溯源行号、不写「与某批同一处理」、不写长文档块。
本批已按此执行（只保留 1.20 的分段注释，且逐条核对了 1.20 的原文标签）。

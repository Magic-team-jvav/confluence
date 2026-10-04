# WP2 蝙蝠族（9 个 id）：注册层第一批落地

> 编译门 `ConfluenceOtherworld` **0 错误 / 0 文件**（一次通过，无需返工）。
> 这是 `notes/WP2-MONSTER-REGISTRY-GAP.md` 普查后按其中「推荐下一批」执行的**第一个注册层批次**：
> 目标不是移植类，而是把**早已存在于 1.21、却拿不到 `EntityType`** 的怪物真正接进游戏。

## 一、本批内容（1 新 java + 4 改 + 25 资源）

| 文件 | 改动 |
|---|---|
| `client/entity/renderer/BatRenderer`（新） | 1.20 同名文件逐字移植（27 行）：`GeoNormalRenderer<CaveBat>` + 匿名 `GeoNormalModel` 覆盖 `getAnimationResource`（统一指向 `cave_bat.animation.json`）+ `luminous` 时 `getBlockLightLevel` 返回 15 |
| `common/init/entity/MonsterEntities` | **+9 条注册**（`CAVE_BAT`/`GIANT_BAT`/`JUNGLE_BAT`/`GIANT_FLYING_FOX`/`ICE_BAT`/`ILLUMINANT_BAT`/`SPORE_BAT`/`HELL_BAT`/`LAVA_BAT`），属性、尺寸、`developmentOnly`、`fireImmune` 全部照 1.20 `:94/96/214/216/276/459/497/585/587` 逐字 |
| `client/event/ModClientEvents` | **+9 条渲染器注册**（1.20 `:784-792`）：7 条 `GeoNormalRenderer<CaveBat>` + 2 条 `BatRenderer`（熔岩/荧光），其中巨型飞狐用显式 `GeoNormalModel`（与 1.20 同） |
| `common/entity/SpawnPlacementChecks` | **+5 支谓词**：`checkUndergroundMonsterSpawn`(`:476`)、`checkCaveMonsterSpawn`(`:557`)、`checkNetherMonsterSpawn`(`:647`)、`checkPostMechanicalNetherSpawn`(`:652`)、`checkSurfaceNightMonsterSpawn`(`:765`) |
| `common/init/entity/CreatureSpawnPlacements` | **+7 条 `group(...)`**（1.20 `:101/:137/:139/:148/:149/:176/:180/:190/:223` 的**增量版**：每组只登记蝙蝠，1.20 同组的其它物种随各自批次补） |
| 资源 25 | `geo/entity/*.geo.json` 9、`animations/entity/*.animation.json` 7、`textures/entity/*.png` 9 —— 与 1.20 逐字节相同 |

## 二、为什么这批的价值不在「代码量」

`notes/WP2-MONSTER-REGISTRY-GAP.md` 的结论：`MonsterEntities` 1.20/1.21 = **213/63**，
而 `common/entity/**` 的类文件只差 10 个 —— 即一百多个怪物的类**早就在 1.21、能编译、能 `new`**，
但没有 `EntityType`，在游戏里**连 `/summon` 都不行**。本批把其中 9 个一次接完，并且遵守「可见性 + 生成」两条纪律：

| 纪律 | 本批如何满足 |
|---|---|
| 注册必须有渲染器（否则「隐形实体」） | 9/9 都有渲染器（已用脚本逐条核对 = 1 条） |
| 注册必须有放置规则（否则只能 `/summon`） | 7 条 `group(...)` 覆盖 9 个 id，谓词同批落地 |
| 谓词依赖 | 5 支新谓词只依赖本会话已落地的 `checkMonsterSpawnRules` / `checkGroundSpawn` + 既有的 `OverworldUtils` / `KillBoard` |

## 三、顺带踩到的两个坑（写进记录，避免下次重复）

1. **`giant_flying_fox` 不含「bat」子串** —— 按名字 `*bat*` 过滤找资源时漏掉了它的 geo/动画/贴图三件套；
   补拷后 `asset_gap.py` 复算才确认无遗漏。**教训：资源清点要用「代码里的 `asResource` 路径」做基准，
   不要用名字模糊匹配。**（`asset_gap.py` 正是这个口径，本批收尾复算：引用 914 处、缺口仍为 18 处 = B 类 6 + C 类 12）
2. **困难模式组要按 1.20 的组内组合还原**：`GIANT_BAT` 在 1.20 与 `ARMORED_SKELETON` 同组、
   `ILLUMINANT_BAT` 与 `ARMORED_VIKING`/`ICY_MERMAN` 同组 —— 1.21 这几个还没注册，
   故本批只登记蝙蝠并在注释里注明「其余随各自批次补」，避免出现「引用未注册成员」的编译错。
   ⚠️ 副作用：`GIANT_BAT` 与 `ILLUMINANT_BAT` 现在各占一条 `group`，等那些物种落地时**要合并回 1.20 的组合**
   （已在注释里写明具体成员）。

## 四、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --maxerrs 2000` | **0 错误 / 0 文件**（一次通过） |
| `check_duplicates.py`（`BatRenderer`） | 0 处「疑似移动/重复」 |
| 渲染器覆盖 | 9/9（脚本逐条计数） |
| 资源哈希 | 25/25 与 1.20 相同（含补拷的 fox 三件套） |
| `asset_gap.py` 复算 | 缺口仍为 **18**（= B 类 6 + C 类 12），本批未新增任何「引用得到、文件不存在」 |
| 待游戏内验收 | 洞穴/丛林/冰雪/地狱层蝙蝠生成；荧光蝙蝠自发光；熔岩蝙蝠「机械 Boss 后 + 地狱」门槛；巨型飞狐困难模式地表夜晚；巨型蝙蝠困难模式洞穴 |

## 五、下一批

按普查表继续吃「同段一体」的组，优先级与前置都已列在 `notes/WP2-MONSTER-REGISTRY-GAP.md` 第三节。
下一个最省事的是**水母族**（`BLUE_JELLYFISH`/`GREEN_JELLYFISH`/`PINK_JELLYFISH`，3 个 id）或
**宝箱怪族**（7 个 id，但要确认 `Mimic` 系列的类都在 1.21）；纯注册批次现在已证明「一次通过」，
因此可以适当放大单批规模。

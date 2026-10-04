# WP3 切片批（史莱姆王完整落地 + 克苏鲁之眼「先加不接线」）：落地记录

> 承接 `notes/WP2-REMAINDER-GATE.md` 第四节建议的「38 文件 WP3 切片」。
> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**。规模：**7 新 java + 8 改 java + 4 资源**（19 文件 / +1672 −12）。
> 子模块未改动。

## 一、重新测量：38 → 5（但客户端另有一道墙）

```powershell
python tools/port2native/dep_subset.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities `
  --seed …/boss/EyeOfCthulhu.java --seed …/boss/KingSlime.java `
  --seed …/boss/ServantOfCthulhu.java --seed …/boss/BossMultiplayerEnhancement.java
# {"candidates": 5, "kept": 5, "new": 5, "removed": 0}    ← 服务端只有 5 个文件
```

服务端很便宜，**客户端不便宜**：三个 boss 的渲染器：

| 渲染器 | 闭包 | 依赖 |
|---|---:|---|
| `KingSlimeRenderer` | **3** | 只用 `BaseSlimeModel`/`BaseSlimeOuterLayer`/`CrownOfKingSlimeModel` —— 史莱姆批刚落地的资产，**无额外依赖** |
| `EyeOfCthulhuRenderer` | 14 | 继承 1.20 自有的 `BossGeoRenderer` → `GeoNormalRenderer` → **`BaseWormBoss`**（属蠕虫 boss 族）+ `EntityLightSampler` + `GeoNormalModel` + **整类未迁的 `client/effect/RenderStateShardAccessor`** |
| `CrownOfKingSlimeModelRenderer` | 3 | 只用王冠模型 |

→ **批次据此切分**：史莱姆王（含客户端）本批完整落地；克苏鲁之眼「先加实体、不接线」。

## 二、本批内容（7 新 / 8 改 / 4 资源）

### 2.1 新增（7 java，全部 1.20 同名文件）

| 文件 | 来源 |
|---|---|
| `common/entity/boss/KingSlime`（556 行） | 同名 |
| `common/entity/model/CrownOfKingSlimeModelEntity` | 同名（王冠模型载体，`KingSlime:230` 生成） |
| `client/entity/renderer/KingSlimeRenderer` | 同名 |
| `client/entity/model/CrownOfKingSlimeModel` | 同名 |
| `client/entity/renderer/CrownOfKingSlimeModelRenderer` | 同名 |
| `common/entity/boss/EyeOfCthulhu`（~420 行） | 同名（**本批不接线**，见 §四） |
| `common/entity/boss/ServantOfCthulhu` | 同名（同上） |

### 2.2 改动（8 java）

| 文件 | 要点 |
|---|---|
| `common/init/entity/BossEntities` | +5 成员：`KING_SLIME`、`CROWN_OF_KING_SLIME_MODEL`、`EYE_OF_CTHULHU`、`SERVANT_OF_CTHULHU`、`DEERCLOPS`（1.20 `BossEntities:24/28/31/40/86`）；并把 `registerEntity` 的泛型上界 **`Mob` → `Entity`**（王冠模型实体不是 `Mob`；1.20 原文即 `<T extends Entity>`） |
| `common/CommonConfigs` | +`KING_SLIME_LARGE_MINIONS`（1.20 `kingSlimeLargeMinions`，默认 `false`）——**成员级盲区**：`KingSlime:381/410` 读它，1.21 此前没有该配置项 |
| `common/gameevent/SlimeRainGameEvent` | `TEBossEntities.KING_SLIME` → `BossEntities.KING_SLIME`（5 处） |
| `common/util/ModUtils` | 同上（1 处，`summonBoss` 的史莱姆雨粘性判定） |
| `common/data/gen/ModClientBestiaryEntryProvider` | 图鉴条目改指（1 处） |
| `common/data/gen/data_map/LivingInvulnerableEffectsSubProvider` | 免疫效果表改指（1 处） |
| `common/data/gen/data_map/TreasureBagSubProvider` | 宝藏袋改指（1 处） |
| `client/event/ModClientEvents` | +1 条层定义（`CrownOfKingSlimeModel.LAYER_LOCATION`，1.20 `:318`）+ 2 条实体渲染器（`KING_SLIME`、`CROWN_OF_KING_SLIME_MODEL`，1.20 `:710/711`） |

**史莱姆王的改指共 9 处**（`SlimeRainGameEvent` ×5、`ModUtils` ×1、图鉴/免疫/宝藏袋各 ×1）——改指后史莱姆雨召出的就是主模组 boss，且**渲染器同批落地**，因此**没有「看不见的 boss」这个中间态**。

### 2.3 资源（4 张）

`textures/entity/boss_bar/king_slime_bar_{1,2}.png`、`textures/gui/king_slime_bar.png`、`textures/item/egg/king_slime_spawn_egg.png`
（王冠贴图 `textures/entity/model/crown_of_king_slime.png` 与 `slime_king.png` 1.21 侧本就有。）

## 三、1.20 → 1.21 的 API 差异（本批实测，均已在文件内注明出处）

| 1.20 写法 | 1.21.1 实际 | 处理 |
|---|---|---|
| `Entity#setMaxUpStep(float)` | **已删除**（`maxUpStep()` 只剩 getter）；台阶高度由属性 `Attributes.STEP_HEIGHT` 表达 | `KingSlime` 构造里 `getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0F)` —— 与同仓库先例 `DeerClops.java:83-87`、`Snail.java:42-44` 完全同一写法 |
| `EntityDimensions.height`（public 字段） | **record**（`EntityDimensions.java:6`）→ 访问器 `.height()` | `KingSlime:230`、`KingSlimeRenderer:40` |
| `LivingEntity#getDimensions(Pose)` 可覆写 | **final**（内部 `getDefaultDimensions(pose).scale(getScale())`） | `KingSlime` 改覆写 `getDefaultDimensions` 并去掉原作的 `getScale()`（同 `BaseSlime.java:166`） |
| `Model#renderToBuffer(…, float r,g,b,a)` | `…, int color)`（`Model.java:23`） | `CrownOfKingSlimeModel`、`KingSlimeRenderer`、`CrownOfKingSlimeModelRenderer`（传 `-1` = 全 1） |
| `LibAttributes.getArmorPenetration().get()` | `LibAttributes.getArmorPenetration()` | `BossEntities` 新增成员 |

## 四、**本批不接线**的部分（附「为什么不接线」）

| 项 | 原因 |
|---|---|
| 克苏鲁之眼 / 克苏鲁之仆的 **8 处消费点**（`BossDelaySpawner` ×2、`BloodMoonGameEvent`、`BasePotBlock`、`MoneyTradeHealthFull`、图鉴、免疫表、宝藏袋）**仍指向 `TEBossEntities`** | 它们的客户端渲染器要 `BossGeoRenderer` 全族（§一）。**若本批就改指，boss 会在世界里隐形** —— 这是可见回归。故本批只把实体与注册加上（「先加」），改指留给客户端批次（「后切换」） |
| `BossDelaySpawner` 的类型对齐（`AbstractTerraBossBase` → 主模组 `BaseBoss`）与 `DEERCLOPS` 改指 | 同上：该文件的两个 boss（眼、独眼巨鹿）都还没到切换时机。1.20 该文件用的是主模组 `BaseBoss`（见 `notes/` 该批注），属客户端批次一起做 |
| `BossMultiplayerEnhancement`（21 处编译错） | 1.20 版是 PortLib 形状：`AttributeModifier.rl2uuid(...)` + **UUID** 作 id + `getModifier(uuid)`/`getId()`/`getName()`；1.21.1 的 `AttributeModifier` 是 **record**（`ResourceLocation id, double amount, Operation`），`getAttribute` 吃 `Holder<Attribute>`。另：1.21 侧**当前没有任何文件引用它**（`EntityEvents`/`LivingEntityEvents` 未接该调用），故不影响本批 |
| 克苏鲁之眼的完整客户端族（`BossGeoRenderer`+`GeoNormalRenderer`+`GeoNormalModel`+`EntityLightSampler`+`RenderStateShardAccessor`）+ **蠕虫 boss 族**（`BaseWormBoss`/`BossWormPart`/`EaterOfWorlds`/`TheDestroyer`/`TheDestroyerProbe`） | 这是**下一批**：`BossWormPart` 直接 `instanceof EaterOfWorlds/TheDestroyer`，是紧耦合整体；做完它同时解锁**史莱姆族的特效渲染尾巴**（`GeoSpecialSlimeRenderer`/`TownSlimeRenderer`） |
| 刷怪蛋 / `ModTabs` / 掉落表 / 实体 lang / bestiary desc | 随 WP7 datagen |
| `CrownOfKingSlimeModelEntity` 的 `height` 字段 | 客户端渲染器读 `entity.height`（实体自己的字段），非 `EntityDimensions`，无需改 |

## 五、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（四轮：26 → 4 → 2 → 0，全部错在 staged 文件与新增成员引用上） |
| `check_duplicates.py`（7 个新增） | 0 处「疑似移动/重复」；6 处 DIFF/NEAR 全部是对 **TerraEntity** 的同名 boss/模型（`terraentity.entity.boss.{KingSlime,EyeOfCthulhu}`、`terraentity.client...`）—— 既定的「先加后删」，TE 退役另立批次 |
| 改指残留 | `BossEntities.KING_SLIME` 9 处（已接）；`BossEntities.{EYE_OF_CTHULHU,SERVANT_OF_CTHULHU,DEERCLOPS}` 在注册层之外 **0 处**（有意未接） |
| 子模块 | 未改动 |
| 待游戏内验收 | 史莱姆雨刷出的史莱姆王外观/王冠/分裂、击杀记入 `KillBoard`、宝藏袋与图鉴条目指向新实体；**克苏鲁之眼仍走 TE 旧实体**（回归零） |

## 六、踩坑记录（写给下一个执行者）

**不要用 PowerShell 读写 UTF-8 源码**：本批我一度用 `Get-Content -Raw` + `[IO.File]::WriteAllText($f, $t, UTF8)` 批量改 `BossDelaySpawner`，PowerShell 以系统默认编码（GBK）读入 → 中文注释整段变乱码，且**乱码把换行吞掉导致两行合并**（javadoc 与 `pushBoss` 签名并成一行）。
处理：`git checkout --` 回退该文件，改用 **Python（显式 `encoding="utf-8"`）** 重做；`java`/`json` 的批量改写一律走 Python 或 `edit` 工具。
（`fix_eol.py` 仍然照跑，它只关心行尾。）

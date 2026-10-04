# WP3 客户端族批·**服务端半**：蠕虫 Boss 族落地

> 编译门：`ConfluenceOtherworld` **0 错误 / 0 文件**、`Confluence-Magic-Lib` **0 错误 / 0 文件**。
> 规模：根仓库 **5 新 + 3 改**（9 项 / +2645 −1）+ 子模块 Lib `a762fe6`。
>
> 这是 `notes/WP3-CLIENTFAMILY-HANDOFF.md`（15 文件 / 42 处编译错）切出来的**服务端半**。
> 交接单剩余 22 处里的 **17 处**（`BossWormPart` 4 + `EaterOfWorlds` 8 + `TheDestroyer` 1 + 依赖）在本批清零，
> 只剩 **客户端半**（5 处 + 1 处 warning）。

## 一、子模块先做：Lib `interpolateBasis` 家族（`a762fe6`）

`TheDestroyer:228` 用 `LibMathUtils.interpolateBasis(...)` 算追踪弹道的转向与加速，而
**1.21 的 Lib 此前这四个方法都不存在**。按「子模块提交先于根仓库」的顺序，先补：

| 方法 | 1.20 位置 | 说明 |
|---|---|---|
| `interpolateBasis(Vec3, Vec3, ToDoubleFunction, ToDoubleFunction)` | `:385` | 追踪弹道核心（旋转+缩放插值），依赖 `vectorProjection` |
| `getLerp(double)` | `:451` | 线性插值器工厂 |
| `getThresholdInterpolator(double)` | `:465` | 阈值式插值器工厂 |
| `vectorProjection(Vec3, Vec3)` | `:474` | 向量投影 |

四者**实现与 javadoc 逐字搬入**，仅新增 `import java.util.function.ToDoubleFunction;`。

## 二、根仓库：蠕虫 Boss 族（5 新 + 3 改）

### 2.1 新增（5，全部 1.20 同名文件）

| 文件 | 规模 |
|---|---:|
| `common/entity/boss/BaseWormBoss` | 蠕虫 boss 基类 |
| `common/entity/boss/BossWormPart` | 体节 |
| `common/entity/boss/EaterOfWorlds` | 世界吞噬者（≈1030 行） |
| `common/entity/boss/TheDestroyer` | 毁灭者 |
| `common/entity/boss/TheDestroyerProbe` | 探测怪 |

### 2.2 改动（3）

| 文件 | 要点 |
|---|---|
| `common/init/entity/BossEntities` | +5 成员（1.20 `BossEntities:44/48/116/120/122`）：`EATER_OF_WORLDS`、`EATER_OF_WORLDS_SEGMENT`（**注册 id 是 `boss_worm_segment`**，不是 `eater_of_worlds_segment`）、`THE_DESTROYER`、`THE_DESTROYER_PART`、`THE_DESTROYER_PROBE` |
| `common/init/ModEntities` | +`DESTROYER_LASER`（1.20 `common/init/entity/ModEntities:250`）——`DestroyerLaserProjectile` 类在 1.21 早已存在，**注册条目一直缺**（成员级盲区） |
| `common/init/entity/MonsterEntities` | +`EATER_OF_SOULS`（1.20「腐化：噬魂怪与腐化者」段）——**成员级盲区**：`EaterOfSouls` 类与猩红对应物 `CRIMERA` 都在，唯独本体没注册；`EaterOfWorlds:438` 的体节再生要用它 |

### 2.3 1.20 → 1.21 的 API 差异（本批实测，均已写进代码注释）

| 1.20 写法 | 1.21.1 实际 | 处理 |
|---|---|---|
| `Entity.lerpTo(…, int steps, boolean teleport)` | 去掉了 `boolean teleport`（`Entity.java:2202`） | `BossWormPart` / `EaterOfWorlds` 各 1 处：删形参、删分支 |
| `Entity.getAddEntityPacket()` | `getAddEntityPacket(ServerEntity entity)`（`Entity.java:3428`）；`ClientboundAddEntityPacket(Entity, ServerEntity)` | `BossWormPart` 改写 |
| `LivingEntity.getDimensions(Pose)` 可覆写 | **final**（内部 `getDefaultDimensions(pose).scale(getScale())`） | `BossWormPart` 改覆写 `getDefaultDimensions`，并**先除掉自身 scale** 再加 `ownerScale` —— 与同仓库先例 `BaseLivingBossPart.java:251-256` 逐字同法 |
| `CombatRules.getDamageAfterAbsorb(float, float, float)` | `(LivingEntity, float damage, DamageSource, float armorValue, float armorToughness)` | `EaterOfWorlds:212` 传 `segment, amount, source, armor, 0.0F` |
| `LootContextParams.{KILLER_ENTITY,DIRECT_KILLER_ENTITY}` | 改名为 `ATTACKING_ENTITY` / `DIRECT_ATTACKING_ENTITY` | `EaterOfWorlds:254/255` |
| `server.getLootData().getLootTable(...)` | `server.reloadableRegistries().getLootTable(...)` | `EaterOfWorlds:259` |
| `segmentAttribute(Attribute)` | `getBaseValue`/`getAttribute` 只吃 `Holder<Attribute>` | 形参改 `Holder<Attribute>` |

## 三、为什么这批「只注册不接线」是安全的

5 个实体都注册进 `BossEntities`，但**它们的生成路径与渲染器都在客户端半**：

* 1.21 侧此前由 TE 的 `TEBossEntities.{EATER_OF_WORLDS,THE_DESTROYER,...}` 承担这些 boss（`--defer` 之外无引用），
  本批**没有**改指任何生成点 → 世界里出现的仍是 TE 旧实体，**零回归**；
* 新实体只能通过命令/刷怪蛋出现 —— 与克苏鲁之眼的「先加不接线」同一口径
  （见 `notes/WP3-EYE-KING-SUBSET.md` 第四节）。

## 四、验证

| 项 | 结果 |
|---|---|
| `build_errors.py --module ConfluenceOtherworld --repo . --maxerrs 2000` | **0 错误 / 0 文件**（一轮 11 处 → 0） |
| `build_errors.py --module Confluence-Magic-Lib --repo . --maxerrs 2000` | **0 错误 / 0 文件** |
| `check_duplicates.py`（5 新增） | 0 处「疑似移动/重复」；3 处 DIFF 均对 **TerraEntity** 同名 boss（`terraentity.entity.boss.{EaterOfWorlds,thedestroyer.*}`）—— 既定「先加后删」，TE 退役另立批次 |
| 子模块 | Lib `a762fe6`（gitlink 已随本批更新） |
| 待游戏内验收 | 命令生成这三个 boss：体节连接/再生、吞噬者体节护甲与战利品、毁灭者激光与探测怪 —— **注意本批暂无可视渲染**（渲染器在客户端半） |

## 五、交接单剩余（客户端半）

`notes/WP3-CLIENTFAMILY-HANDOFF.md` 现在只剩：

* `GeoNormalModel`（3）、`GeoNormalRenderer`（4）、`MissingModelRenderer`（1）+ 1 处 deprecation；
* 客户端族余下文件：`WormPartGeoModel`、`BossGeoRenderer`、`BossWormPartRenderer`、`EntityLightSampler`、
  `EyeOfCthulhuRenderer`（自身 0 错，但依赖上面的）；
* §4 配套：5 条渲染器注册（1.20 `ModClientEvents:714/715/716/723/724` + 目 `:712/713`）、
  克苏鲁之眼 8 处改指 + `BossDelaySpawner` 类型对齐、`CLUMSY_BALLOON_SLIME` 的 `MissingModelRenderer` 注册、
  `GeoSpecialSlimeRenderer`/`TownSlimeRenderer`/`DivaSlimeVertexConsumer`（史莱姆尾巴）、worm/eye 的 geo 与贴图资源。

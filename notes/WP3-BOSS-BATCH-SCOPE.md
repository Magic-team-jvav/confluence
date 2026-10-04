# BOSS 批（WP3 剩余 19 个 Boss）实测与执行方案

> ## 🔁 第 2 轮（2026-09-29，第 22 轮）状态：已重放流水线，正在清零编译错误
>
> 上一轮（下面第一~五节）把错误压到 94 后因上下文耗尽**回退**。本轮改用一份
> **合并流水线**（`%TEMP%\port2native\boss_batch.py`）重放，并修掉两个坑：
>
> 1. **坑 A（新发现，代价最大）**：`systemic` 的替换表（`.color(`→`.setColor(`、
>    `RenderUtils.`→`RenderUtil.`、`.vertex(`→`.addVertex(` 等）一旦**按目录**作用于
>    「新文件 + 同目录下的既有文件」，就会误伤既有代码（例如把 1.21 的
>    `LibRenderUtils.isBlendEnabled()` 改成 `LibRenderUtil.…`，把 `target.setColor()` 改坏）。
>    本轮对 94 个**已跟踪文件**造成了这种误伤，已用 `git checkout -- .` 全部回退，
>    随后只重新应用「4 个接线文件」的增量改动（`ModEntities` 14 条、`BossEntities` 19 条、
>    `MonsterEntities` 4 条、`ModClientEvents` 22 行渲染器注册）。
>    **教训：替换只能作用于本轮新拷入的文件集合，不能按目录扫。**
> 2. **坑 B（第五节那个坑的准确版本）**：注册层插入点既不能是「最后一条 `public static final` 之后」，
>    也不能是「第一条 `private static`/`public static` 之前」——因为 `ENTITIES` 本身就是
>    `public static final DeferredRegister<...> ENTITIES = ...`，插到它前面会让所有
>    `ENTITIES.register(...)` 触发 **illegal forward reference**（本轮 14+19+4 条全中）。
>    **正确插入点 = 第一个「方法签名行」（`^    (public|private|protected) … \w+\s*\(`、
>    且 `(` 之前不出现 `=`）之前。** 修正脚本：`%TEMP%\port2native\fix_insert_points.py`。
>
> **结果：编译门 0 错误 / 0 文件**（3 轮收敛 77 → 7 → 0，另做了一次
> `--rerun-tasks --no-build-cache` 的强制全量编译复核：`BUILD SUCCESSFUL in 2m26s`，0 错误 0 警告）。
>
> 本轮实修（24 个新文件的 API 适配 + 3 处注册层）：
>
> | 类别 | 内容 |
> |---|---|
> | PortLib/Forge 残留 | `ProjectileImpactEvent.onProjectileImpact` → `NeoForge.EVENT_BUS.post(new ProjectileImpactEvent(...)).isCanceled()`（照抄已移植的 `StraightMonsterProjectile:126`）；`PortMobEffect` → 原生 `MobEffect`（`applyEffectTick` 返回 `boolean`）；`ForgeEventFactory.onFinalizeSpawn` → `EventHooks.finalizeMobSpawn`（6 参版在 21.1.219 不存在）；`WallOfFlesh` 的 `IEntityAdditionalSpawnData`+`NetworkHooks`+`PlayMessages.createClient` → NeoForge `IEntityWithComplexSpawn`（`write/readSpawnData(RegistryFriendlyByteBuf)`），`BossEntities` 里去掉 `.setCustomClientFactory(...)` |
> | `defineSynchedData` | 补 `SynchedEntityData` import 2 处；部件类的钩子 1.21 是 `definePartSynchedData(SynchedEntityData.Builder)`（`BaseBossPart:235`），5 个部件类改签名 |
> | 效果层 | `ModEffects` 补 `THE_TONGUE`/`HORRIFIED`/`CRIMSON_STORM`（注册名照抄 1.20），新增原生 `TheTongueEffect`/`CrimsonStormEffect`；`SCARED` **故意不补**（1.21 由子模块 `TEEffects.SCARED` 提供，本类再注册一份就是死代码），登记为待决策项 |
> | geckolib 4.8.4 | 5 个渲染器改覆写 6 参 `applyRotations`；`renderRecursively` 的颜色尾参收成 `int colour`；`FastColor` 打包 |
> | 1.21 渲染 API | `.normal`→`.setNormal`（含 `setNormal(Pose,…)` 形态）；`TwinEyeDissolveRenderer` 的 13 参 `addVertex`；`TheTwinsRenderer` 的三个私有 helper 改 `PoseStack.Pose`（并补回机械移植时丢掉的 rgba 透传） |
> | 其他 API | `CombatRules.getDamageAfterAbsorb` 5 参；`Mob#finalizeSpawn` 4 参；`EntityDimensions#width()`；`AABB(BlockPos,BlockPos)` → `Vec3` 形态；`getDefaultDimensions(Pose)` 只属于 `LivingEntity`，3 个 `Entity` 派生的部件类改覆写 `getDimensions(Pose)`；效果 API 要 `Holder<MobEffect>`（去掉 `DeferredHolder#get()`）；`DeltaTracker` 取 partial tick |
>
> **资源**：另补 42 个文件（`animations/entity/boss` 9、`geo/entity/boss` 10、`geo/entity/proj` 6、
> `textures/entity/boss` 14、`cluckshroom` 2、`chat_bubble.png`）。音效经核对：新文件引用的 11 个
> `ModSoundEvents` 成员都存在且 `sounds.json` 有条目。
>
> **已知偏差（后续批次处理）**：`WallOfFleshMouth` 的 `CombatRules` 只能传 `getParent()`（1.20 的 3 参版
> 没有武器附魔分支）；`WallOfFlesh` 的生成数据在 NeoForge 下晚于客户端实体入场，首帧可能短暂没有眼/嘴部件。


用户 2026-09-27 要求「BOSS 直接一次性移植过来」。本轮**按整批方式实测了两遍**，
把错误从 **360 压到 94** 后因**上下文预算耗尽**回退（工作树保持绿）。
本文件记录收敛数据、剩余分类与可复用工具，下一批照此清零即可。

## 一、规模实测

| 目录 | 1.21 缺失文件 | 行数 |
|---|---:|---:|
| `common/entity/boss/**` | 25 | 7179 |
| `client/entity/renderer/**` | 25 | 2101 |
| `client/entity/model/**` | 5 | 188 |
| 合计 | **55** | **≈ 9468** |

25 个 boss 类里有 6 组紧耦合小家族：`WallOfFlesh`(+Part/Eye/Mouth/HillOfFlesh/HillOfFleshEye/HillOfFleshMouth)、
`BrainOfCthulhu`(+BrainFake)、`TheTwins`(+AbstractTwinEye/Retinazer/Spazmatism)、`SkeletronPrime`(+Arm)、
`Plantera`(+Hook/Tentacle)、`PrimeEnderDragon`(+Part)、`LunaticCultist`(+Clone)、`PhantasmDragon`、`QueenBee`、
`DungeonGuardian`、`MechanicalMayhemTracker`。`BossEntities` 需补 **19 个成员**。

> ✅ **没有被卡住的物种**：原本被挡的 4 个 id（`DUNGEON_SPIRIT`/`VISUAL_NEURON`/`THE_HUNGRY`/`HILL_HUNGRY`）
> 在本轮里已能和 Boss 一起编译，剩下的 94 处**全是通用 API 差异**，与「谁依赖谁」无关。

## 二、错误收敛曲线（两轮实测）

| 步骤 | 错误数 |
|---|---:|
| 55 个类按固定替换表拷入（`bulk_port.py copy-classes`） | 360 |
| + 系统性替换（`PartEntity` 包移动、`core.object`→`animation`、颜色尾参收成 `int colour`、`getDimensions`→`getDefaultDimensions`） | 212 |
| + 补回被依赖的类（`TheHungry`/`HillHungry`/`VisualNeuron`/`DungeonSpirit`/`HungryMovementAction`/`MysticFrog`；`AncientLightProjectile`/`PrimeCannonballProjectile`/`HorrifiedEffect`/`BrainDissolveTexture`） | 180 |
| + 补 `ModEntities` 的 14 个成员（`ANCIENT_LIGHT`/`CULTIST_*`/`HILL_*`/`PLANTERA_*`/`PRIME_*`/`RETINAZER_LASER`/`SPAZMATISM_FLAME`/`WALL_OF_FLESH_LASER`） | 159 |
| + `Color`→`util.Color`、`RenderUtils`→`RenderUtil`、`CoreGeoBone`→`GeoBone`、`.uv(`→`.setUv(` | 145 |
| + `defineSynchedData(SynchedEntityData.Builder)` 改写（含 CRLF 宽容地清掉遗留的 `super.defineSynchedData();`，共 75 处） | 116 |
| + `BossEntities` 19 个成员注册（并恢复被脚本误删的 `import static …withAttributes`） | 103 |
| + 再补 `MonsterEntities` 的 `THE_TONGUE` 等成员 + PortLib 残留映射 | **94** |

## 三、剩余 94 处的分类（下一批的清单）

| 类别 | 约数 | 处理方式 |
|---|---:|---|
| `builder`：`defineSynchedData` 的签名/继承链写法与脚本匹配的模式不同 | 7 | 逐个核对父类并统一成 `defineSynchedData(SynchedEntityData.Builder builder)` |
| `LibUtils.getRandom1211`（Lib 子模块缺该方法） | 4 | **子模块先提交**再加回 |
| `PoseStack.Pose#normal()` 调用点 | 4 | 改 `pose.normal()` / `setNormal(pose, …)` |
| `MonsterEntities` 里 4 个类的 import（`TheHungry`/`HillHungry`/`VisualNeuron`/`DungeonSpirit`） | 13 | 补 import（本轮脚本的 index 路径算错，导致没补上） |
| `THE_TONGUE` 等成员 | 4 | 同批补 |
| geckolib 4.8.4 改名残留（`fixInvertedFlatCube`/`prepMatrixForBone` 等） | 5 | 按 1.21 jar 逐个核 |
| `ModEffects.HORRIFIED` 成员 | 2 | 补成员或改指 |
| PortLib 残留（`IPortProjectileExtension`/`PortProjectileImpactEvent`/`ForgeEventFactory`/`NetworkHooks`） | ≈10 | 换 NeoForge 原生（第一轮已映射一部分） |
| `CombatRules.getDamageAfterAbsorb` | 1 | 新签名 `(LivingEntity, float, DamageSource, float, float)` |
| 零散类型差异（`BlockPos`↔`Vec3`、`setCustomClientFactory`、`AbstractTwinEye` import、`colour` 未传等） | ≈40 | 逐个改 |

## 四、下一批的执行顺序

1. `python tools/port2native/bulk_port.py copy-classes --roots common/entity/boss client/entity/renderer client/entity/model --src120 <1.20 根> --root121 <1.21 根>`
2. `systemic` + 本文件第二节的追加替换表（`Color` / `RenderUtils` / `.uv(` / `defineSynchedData` / `super.defineSynchedData();` 清理）
3. 补 `ModEntities` 14 个成员 → `BossEntities` 19 个成员 → `MonsterEntities` 5 个成员（脚本抽取 1.20 条目；**注意插入点要避开跨行的声明**，本轮两次都栽在这里）
4. 按第三节逐类清零（建议顺序：Lib 方法 → 成员与 import → geckolib 改名 → PortLib 残留 → 零散类型差异）
5. `BossEntities` 渲染器 + 资源（`renderers` + id 派生资源）
6. 收尾：`check_duplicates` → `fix_eol` → 门禁 0 → 提交

## 五、本轮两次踩到的同一个坑（务必避免）

`registrations` 类脚本的插入点是「最后一个 `public static final` 行之后」，但**声明可能跨行**
（lambda 写在下一行），结果把上一个声明劈成两半（`ModEntities.RIDEABLE_BEE`、`BossEntities.THE_DESTROYER_PROBE` 各中一次）。
**下一批的插入点应改为「最后一个以 `);` 结束的声明之后」**，或在插入前先把目标文件按「完整声明」重新切分。

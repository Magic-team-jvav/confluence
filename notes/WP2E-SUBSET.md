# WP2 批次 5 · 地下 / 岩石系物种（11 文件 + ModEntities 增量注册）

> **编号说明**：本批按「批次 4」起草，中途因边界从 15 个文件收缩到 11 个而改写内容，
> 提交信息用的是「批次 5」。**以提交信息为准**（它是 WP2 的第 4 个落地批次，编号跳过了一格）。
> 临时目录名 `wp2e` 与编号无关。

> 本批原计划 15 个文件（1300 非空行），因触及**注册层 / 效果层**两个缺口，实际落地 **11 个**
> （1010 非空行），4 个按依赖还原给后续批次 —— 见第三节。

## 一、边界

`dep_subset.py` 从 10 个怪物种子算出的闭包是 15 个（10 怪物 + 5 个各自专属的弹幕），
**14 个 `--defer` 一个都没触发**。但**工具报「闭包干净」≠ 能编译**：它的依赖判定是类型级，
看不到「1.21 有 `ModEntities` 这个类、但没有 `ModEntities.THROWN_ROCK` 这个成员」。
本批最终边界是**编译器定下来的**。

落地：

| 文件 | 非空行 |
|---|---|
| `GraniteElemental` | 206 |
| `ChaosElemental` | 122 |
| `RockGolem` | 113 |
| `GraniteGolem` | 112 |
| `Hoplite` | 102 |
| `AngryDandelion` | 92 |
| `Decayeder` | 83 |
| `EnchantedSword` | 79 |
| `HopliteJavelin` | 54 |
| `DandelionSeed` | 25 |
| `ThrownRockProjectile` | 21 |

外加既有文件 `common/init/ModEntities.java` 的**增量注册**（见第二节第 1 条）。

## 二、API 手术（4 类）

### 1. `ModEntities` 增补 3 条弹幕注册（**注册层开始增量补齐**）

`RockGolem` / `AngryDandelion` / `Hoplite` 分别要 `ModEntities.THROWN_ROCK` /
`DANDELION_SEED` / `HOPLITE_JAVELIN`，1.21 的 `ModEntities` 里没有这三条。
参数**逐条照抄** 1.20.1 的 `common/init/entity/ModEntities.java`（`:60` `:61` `:62`），
只把 `RegistryObject` 换成 `DeferredHolder<EntityType<?>, EntityType<X>>`、
注册调用换成 1.21 既有的 `ENTITIES.register(name, id -> ...build(id.toString()))` 写法。

这是**注册层的第一次增量落地**。完整注册层（`ModEntities` 703 行 + `MonsterEntities` 826 行等）
仍然要等物种补齐后单独做，但现在证明「跟着物种增量补成员」这条路是通的。

### 2. `defineSynchedData()` → `(SynchedEntityData.Builder builder)`

`GraniteElemental` / `GraniteGolem` / `Hoplite` 三处，`entityData.define(...)` → `builder.define(...)`。

### 3. `getStandingEyeHeight()` **被删除**（这次按新纪律查过「会不会是改名」）

`Hoplite` 覆写 `LivingEntity#getStandingEyeHeight(Pose, EntityDimensions)`（1.20.1 `LivingEntity.java:3354`）。
**全 jar 搜 `StandingEyeHeight` 命中 0 个文件 → 确认是删除，不是改名**。
1.21 的替代机制是 `EntityDimensions#eyeHeight()`：`Entity#getEyeHeight(Pose)` 现在是 `final`，
直接返回 `getDimensions(pose).eyeHeight()`（`Entity.java:3038`）。
因此改为覆写 `getDefaultDimensions(Pose)` → `super.getDefaultDimensions(pose).withEyeHeight(1.75F)`
（`withEyeHeight` 的用法见 `LivingEntity.java:177` 的 `SLEEPING_DIMENSIONS`）。
**这一处不可机械化**（不是改名），所以只记进 notes，不进规则。

### 4. 新规则：`SoundEvents` 里只有 22 个字段变成 `Holder<SoundEvent>`（**不能统配**）

`Hoplite` 的 `playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F)` 报
「`Holder<SoundEvent>` cannot be converted to `SoundEvent`」，但**同批** `Piranha` 的
`playSound(SoundEvents.DOLPHIN_ATTACK, ...)` 无需改动 —— 这说明差异是**逐字段**的。
核对结果（`neoforge-21.1.219-sources.jar` → `net/minecraft/sounds/SoundEvents.java`）：

| 类型 | 字段数 |
|---|---|
| `SoundEvent` | **1486** |
| `Holder<SoundEvent>` | **22** |

那 22 个是：`ARMOR_EQUIP_*`（10）、`CROSSBOW_LOADING_*`（3）、`CROSSBOW_QUICK_CHARGE_*`（3）、
`LLAMA_SWAG`、`TRIDENT_RIPTIDE_1/2/3`、`TRIDENT_THROW`、`TRIDENT_THUNDER`。
1.20.1 侧同名字段全是裸 `SoundEvent`（`SoundEvents.java:1298`），而
`Entity#playSound` 收的仍是 `SoundEvent`（`Entity.java:1138/:1144`），
所以 1.20 写法在这些字段上都要补 `.value()`。

已固化为**逐字段**规则 `rules/soundevents-holder-1.21.json`（22 个候选名放在一条正则的
交替组里，并带 `(?!\.value\(\))` 负向断言防止重复加）。实测：拿 1.20 的 `RedDevil` 过转换器，
`call:soundevents-holder-value: 1`，输出 `playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 0.8F)`。
1.20 侧共有 **44 处** `playSound(SoundEvents.` 调用，后续批次直接受益。

## 三、推迟了什么、为什么

| 推迟项 | 非空行 | 原因 |
|---|---|---|
| `ClimbingSpider` | 130 | 需要 `org.confluence.lib.common.LibEffects`（**1.21 的 Magic-Lib 里没有这个类**）与 `ModEffects.WEBBED`（1.21 的 `ModEffects` 只有 `FROZEN`） |
| `SpiderWebSpit` | 28 | 只被 `ClimbingSpider` 使用；自身也要 `ModEffects.WEBBED` |
| `IceElemental` | 89 | 需要 `ModEntities.FROST_BLAST` |
| `FrostMonsterProjectile` | 51 | 需要 `ModEffects.FROST_BURN`（1.21 没有；其效果类 `FrostburnEffect` 也不存在，属 WP6 效果层） |

即：**效果层（`ModEffects` 的 `WEBBED`/`FROST_BURN` + `LibEffects`）是这条线上的下一个缺口**，
其次是 `FROST_BLAST` 那条注册。

## 四、本批的过程事故（已修工具，值得记）

**我把 15 个文件写进了隔壁一个同名项目。** `stage_batch.py`（本批新增）里
`tools = dirname(dirname(dirname(HERE)))` **多写了一级 dirname**，`cwd` 变成仓库的上一级
`D:\Minecraft\1.21neoforge`，于是 `--dest ConfluenceOtherworld/src/main/java` 被解析到
`D:\Minecraft\1.21neoforge\ConfluenceOtherworld\src\main\java`；
而 `apply_batch.py` 里的 `makedirs(exist_ok=True)` 不报错，静悄悄建了整棵树。

**怎么确认没造成损失**：那个目录连同各级子目录的**创建时间全是本次运行的 18:41:40**，
证明它原本不存在；留下的 6 个文件是「转换器没改动、由 `copy2` 从暂存目录直接拷贝」的那些，
`copy2` 保留了 1.20 源文件的 mtime，所以按 mtime 判断时会误以为它们「本来就在」。
已整棵删除。

**两处工具加固**：

1. `stage_batch.py`：dirname 改成两级，并加断言「工具链根必须含 `.git`」。
2. `apply_batch.py`：加安全闸 —— `--dest` 必须落在某个 git 仓库内、且形如 `.../src/main/java`，
   否则**拒绝执行**（`--allow-outside-repo` 可显式绕过）。实测传入错误路径时 exit=2、
   且不再创建任何目录。这正是本条事故的根因：**写盘工具不该在「目标不存在」时默默造一棵树**。

**另一个反复踩到的坑**：用 `Select-Object -First N` 截断输出会**提前终止上游命令**。
本轮第一次跑 `stage_batch.py --convert --apply` 时就是这样被掐断的，导致 `--apply` 根本没执行，
而我又据此看到「0 errors」并一度以为本批「一次编译通过」—— 那是个**假结论**，
文件其实还没进树。后来按 `git status` 核对才发现。
**规矩：会产生副作用的命令，绝不接 `Select-Object -First` 截断**（用 `-Last` 或写文件）。

## 五、复现命令

```powershell
python tools/port2native/stage_batch.py --name wp2e `
  --src120 D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --dest   ConfluenceOtherworld/src/main/java `
  --file org/confluence/mod/common/entity/monster/GraniteElemental.java `
  --file org/confluence/mod/common/entity/monster/RockGolem.java `
  --file org/confluence/mod/common/entity/monster/GraniteGolem.java `
  --file org/confluence/mod/common/entity/monster/ChaosElemental.java `
  --file org/confluence/mod/common/entity/monster/EnchantedSword.java `
  --file org/confluence/mod/common/entity/monster/Hoplite.java `
  --file org/confluence/mod/common/entity/monster/Decayeder.java `
  --file org/confluence/mod/common/entity/monster/AngryDandelion.java `
  --file org/confluence/mod/common/entity/projectile/HopliteJavelin.java `
  --file org/confluence/mod/common/entity/projectile/DandelionSeed.java `
  --file org/confluence/mod/common/entity/projectile/ThrownRockProjectile.java `
  --convert --apply
```

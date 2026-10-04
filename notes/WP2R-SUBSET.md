# WP2 批次 20 · 动物半边收口：10 个动物 + 4 条邪恶转化注册

> 批次 19 把注册层 `CritterEntities` 落地之后，动物半边只剩 11 个种子；
> 本批把其中 **10 个轻量种子一次吃完**，并补上它们在 `getBreedOffspring` / `corrupt()` 里
> 回指的 4 条**怪物侧**注册（金鱼与企鹅的血月/邪恶转化目标）。
> 收口结果：`seed-dir common/entity/animal` 只剩 **`MysticFrog`** 一个重量种子（闭包 157 文件）。

## 一、开工测量

```powershell
python tools/port2native/seed_closures.py --seed-dir common/entity/animal ... --out notes/WP2R-CLOSURES.md
# 批次 19 之后：{"seeds": 11, "light": 10, "heavy": 1}
# 批次 20 之后：{"seeds": 1,  "light": 0,  "heavy": 1}   ← 只剩 MysticFrog

python tools/port2native/dep_subset.py --seed <10 个种子> ...
# {"candidates": 10, "kept": 10, "new": 10}  —— 10/10 零扩张（10 个文件一次落）
```

## 二、本批落地（10 个新增 + 2 个既有文件改动）

| 文件 | 非空行 | 关键改动 |
|---|---:|---|
| `common/entity/animal/Snail` | 394 | `setMaxUpStep(0.0F)` → `Attributes.STEP_HEIGHT`（同 `DeerClops`）；`defineSynchedData` 走规则 |
| `common/entity/animal/Fairy` | 379 | `finalizeSpawn` 第 5 参 → `guidingLoadedFromSave` 标记位；`PortDataResultExtension.ifSuccess` → 实例版；`getRandom1211()` → `getRandom()`（规则命中）；`PortTags.Blocks.ORES_GOLD` → NeoForge `Tags.Blocks`（规则命中） |
| `common/entity/animal/Bunny` | 210 | 同 `finalizeSpawn` 改法（`variantLoadedFromSave`）；`PortDataResultExtension.ifSuccess` → 实例版 |
| `common/entity/animal/Goldfish` | 96 | `canBreatheUnderwater()` 在 1.21 是 **final** → 改 `canDrownInFluidType(FluidType)` |
| `common/entity/animal/Penguin` | 48 | 无需改（转换器直出） |
| `common/entity/animal/HostileBunny` | 48 | 无需改 |
| `common/entity/animal/ExplosiveBunny` | 48 | `IPortExplosionExtension.getDefaultDamageSource` → 原版 `Explosion.getDefaultDamageSource` |
| `common/entity/animal/JewelBunny` | 34 | 无需改 |
| `common/entity/animal/RedSquirrel` | 22 | 无需改 |
| `common/entity/animal/JewelSquirrel` | 21 | 无需改 |

既有文件改动两处：

1. `common/init/entity/CritterEntities` **+12 条**（`bunny`、`red_squirrel`、`fairy`、`goldfish`、
   `penguin`、`snail`、`glowing_snail`、`magma_snail`、`jewel_bunny`、`jewel_squirrel`、
   `explosive_bunny`、`hostile_bunny`），逐条照 1.20 `CritterEntities.java` 的属性与尺寸；
   顺带补上 1.20 有、批次 19 尚未用到的 `registerHostile` 辅助方法（MONSTER 分类、0.4×0.5、追踪 10）。
2. `common/init/entity/MonsterEntities` **+4 条**（1.20 `MonsterEntities.java:650-658`）：
   `corrupt_penguin` / `vicious_penguin`（`EvilPenguin`）、`corrupt_goldfish` / `vicious_goldfish`
   （`Piranha` + `AnimationProfile.GOLDFISH`）。**这四个类 1.21 侧早已存在**（`EvilPenguin`、
   `Piranha` 都在），只是从来没有注册条目 —— 而 `Goldfish.corrupt()` / `Penguin.corrupt()`
   正好要用它们。

## 三、成员级盲区第 8 例：转化目标没注册（`dep_subset` 再次报 10/10 零扩张）

编译门第一轮报了 **6 处**错误，涉及 2 个文件：

```
Goldfish.java  [88,88,89] cannot find symbol                       ← MonsterEntities.VICIOUS_GOLDFISH / CORRUPT_GOLDFISH
Goldfish.java  [71] canBreatheUnderwater() cannot override ...      ← 1.21 是 final
Penguin.java   [47,47] cannot find symbol                           ← MonsterEntities.VICIOUS_PENGUIN / CORRUPT_PENGUIN
```

`dep_subset.py` 看不见这两处，因为它是**类型级**的：`MonsterEntities` 这个**类型**在 1.21 存在
（批次 9 就落了），但里面的**成员**是增量长的，4 个转化条目当时都还没有。
这与「批次 10 的 `ModEntities.NIMBUS_RAIN`」「WP2P 的 `ModEntities.RIDEABLE_*`」是同一类盲区，
**注册层的类建出来之后成员必须单独核对**。

## 四、四处 API 差异（本批实测，下次直接查）

### 1. `finalizeSpawn` 删参 —— 本批新增第 5、6 例（累计 6 例）

| 实体 | 1.20 用法 | 1.21 改法 |
|---|---|---|
| `Bunny` | `if (tag == null \|\| !tag.contains(VARIANT_KEY)) initializeSpawnVariant();` | `variantLoadedFromSave` 标记位（`readAdditionalSaveData` 置位）+ 4 参 `finalizeSpawn` |
| `Fairy` | `if (tag == null \|\| !tag.contains(GUIDING_KEY)) setGuiding(...)` | `guidingLoadedFromSave` 标记位（同上） |

`Bunny` 继承原版 `Rabbit`，用不了 1.21 侧 `BaseCritter` 已实现的那一份，所以**在类内复刻**了同一套
（字段名与注释都对齐 `BaseCritter.java:44-70`）。`Fairy` 的变体半边由父类
`BaseFlyingCritter → BaseCritter` 的 `variantLoadedFromSave` 处理，自己只需管 `Guiding` 那半边。

### 2. `LivingEntity#canBreatheUnderwater()` 在 1.21 是 final（`Goldfish`）

1.20 的 `@Override public boolean canBreatheUnderwater() { return true; }` 直接编不过。
1.21 本体改成查 `EntityTypeTags.CAN_BREATHE_UNDER_WATER`，NeoForge 给的替代钩子是
`ILivingEntityExtension#canDrownInFluidType(FluidType)` —— **1.21 侧既有先例是
`BaseAquaticMonster.java:88-97`**，本批照抄同一写法（水类型返回 false，语义等价）：

```java
@Override
public boolean canDrownInFluidType(FluidType type) {
    return type != NeoForgeMod.WATER_TYPE.value() && super.canDrownInFluidType(type);
}
```

### 3. `IPortExplosionExtension.getDefaultDamageSource(Level, Entity)` → 原版静态方法

1.20 侧是 PortLib 的 Forge 扩展；1.21.1 **原版就有** `Explosion.getDefaultDamageSource(Level, @Nullable Entity)`
（`Explosion.java`，已在 `build/_nfsrc_219` 里核对）。改成 `Explosion.getDefaultDamageSource(level(), this)`。

### 4. ⚠️ 转换器缺陷（第 2 类）：`shared` / `copyTo` 的目标包**在两个仓库里都不存在**

`ExplosiveBunny` 的 import 被改写成 `org.confluence.lib.extensioninjection.IPortExplosionExtension`，
调用点却仍留着 PortLib 名字（`leftovers.txt` 命中 2 行）。查规则表：

- `imports.json` / `types.json` 里 `IPortExplosionExtension` 是 `kind: shared` / `copyTo:
  org.confluence.lib.extensioninjection`；
- 但 `org.confluence.lib.extensioninjection` 这个包 **1.20 侧没有、1.21 侧也没有**
  （`Get-ChildItem -Recurse -Directory -Filter extensioninjection` 两侧都是空），
  `IPortExplosionExtension.java` 只存在于 `PortLib/src/main/java/org/mesdag/portlib/wrapper/common/extensions/`。

也就是说 `shared` 的语义是「把 PortLib 那个源文件**原样复制**到 1.21 侧的 lib 命名空间」，
而 `stage_batch.py` 的 `--file` 以 `ConfluenceOtherworld/src/main/java` 为根、**staging 不到 PortLib
的源码**，于是引擎只改了引用、没有真正把类落下来。**以后凡是命中 `shared` / `copyTo` 的
PortLib 类型，只有两条路**：① 手写 1.21 原生等价物（本批做法）；② 真的把那个类落到
Magic-Lib 的 `lib/extensioninjection`（子模块改动，单独成批）。这条与「WP2 批次 14 的
`PortTranslatableEnum` 被前缀规则改写成不存在的类」是**同一族**缺陷（改引用不落定义）。

## 五、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器报告：`uncovered.json` = `[]`；`leftovers.txt` **4 行**（2 行是 `PortDataResultExtension.ifSuccess`
无规则、2 行是上面的 `IPortExplosionExtension`），**全部本批手改**；
`manual-todo.md` 76 处，其中真正要人判的只有 3 类：`PortTags`（514 个标签常量，逐条判）、
`IPortExplosionExtension`（已按上文处置）、以及既有的 `port-registryentry-get/value` 过宽规则噪声。

## 六、本批未做（明确推迟）

| 推迟项 | 原因 |
|---|---|
| `MysticFrog`（闭包 **157** 文件 / 156 新增 / 20919 非空行） | 重量种子，单独成批；见 `notes/WP2R-CLOSURES.md` |
| 1.20 动物包的 `package-info.java` | 无语义，随渲染器批次补 |
| `CreatureSpawnPlacements`（刷怪放置层） | 同批次 19 的推迟说明（整文件 PortLib 词汇，单独成批） |
| 动物的客户端渲染器 / GeckoLib 模型贴图 | 随 WP2 渲染器批次统一处理（既有 `MonsterEntities` 同样还没渲染器） |

---

# 附：`dep_subset.py` 原始报告（本批种子 10 个）

# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 10 个：`Snail`, `Fairy`, `Goldfish`, `Penguin`, `RedSquirrel`, `JewelSquirrel`, `Bunny`, `HostileBunny`, `ExplosiveBunny`, `JewelBunny`
- 扩张后候选 **10** 个 `org.confluence.*` 类型
- 本批保留 **10** 个（其中 1.21 侧**新增** 10 个），因 `--defer` 剔除 **0** 个
- `--alias` 3 条（跨分支改过包路径的等价关系，不算新增）：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.animal.Bunny`
- `org.confluence.mod.common.entity.animal.ExplosiveBunny`
- `org.confluence.mod.common.entity.animal.Fairy`
- `org.confluence.mod.common.entity.animal.Goldfish`
- `org.confluence.mod.common.entity.animal.HostileBunny`
- `org.confluence.mod.common.entity.animal.JewelBunny`
- `org.confluence.mod.common.entity.animal.JewelSquirrel`
- `org.confluence.mod.common.entity.animal.Penguin`
- `org.confluence.mod.common.entity.animal.RedSquirrel`
- `org.confluence.mod.common.entity.animal.Snail`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_

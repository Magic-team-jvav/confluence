# WP2 批次 3 · 水生 / 飞行系具体物种（13 文件）

## 一、边界

`dep_subset.py` 从 15 个怪物种子算出的闭包是 17 个（15 怪物 + 2 个它们各自专属的弹幕），
其中 **4 个被推迟**（原因见第三节），实际落地 **13 个文件 / 1241 非空行**：

| 文件 | 非空行 | 继承 |
|---|---|---|
| `DemonEye` | 244 | `BaseFlyingMonster`（批次 1） |
| `Piranha` | 144 | `BaseAquaticMonster` |
| `Crawdad` | 139 | `BaseAquaticMonster` |
| `Shark` | 122 | `BaseAquaticMonster` |
| `SandShark` | 120 | `BaseAquaticMonster` |
| `CaveBat` | 96 | `BaseFlyingMonster` |
| `Arapaima` | 67 | `BaseAquaticMonster` |
| `MeteorHead` | 65 | `BaseFlyingMonster` |
| `Wraith` | 65 | `BaseFlyingMonster` |
| `CursedSkull` | 62 | `BaseFlyingMonster` |
| `Ghost` | 47 | `BaseFlyingMonster` |
| `ZombieMerman` | 43 | `BaseWarriorMonster`（批次 2） |
| `AnglerFish` | 27 | `BaseAquaticMonster` |

**14 个 `--defer` 一个都没触发**（工具报 `unused_defer: 14`）——
说明这批物种的 `org.confluence.*` 依赖在 1a~1c、WP2 批次 1/2 与音效批之后**已经全部满足**，
闭包不再外溢。这是「基类层先落」的直接回报。

## 二、本批的 API 手术（5 类，前 3 类是新面孔）

### 1. `Mob#finalizeSpawn` 少了第 5 个形参（新）

- 1.20.1：`finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, @Nullable CompoundTag)`
  （`Mob.java:1097`）
- 1.21.1：`finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, @Nullable SpawnGroupData)`
  （`Mob.java:1192`）

`Piranha` 没用那个 tag，直接去掉；`DemonEye` 用它做了判断
（`tag == null || !tag.contains(VARIANT_KEY)`），所以不能简单删：
`SummonCommand.java:86-95` 显示 1.21 里 `loadEntityRecursive`（会走 `readAdditionalSaveData`）
**仍然在 `finalizeSpawn` 之前**执行，因此改用一个由 `readAdditionalSaveData` 置位的
`variantLoadedFromSave` 标记，语义与原来等价。

### 2. PortLib 的 `PortDataResultExtension` 是原生 API 的垫片（新）

`PortDataResultExtension.ifSuccess(result, consumer)` 就是 `DataResult#ifSuccess` 的静态包装，
1.21 原生直接 `Variant.CODEC.parse(...).ifSuccess(this::setVariant)` 即可。

### 3. `canBreatheUnderwater()` 是 final（已固化）

1.21.1 `LivingEntity.java:382` 是 `public final`，官方替代钩子是 `canDrownInFluidType`
（见 `BaseAquaticMonster` 的注解）。本批 `ZombieMerman`、`SandShark` 两处按同样写法改。

### 4. `defineSynchedData()` → `(SynchedEntityData.Builder)`（已固化）

`Crawdad`、`DemonEye` 两处，`entityData.define(...)` → `builder.define(...)`。

### 5. `Entity#onAddedToWorld()` → `onAddedToLevel()`（**已更正**：是改名，不是删除）

`Crawdad` 一处。1.21.1 只是改名（`Entity.java:3733`，调用点 `ServerLevel.java:945` 等），
所以照旧覆写，并保留 `super.onAddedToLevel()`（它会置 `isAddedToLevel`）；
本类在 1.20 里是在 `super` **之前**做事，搬过去后保持同样顺序。
原先误判成删除、抽成 `initializeVariantOnce()` 放进 `tick()`，已回退。

## 三、推迟了什么、为什么

| 推迟项 | 非空行 | 原因 |
|---|---|---|
| `Harpy` | 71 | 引用 `ModEntities.HARPY_FEATHER`，1.21 的 `ModEntities` 里没有这条 |
| `AngryNimbus` | 70 | 引用 `ModEntities.NIMBUS_RAIN`，同上 |
| `HarpyFeatherProjectile` | 21 | 只被 `Harpy` 使用 |
| `NimbusRain` | 13 | 只被 `AngryNimbus` 使用 |

**这里暴露了 `dep_subset.py` 的一个已知粒度限制**：它按**类型**判定依赖是否满足，
而 `ModEntities` 这个类型在 1.21 里**存在**（1b 落的），于是 `--alias` 把它当作已满足、
不再检查**成员**。实际 1.21 的 `ModEntities` 只注册了一部分实体，`NIMBUS_RAIN` / `HARPY_FEATHER`
这类成员仍然缺失。所以「类型级闭包通过」≠「能编译」——本批就是靠编译器才发现这 4 个文件要推迟。

对应的正解是**注册层批次**（`common/init/entity/ModEntities` 703 行 + `MonsterEntities` 826 行等），
它必须在物种之后做（注册层引用全部物种）。批次 1~3 落的物种因此在注册层落地前**不可生成**，
这是本仓库既定的依赖顺序（同 1c-1 的 `BaseMonster` 先于物种）。

## 四、复现命令

```powershell
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias   "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --seed    org/confluence/mod/common/entity/monster/AnglerFish.java `
  --seed    org/confluence/mod/common/entity/monster/Arapaima.java `
  --seed    org/confluence/mod/common/entity/monster/Piranha.java `
  --seed    org/confluence/mod/common/entity/monster/Shark.java `
  --seed    org/confluence/mod/common/entity/monster/SandShark.java `
  --seed    org/confluence/mod/common/entity/monster/Crawdad.java `
  --seed    org/confluence/mod/common/entity/monster/ZombieMerman.java `
  --seed    org/confluence/mod/common/entity/monster/CaveBat.java `
  --seed    org/confluence/mod/common/entity/monster/DemonEye.java `
  --seed    org/confluence/mod/common/entity/monster/Wraith.java `
  --seed    org/confluence/mod/common/entity/monster/CursedSkull.java `
  --seed    org/confluence/mod/common/entity/monster/Ghost.java `
  --seed    org/confluence/mod/common/entity/monster/MeteorHead.java `
  --out     notes/WP2C-SUBSET.md
```

边界与推迟链还可用 `tools/port2native/summarize_subset.py --report <报告> --src120 <根>`
按子系统看体量；漏文件清单可用 `tools/port2native/missing_files.py --sub common/entity/monster` 生成。

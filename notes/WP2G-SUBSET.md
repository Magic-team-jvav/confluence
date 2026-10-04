# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 19 个：`JellyFish`, `GiantShelly`, `GiantTortoise`, `Unicorn`, `AntlionSwarmer`, `Drippler`, `Derpling`, `Wyvern`, `ShadowflameApparition`, `Gnome`, `Hornet`, `Harpy`, `AngryNimbus`, `RedDevil`, `Zombie`, `HornetStingerProjectile`, `HarpyFeatherProjectile`, `NimbusRain`, `UnholyTridentProjectile`
- 扩张后候选 **19** 个 `org.confluence.*` 类型
- 本批保留 **19** 个（其中 1.21 侧**新增** 19 个），因 `--defer` 剔除 **0** 个
- `--alias` 2 条（跨分支改过包路径的等价关系，不算新增）：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.monster.AngryNimbus`
- `org.confluence.mod.common.entity.monster.AntlionSwarmer`
- `org.confluence.mod.common.entity.monster.Derpling`
- `org.confluence.mod.common.entity.monster.Drippler`
- `org.confluence.mod.common.entity.monster.GiantShelly`
- `org.confluence.mod.common.entity.monster.GiantTortoise`
- `org.confluence.mod.common.entity.monster.Gnome`
- `org.confluence.mod.common.entity.monster.Harpy`
- `org.confluence.mod.common.entity.monster.Hornet`
- `org.confluence.mod.common.entity.monster.JellyFish`
- `org.confluence.mod.common.entity.monster.RedDevil`
- `org.confluence.mod.common.entity.monster.ShadowflameApparition`
- `org.confluence.mod.common.entity.monster.Unicorn`
- `org.confluence.mod.common.entity.monster.Wyvern`
- `org.confluence.mod.common.entity.monster.humanoid.Zombie`
- `org.confluence.mod.common.entity.projectile.HarpyFeatherProjectile`
- `org.confluence.mod.common.entity.projectile.HornetStingerProjectile`
- `org.confluence.mod.common.entity.projectile.NimbusRain`
- `org.confluence.mod.common.entity.projectile.UnholyTridentProjectile`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_

---

# WP2 批次 7（本文件是它的开工计算）

## 一、`seed_closures.py` 先量再切

19 个种子是先用新工具 `tools/port2native/seed_closures.py`（本批新增）逐个量出来的**单独闭包**
（报告 `notes/WP2G-CLOSURES.md`）：49 个待移植怪物类里 13 个是「闭包=1」（零新增依赖），
5 个是「闭包=2」（各带一个弹幕），其余 31 个的闭包都在 **244 个文件 / 29940 行** ——
它们都引用 `MonsterEntities`（826 行，注册层）或 `MonsterAttributeScaling`，
即**注册层批次**的活（见 `notes/WP2C-SUBSET.md` 第三节的既定顺序：注册层必须在物种之后）。

本批只取这 18 个轻量种子（另有 1 个 `Gnome` 落地失败，见下），不碰注册层。

## 二、本批落地（18 个文件 / 2016 非空行）

| 文件 | 非空行 |
|---|---:|
| `common/entity/monster/JellyFish` | 323 |
| `common/entity/monster/GiantShelly` | 305 |
| `common/entity/monster/GiantTortoise` | 283 |
| `common/entity/monster/humanoid/Zombie` | 226 |
| `common/entity/monster/Hornet` | 202 |
| `common/entity/monster/Unicorn` | 87 |
| `common/entity/monster/RedDevil` | 73 |
| `common/entity/monster/Harpy` | 71 |
| `common/entity/monster/AngryNimbus` | 70 |
| `common/entity/monster/AntlionSwarmer` | 64 |
| `common/entity/monster/Drippler` | 59 |
| `common/entity/monster/Derpling` | 54 |
| `common/entity/monster/Wyvern` | 53 |
| `common/entity/monster/ShadowflameApparition` | 48 |
| `common/entity/projectile/HornetStingerProjectile` | 56 |
| `common/entity/projectile/HarpyFeatherProjectile` | 21 |
| `common/entity/projectile/NimbusRain` | 13 |
| `common/entity/projectile/UnholyTridentProjectile` | 8 |

外加既有文件改动：`ModEntities`（+4 条注册，见下）。

## 三、推迟了什么、为什么

| 推迟项 | 非空行 | 原因 |
|---|---:|---|
| `Gnome` | 37 | 用 `DecorativeBlocks.GARDEN_GNOME`，而 1.21 侧**连方块都没有**：1.20 的 `common/block/natural/GardenGnomeBlock`（方块 + `BlockEntityType`）与 `client/model/block/GardenGnomeBlockModel` 在 1.21 全是缺的。这是方块工作包的活，不是实体批次的活 —— **文件已在暂存后删除**，不留半成品。 |
| `Nymph` / `SpittingPlant` / `ClimbingSpider` / `SpiderWebSpit` | 169/44/130/28 | 仍卡在 `LibEffects`（Magic-Lib 无此类）与 `TCEffects.GRAVITATION` 的关系裁决上，见 `notes/WP6A-SUBSET.md` 第三节。 |

## 四、本批挖出的四处 API 差异

### 1. `Mob#finalizeSpawn` 的第 5 个形参 `@Nullable CompoundTag tag` 在 1.21 被删

`humanoid/Zombie` 用 `tag == null || !tag.contains(VARIANT_KEY)` 判断「本次生成是否已带变体 NBT」。
1.20.1 `Mob.java:1097` 有该形参、1.21.1 `Mob.java:1192` 没有。
**这与 `DemonEye` 是同一个问题**（上一批已解决），本批沿用同一套办法：由 `readAdditionalSaveData`
置位 `variantLoadedFromSave`，`finalizeSpawn` 改判该标记 —— 依据是 1.21 的
`SummonCommand.java:86-95` 里 `loadEntityRecursive`（会走 `readAdditionalSaveData`）仍在
`finalizeSpawn` **之前**执行。**两处都不进规则**：这是语义改写，机械替换会错。

### 2. `PortDataResultExtension.ifSuccess(result, consumer)` → 原生 `DataResult#ifSuccess(consumer)`

`humanoid/Zombie` 2 处。转换器按设计不动它（`uncovered 0` 但 `leftovers.txt` 报了这 2 行，属预期拦截）。

### 3. `AttributeModifier` 在 1.21 是原生 record：没有 `.unwrap()`，也没有 `getId()`

`Unicorn.java:69-70` 两处。1.21.1 `AttributeInstance.java:118` 保留了按 id 移除的重载
`removeModifier(ResourceLocation)`，所以把 id 提成 `ACCELERATION_ID` 常量、加减共用。
（`CasterCycleAction` 上一批遇到的是同一件事，只是它的量恒定、可以提成 `static final`。）

### 4. `dep_subset.py` 的成员级盲区**第三次**现身：`ModEntities` 缺 4 条注册

`NIMBUS_RAIN` / `HARPY_FEATHER` / `HORNET_STINGER` / `UNHOLY_TRIDENT`。
19 个种子的类型级闭包是干净的（**19/19，扩张为 0**），但 1.21 的 `ModEntities`
（1b 落的）本来就只注册了一部分实体 —— 类型在、成员不在。**只有编译器发现得了**，
本批增量注册这 4 条。这与 `notes/WP2C-SUBSET.md:73-76` 记的是同一条限制。

## 五、度量与门槛

```powershell
# 编译门槛（工具自带 --offline，见第六节）
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 结果：总错误数: 0，涉及 0 个文件；[build] exit=0
```

## 六、复现命令

```powershell
python tools/port2native/dep_subset.py `
  --src120  D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --alias   "org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities" `
  --alias   "org.confluence.lib.util.LibEntityUtils=org.confluence.lib.util.LibUtils" `
  --seed    org/confluence/mod/common/entity/monster/JellyFish.java `
  ...（其余 18 个种子见本文件开头）...
  --out     notes/WP2G-SUBSET.md
```

## 七、过程记录：Maven 仓库 530 与 `--offline`

本批第一次编译**不是代码错**：`maven.bawnorton.com` 返回 `530`（Cloudflare 源站错误），
`org.mesdag:ParticleStorm-neoforge-1.21.1:1.4.4` 解析失败 → `BUILD FAILED in 9s`，
而 `build_errors.py` 报「总错误数 0」。原因是那次我手敲 `.\gradlew` 且**没加 `--offline`**。
`build_errors.py` 自己就带 `--offline`（依赖已在本地 Gradle 缓存里），重跑即正常。
教训：**编译门槛一律走 `build_errors.py`，不要手敲 gradlew**。


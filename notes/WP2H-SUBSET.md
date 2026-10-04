# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）

- 种子 4 个：`Nymph`, `SpittingPlant`, `ClimbingSpider`, `SpiderWebSpit`
- 扩张后候选 **4** 个 `org.confluence.*` 类型
- 本批保留 **4** 个（其中 1.21 侧**新增** 4 个），因 `--defer` 剔除 **0** 个
- `--alias` 2 条（跨分支改过包路径的等价关系，不算新增）：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 本批新增（直接拷贝即可）

- `org.confluence.mod.common.entity.monster.ClimbingSpider`
- `org.confluence.mod.common.entity.monster.Nymph`
- `org.confluence.mod.common.entity.monster.SpittingPlant`
- `org.confluence.mod.common.entity.projectile.SpiderWebSpit`

## 被 `--defer` 剔除（连依赖链一起还给后续批次）

_无_

---

# WP2 批次 8（本文件是它的开工计算）

## 一、为什么这批现在才做

这四个物种在 WP2 批次 6/7 都因 `LibEffects.CONFUSED` 被推迟（见 `notes/WP2F-SUBSET.md:81-85`、
`notes/WP6A-SUBSET.md:50`）。WP6a 把效果层按 1.20 的归属搬进 Magic Lib 之后
（`notes/WP6a-EFFECTS-MIGRATION.md`），它们的类型级闭包立刻变成 **4/4、扩张为 0**。

## 二、落地 5 个文件 / 435 非空行

| 文件 | 非空行 |
|---|---:|
| `common/entity/monster/Nymph` | 169 |
| `common/entity/monster/ClimbingSpider` | 130 |
| `common/entity/projectile/PlantSpit` | 64 |
| `common/entity/monster/SpittingPlant` | 44 |
| `common/entity/projectile/SpiderWebSpit` | 28 |

外加既有文件改动：`ModEntities`（+3 条注册，见下）。

## 三、`PlantSpit` 是**闭包之外**的漏网之鱼（第四例成员级盲区）

`SpittingPlant` 里写的是

```java
var projectile = (clinger ? ModEntities.CLINGER_FLAME : ModEntities.FUNGI_SPORE).get().create(level());
projectile.configure(this, target, ...);
```

类型靠 `var` 推断、只经 `ModEntities` 的**成员**访问，所以 `dep_subset.py` 的
「类型在不在 1.21」判据看不到它 —— 而 1.21 侧 `PlantSpit` **根本不存在**
（`Get-ChildItem -Filter PlantSpit.java` 无命中），`configure(...)` 也就编不过。
本批把 `PlantSpit`（1.20 的 `common/entity/projectile/PlantSpit.java`，单文件闭包=1）一并搬入。

## 四、本批挖出的两处差异 + 一条新规则

### 1. `LibEffects.X.get()` 要去掉 `.get()` —— 已固化成规则 `libeffects-holder-unwrap`

与 `modeffects-holder-unwrap` 同理：1.20 的 `LibEffects.X` 是 `RegistryObject<MobEffect>`，
1.21 侧是 `DeferredHolder<MobEffect, MobEffect>`（本身就是 `Holder`），
而 `MobEffectInstance(Holder<…>)` / `hasEffect(Holder<…>)` / `getEffect(Holder<…>)` 全收 Holder。
实证：本批第一次转换后 `ClimbingSpider.java:106`、`SpittingPlant.java:27` 报
`incompatible types: MobEffect cannot be converted to Holder<MobEffect>`。
规则已做正/反例自检（`ModSoundEvents.X.get()` 不命中、`LibEffects.X.getDelegate()` 不命中），
加完规则重跑转换后 `.get()` 残留 **0 处**。
（`LibEffects` 的归属与迁移见 `notes/WP6a-EFFECTS-MIGRATION.md`。）

### 2. `Nymph` 的两个尺寸覆写在 1.21 必须合并成 `getDefaultDimensions`

| 1.20 | 1.21.1 |
|---|---|
| `@Override public EntityDimensions getDimensions(Pose)` | **final**（`LivingEntity.java:3423`，内部即 `getDefaultDimensions(pose).scale(getScale())`） |
| `@Override protected float getStandingEyeHeight(Pose, EntityDimensions)` | **删除**（全 jar 搜 `StandingEyeHeight` 命中 0）；眼睛高度由 `EntityDimensions#eyeHeight()` 提供，`Entity#getEyeHeight(Pose)` 现在 final 并直接返回 `getDimensions(pose).eyeHeight()`（`Entity.java:3038`） |

合并为一次 `getDefaultDimensions` 覆写：未触发时 `dimensions.scale(1.0F, 0.75F).withEyeHeight(1.05F)`，
触发时返回父类尺寸。与同目录 `Hoplite.java:41-51`、`BaseLivingBossPart.java:245-252` 同一套办法。

### 3. `ModEntities` 再缺 3 条注册（第四例成员级盲区，与 `PlantSpit` 同源）

`CLINGER_FLAME`（`EntityType<PlantSpit>`，`Kind.CURSED_FLAME`，0.3×0.3）、
`FUNGI_SPORE`（`Kind.SPORE`，0.4×0.4）、`SPIDER_WEB_SPIT`（`EntityType<SpiderWebSpit>`，0.3×0.3）
—— 尺寸与 `clientTrackingRange(10).updateInterval(1)` 照 1.20 的 `ModEntities.java:64/65/73` 原样搬。

## 五、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器报告：`uncovered.json` = `[]`、`leftovers.txt` 空。
残留自查：三个怪物文件里 `LibEffects.\w+\.get()` 命中 **0**。

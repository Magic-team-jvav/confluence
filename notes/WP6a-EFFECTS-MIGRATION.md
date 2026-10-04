# WP6a · 效果层迁移（1.21 的 TerraCurio → Confluence-Magic-Lib）

> 用户 2026-09-27 裁决 **A：完整对齐 1.20**（见 `notes/WORK-QUEUE.md` 的 WP6a 小节）。
> 本文件记批次 2（Lib 侧落地）与批次 3（消费侧迁移）的实际做法、挖出的差异、推迟项。

## 一、为什么这条线要插在 WP2 中间

`MonsterEntities`（826 行注册层）引用全部物种，而 `Nymph` / `SpittingPlant` / `ClimbingSpider` /
`SpiderWebSpit` 四个物种引用 `LibEffects.CONFUSED`；注册层又是其余 31 个「重量」怪物种子的共同前置。
所以效果层不落地，WP2 就只能在 49 个物种里做完 14 个。

**归属依据（两侧都核对过）**：

| | 1.20（分叉后，事实来源） | 1.21（迁移前） |
|---|---|---|
| 效果注册 | `Confluence-Magic-Lib` 的 `lib/common/LibEffects.java`，命名空间 `confluence_magic_lib` | TerraCurio 的 `terra_curio/common/init/TCEffects.java`，命名空间 `terra_curio` |
| TerraCurio 里 | **已无** `TCEffects`，且它自己引用 Lib 的 `LibEffects`（7 个文件：`PaladinsShield.java:27`、`TCUtils.java:145/183/187`、`CelestialShell`、`SunStone`、`BandOfRegeneration`、`HealPerSecondCurioItem`、`NightBonusCurioItem`） | 效果全在这里，另有重力栈（GravitationEffect/HoneyEffect/GravitationHandler/2 个网络包/`IEntity`/`TCKeyBindings`） |
| 贴图 | 5 张 `assets/confluence_magic_lib/textures/mob_effect/*.png` | 同名 5 张在 `assets/terra_curio/textures/mob_effect/`（**与 1.20 逐字节相同**，SHA256 已核对） |

## 二、批次 2（Lib 侧）落地内容

| 文件 | 非空行 | 说明 |
|---|---:|---|
| `lib/common/LibEffects.java` | 31 | 5 条效果 + `healPerSecond`；`DeferredRegister` 命名空间 `confluence_magic_lib` |
| `lib/common/effect/GravitationEffect.java` | 25 | 1.21 原生形态（见第四节） |
| `lib/common/effect/HoneyEffect.java` | 41 | 去掉对 TerraCurio `IEntity` 的反向依赖（见第四节） |
| `ConfluenceMagicLib.java` | +11 | 构造函数里注册 `LibEffects.EFFECTS`，并对 5 个旧 id 登记 `addAlias` |
| `assets/confluence_magic_lib/textures/mob_effect/*.png` | 5 张 | 从 1.21 TerraCurio 复制（与 1.20 MagicLib 同哈希） |

**`PublicMobEffect` 不需要动**：1.20 是 `extends PortMobEffect`（PortLib 垫片），1.21 是
`extends MobEffect`，两个构造器签名与语义完全一致，1.21 侧本来就是等价物（已 diff）。

## 三、批次 3（消费侧）落地内容

一次性改写 **28 个文件 / 126 处**（脚本 `$env:TEMP/migrate-libeffects.py`，替换表见下）：

```
org.confluence.terra_curio.common.init.TCEffects            -> org.confluence.lib.common.LibEffects
org.confluence.terra_curio.common.effect.GravitationEffect  -> org.confluence.lib.common.effect.GravitationEffect
org.confluence.terra_curio.common.effect.HoneyEffect        -> org.confluence.lib.common.effect.HoneyEffect
简名 TCEffects.                                             -> LibEffects.
```

之所以能机械替换：`LibEffects.CONFUSED` 等成员的类型与 `TCEffects.CONFUSED` 完全一致
（都是 `DeferredHolder<MobEffect, MobEffect>`），所以原有 `.get()` / `.getDelegate()` 用法一处都不用改。

**删除的类（TerraCurio 侧）**：`common/init/TCEffects.java`、`common/effect/GravitationEffect.java`、
`common/effect/HoneyEffect.java`、`assets/terra_curio/textures/mob_effect/*.png`（5 张，已无引用）。

**非机械的 4 处**：

1. `TerraCurio.java:30` 删掉 `TCEffects.EFFECTS.register(eventBus)`（注册权移交 Lib）。
2. `TCLanguageProvider.java:952-958` 删掉「遍历 TC 效果注册表自动命名」的循环与 5 条手工 `add(...)` ——
   效果已不属于 TC 的注册表；1.20 的 TC 语言提供者同样**不提** `LibEffects`（已核对）。
   效果的 en_us/zh_cn 名字由主模组的 `ModChineseProvider`/`ModEnglishProvider` 生成（键随效果 id 走）。
3. `util/TCUtils.java` 原来靠 `import ...common.init.*` 通配引 `TCEffects`，改名后要补
   `import org.confluence.lib.common.LibEffects;`。
4. **lang 键改名**（4 个已提交的翻译文件，35 行，逐字符替换不重排）。
   `en_us`/`zh_cn` 在 `src/generated`，不入库，故不在本批范围。

| 文件 | 改了几处 |
|---|---:|
| `de_de.json` | 5（只有 tooltip 键） |
| `es_es.json` | 10 |
| `lzh.json` | 10 |
| `pt_br.json` | 10 |

`key.terra_curio.flip_gravitation` **本批不动**：按键映射本批仍在 TerraCurio（见第五节）。

## 四、本批挖出的差异（都有出处）

### 1. `Attributes.GRAVITY` 在 1.21.1 是**原版属性**，不需要 PortLib

`build/_nfsrc_219/.../Attributes.java:74` `public static final Holder<Attribute> GRAVITY`。
所以 `GravitationEffect` 只需要把 1.20 的三处 PortLib 形态换掉：
`UUID.nameUUIDFromBytes(...)` → `ResourceLocation`（1.21 的 `AttributeModifier` 是 record）；
`Attributes.GRAVITY.value()` → `Attributes.GRAVITY`（原生就是 `Holder`）；
`@Diff`（`org.mesdag.portlib.diff.Diff`）去掉。数值 `-2.0` 不变，
`Operation.MULTIPLY_TOTAL` 在 1.21 叫 `ADD_MULTIPLIED_TOTAL`，语义等价。

### 2. `HoneyEffect` 不能照 1.21 版本搬：它反向依赖 TerraCurio

1.21 的写法是 `IEntity.of(living).terra_curio$isPlayer()`（TerraCurio 的 mixed 接口），
搬进 Magic-Lib 会成环（TerraCurio 依赖 Magic-Lib）。**1.20 的 Lib 版本用的是
`living instanceof Player`** —— 照 1.20 写。这也是「为什么要以 1.20 为准而不是照搬 1.21」的实例。

### 3. `TCEffects.healPerSecond` 的 5 个调用点全在 TerraCurio

`CelestialShell:61`、`SunStone:40`、`HealPerSecondCurioItem:17`、`BandOfRegeneration:17`、
`NightBonusCurioItem:32`；1.20 侧同名方法在 Lib 里，调用点一致（已核对两侧方法体逐字相同）。

### 4. `addAlias` 兜住了数据侧的旧 id

主模组的 data map 里仍写着 `terra_curio:confused`（手写 `data/terra_curio/data_maps/item/accessories.json`
3 处、生成物 `living_invulnerable_effects.json` 58 处、`value.json` 里 `terra_curio:paladins_shield` 1 处）。
本批在 `ConfluenceMagicLib` 构造函数里对 5 个旧 id 登记了 `addAlias`（与既有 14 条属性别名同一做法），
所以这些 JSON **现在仍然解得开**；等 WP7 重跑 datagen 时会自然变成 `confluence_magic_lib:*`。
**这是一处「本批故意留的过渡态」**，不是遗漏。

## 五、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| `GravitationHandler` / `LibKeyBindings` / 2 个网络包 / `ILibEntity` / 相关 mixin 搬到 Lib | 这是**重力翻转整条特性**，不是效果注册。硬约束：1.21 TerraCurio 的 `mixin/EntityMixin.java` 用 `@Mixin(Entity.class) implements IEntity` 承载 `shouldRot`/`dimensionHeight`/cthulhu 冲刺/`isPlayer`/`TCUtils.applyLavaImmune`，而 1.20 Lib 版 `EntityMixin` 的 `@Unique` 字段**同名**（`terra_curio$isShouldRot`、`terra_curio$dimensionHeight`）→ 两个 mixin 注入同一个 `Entity` 会直接崩，**不能先加一套再拆另一套**。另外 1.21 的 `IEntity` 有 6 个成员，只有 3 个属 `ILibEntity`（cthulhu 一对在 1.20 归 `ITCEntity`，`isPlayer` 在 1.20 没有对应物），`isShouldRot` 有 38 处调用点跨 20+ 个 TerraCurio mixin（其中 4 个「反转 AI」mixin 在 1.20 无对应物，需单独决策）。→ 单独立 WP6c，原子切换。 |
| `IEntity` / `TCKeyBindings.FLIP_GRAVITATION` 的改名 | 同上，属 WP6c。 |
| 4 个「反转 AI」mixin 的归属 | 1.20 无对应物（1.21 TerraCurio 里仍是 `// todo 反转AI`），待 WP6c 决策。 |

## 六、验证

```powershell
# 三个模块都要过（本批动了三个 git 仓库）
python tools/port2native/build_errors.py --module :Confluence-Magic-Lib  --repo .
python tools/port2native/build_errors.py --module :TerraCurio           --repo .
python tools/port2native/build_errors.py --module :ConfluenceOtherworld --repo .
# 结果：三者都是「总错误数: 0，涉及 0 个文件」，[build] exit=0
```

残留自查：`TCEffects` 在三个模块的 `src/main/java` 下 **0 命中**（只剩注释里的历史说明）；
`effect.terra_curio.{confused,gravitation,honey,paladins_shield,cerebral_mindtrick}` 在已提交的
5 个 lang 文件里 **0 命中**，`effect.confluence_magic_lib.*` **35 命中**（= 4 文件 × 改名条数）。

## 七、提交方式（三个仓库）

效果层跨子模块，所以是三个提交（子模块在前，根仓库最后记 gitlink）：

1. `Confluence-Magic-Lib` 子模块：批次 2 的 5 项（3 个新类 + 注册 + 5 张贴图）
2. `TerraCurio` 子模块：批次 3 的删除与引用改写（20 个文件）
3. 根仓库 `ConfluenceOtherworld`：16 个文件的引用改写 + 4 个 lang 文件改名 + 本笔记

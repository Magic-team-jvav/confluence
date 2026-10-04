# WP6 批次 1 · 效果层（把 WP2 卡住的 4 个文件放行）

上一批（`notes/WP2E-SUBSET.md`）推迟了 4 个物种文件，卡在**效果层**。本批就是补这一层。

## 一、量化（先把缺口说清楚）

1.20 的 `ModEffects` 有 76 条、1.21 只有 67 条 → **缺 9 条**。

| 缺失项 | 归因 | 本批 |
|---|---|---|
| `JUNGLES_FURY` | 内联 `PublicMobEffect`，无类 | ✅ 补 |
| `GARDEN_GNOME_LUCK` | 内联 + `Attributes.LUCK` | ✅ 补 |
| `SPARKLE_SLIME` | `SparkleSlimeEffect`(25) | ✅ 补 |
| `WEBBED` | 内联 + 两个属性修正 | ✅ 补 |
| `DEMONIC_THOUGHTS` | `DemonicThoughtsEffect`(8) | ✅ 补 |
| `FROST_BURN` | `FrostburnEffect`(30) + `LibDamageTypes.FROST_BURN` | ✅ 补 |
| `HELLFIRE` | `HellFireEffect`(52) | ✅ 补 |
| `DRYADS_BANE` | `DryadsBaneEffect`(19) | ✅ 补 |
| `DRYADS_BLESSING` | `DryadsBlessingEffect`(45) | ⛔ 推迟（见第三节） |

逐个测过每个效果类的独立闭包，结果是**干净的两分**：7 个自成一类（闭包=1），
3 个会牵进怪物（`DryadsBlessingEffect` / `HorrifiedEffect` / `TheTongueEffect`，
闭包 92 个文件）→ 后者推迟。

## 二、本批落地

| 文件 | 非空行 |
|---|---|
| `common/effect/harmful/FrostburnEffect` | 30 |
| `common/effect/harmful/HellFireEffect` | 52 |
| `common/effect/neutral/SparkleSlimeEffect` | 25 |
| `common/effect/harmful/DryadsBaneEffect` | 19 |
| `common/effect/harmful/DemonicThoughtsEffect` | 8 |
| `common/entity/projectile/FrostMonsterProjectile` | 51 |
| `common/entity/monster/IceElemental` | 89 |

外加既有文件改动：`ModEffects`（+8 条注册）、`ModEntities`（+`FROST_BLAST`）、
子模块 `Confluence-Magic-Lib` 的 `LibDamageTypes`（+`FROST_BURN`，单独提交 `d6ead5b`）。

`FrostMonsterProjectile` 与 `IceElemental` 就是上一批推迟的两个，本批被效果层放行。

## 三、推迟了什么、为什么

| 推迟项 | 原因 |
|---|---|
| `DryadsBlessingEffect`(45) | 闭包 92 个文件（引用怪物） |
| `HorrifiedEffect`(65) / `TheTongueEffect`(83) | 同上 |
| `CrimsonStormEffect`(21) / `DriveAwayEffect`(51) / `DriveAwayController`(72) | 1.20 的 `ModEffects` **并不注册**它们，且引用者（物品/怪物）尚未移植 → 现在落地就是死代码 |
| `LibEffects` + `HoneyEffect`(32) + `GravitationEffect`(20) | **需要设计裁决，不是机械活**：`GravitationEffect` 依赖 `Attributes.GRAVITY`（1.21 的 `LibAttributes` 没有）与静态 `Multimap<Attribute, AttributeModifier>`，而 1.21 侧已经有 `TCEffects.GRAVITATION`（TerraCurio）在做同一件事（`StepOnTrapBlock.java:45`）。先要决定两套重力效果如何合并，不能照搬。 |
| `ClimbingSpider` / `SpiderWebSpit` | 仍卡在 `LibEffects` 上（`ClimbingSpider` 用 `LibEffects.CONFUSED`） |

## 四、本批挖出的三处 API 差异

### 1. `MobEffect#applyEffectTick` 的**返回值**从 `void` 变成 `boolean`（不可机械化）

| 版本 | 签名 |
|---|---|
| 1.20.1 | `MobEffect.java:64` `public void applyEffectTick(LivingEntity, int)` |
| 1.21.1 | `MobEffect.java:74` `public boolean applyEffectTick(LivingEntity, int)` |

返回值表示「本次是否真的施加了效果」。1.20 的实现没有这个语义，所以两处都补 `return true`。
**这不是改名也不是加参数，机械替换会错**，所以只记 notes 不进规则。

### 2. `ExplosionDamageCalculator#getEntityDamage` → `getEntityDamageAmount`（改名，已固化）

1.21.1 `ExplosionDamageCalculator.java:29` 是 `getEntityDamageAmount(Explosion, Entity)`；
同批里 `getKnockbackMultiplier` 名字没变。已写成规则
`vanilla-explosiondamagecalculator-getentitydamage-rename`。

### 3. `ModEffects.X.get()` 要去掉 `.get()`（已固化）

1.20 的 `ModEffects.X` 是 `RegistryObject<MobEffect>`、要 `.get()`；
1.21.1 是 `DeferredHolder<ModEffect, MobEffect>`，**本身就是 `Holder<MobEffect>`**，
而 1.21 的 `MobEffectInstance(Holder<...>, ...)` / `hasEffect(Holder<...>)` / `getEffect(Holder<...>)`
全都收 Holder。规则 `modeffects-holder-unwrap` 只覆盖 `ModEffects`：
`ModSoundEvents.X.get()` **不能**照此处理（`getHurtSound` 等返回裸 `SoundEvent`），
这一条差别写在规则 notes 里，防止以后有人顺手推广。

## 五、子模块的提交方式（第一次踩）

`Confluence-Magic-Lib` 是**子模块**，改动要在子模块里单独提交（`d6ead5b`），
主仓库记的是 gitlink。两个坑：

1. 子模块里的 `.git` 是**文件**（gitlink），所以 `open('.git/COMMIT_MSG.txt','w')` 会
   `FileNotFoundError` —— 提交信息要写到 `$env:TEMP` 这种仓库外的路径。
2. `src/generated` 在子模块 `.gitignore` 第 26 行里，**datagen 产物不进版本库**：
   所以 `LibDamageTypes.FROST_BURN` 的**源码改动**是 bootstrap 那一行，
   `frost_burn.json` 由 `runData` 生成（WP7）。本地手工放了一份与 1.20 逐字节一致的 json
   让本地构建/运行可用，但它不进提交。

## 六、一处过程失误（自查发现并修好）

编辑 `rules/vanilla-api-1.20-to-1.21.json` 追加规则时，第一次的 `old_string` 写少了
一行，把 `vanilla-onaddedtoworld-rename` 的 `pattern` 字段删掉了 —— 那条规则会变成
「无 pattern」而静默失效。立刻用 `json.load` + 逐条打印 id/pattern 校验，发现并恢复。
**改规则文件后必须跑一次 JSON 解析 + 逐条 pattern 打印**，不能只看文件「长得对」。

## 七、复现命令

```powershell
python tools/port2native/stage_batch.py --name wp6a `
  --src120 D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java `
  --dest   ConfluenceOtherworld/src/main/java `
  --file org/confluence/mod/common/effect/harmful/FrostburnEffect.java `
  --file org/confluence/mod/common/effect/neutral/SparkleSlimeEffect.java `
  --file org/confluence/mod/common/effect/harmful/DryadsBaneEffect.java `
  --file org/confluence/mod/common/effect/harmful/DemonicThoughtsEffect.java `
  --file org/confluence/mod/common/effect/harmful/HellFireEffect.java `
  --convert --apply
# 再同样跑 wp6d 把 IceElemental / FrostMonsterProjectile 放进来
```

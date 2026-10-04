# WP6 附魔簇：**判定为「不移植」**（1.21 已用原生数据驱动实现，机制不同）

> **结论先说**：`common/enchantment/` 下这 10 个文件在 `dep_subset.py` 里是「10/10 零扩张」的
> 完美批次（闭包 1~2 个文件、合计约 216 非空行），**但不能按 1.20 的形态搬** ——
> 1.20 这层是 Forge 时代的 `Enchantment` 子类 + `EnchantmentCategory` 模型，
> 而 1.21.1 **删掉了 `EnchantmentCategory`、并把附魔变成数据驱动**（`Enchantment` 是 record，
> 由 datapack JSON 定义）。1.21 侧同一批附魔**早已用原生方式实现**，硬搬只会造出一套编不过、
> 或者能编过但不在注册表里的平行系统。
> 本文件记录判定依据与后续该怎么做（其中 3 个附魔确实还缺，但**必须连带它们的消费端**一起做）。

## 一、测量（这批本来是「标准轻量批次」）

```powershell
python tools/port2native/dep_subset.py `
  --seed org.confluence.mod.common.enchantment.AbstractManaEnchantment ` ... 共 10 个种子 ...
  --alias ... --out notes/WP6E-SUBSET.md
# {"candidates": 10, "kept": 10, "new": 10, "removed": 0, "unused_defer": 0}
```

`notes/ALL-COMMON-CLOSURES.md` 那份全 `common/` 扫描（425 个缺失文件 / 109 个轻量种子）里，
它们也都在「闭包 1~2」的轻量名单里（`AbstractManaEnchantment` 25、`SummonerPactEnchantment` 25、
`ArcaneProtectionEnchantment` 14、`MultiBoomerangEnchantment` 29、`WhipSweepEnchantment` 29、
`ManaMendingEnchantment` 18、`ManaAffectiveEnchantment` 29、`MagicAttackEnchantment` 17、
`ManaIOEnchantment` 17、`ManaAttackEnchantment` 13 —— 这一列是「自己单独搬时的闭包文件数」）。

## 二、为什么不能搬：两侧的附魔机制根本不是一回事

| 维度 | 1.20 侧（本仓 1.20 分支） | 1.21.1 |
|---|---|---|
| 定义方式 | Java 类继承 `Enchantment`（`WhipSweepEnchantment.java:11`） | **datapack JSON**，`Enchantment` 是 record，由 `ModDataProvider` 生成 |
| 适用物品 | `EnchantmentCategory.create(...)`（Forge 概念，`ModEnchantments.java:41-47`） | `supported_items` 物品标签 + `EnchantmentTags` |
| 注册 | `DeferredRegister<Enchantment>` + `RegistryObject` | `ResourceKey<Enchantment>` + `Holder`（`ModEnchantments.java:26-38`） |
| 效果 | 覆写 `checkCompatibility` / `isTradeable` / `getMinCost` 等方法 | 数据组件（`EffectComponentTypes`）+ `exclusiveWith` 标签 |

1.21 侧对这些内容**已经有等价实现**，而且是 1.20 那份源码自己承认的：
`WhipSweepEnchantment.java:9` 的 javadoc 原文就是「等级、附魔台消耗和适用槽位**与 1.21 侧保持一致**」
—— 即 1.20 这层是**照着 1.21 的数据驱动结果反写的模拟层**。

### 逐个对照（1.20 的 11 条注册 vs 1.21 的 datapack 条目）

| 1.20 注册（类） | 1.21 现状 |
|---|---|
| `mana_regeneration`（`ManaIOEnchantment`） | ✅ `ModDataProvider.java:1868` |
| `efficient_magic`（`ManaIOEnchantment`） | ✅ `:1881` |
| `mana_mending`（`ManaMendingEnchantment`） | ✅ `:1894` |
| `celestial_absorption`（`ManaAffectiveEnchantment` + lambda） | ✅ `:1907`（连 `SummonItemEffect` 都已有，`:1922`） |
| `soothed_mana`（`ManaAffectiveEnchantment`） | ✅ `:1926` |
| `arcane_protection`（`ArcaneProtectionEnchantment`） | ✅ `:1939` |
| `spell_desperation`（`ManaAttackEnchantment`） | ✅ `:1958` |
| `mystic_surge`（`ManaAttackEnchantment`） | ✅ `:1971` |
| —— | 1.21 另有 1.20 主模组没有的 `flail_wind_burst` / `flail_turbine`（`TurbineEnchantments` / `WindBurstEnchantments`） |

也就是说 **10 个待移植文件里 7 个（`AbstractManaEnchantment` + 6 个子类）对应的内容在 1.21 已 100% 覆盖**
（含标签 `ModEnchantmentTagsProvider` 与战利品表接入 `ChestSubProvider`）。
第 8 个 `MagicAttackEnchantment` 是**没有任何注册使用的基类**（1.20 的 `ModEnchantments` 里没有它的实例），
属死代码，更不该搬。

### 台账口径

这批文件在 `notes/COMMIT-LAG.md` 里的判定应当按 **COVERED（1.21 用原生方式已实现）** 处理，
不是 NEEDS-PORT；`notes/FILE-LAG.md` 的 file-lag 仍会显示它们 MISSING，因为那是纯文件级度量、
看不见「同一特性换机制实现」这件事 —— 这正是「判定逐提交、执行按工作包」要人工补的那一层。

## 三、确实还缺的 3 个（但**不能单独搬**）

`whip_sweep`（`WhipSweepEnchantment`）、`multi_boomerang`（`MultiBoomerangEnchantment`）、
`summoner_pact`（`SummonerPactEnchantment`）在 1.21 侧**完全不存在**：

```powershell
# 1.21 全java搜索：WHIP_SWEEP / whip_sweep / MULTI_BOOMERANG / multi_boomerang / SUMMONER_PACT / summoner_pact
# -> 0 命中（ModEnchantments 的 ResourceKey 段也没有）
```

它们的**消费端也全在 1.20 侧、1.21 都没有**：

| 附魔 | 1.20 消费点 | 1.21 是否有 |
|---|---|---|
| `whip_sweep` | `common/entity/projectile/whip/WhipAttackEntity.java:112`（`EnchantmentHelper` 读等级决定横扫） | ❌ 无该文件 |
| `multi_boomerang` | `common/item/boomerang/BoomerangItem.java:43`（额外回旋镖数量） | ❌ 无该文件 |
| `summoner_pact` | `ItemEvents.java:58/85-88`（给仆从容量加成属性修饰符）+ `ModTabs.java:1917`（附魔书页签） | ❌ 无对应连线 |

**所以正确的批次边界是「附魔 + 它的消费端一起」**（`WhipAttackEntity` / `BoomerangItem` /
仆从容量属性），而不是只搬 3 个 20~35 行的类。落地形态也必须是 **1.21 的原生写法**：
`ModEnchantments` 加 3 个 `ResourceKey` + `ModDataProvider` 加 3 段 `Enchantment.enchantment(...)`
（数值照 1.20 那三个类：如 `whip_sweep` minCost `10+8*(l-1)`、maxCost `18+8*(l-1)`、maxLevel 1、
`MAINHAND`、`VERY_RARE`、`isTradeable=false`）+ `ModEnchantmentTagsProvider` /
`ModChineseProvider`+`ModEnglishProvider` 的 lang（1.20 的键在 `ModChineseProvider.java:493-498`、
`ModEnglishProvider.java:626-631`，可直接照抄文案）+ 创造页签附魔书条目。

## 四、给后续批次的建议顺序

1. 先落 `WhipAttackEntity` / `BoomerangItem` 所在的武器簇（WP6 的剑/悠悠球/连枷/鞭子那一层）；
2. 再补 3 个附魔的 ResourceKey + datapack 定义 + 标签 + lang（这一步是**数据驱动**的，不是 Java 类移植）；
3. 台账里给这 7 个 mana 系附魔文件记 `COVERED`，并注明「1.21 原生数据驱动实现」。

---

# 附：`dep_subset.py` 原始报告（10 个种子；**据本文判定不落地**）

- 种子 10 个：`AbstractManaEnchantment`, `ArcaneProtectionEnchantment`, `MagicAttackEnchantment`, `ManaAffectiveEnchantment`, `ManaAttackEnchantment`, `ManaIOEnchantment`, `ManaMendingEnchantment`, `MultiBoomerangEnchantment`, `SummonerPactEnchantment`, `WhipSweepEnchantment`
- 扩张后候选 **10** 个 `org.confluence.*` 类型（10/10 零扩张）
- `--alias` 3 条：`org.confluence.lib.util.LibEntityUtils` → `org.confluence.lib.util.LibUtils`, `org.confluence.mod.common.data.GamePhase` → `org.confluence.mod.common.data.saved.GamePhase`, `org.confluence.mod.common.init.entity.ModEntities` → `org.confluence.mod.common.init.entity.ModEntities`

## 工具列的「本批新增」（**本批不采纳**，理由见上文第二、三节）

- `org.confluence.mod.common.enchantment.AbstractManaEnchantment`（1.21 已有原生实现 → COVERED）
- `org.confluence.mod.common.enchantment.ArcaneProtectionEnchantment`（COVERED）
- `org.confluence.mod.common.enchantment.MagicAttackEnchantment`（1.20 里无人注册，死代码）
- `org.confluence.mod.common.enchantment.ManaAffectiveEnchantment`（COVERED）
- `org.confluence.mod.common.enchantment.ManaAttackEnchantment`（COVERED）
- `org.confluence.mod.common.enchantment.ManaIOEnchantment`（COVERED）
- `org.confluence.mod.common.enchantment.ManaMendingEnchantment`（COVERED）
- `org.confluence.mod.common.enchantment.MultiBoomerangEnchantment`（**缺**，要连带消费端）
- `org.confluence.mod.common.enchantment.SummonerPactEnchantment`（**缺**，要连带消费端）
- `org.confluence.mod.common.enchantment.WhipSweepEnchantment`（**缺**，要连带消费端）

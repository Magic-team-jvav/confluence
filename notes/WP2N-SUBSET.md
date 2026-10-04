# WP2 批次 13 · 把 26 个「自包含枢纽」一次吃掉 23 个

> 承接第 8/9 轮的结论：WP2 剩余 18 个重量物种的闭包是「186 个文件的枢纽」，共同前置是 NPC 层。
> 本轮把枢纽拆开量，发现**大量枢纽成员本身完全自包含**，于是按「自包含优先」批量落地。

## 一、方法：给每个枢纽量一次「它自己的闭包」

```powershell
# 1) 从最新闭包报告里抽出被 ≥10 个种子需要的枢纽（184 个）
python $env:TEMP\extract_hubs.py notes/WP2N-CLOSURES.md 10
# 2) 把这 184 个枢纽**当作种子**跑一遍（--small 6 只看轻量的）
python tools/port2native/seed_closures.py --src120 <1.20 src/main/java> `
  --root121 ConfluenceOtherworld/src/main/java --root121 Confluence-Magic-Lib/src/main/java `
  --assume-present org.confluence.mod.common.init.entity.MonsterEntities `
  --alias "…ModEntities=…ModEntities" --alias "…LibEntityUtils=…LibUtils" `
  --alias "…data.GamePhase=…data.saved.GamePhase" `
  --small 6 --seed …（184 个枢纽）… --out <报告>
# {"seeds": 184, "light": 33, "heavy": 151, "min_new": 0, "max_new": 183}
```

**结果：184 个枢纽里 33 个是轻量的，其中 26 个闭包=1（真·自包含，零依赖）**：

```
11 个 boss 弹幕：DeerclopsIcePillar / Plantera / DeerclopsShadowHand / DeerclopsThrownIce /
                 HillLavaPillar / Cultist / SkeletronSkull / TwinEye / PrimeLaser /
                 DestroyerLaser / NPCShadowflameSkull
 3 个 boss 部件：DungeonGuardian(130) / BossMultiplayerEnhancement(92) / SkeletronArmPose(60)
 6 个 NPC 叶子：NPCAttackBlacklist(96) / NPCNames(74) / House(29) / NPCDialog(21) / NPCChat(19) / Mood(16)
 4 个枪械构件：BulletPropertyComponent(48) / HomingController(34) / BulletImpactEffect(22) / ILeftClickStateItem(12)
 1 个坐骑基类：AbstractMountEntity(307)
 1 个世界工具：IncrementalCylinderDestruction(100)
```

**这 26 个成员本来就在 18 个重量物种的闭包里**（公共底座表里 count=18），
所以「吃掉它们」是最直接、可验证的收敛手段。

## 二、本批落地 23 个 / 1477 非空行

26 个里落地 23 个（3 个推迟，见第四节），全部零注册层依赖
（`refs_of.py` 扫 `ModEntities|MonsterEntities|BossEntities|NpcEntities|CritterEntities` 全为 `-`），
所以本批不需要任何注册条目 —— 纯增量。

子模块改动：`Confluence-Magic-Lib` 的 `LibDamageTypes` **+`DUNGEON_GUARDIAN`**
（1.20 `LibDamageTypes.java:19` 声明 + `:60` bootstrap `DamageScaling.ALWAYS, 0.1F`；
使用者是落地进来的 `DungeonGuardian`）。这与上一个大类 `FROST_BURN` 是同一套做法。

## 三、实测收敛（可复现）

| 时点 | 18 个重量种子的闭包 | hubs |
|---|---:|---:|
| 批次 11 之前 | 189 | 189 |
| 批次 12 之后 | 185 → 186（加了 `GamePhase` alias 后重算） | 184 |
| **批次 13 之后** | **163** | **161** |

即本批 23 个文件**逐个**都从 18 个重量物种的闭包里消失（186 − 23 = 163）。

## 四、推迟了 3 个（各有明确原因，不是漏做）

| 推迟项 | 非空行 | 编译错误与原因 |
|---|---:|---|
| `BossMultiplayerEnhancement` | 92 | **21 处**：`AttributeModifier` 在 1.21 是 record（`UUID` → `ResourceLocation`，5 处）、`Holder<Attribute>` 与 `Attribute` 互转（6 处）、另 5 处符号缺失。需要逐个对照 1.21 的属性 API 改写，属独立一批的活。 |
| `AbstractMountEntity` | 307 | 5 处：49/172 符号缺失、296/352 覆写签名不匹配、354 类型不匹配 —— 1.21 的坐骑/实体序列化 API 与 1.20 有差异，需先 diff 1.21 侧坐骑现状。 |
| `NPCShadowflameSkullProjectile` | 26 | 1 处：1.21 的 `SkullProjectile(EntityType<SkullProjectile>, Level)` 是**不变泛型**，而子类传 `EntityType<? extends NPCShadowflameSkullProjectile>` → 需要决策（改构造器签名 vs 加一次 unchecked cast），不适合临时凑合。 |

## 五、本批挖出的两处 API 差异

### 1. `DataResult.PartialResult` → `DataResult.Error`（已固化成规则）

DataFixerUpper 新版把嵌套类改名（`message()` 不变）。实证：`NPCNames.java:52` 报 `cannot find symbol`，
源码是 `decoded.error().map(DataResult.PartialResult::message)`；1.21.1 侧一律写 `DataResult.Error`
（`AttributeModifier.java:54`、`ExtraCodecs.java:542`）。1.20 侧共 10 处。
规则 `dataresult-partialresult-to-error`，已做正/反例自检。

### 2. 又一次 `ForgeRegistries` 漏网（与批次 12 同一类）

`NPCAttackBlacklist.java:57` `ForgeRegistries.ENTITY_TYPES.getKey(type)` —— 转换器删了 import 没改调用点，
`leftovers.txt` 依旧为空。已手改为 `BuiltInRegistries.ENTITY_TYPE.getKey(type)`。
**这已经是同一坑的第 2 例**，批次 12 记的 16 处清单里就有它；仍不加规则（理由见 `notes/WP2M-SUBSET.md` 第二节）。

## 六、验证

```powershell
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .   # 0 错误
python tools/port2native/build_errors.py --module :Confluence-Magic-Lib --repo .   # 0 错误（子模块改过）
```

转换器：`uncovered.json` = `[]`、`leftovers.txt` 空（**注意本批再次证明这两项为空不代表能编译**）。
规则文件 24 条，`json.load` 通过、无空 pattern/replace、新规则自检通过。

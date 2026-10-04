# WP6 枪械批次 G · 枪械数值/定义词汇 8 个（自包含，一次过编译门）

> 承接 `notes/ALL-COMMON-CLOSURES.md` 的轻量名单（闭包 1~4、零扩张）。这 8 个是 1.20 枪械系统
> （WP6 的 `00b72167d` 枪械合并 / `90dfd7804` 重构那两条来源）的**数值与定义词汇**：
> 计弹、弹道、射击上下文、枪械属性组件、开火模式枚举、弹幕图案。
> 与 `CritterEntities`/`OCC` 那类注册层一样，它们先落地可以让后续枪械物品/弹幕批次的闭包变小。

## 一、本批落地（8 个新文件 / 134 非空行）

| 文件 | 非空行 | 位置 |
|---|---:|---|
| `common/combat/gun/GunStats` | 4 | 枪械数值（射速/伤害等） |
| `common/combat/gun/AmmoStats` | 4 | 弹药数值 |
| `common/combat/gun/Ballistics` | 3 | 弹道参数 |
| `common/combat/gun/BallisticsResolver` | 10 | 弹道解算 |
| `common/combat/gun/ShotContext` | 14 | 射击上下文 |
| `common/component/GunPropertyComponent` | 47 | 枪械属性数据组件 |
| `common/item/gun/definition/FireMode` | 5 | 开火模式枚举 |
| `common/item/gun/definition/GunProjectilePattern` | 47 | 弹幕图案 |

```powershell
python tools/port2native/dep_subset.py --seed ...（8 个）
# {"candidates": 8, "kept": 8, "new": 8, "removed": 0, "unused_defer": 0}
python tools/port2native/stage_batch.py --name wp6g ... --convert --apply
# 暂存 8 个文件 / 134 非空行；filesChanged=1（其余 7 个无 PortLib 词汇，逐字相同）
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

转换器：`uncovered.json` = `[]`；`leftovers.txt` 0 行；重写计数
`port-bytebufcodecs-qualifier` 6、`type:alias` 3、`simple-name` 2、`type:static-alias` 1、
`port-datacomponenttype-builder` 1（都是既有的机械规则，无手工改写）。

## 二、为什么这批能「一次过」

- 8 个文件都是**纯数据/枚举/记录**（没有实体、没有注册、没有网络），依赖全落在 1.21 既有类型上；
- `common/combat/gun/**` 与 `common/item/gun/definition/{FireMode,GunProjectilePattern}`、
  `common/component/GunPropertyComponent` 在 1.21 侧此前**都不存在**（不是同名不同版），
  因此没有「覆盖已有文件」的风险；
- 1.21 既有的 `common/item/gun/{BeeGunItem,ManaGunItem,SpaceGunItem,StarCannonItem}` 与
  `common/init/ModGunProperties` 是**旧的枪械形态**，与本批不冲突（本批只加词汇类型，
  不注册任何东西、不改任何既有文件）。

## 三、本批**不是**功能完整的一批（明确说明）

这 8 个类型目前**没有消费点**：1.20 的枪械物品/弹幕（`common/item/gun/**` 的枪械族、
`BulletDefinition` + `behavior/*` 那一族）尚未落地，`GunDefinition`（1.20，闭包 4，会牵出
`GunProjectileFactory` → 164 文件的闭包）也没有搬。
之所以先落：它们是后续枪械批次的**公共词汇**，先落地可以把那批的闭包降下来，
与 WP2 批次 19（`CritterEntities` 先落地使动物闭包 171→3）、WP5 批次 A（几何基座）是同一手法；
本批没有任何「半成品」（不留编译不过的文件、不改既有行为），故按前置词汇交付。

## 四、下一步

1. **WP5 核心 28 文件**（`notes/WP5B-SUBSET.md` 第五节，两块前置 `VEC_3`/`Immunity.*` 已就位，
   开工清单只剩 3 步：注册表改造 + `RegistryObject→Holder`、两个 payload 改 `CustomPacketPayload`、
   零散 API 五条；`WhipMarkTracker` 的 `ModPrefix.Summon` 依赖改判给 WP6 词缀批次）。
2. **`CreatureSpawnPlacements` 刷怪放置层**（动物的放置规则；整篇 PortLib 词汇，要按
   `RegisterSpawnPlacementsEvent` 改写并核对 `SpawnPlacementChecks` 成员）。
3. **WP4 NPC 基座**（怪物侧 18/20 的 156 文件闭包都吊在它上面）。
4. 枪械后续批次：`GunDefinition` + `BulletDefinition`/`behavior/*` + 枪械物品/弹幕（消费本批词汇）。

---

# 附：`dep_subset.py` 原始报告

- 种子 8 个：`FireMode`, `GunStats`, `AmmoStats`, `Ballistics`, `BallisticsResolver`, `ShotContext`, `GunPropertyComponent`, `GunProjectilePattern`
- 扩张后候选 **8**（8 新增，0 被 `--defer` 剔除）
- `--alias` 3 条：`LibEntityUtils→LibUtils`、`common.data.GamePhase→common.data.saved.GamePhase`、`init.entity.ModEntities→init.ModEntities`

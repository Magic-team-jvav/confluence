# WP7 批次 A · 两个战利品条件（`entity_variant` / `difficulty_chance`）：词汇层可注册化

> `notes/ALL-COMMON-CLOSURES.md` 轻量名单里的自包含叶子（闭包 1、零扩张）。
> 这两个条件是 1.20 战利品表（WP7）的**条件词汇**：按实体变体名筛选掉落、
> 按难度在普通/专家两档概率之间取值。1.21 侧此前**没有**这两个类、也**没有**对应的
> `LootItemConditionType` 注册（`ModLootTables.ItemConditions` 只有 `date`/`game_phase`/`secret_flag`）。

## 一、本批落地（2 个新文件 / 74 非空行 + 2 条注册）

| 文件 | 非空行 | 说明 |
|---|---:|---|
| `common/loot/EntityVariantLootItemCondition` | 41 | record + `MapCodec`（`Codec.STRING.xmap`）；判定走 `VariantHolder` 的 `StringRepresentable`/枚举名（不用枚举序号，保证「同名外观映射到同材料」） |
| `common/loot/DifficultyChanceLootItemCondition` | 33 | record + `RecordCodecBuilder`（`normal_chance`/`expert_chance`，都限 0~1）；判定用 `LibUtils.isAtLeastExpert(level, pos)` |

```powershell
python tools/port2native/stage_batch.py --name wp7a ... --convert --apply
# 暂存 2 个文件 / 74 非空行；filesChanged=0（两个类都不含 PortLib 词汇，逐字相同）
python tools/port2native/build_errors.py --module ConfluenceOtherworld --repo .
# 总错误数: 0，涉及 0 个文件；[build] exit=0
```

既有文件改动（`ModLootTables.ItemConditions`，+4 行）——注册名与 1.20 逐字对应：

```java
// 1.20 ModLootTables.java:105 / :107
public static final Supplier<LootItemConditionType> DIFFICULTY_CHANCE = TYPES.register("difficulty_chance", () -> new LootItemConditionType(DifficultyChanceLootItemCondition.CODEC));
public static final Supplier<LootItemConditionType> ENTITY_VARIANT     = TYPES.register("entity_variant",     () -> new LootItemConditionType(EntityVariantLootItemCondition.CODEC));
```

（1.21 侧既有三条的写法是 `TYPES.register("date", () -> new LootItemConditionType(...))`，
本批沿用同一形状；`RegistryObject` → `Supplier` 是既有的 1.21 约定。）

## 二、本批的边界

- 交付的是**可注册、可被数据包/后续 datagen 使用的条件**（类型已进注册表，`getType()` 指回本模组条目），
  不是「只写了个类放在那儿」。缺的是**使用方**：1.21 的战利品数据生成器还没接这两个条件
  （1.20 侧的用法在 `common/data/gen/loot/**`），那属 WP7 数据生成批次。
- 没有留半成品：2 个新文件 + 2 条注册，编译门 0 错误，没有改动任何既有行为。

## 三、下一步（顺序不变）

1. **WP5 核心 28 文件**（`notes/WP5B-SUBSET.md` 第五节）：前置三块已就位 —— `VEC_3`（子模块 `95133c3`）、
   `Immunity.isActive/apply`（`e3820ac55`）、召唤粒子层（`d4c9d56df`）；开工清单剩 3 步（注册表改造 +
   `RegistryObject→Holder`、两个 payload 改 `CustomPacketPayload`、零散 API 五条）。
2. **`CreatureSpawnPlacements` 动物半边**（口径见 `notes/WP5C-SUBSET.md` 第三节：
   1.20 的 `SpawnPlacementChecks` 单点闭包 159 文件，被 `MonsterEntities` 的成员挡住，
   故只摘动物用的十几支谓词先做）。
3. **WP4 NPC 基座**（156 文件大团核心）。
4. WP7 数据生成批次：把这两个条件接进战利品 provider（与 lang/刷怪蛋/图鉴同批）。
5. 枪械功能批次（消费 `notes/WP6G-SUBSET.md` 的词汇层）。
